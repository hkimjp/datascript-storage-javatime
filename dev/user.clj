(ns user
  (:require
   [clj-reload.core :as reload]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]))

(t/set-min-level! :debug)

;;------
(reload/init
 {:dirs ["src" "dev" "test"]
  :no-reload '#{user}})

; (reload/reload)
;;------

(comment
  (require '[java-time.api :as jt])
  (jt/local-date-time)
  (ds/conn?)

  (rand-int 100)
  (ds/start)

  (dotimes [_ 10]
    (ds/put! {:db/id -1 :num (rand-int 100)}))

  (-> (ds/qq '[:find ?e ?num
               :where
               [?e :num ?num]])
      count)

  (def data (mapv (fn [n] {:db/id -1, :num n}) (range 200 210)))

  data

  (ds/start)
  (ds/puts! data)

  (-> (ds/qq '[:find ?e ?num
               :where
               [?e :num ?num]
               [(< 300 ?num)]])
      count)

  (def d2 (mapv (fn [x] {:num x}) (range 300 310)))

  (ds/puts! d2)

  :rcf)
