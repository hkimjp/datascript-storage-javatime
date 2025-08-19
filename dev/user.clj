(ns user
  (:require
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

  (defn f
    ([] (f {}))
    ([{:keys [schema storage]}]
     [schema storage]))

  (f {:schema 1 :storage 2})
  ;; => [1 2]
  (f)
  ;; => [nil nil]
  :rcf)

;; ------

(comment

  (start)

  (ds/puts! [{:db/id -1, :name "name"}])

  (ds/q '[:find ?e ?name
          :where
          [?e :name ?name]])

  (ds/pull 1)
  (ds/entity 1)
  (stop)
  (conn?)

  :rcf)
