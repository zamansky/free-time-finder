(ns db-test
  (:require [db :as db]
            [clojure.test :refer :all])
  
  )
(deftest teststuff
  (testing "first test"
    (is (= 1 1)))
  (testing "second test"
    (is (= 2 2)))
  )


(defn -main []
  (run-tests 'db-test))

