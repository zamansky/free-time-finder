(ns user-management
  (:require
   [buddy.sign.jwt :as jwt]
   [clj-time.core :as time]

   ))


;;---------------------------- Buddy JWT Start ----------------------

(def secret "this is my secret")

(defn generate-token
  "Return sig or empty string if user / pass combo invalid"
  [payload lifespan-seconds]
  (jwt/sign {:payload payload :exp (time/plus (time/now) (time/seconds lifespan-seconds))} secret))

(defn decode-token
  [token]
  (try (jwt/unsign token secret)
       (catch Exception e nil)))


(defn is-token-valid [token]
  (not (not (decode-token token))))

(def lifespan 120)

(defn authenticate-user-get-token [email password]
  (if (and
       (= email "zamansky")
       (= password "zpass") )
    (generate-token {:email email} lifespan)
    nil))

;;---------------------------- Buddy JWT End ----------------------



