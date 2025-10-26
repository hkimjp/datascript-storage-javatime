(ns user
  (:require
   [hkimjp.datascript :as ds :refer [q transact! pull entity conn put!]]
   [clj-reload.core :as reload]
   [java-time.api :as jt]
   [taoensso.telemere :as t]))

(t/set-min-level! :debug)

;;------
(reload/init
 {:dirs ["src" "dev" "test"]
  :no-reload '#{user}})
; (reload/reload)
;;------
