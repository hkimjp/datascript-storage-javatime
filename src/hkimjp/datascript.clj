(ns hkimjp.datascript
  (:refer-clojure :exclude [read-string])
  (:require
   [clojure.string :as str]
   [datascript.core :as d]
   [datascript.storage.sql.core :as storage-sql]
   [fast-edn.core :refer [read-string]]
   [time-literals.read-write :as rw]
   [taoensso.telemere :as t]))

(def conn nil)

(def storage nil)

(def default-storage-url "jdbc:sqlite:/tmp/db.sqlite")

(defn- datasource
  [url]
  (doto (org.sqlite.SQLiteDataSource.)
    (.setUrl url)))

(defn- pooled-datasource
  [ds]
  (storage-sql/pool ds {:max-conn 10 :max-idle-conn 4}))

(time-literals.read-write/print-time-literals-clj!)

(defn- sqlite-storage
  [datasource]
  (storage-sql/make datasource
                    {:dbtype     :sqlite
                     :freeze-str pr-str
                     :thaw-str   #(read-string {:readers rw/tags} %)}))

(defn- make-storage [url]
  (t/log! :info (str "make-storage url: " url))
  (let [st (-> url
               datasource
               pooled-datasource
               sqlite-storage)]
    (alter-var-root #'storage (constantly st))))

(defn- create-conn
  ([schema]
   (t/log! :info (str "create-conn on-memory schema: " schema))
   (alter-var-root #'conn (constantly (d/create-conn schema))))
  ([schema storage]
   (t/log! :info (str "create-conn with storage schema: " schema))
   (alter-var-root #'conn (constantly (d/create-conn schema storage)))))

(defn- restore-conn [storage]
  (alter-var-root #'conn (constantly (d/restore-conn storage))))

(defn- close-conn []
  (when (some? storage)
    (storage-sql/close storage)
    (alter-var-root #'storage (constantly nil)))
  (when (some? conn)
    (alter-var-root #'conn (constantly nil))))

;; -------------------------------------------

(defn- exist? [url]
  (try
    (let [[_ _ path] (str/split url #":")]
      (.exists (java.io.File. path)))
    (catch Exception _ false)))

(defn restore
  ([] (restore {:url default-storage-url}))
  ([{:keys [url]}]
   (t/log! :info (str "restore " url))
   (if (exist? url)
     (restore-conn (make-storage url))
     (throw (Exception. (str "does not exist " url))))))

(defn start
  "If the :url argument is found and its value is nil, use the value of
   `default-storage-url` instead of nil.
   If you want an on-memory database, do not give the :url option.
   Use (restore) or (restore storage-url) when restoring."
  ([] (create-conn nil nil))
  ([{:keys [schema url] :as params}]
   (t/log! :info (str "start " params))
   (if (contains? params :url)
     (create-conn schema
                  {:storage (make-storage (or url default-storage-url))})
     (create-conn schema nil))))

(defn start-or-restore [{:keys [url] :as params}]
  (if (exist? url)
    (restore {:url url})
    (start params)))

(defn stop []
  (close-conn))

(defn conn? []
  (d/conn? conn))

(defn gc []
  (when (some? storage)
    (d/collect-garbage storage)))

;; how to call them? indirect or proxy functions?
;; shadow functions?

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

(defn- supply-id [fact]
  (if (:db/id fact)
    fact
    (assoc fact :db/id -1)))

(defn put! [fact]
  (t/log! :info (str "put! " fact))
  (d/transact! conn [(supply-id fact)]))

(defn puts! [facts]
  (t/log! :info (str "puts! " (abbrev facts)))
  (d/transact! conn (map supply-id facts)))

(defn pl
  ([eid] (pl ['*] eid))
  ([selector eid]
   (t/log! :info (str "pull " selector " " eid))
   (d/pull @conn selector eid)))

(defn et [id]
  (d/entity @conn id))
