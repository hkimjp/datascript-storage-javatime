(ns user
  (:require
   [hkimjp.datascript :as ds :refer [q transact! pull entity conn put!]]
   [clj-reload.core :as reload]
   [java-time.api :as jt]
   [taoensso.telemere :as t]))

(t/set-min-level! :debug)

(comment
  (jt/instant)
  (jt/local-date-time)
  (java.time.LocalDate/now)
  (java.time.LocalDateTime/now)
  :rcf)

;;------
(reload/init
 {:dirs ["src" "dev" "test"]
  :no-reload '#{user}})
; (reload/reload)
;;------

(comment
  ds/version
  (ds/start)

  (transact! conn [{:db/id -1 :name "clojure"}])
  (transact! conn [{:db/id -1 :name "haskell"}])
  (transact! conn [{:db/id -1 :name "python"}])
  (put! {:name "C"})
  (q '[:find ?name
       :where
       [?e :name ?name]]
     @conn)
  (pull @conn '[*] 1)
  (entity @conn 1)

  (ds/stop)
  (ds/start {:url "jdbc:sqlite:/tmp/db.sqlite"})
  (ds/pl 1)
  (ds/pl 2)
  (ds/pl 3)
  (ds/stop)
  (ds/start nil)
  (ds/stop)
  (class "str")
  (class {:url "url"})

  (= (class "str") (class "string"))

  :rcf)
