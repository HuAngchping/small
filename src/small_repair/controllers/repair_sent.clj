(ns small_repair.controllers.repair_sent
  (:use [small_repair.utils.web])
  (:require [small_repair.models.repair_sent :as repair_sent])
  (:require [ring.util.response :as resp]))

(defhandler search
  [req]
  (let [params (:params req)
        results (repair_sent/get-sent-repairs params)]
    (resp/response results)))

(defhandler view
  [req]
  (let [params (:params req)
        results (repair_sent/get-sent-repair params)]
    (resp/response results)))

(defhandler modify-vehicle
  [req]
  (let [params (:params req)
        results (repair_sent/update-vehicle params)]
    (resp/response results)))

(defhandler add-failure
  [req]
  (let [params (:params req)
        results (repair_sent/save-failure params)]
    (resp/response results)))

(defhandler modify-failure
  [req]
  (let [params (:params req)
        results (repair_sent/update-failure params)]
    (resp/response results)))

(defhandler view-failure
  [req]
  (let [params (:params req)
        results (repair_sent/get-failures params)]
    (resp/response results)))

(defhandler drop-failure
  [req]
  (let [params (:params req)
        results (repair_sent/delete-failure params)]
    (resp/response results)))

(defhandler add-other-cost
  [req]
  (let [params (:params req)
        results (repair_sent/save-other-cost params)]
    (resp/response results)))

(defhandler modify-other-cost
  [req]
  (let [params (:params req)
        results (repair_sent/update-other-cost params)]
    (resp/response results)))

(defhandler view-other-cost
  [req]
  (let [params (:params req)
        results (repair_sent/get-other-cost params)]
    (resp/response results)))

(defhandler drop-other-cost
  [req]
  (let [params (:params req)
        results (repair_sent/delete-other-cost params)]
    (resp/response results)))

(defhandler upload
  [req]
  (let [params (:params req)
        results (repair_sent/upload-repair-sent params)]
    (resp/response results)))

(defhandler customer
  [req]
  (let [params (:params req)
        results (repair_sent/modify-customer params)]
    (resp/response results)))

(defhandler vehicle
  [req]
  (let [params (:params req)
        results (repair_sent/modify-vehicle params)]
    (resp/response results)))

(defhandler failure
  [req]
  (let [params (:params req)
        results (repair_sent/modify-failure params)]
    (resp/response results)))



