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

(def reload! reload/reload)

;;------

(comment
  (require '[java-time.api :as jt])
  (jt/local-date-time)

  (ds/conn?)
  (ds/restore {:url "jdbc:sqlite:/tmp/test.sqlite"})
  (ds/conn?)

  (rand-int 100)

  (time (dotimes [_ 1000]
          (ds/put! {:db/id -1 :num (rand-int 100)})))

  (-> (ds/qq '[:find ?e ?num
               :where
               [?e :num ?num]
               [(< 30 ?num)]]))

  (def data (mapv (fn [n] {:db/id -1, :num n}) (range 1000)))

  (time (ds/puts! data))

  (-> (ds/qq '[:find ?e ?num
               :where
               [?e :num ?num]
               [(< 300 ?num)]])
      count)

  (-> (ds/q '[:find ?e ?num
              :where
              [?e :num ?num]
              [(< 90 ?num)]]
            @ds/conn)
      count)

  (def d2 (mapv (fn [x] {:num x}) (range 300 310)))

  (ds/puts! d2)

  ;; pull

  (ds/qq '[:find (pull ?e [*])
           :where
           [?e :num ?num]
           [(< 300 ?num)]])

  (ds/close)
  :rcf)
