(ns small_repair.controllers.repair_untreated
  (:use [small_repair.utils.web])
  (:require [small_repair.models.repair_untreated :as repair_untreated])
  (:require [ring.util.response :as resp]))

(defhandler service-search
  [req]
  (let [params (:params req)
        results (repair_untreated/get-repairs params)]
    (resp/response {:results results})))

(defhandler service-upload
  [req]
  (let [params (:params req)
        results (repair_untreated/upload params)]
    (resp/response results)))

(defhandler view-vehicle
  [req]
  (let [params (:params req)
        results (repair_untreated/get-repair-vehicle params)]
    (resp/response results)))

(defhandler untreated-search
  [req]
  (let [params (:params req)
        results (repair_untreated/get-untreated-repairs params)]
    (resp/response results)))

(defhandler view
  [req]
  (let [params (:params req)
        results (repair_untreated/get-untreated-repair params)]
    (resp/response results)))

(defhandler process
  [req]
  (let [params (:params req)
        results (repair_untreated/process-untreated-repair params)]
    (resp/response results)))
