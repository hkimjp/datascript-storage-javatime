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

;; ^:private?
(def conn nil)
(def schema nil)
(def storage nil)

;; defn-?
(defn- datasource
  ([] (datasource "jdbc:sqlite:data/db.sqlite"))
  ([url]
   (doto (org.sqlite.SQLiteDataSource.)
     (.setUrl url))))

(defn- pooled-datasource
  [ds]
  (storage-sql/pool ds {:max-conn 10 :max-idle-conn 4}))

;; currently sqlite3 only
(defn- sqlite-storage
  [datasource]
  (storage-sql/make datasource
                    {:dbtype :sqlite
                     :freeze-str pr-str
                     :thaw-str   #(read-string {:readers rw/tags} %)}))

(defn- make-storage [url]
  (let [st (-> url
               datasource
               pooled-datasource
               sqlite-storage)]
    (alter-var-root #'storage (constantly st))))

(defn- create-conn [schema storage]
  (alter-var-root #'conn (constantly (d/create-conn schema storage))))

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

(defn start
  ([] (create-conn nil nil))
  ([url] (if (exist? url)
           (restore-conn (make-storage url))
           (create-conn nil {:storage (make-storage url)}))))

(defn stop []
  (close-conn))

(defn conn? []
  (d/conn? conn))

(defn gc []
  (d/collect-garbage storage))

;;-----------------------------

(defn- shorten
  ([s] (shorten s 20))
  ([s n] (let [pat (re-pattern (str "(^.{" n "}).*"))]
           (str/replace-first s pat "$1..."))))

(defmacro q [query & inputs]
  (t/log! :info (str "q " query))
  `(d/q ~query @conn ~@inputs))

(defn entity [id]
  (d/entity @conn id))

(defn pull
  ([eid] (pull ['*] eid))
  ([selector eid]
   (t/log! :info (str "pull " selector " " eid))
   (d/pull @conn selector eid)))

(defn puts! [facts]
  (t/log! :info (str "puts " (shorten facts)))
  (d/transact! conn facts))

(comment
  (d/pull @conn '[*] 1)
  (d/pull @conn ['*] 1)
  (d/pull @conn '[:age] 1)
  (d/pull @conn [':age] 1)
  (get (d/entity @conn 1) :age)
  (get (entity 1) :name)
  :rcf)
