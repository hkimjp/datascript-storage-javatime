(ns hkimjp.datascript
  (:refer-clojure :exclude [read-string])
  (:require
   [clojure.string :as str]
   [datascript.core :as d]
   [datascript.storage.sql.core :as storage-sql]
   [fast-edn.core :refer [read-string]]
   [time-literals.read-write :as rw]))

(time-literals.read-write/print-time-literals-clj!)

;; ^:private?
(def conn nil)
(def schema nil)
(def storage nil)

;; defn-
(defn datasource
  ([] (datasource "jdbc:sqlite:data/db.sqlite"))
  ([url]
   (doto (org.sqlite.SQLiteDataSource.)
     (.setUrl url))))

(defn pooled-datasource
  [ds]
  (storage-sql/pool ds {:max-conn 10 :max-idle-conn 4}))

;; currently sqlite3 only
(defn sqlite-storage
  [datasource]
  (storage-sql/make datasource
                    {:dbtype :sqlite
                     :freeze-str pr-str
                     :thaw-str   #(read-string {:readers rw/tags} %)}))

(defn make-storage [url]
  (let [st (-> url
               datasource
               pooled-datasource
               sqlite-storage)]
    (alter-var-root #'storage (constantly st))))

(defn create-conn [schema storage]
  (alter-var-root #'conn (constantly (d/create-conn schema storage))))

(defn restore-conn [storage]
  (alter-var-root #'conn (constantly (d/restore-conn storage))))

(defn gc []
  (d/collect-garbage storage))

(defn close-conn []
  (storage-sql/close storage)
  (alter-var-root #'storage (constantly nil))
  (alter-var-root #'conn (constantly nil)))

;---------

(defn- exist? [url]
  (let [[_ _ path] (str/split url #":")]
    (.exists (java.io.File. path))))

(defn start
  ([] (create-conn nil nil))
  ([url] (if (exist? url)
           (restore-conn (make-storage url))
           (create-conn nil {:storage (make-storage url)}))))

(defn stop []
  (close-conn))

(defn conn? []
  (d/conn? conn))
