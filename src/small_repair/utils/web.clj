(ns small_repair.utils.web
  (:use ring.util.response)
  (:require [small_repair.utils.log :as log]))

(defn error-response
  [errno msg]
  (response {:success false
             :errno errno
             :message msg}))

(defn request-error
  [errno msg]
  {:success false
   :errno errno
   :message msg})

(defmacro defhandler
  [name args & body]
  `(defn ~name [req#]
     (try
       (let [{:keys ~args :or {~'req req# ~'abc 0}} (:params req#)]
         ~@body)
       (catch ~'Exception e# (log/error e# "error") (error-response "-1" (.getMessage e#)))
       )
     )
  )

(defn log-request-response [handler]
  "middleware for log request and response directly"
  (fn [req]
    (let [resp (handler req)]
      (println "println response" resp)
      (log/info "\nrequest -> \n" req "\nresponse -> \n" resp)
      resp
      )))

(defn convert-8859-2-utf8 [s]
  (String. (.getBytes s "iso-8859-1") "UTF-8")
  )

(defn- parse-headers [headers]
  (into {} (map (fn [k] [(keyword (first k)) (convert-8859-2-utf8 (last k))])
             headers)))

(defn wrap-request-header
  "Middleware that converts request headers to a map of
  parameters, which is added to the request map on the :params"
  [handler]
  (fn [request]
    (if-let [header (parse-headers (get request :headers ))]
      (handler (-> request
                 (update-in [:params ] merge header)))
      (handler request))))
