(ns db-test
  (:require [db :as db]
            [clojure.test :refer :all])
  )

(deftest one
  (is (= 1 1)))

(defn -main []
  (run-tests))
