(ns core
  (:require
   [compojure.core :refer :all]
   [compojure.route :as route]
   [ring.adapter.jetty :as jetty]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
   [ring.middleware.reload :refer [wrap-reload]]
   [hiccup.core :as hiccup]
   [user-management :as user]
   ))


;;---------------------------- middleware Start ----------------------

(def protected-routes ["/api-call" "/z"])

(defn handle-protected [ {:keys [:headers] :as req} handler]
  (let [btoken (get headers "authorization")
        token (try (get (clojure.string/split  btoken #" ") 1)
                   (catch Exception e ""))
        payload (user/decode-token token)
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
      response)))

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
        payload (user/authenticate-user-get-token email password)
        resp (-> (ring.util.response/response "")
                 (ring.util.response/header "token" payload)
                 (ring.util.response/status (if payload 200 401))
                 )]
    resp))    

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
