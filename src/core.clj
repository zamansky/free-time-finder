(ns core
  (:require
   [compojure.core :refer :all]
   [compojure.route :as route]
   [ring.adapter.jetty :as jetty]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
   [ring.middleware.reload :refer [wrap-reload]]
   [hiccup.core :as hiccup]
   [monger.core :as mg ]))

(defn -main []
  (println "HELLO"))


(defn index [req]
  (println "HELLO\n")
  (clojure.pprint/pprint (:headers req))
  (println "HELLO\n")
  {:statis 200
   :headers {"Content-Type" "text/html"}
   :body  (hiccup/html [:h1 "Hello Wld from H"])
   }
  )

(defroutes myroutes
  (GET "/" [] index)
  (GET "/z" [] "<h1>ZZZZ</h1>")
  (route/not-found "<h1>Page not found</h1>"))


(def myapp
  (-> myroutes
      (wrap-defaults api-defaults)
      wrap-reload
      ))
(defonce server (jetty/run-jetty #'myapp {:port 8080 :join? false}))
;; (.stop server) and (.start server)

(.start server)
