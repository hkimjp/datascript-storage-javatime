(ns hkimjp.datascript
  (:refer-clojure :exclude [read-string])
  (:require
   [datascript.core :as d]
   [datascript.storage.sql.core :as storage-sql]
   [fast-edn.core :refer [read-string]]
   [time-literals.read-write]
   #_[cognitect.transit :as t])
  #_(:import [java.io ByteArrayInputStream ByteArrayOutputStream]))

(def datasource
  (doto (org.sqlite.SQLiteDataSource.)
    (.setUrl "jdbc:sqlite:data/db.sqlite")))

(time-literals.read-write/print-time-literals-clj!)

(def storage
  (storage-sql/make
   datasource
   {:dbtype :sqlite
    :freeze-str pr-str
    :thaw-str   #(read-string {:readers time-literals.read-write/tags} %)
    ; :freeze-bytes
    ; (fn ^bytes [obj]
    ;   (with-open [out (ByteArrayOutputStream.)]
    ;     (t/write (t/writer out :msgpack) obj)
    ;     (.toByteArray out)))
    ; :thaw-bytes
    ; (fn [^bytes b]
    ;   (t/read
    ;    (t/reader (ByteArrayInputStream. b) :msgpack)))
    }))

(def schema nil)

(def conn nil)

(defn create-conn [schema storage]
  (alter-var-root #'conn (constantly (d/create-conn schema {:storage storage}))))

(defn restore-conn [storage]
  (alter-var-root #'conn (constantly (d/restore-conn storage))))

(defn close-conn []
  (storage-sql/close storage)
  (alter-var-root #'conn (constantly nil)))
