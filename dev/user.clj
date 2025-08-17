(ns user
  (:require
   [datascript.core :as d]
   [hkimjp.datascript :as ds :refer [start stop gc conn?]]
   [java-time.api :as jt]))

;;------
(comment
  (require '[clj-reload.core :as reload])

  (reload/init
   {:dirs ["src" "dev" "test"]})

  (reload/reload)
  :rcf)
;;------

(comment
  (def url "jdbc:sqlite:data/db.sqlite")

  (conn?)
  (def conn (start url))
  (conn?)

  (d/q '[:find ?e ?time
         :where
         [?e :time ?time]]
       @conn)

  (d/transact! conn [{:db/id -1, :time (java.util.Date.)}])
  (d/transact! conn [{:db/id -1, :time (jt/local-date-time)}])

  (stop)

  (conn?)

  (def conn (start url))

  (stop)

  (def conn (start))

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

  (gc)

  (stop)
  :rcf)
