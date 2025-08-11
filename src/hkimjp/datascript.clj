(ns hkimjp.datascript
  (:refer-clojure :exclude [read-string])
  (:require
   [clojure.string :as str]
   [datascript.core :as d]
   [datascript.storage.sql.core :as storage-sql]
   [fast-edn.core :refer [read-string]]
   [taoensso.telemere :as t]
   [time-literals.read-write]
   #_[cognitect.transit :as t])
  #_(:import [java.io ByteArrayInputStream ByteArrayOutputStream]))

(def datasource
  (doto (org.sqlite.SQLiteDataSource.)
    (.setUrl "jdbc:sqlite:data/db.sqlite")))

(def pooled-datasource
  (storage-sql/pool datasource
                    {:max-conn 10
                     :max-idle-conn 4}))

(time-literals.read-write/print-time-literals-clj!)

(def storage
  (storage-sql/make pooled-datasource
                    {:dbtype :sqlite
                     :freeze-str pr-str
                     :thaw-str #(read-string {:readers time-literals.read-write/tags} %)
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

(defn create-conn []
  (alter-var-root #'conn (constantly (d/create-conn schema {:storage storage}))))

(defn restore-conn []
  (alter-var-root #'conn (constantly (d/restore-conn storage))))

(defn close-conn []
  (storage-sql/close storage)
  (alter-var-root #'conn (constantly nil)))

(defn gc []
  (d/collect-garbage storage))

;------

(defn- shorten
  ([s] (shorten s 80))
  ([s n] (let [pat (re-pattern (str "(^.{" n "}).*"))]
           (str/replace-first s pat "$1..."))))

(defn put! [facts]
  (t/log! :info (str "put! " (shorten facts)))
  (d/transact! conn facts))

(defmacro q? [query & inputs]
  (t/log! :info (str "q " query))
  `(d/q ~query @conn ~@inputs))

(defn pull?
  ([eid] (pull? '[*] eid))
  ([selector eid]
   (t/log! :info (str "pull " selector " " eid))
   (d/pull @conn selector eid)))

(defn entity?
  [eid]
  (t/log! :info (str "entity " eid))
  (d/entity @conn eid))
