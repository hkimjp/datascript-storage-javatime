(ns user
  (:require
   [hkimjp.datascript :as ds :refer [start stop gc conn?]]
   [clj-reload.core :as reload]
   [java-time.api :as jt]))

;;------
(comment
  (reload/init
   {:dirs ["src" "dev" "test"]})

  (reload/reload)
  :rcf)
;;------

(comment

  (defn f
    ([] (f {}))
    ([{:keys [schema storage]}]
     [schema storage]))

  (f {:schema 1 :storage 2})
  ;; => [1 2]
  (f)
  ;; => [nil nil]
  :rcf)

;;------

(comment

  (start)

  (ds/puts! [{:db/id 250, :name "250", :friends 251}
             {:db/id 251, :name "251"}])

  (ds/q '[:find ?f
          :where
          [?e :name "250"]
          [?e :friends ?f]])

  (get (ds/entity 11) :friends)
  (stop)
  (conn?)

  :rcf)

;;-----
(comment
  (def schema {:aka {:db/cardinality :db.cardinality/many}})

  (start schema)

  (ds/puts! [{:db/id -1
              :name  "Maksim"
              :age   45
              :aka   ["Max Otto von Stierlitz", "Jack Ryan"]}])
  (ds/q '[:find  ?n ?a
          :where [?e :aka "Max Otto von Stierlitz"]
          [?e :name ?n]
          [?e :age  ?a]])
  :rcf)
