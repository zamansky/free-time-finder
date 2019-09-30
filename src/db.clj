(ns db
  (:require [monger.core :as mg ]
            [monger.collection :as mc]
            [monger.conversion :refer [from-db-object]])
  (:import org.bson.types.ObjectId))



(def conn (mg/connect))
(def db (mg/get-db conn "monger-test"))



(defn dbtest[] ;; localhost, default port


  (let [conn (mg/connect)
        db   (mg/get-db conn "monger-test")
        oid  (ObjectId.)
        doc  {:first_name "John" :last_name "Lennon"}]
    (mc/insert db "documents" (merge doc {:_id oid})))
  )

(defn add-user [email password first last]
  " use (.getN on the result to see how many changed)"
  (let [conn (mg/connect)
        db (mg/get-db conn "monger-test")
        oid  (ObjectId.)
        ]
    (try 
      (.getN (mc/update db "documents" {:last_name "smith"}
                        {:email email :password password :last last :first first :_id oid}
                        {:upsert true}
                        )
             )
      (catch Exception e 0))
    ))
