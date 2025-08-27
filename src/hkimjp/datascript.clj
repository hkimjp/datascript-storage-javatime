(ns hkimjp.datascript
  (:refer-clojure :exclude [read-string])
  (:require
   [clojure.string :as str]
   [datascript.core :as d]
   [datascript.storage.sql.core :as storage-sql]
   [fast-edn.core :refer [read-string]]
   [time-literals.read-write :as rw]
   [taoensso.telemere :as t]))

(time-literals.read-write/print-time-literals-clj!)

(def conn nil)

(def storage nil)

(def default-storage-url "jdbc:sqlite:resources/db.sqlite")

(defn- datasource
  [url]
  (doto (org.sqlite.SQLiteDataSource.)
    (.setUrl url)))

(defn- pooled-datasource
  [ds]
  (storage-sql/pool ds {:max-conn 10 :max-idle-conn 4}))

(defn- sqlite-storage
  [datasource]
  (storage-sql/make datasource
                    {:dbtype     :sqlite
                     :freeze-str pr-str
                     :thaw-str   #(read-string {:readers rw/tags} %)}))

(defn- make-storage [url]
  (let [url (or url default-storage-url)
        st (-> url
               datasource
               pooled-datasource
               sqlite-storage)]
    (alter-var-root #'storage (constantly st))))

(defn- create-conn
  ([schema]
   (alter-var-root #'conn (constantly (d/create-conn schema))))
  ([schema storage]
   (alter-var-root #'conn (constantly (d/create-conn schema storage)))))

(defn- restore-conn [storage]
  (alter-var-root #'conn (constantly (d/restore-conn storage))))

(defn- close-conn []
  (when (some? storage)
    (storage-sql/close storage)
    (alter-var-root #'storage (constantly nil)))
  (when (some? conn)
    (alter-var-root #'conn (constantly nil))))

;; ---------------------------------

(defn- exist? [url]
  (try
    (let [[_ _ path] (str/split url #":")]
      (.exists (java.io.File. path)))
    (catch Exception _
      false)))

(defn restore [url]
  (if (exist? url)
    (restore-conn (make-storage url))
    (throw (Exception. (str "does not exist " url)))))

(defn start
  ([] (create-conn nil nil))
  ([{:keys [schema url] :as params}]
   (if (contains? params :url)
     (create-conn schema {:storage (make-storage url)})
     (create-conn schema nil))))

(defn stop []
  (close-conn))

(defn conn? []
  (d/conn? conn))

(defn gc []
  (when (some? storage)
    (d/collect-garbage storage)))

;; indirect or proxy functions? how to call them?

(def transact! d/transact!)

(def q d/q)

(def pull d/pull)

(def entity d/entity)

;; convenience functions

(defn- abbrev
  "shorten string for concise log."
  ([s] (abbrev s 80))
  ([s n] (let [pat (re-pattern (str "(^.{" n "}).*"))]
           (str/replace-first s pat "$1..."))))

;; FIXME: this did not work with (def ^:private conn nil)
;; (defmacro qq [query & inputs]
;;   (t/log! :info (str "q " query))
;;   `(d/q ~query @conn ~@inputs))

(defn qq [query & inputs]
  (t/log! :info (str "q " query))
  (apply d/q query @conn inputs))

(defn puts! [facts]
  (t/log! :info (str "puts " (abbrev facts)))
  (d/transact! conn facts))

(defn pl
  ([eid] (pl ['*] eid))
  ([selector eid]
   (t/log! :info (str "pull " selector " " eid))
   (d/pull @conn selector eid)))

(defn et [id]
  (d/entity @conn id))
