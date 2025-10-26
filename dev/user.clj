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
  (def (ds/start)

  (transact! conn [{:db/id -1 :name "clojure" :time (jt/local-date)}])
  (transact! conn [{:db/id -1 :name "haskell" :time (jt/local-date 2025 10 10)}])
  (transact! conn [{:db/id -1 :name "python"  :time (jt/local-date 2025 10 1)}])
  (put! {:name "C" :time (jt/local-date 1970 1 1)})

  (let [date (jt/local-date 2025 10 1)
        ret (ds/qq '[:find ?name ?time ?date
                     :in $ ?date
                     :where
                     [?e :name ?name]
                     [?e :time ?time]
                     [(< 1 2 (+ 1 3))]] ; no (+ 1 3) ok 4.
                   date)]
    ret)

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
