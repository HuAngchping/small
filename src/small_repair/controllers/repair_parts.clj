(ns small_repair.controllers.repair_parts(:use [small_repair.utils.web])
  (:require [small_repair.models.repair_parts :as repair_parts])
  (:require [ring.util.response :as resp]))

(defhandler search
  [req]
  (let [params (:params req)
        results (repair_parts/search-parts params)]
    (resp/response results)))

(defhandler apply-ps
  [req]
  (let [params (:params req)
        results (repair_parts/apply-parts params)]
    (resp/response results)))

(defhandler revoke
  [req]
  (let [params (:params req)
        results (repair_parts/revoke-parts params)]
    (resp/response results)))

(defhandler revoke-all
  [req]
  (let [params (:params req)
        results (repair_parts/revoke-all-parts params)]
    (resp/response results)))

(defhandler add-all
  [req]
  (let [params (:params req)
        results (repair_parts/add-all-parts params)]
    (resp/response results)))

(defhandler parts
  [req]
  (let [params (:params req)
        results (repair_parts/get-parts params)]
    (resp/response {:parts results})))

(defhandler lend
  [req]
  (let [params (:params req)
        results (repair_parts/parts-lend params)]
    (resp/response results)))

(defhandler back
  [req]
  (let [params (:params req)
        results (repair_parts/parts-back params)]
    (resp/response results)))

(defhandler factory
  [req]
  (let [results (repair_parts/parts-factory)]
    (resp/response results)))

(defhandler brand
  [req]
  (let [params (:params req)
        results (repair_parts/parts-brand params)]
    (resp/response results)))
