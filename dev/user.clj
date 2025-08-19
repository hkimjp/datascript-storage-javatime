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

(start)
(ds/puts [{:db/id -1, :greeting "hello"}
          {:db/id -1, :name "hiroshi"}
          {:db/id -1, :age 63}])
(ds/q '[:find ?e ?greeting ?to
        :where
        [?e :greeting ?greeting]
        [?e :name ?to]])

(ds/pull 1)
(ds/pull 2)
(ds/pull 3)
(ds/q '[:find ?e ?name ?to
        :where
        [?e :name ?name]
        [?e :greeting ?to]])
(ds/q '[:find ?age
        :in $ ?name
        :where
        [?e :name ?name]
        [?e :age ?age]]
      "hiroshi")

(stop)
