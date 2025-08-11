(ns user
  (:require
   [clj-reload.core :as reload]
   [datascript.core :as d]
   [hkimjp.datascript
    :refer [schema storage create-conn restore-conn close-conn]]
   [java-time.api :as jt]))

;------
(reload/init
 {:dirs ["src" "dev" "test"]})

(comment
  (reload/reload)
  :rcf)
;------

(comment
  schema
  storage
  (create-conn schema storage)

  (def conn (d/create-conn schema {:storage storage}))

  (d/transact! conn [{:db/id -1, :time (java.util.Date.)}])
  (d/transact! conn [{:db/id -1, :time (jt/local-date-time)}])

  (close-conn)

  (def conn (restore-conn storage))

  (d/q '[:find ?e ?time
         :where
         [?e :time ?time]]
       @conn)

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

  (d/pull @conn '[*] 4)

  (jt/plus (:time (d/pull @conn '[*] 2) (jt/days 1)))

  (close-conn)
  :rcf)
