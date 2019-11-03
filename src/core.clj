(ns core
  (:require
   [compojure.core :refer :all]
   [compojure.route :as route]
   [ring.adapter.jetty :as jetty]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
   [ring.middleware.reload :refer [wrap-reload]]
   [hiccup.core :as hiccup]
   [ring.middleware.json :refer [wrap-json-response]]
   [ring.middleware.resource :refer [wrap-resource]]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [user-management :as user]
   ))


;;---------------------------- middleware Start ----------------------

(def protected-routes ["/get-user" "/api-call" "/z" ])

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




(defn index [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body  (hiccup/html [:h1 "Hello Wld from H"])
   }
  )


(defn login [ {:keys [:params] :as req} ]
  (let [email (:email params)
        password (:password params)
        payload (user/authenticate-user-get-token email password)
        resp (-> (ring.util.response/response {:email email})
                 (ring.util.response/header "token" payload)
                 (ring.util.response/status (if payload 200 401))
                 )]
    ;; (clojure.pprint/pprint params)
    resp))    

(defn api-call [ {:keys [:headers] :as req}]
  (print "IN API-CALL")
  "<h1>SECRET API CALL</h1>")

(defn get-user [ {:keys [:headers] :as req}]
  (let [btoken (get headers "authorization")
        token (try (get (clojure.string/split  btoken #" ") 1)
                   (catch Exception e ""))
        decoded-token (user/decode-token token)
        email (:email (:payload decoded-token))
        payload (user/get-user email)
        resp (-> (ring.util.response/response payload)
                 (ring.util.response/status 200))
        ]
    ;;(clojure.pprint/pprint decoded-token)
    resp
    ))


(defroutes myroutes
  (GET "/" [] index)
  (POST "/login" [] login)
  (GET "/get-user" [] get-user)
  (GET "/api-call" [] api-call)
  (route/not-found "<h1>Sage not found</h1>")
  )


(def myapp
  (-> myroutes
      (wrap-resource "/public")
      (wrap-defaults api-defaults)
      wrap-reload
      ring.middleware.params/wrap-params
      ring.middleware.keyword-params/wrap-keyword-params
      wrap-protected-routes
      wrap-json-response
      ))
(defonce server (jetty/run-jetty #'myapp {:port 8080 :join? false}))
;; (.stop server) and (.start server)

(defn -main []
  (println "HELLO")
  (.start server))


