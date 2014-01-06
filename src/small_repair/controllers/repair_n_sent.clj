(ns small_repair.controllers.repair_n_sent
  (:use [small_repair.utils.web])
  (:require [small_repair.models.repair_n_sent :as repair_n_sent])
  (:require [ring.util.response :as resp]))

(defhandler upload
  [req]
  (let [params (:params req)
        results (repair_n_sent/upload-n-sent params)]
    (resp/response results)))

(defhandler search
  [req]
  (let [params (:params req)
        results (repair_n_sent/get-n-sent-reapirs params)]
    (resp/response results)))

(defhandler customer
  [req]
  (let [params (:params req)
        results (repair_n_sent/modify-customer params)]
    (resp/response results)))

(defhandler vehicle
  [req]
  (let [params (:params req)
        results (repair_n_sent/modify-vehicle params)]
    (resp/response results)))

(defhandler failure
  [req]
  (let [params (:params req)
        results (repair_n_sent/modify-failure params)]
    (resp/response results)))

(defhandler add-process
  [req]
  (let [params (:params req)
        results (repair_n_sent/save-process params)]
    (resp/response results)))

(defhandler modify-process
  [req]
  (let [params (:params req)
        results (repair_n_sent/modify-process params)]
    (resp/response results)))

(defhandler drop-process
  [req]
  (let [params (:params req)
        results (repair_n_sent/delete-process params)]
    (resp/response results)))

(defhandler add-use-vehicle
  [req]
  (let [params (:params req)
        results (repair_n_sent/save-vehicle params)]
    (resp/response results)))

(defhandler delete-use-vehicle
  [req]
  (let [params (:params req)
        results (repair_n_sent/drop-vehicle params)]
    (resp/response results)))

(defhandler add-use-worker
  [req]
  (let [params (:params req)
        results (repair_n_sent/save-worker params)]
    (resp/response results)))

(defhandler delete-use-worker
  [req]
  (let [params (:params req)
        results (repair_n_sent/drop-worker params)]
    (resp/response results)))

(defhandler view
  [req]
  (let [params (:params req)
        results (repair_n_sent/get-n-sent-reapir params)]
    (resp/response results)))

(defhandler vehicles
  [req]
  (let [params (:params req)
        results (repair_n_sent/get-vehicles params)]
    (resp/response results)))

(defhandler workers
  [req]
  (let [params (:params req)
        results (repair_n_sent/get-workers params)]
    (resp/response results)))

(defhandler add-other-cost
  [req]
  (let [params (:params req)
        results (repair_n_sent/save-other-cost params)]
    (resp/response results)))

(defhandler modify-other-cost
  [req]
  (let [params (:params req)
        results (repair_n_sent/update-other-cost params)]
    (resp/response results)))

(defhandler view-other-cost
  [req]
  (let [params (:params req)
        results (repair_n_sent/get-other-cost params)]
    (resp/response results)))

(defhandler drop-other-cost
  [req]
  (let [params (:params req)
        results (repair_n_sent/delete-other-cost params)]
    (resp/response results)))

