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

