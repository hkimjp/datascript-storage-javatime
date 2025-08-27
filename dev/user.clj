(ns user
  (:require
   [hkimjp.datascript :as ds :refer [q transact! pull entity conn]]
   [clj-reload.core :as reload]
   [java-time.api :as jt]))

;;------

(reload/init
 {:dirs ["src" "dev" "test"]
  :no-reload '#{user}})

; (reload/reload)

;;------
(comment

  (ds/start)

  (def now (jt/instant))

  (ds/puts! [{:db/id -2, :java-time now}])

  (ds/qq '[:find ?e ?time
           :where
           [?e :java-time ?time]])

  (q '[:find ?time
       :where
       [_ :java-time ?time]]
     @conn)

  (pull @conn '[*] 1)

  (ds/pl 1)

  (ds/pl 2)

  (ds/stop)

  (ds/conn?)

  :rcf)

;;-----
(comment
  (def schema {:aka {:db/cardinality :db.cardinality/many}})

  (ds/start {:schema schema})

  (ds/puts! [{:db/id -1
              :name  "Maksim"
              :age   45
              :aka   ["Max Otto von Stierlitz", "Jack Ryan"]}])

  (ds/qq '[:find  ?n ?a
           :where [?e :aka "Max Otto von Stierlitz"]
           [?e :name ?n]
           [?e :age  ?a]])

  (ds/stop)
  :rcf)

;;---------

(comment
  (def url "jdbc:sqlite:resources/db.sqlite")

  (ds/start {:url url})

  (ds/puts! [{:db/id -1, :now (jt/instant)}])

  (-> (ds/qq '[:find ?e ?time
               :keys e   time
               :where
               [?e :now ?time]])
      first
      :time
      str)

  (ds/stop)

  (conn?)

  (ds/restore url)

  (-> (ds/qq '[:find ?e ?time
               :keys e   time
               :where
               [?e :now ?time]])
      first
      :time
      str)

  (-> (ds/q '[:find ?e ?time
              :keys e   time
              :where
              [?e :now ?time]]
            @conn)
      first
      :time
      str)

  :rcf)
