(ns user
  (:require
   [clj-reload.core :as reload]
   [datascript.core :as d]
   [hkimjp.datascript :as ds]
   [java-time.api :as jt]))

;------
(reload/init
 {:dirs ["src" "dev" "test"]})

(comment
  (reload/reload)
  :rcf)
;------

(comment
  (def url "jdbc:sqlite:data/db.sqlite")

  (ds/conn?)

  (def conn (ds/start url))
  (ds/conn?)

  (d/q '[:find ?e ?time
         :where
         [?e :time ?time]]
       @conn)

  (d/transact! conn [{:db/id -1, :time (java.util.Date.)}])
  (d/transact! conn [{:db/id -1, :time (jt/local-date-time)}])

  (ds/stop)

  (ds/conn?)

  (def conn (ds/start url))

  (ds/stop)

  (def conn (ds/start))

  conn
  conn
  (d/transact! conn [{:db/id -1, :name "bonbay sapphire"}])
  (d/transact! conn [{:db/id -1, :date (jt/local-date)}])
  (d/transact! conn [{:db/id -1, :name "deacon" :date (jt/local-date)}])

  (d/q '[:find ?e
         :where
         [?e _ _]]
       @conn)

  (d/q '[:find ?e ?time ?name
         :keys id time name
         :in $ ?name
         :where
         [?e :date ?time]]
       @conn
       ["daecon" "bonbay sapphire"])

  (d/pull @conn '[*] 3)

  (jt/plus (:time (d/pull @conn '[*] 2) (jt/days 1)))

  (ds/stop)
  :rcf)
