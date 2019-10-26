(ns db
  (:require [monger.core :as mg ]
            [monger.collection :as mc]
            [monger.conversion :refer [from-db-object]]
            )
  (:import org.bson.types.ObjectId))



(def conn (mg/connect))
(def db (mg/get-db conn "monger-test"))


(def dbname "monger-test")

(defn add-base-user [email password]
  " use (.getN on the result to see how many changed)"
  (let [conn (mg/connect)
        db (mg/get-db conn "monger-test")
        oid  (ObjectId.)
        ]
    (try 
      (.getN (mc/update db "documents" {:email email}
                        {:email email :password password :_id oid}
                        {:upsert true}
                        )
             )
      (catch Exception e 0))
    ))


(defn update-user [{:keys [:email] :as payload}]
  nil
  )
(defn get-user [email]
  (let [conn (mg/connect)
        db (mg/get-db conn "monger-test")
        result (mc/find-maps db "documents" {:email email})
        ]
    (first result)
    ))


(defn -main []
  (println "IN db.clj"))
