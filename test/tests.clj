(ns tests
  (:require [clojure.test :refer :all]
            [dbtest]
            )
  )

(defn -main []

  (run-tests 'dbtest)
  )
