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

(defn- datasource
  ([] (datasource "jdbc:sqlite:data/db.sqlite"))
  ([url]
   (doto (org.sqlite.SQLiteDataSource.)
     (.setUrl url))))

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
  (let [st (-> url
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

(defn- exist? [url]
  (let [[_ _ path] (str/split url #":")]
    (.exists (java.io.File. path))))

;; ------------------------------

(defn restore [url]
  (restore-conn (make-storage url)))

(defn start
  ([] (create-conn nil nil))
  ([{:keys [schema url]}]
   (cond
     (nil? url) (create-conn schema)
     (exist? url) (restore url)
     :else (create-conn schema {:storage (make-storage url)}))))

(defn stop []
  (close-conn))

(defn conn? []
  (d/conn? conn))

(defn gc []
  (when (some? storage)
    (d/collect-garbage storage)))

;;-----------------------------

(defn- abbrev
  "shorten string for concise log."
  ([s] (abbrev s 80))
  ([s n] (let [pat (re-pattern (str "(^.{" n "}).*"))]
           (str/replace-first s pat "$1..."))))

;; FIXME: this did not work with (def ^:private conn nil)
;; (defmacro q [query & inputs]
;;   (t/log! :info (str "q " query))
;;   `(d/q ~query @conn ~@inputs))

(defn q [query & inputs]
  (t/log! :info (str "q " query))
  (apply d/q query @conn inputs))

(defn entity [id]
  (d/entity @conn id))

(defn pull
  ([eid] (pull ['*] eid))
  ([selector eid]
   (t/log! :info (str "pull " selector " " eid))
   (d/pull @conn selector eid)))

(defn transact! [facts]
  (t/log! :info (str "puts " (abbrev facts)))
  (d/transact! conn facts))

(def puts! transact!)

;;-------------------
