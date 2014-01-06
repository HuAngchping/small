(ns small-repair.handler
  (:use [compojure.core])
  (:use [small_repair.routes.repair])
  (:use [ring.middleware params
         keyword-params])
  (:require [compojure.handler :as handler]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as middleware]
            [compojure.route :as route])
  (:require [small_repair.utils.web :as web])
  (:gen-class))

(defroutes main-routes
  (context "/repair" [] app-routes)
  (route/not-found "Not Found"))

(def app
  (-> main-routes
      wrap-keyword-params
      wrap-params
      middleware/wrap-json-body
      middleware/wrap-json-response
      middleware/wrap-json-params
      web/wrap-request-header
      web/log-request-response))

(defn start [port]
  (jetty/run-jetty #'app {:port (or port 8181) :join? false :max-threads 1024}))

(defn -main []
  (if-let [port (System/getProperty "PORT")]
    (start (Integer/parseInt port))
    (start 8181)))
