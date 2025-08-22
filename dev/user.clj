(ns user
  (:require
   [hkimjp.datascript :as ds :refer [start stop gc conn? restore]]
   [clj-reload.core :as reload]
   [java-time.api :as jt]))

;;------
(comment
  (reload/init
   {:dirs ["src" "dev" "test"]
    :no-reload '#{user}})

  (reload/reload)
  :rcf)
;;------
(comment

  (start)

  (def now (jt/instant))

  (gc)

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

  (start {:schema schema})

  (ds/puts! [{:db/id -1
              :name  "Maksim"
              :age   45
              :aka   ["Max Otto von Stierlitz", "Jack Ryan"]}])

  (ds/q '[:find  ?n ?a
          :where [?e :aka "Max Otto von Stierlitz"]
          [?e :name ?n]
          [?e :age  ?a]])

  (stop)

  :rcf)
;;-----
(comment

  (def url "jdbc:sqlite:storage/db.sqlite")

  (start {:url url})

  (ds/puts! [{:db/id -1, :now (jt/instant)}])

  (-> (ds/q '[:find ?e ?time
              :keys e   time
              :where
              [?e :now ?time]])
      first
      :time
      str)

  (stop)

  (conn?)

  (restore url)

  (-> (ds/q '[:find ?e ?time
              :keys e   time
              :where
              [?e :now ?time]])
      first
      :time
      str)

  :rcf)
