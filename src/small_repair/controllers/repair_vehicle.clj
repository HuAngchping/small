(ns small_repair.controllers.repair_vehicle
  (:use [small_repair.utils.web])
  (:require [small_repair.models.repair_vehicle :as repair_vehicle])
  (:require [ring.util.response :as resp]))

(defhandler save
  [req]
  (let [params (get req :params)]
    (repair_vehicle/update params)))

(defhandler gvehicle
  [req]
  (let [params (get req :params)
        id (Long/parseLong (get params :id))]
    (let [results (repair_vehicle/get-vehicle id)]
      (resp/response results))))