(ns tests
  (:require [clojure.test :refer :all]
            [db-test]
            )
  )

(defn -main []

  (run-tests 'db-test)
  )
