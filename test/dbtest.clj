(ns dbtest
  (:require [db :as db]
            [clojure.test :refer :all])
  )

(deftest one
  (is (= 1 1)))

