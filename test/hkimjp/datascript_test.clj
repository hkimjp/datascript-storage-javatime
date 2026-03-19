(ns hkimjp.datascript-test
  (:require [clojure.test :refer [deftest testing is]]
            [hkimjp.datascript :as sut]))

(deftest a-test
  (testing "I success."
    (is (= 0 0))))

(deftest sut
  (is (= "0.7.8" sut/version)))
