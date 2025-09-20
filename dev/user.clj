(ns user
  (:require
   [hkimjp.datascript :as ds :refer [q transact! pull entity conn put!]]
   [clj-reload.core :as reload]
   [java-time.api :as jt]
   [taoensso.telemere :as t]))

;;------
(reload/init
 {:dirs ["src" "dev" "test"]
  :no-reload '#{user}})
; (reload/reload)
;;------

(comment
  ds/version
  (ds/start)
  (ds/stop)
  (ds/start {:url "jdbc:sqlite:/tmp/db.sqlite"})
  (ds/stop)
  (ds/start nil)
  (ds/stop)
  (class "str")
  (class {:url "url"})

  (= (class "str") (class "string"))

  :rcf)
