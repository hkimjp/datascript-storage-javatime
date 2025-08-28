(ns user
  (:require
   [hkimjp.datascript :as ds :refer [q transact! pull entity conn]]
   [clj-reload.core :as reload]
   [java-time.api :as jt]))

;;------

(reload/init
 {:dirs ["src" "dev" "test"]
  :no-reload '#{user}})

; (reload/reload)

;;------
(ds/start)

(ds/puts! [{:db/id -1, :name "hkimura"} {:age 63} {:favorite "honami"}])

(ds/qq '[:find ?e ?name ?age ?like
         :where
         [?e :name ?name]
         [?e :age ?age]
         [?e :favorite ?like]])

(ds/pl 3)

(get (ds/et 1) :favorite)

(transact! conn [{:db/id -1, :name "isana"} {:db/id -1, :wife "yuino"}])

(-> (into [] (q '[:find ?name
                  :in $ ?wife
                  :where
                  [?e :name ?name]
                  [?e :wife ?wife]]
                @conn "yuino"))
    ffirst)
