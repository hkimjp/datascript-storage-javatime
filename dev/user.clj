(ns user
  (:require
   [clj-reload.core :as reload]
   [taoensso.telemere :as t]))

(t/set-min-level! :debug)

;;------
(reload/init
 {:dirs ["src" "dev" "test"]
  :no-reload '#{user}})
; (reload/reload)
;;------

(comment
  (require '[java-time.api :as jt]
           '[hkimjp.datascript :as ds])
  (jt/local-date-time)
  (ds/conn?)
  :rcf)
