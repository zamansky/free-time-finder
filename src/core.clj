(ns core
  (:require
   [compojure.core :refer :all]
   [compojure.route :as route]
   [ring.adapter.jetty :as jetty]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
   [ring.middleware.reload :refer [wrap-reload]]
   [hiccup.core :as hiccup]

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
       (catch Exception e nil))
  )


(defn is-token-valid [token]
  (not (not (decode-token token))))

(def lifespan 120)
(defn authenticate-user [email password]
          (if (and
       (= email "zamansky")
       (= password "zpass") )
        (generate-token {:email email} lifespan)
    nil
    ))

;;---------------------------- Buddy JWT End ----------------------



;;---------------------------- middleware Start ----------------------

(def protected-routes ["/api-call" "/z"])

(defn handle-protected [ {:keys [:headers] :as req} handler]
  (let [btoken (get headers "authorization")
        token (try (get (clojure.string/split  btoken #" ") 1)
                   (catch Exception e ""))
        payload (decode-token token)
        body (if payload (:body (handler req)) "")
        resp (-> (ring.util.response/response body)
                 (ring.util.response/header "token" token)
                 (ring.util.response/status (if payload 200 401))
                 )]
    resp))

(defn wrap-protected-routes [handler]
  (fn [req]
    (let [uri (:uri req)
          protected? (some #(= % uri) protected-routes)
          response (if protected? (handle-protected req handler)
                       (handler req))
          ]
      (clojure.pprint/pprint response)
      response))
  )

;;---------------------------- middleware End ----------------------



(defn -main []
(println "HELLO"))


(defn index [req]
{:status 200
 :headers {"Content-Type" "text/html"}
 :body  (hiccup/html [:h1 "Hello Wld from H"])
 }
)


(defn login [ {:keys [:form-params] :as req} ]
  (let [ email (get  form-params "email")
        password (get  form-params "password")
        payload (authenticate-user email password)
        resp (-> (ring.util.response/response "")
                 (ring.util.response/header "token" payload)
                 (ring.util.response/status (if payload 200 401))
                 )]
    (clojure.pprint/pprint form-params)
    resp
    ))    

(defn api-call [ {:keys [:headers] :as req}]
  (print "IN API-CALL")
  "<h1>SECRET API CALL</h1>")


(defroutes myroutes
  (GET "/" [] index)
  (POST "/login" [] login)
  (GET "/api-call" [] api-call)
  (GET "/z" [] "<h1>ZZZZ</h1>")
  (route/not-found "<h1>Page not found</h1>"))


(def myapp
(-> myroutes
    (wrap-defaults api-defaults)
    wrap-reload
    wrap-protected-routes
    ))
(defonce server (jetty/run-jetty #'myapp {:port 8080 :join? false}))
;; (.stop server) and (.start server)

(.start server)
