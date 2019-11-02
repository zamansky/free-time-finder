(ns db
  (:require [monger.core :as mg ]
            [monger.collection :as mc]
            [monger.conversion :refer [from-db-object]]
            )
  (:import org.bson.types.ObjectId))



(def conn (mg/connect))
(def db-name "monger-test")
(def db (mg/get-db conn db-name))

(def empty-user-map
  {:email  ""
   :first  ""
   :last ""
   :resume ""
   :skills {}
   :classees {}
   })


(defn add-base-user [email password]
  " use (.getN on the result to see how many changed)"
  (let [conn (mg/connect)
        db (mg/get-db conn db-name)
        oid  (ObjectId.)
        payload empty-user-map
        payload (assoc payload :email email)
        payload (assoc payload :password password)
        payload (assoc payload :_id oid)
        ]
    (try 
      (.getN (mc/update db "users" {:email email}
                        payload
                        {:upsert true}
                        )
             )
      (catch Exception e 0))
    ))


(defn update-user [{:keys [:email] :as payload}]
  (let [conn (mg/connect)
        db (mg/get-db conn db-name)
        obj (mc/update db "users" {:email email} payload {:multi false})
        result (.getN obj)        
        ]
    result
    ))


(defn get-user [email]
  (let [conn (mg/connect)
        db (mg/get-db conn db-name)
        result (mc/find-maps db "users" {:email email})
        result (dissoc (first result) :_id)
        ]
    result
    ))


(defn -main []
(println "IN db.clj"))
