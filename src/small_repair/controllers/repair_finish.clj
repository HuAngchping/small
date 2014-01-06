(ns small_repair.controllers.repair_finish
  (:use [small_repair.utils.web]
        [small_repair.init.statistics])
  (:require [small_repair.models.repair_finish :as repair_finish])
  (:require [ring.util.response :as resp]))

(defhandler forgo
  [req]
  (let [params (:params req)
        results (repair_finish/repair-forgo params)]
    (resp/response results)))

(defhandler search
  [req]
  (let [params (:params req)
        results (repair_finish/get-finish-repairs params)]
    (resp/response results)))

(defhandler repair-statistics
  [req]
  (resp/response (get-statistics)))

(defhandler view
  [req]
  (let [params (:params req)
        results (repair_finish/get-finish-repair params)]
    (resp/response results)))

(defhandler status
  [req]
  (let [params (:params req)
        results (repair_finish/get-status params)]
    (resp/response results)))
