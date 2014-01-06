(ns small_repair.models.repair_use_vehicle
  (:require [small_repair.dao.repair_use_vehicle :as repair_use_vehicle]
            [small_repair.dao.repair_cost :as repair_cost]
            [small_repair.models.repair :as repair]
            [small_repair.utils.messages :as msg]))

(defn get-vehicle-cost
  [repair-num]
  (first (repair_cost/find-by-repair-num repair-num "vehicle")))

(defn create-vehicle
  [params]
  (let [repair-num (:repair_num params)
        vehicles (:vehicles params)
        cost (:cost params)]
    (repair_use_vehicle/delete-all repair-num)
    (repair_cost/delete-cost repair-num "vehicle")
    (doseq [vehicle vehicles]
      (repair_use_vehicle/create repair-num vehicle))
    (repair_cost/create-cost repair-num cost "vehicle")
    {:vehicles (repair_use_vehicle/find-by-repair-num repair-num)
     :cost (if (nil? (:cost (get-vehicle-cost repair-num))) 0 (:cost (get-vehicle-cost repair-num)))}))

(defn save
  [params]
  (let [rep (repair/get-repair (:repair_num params))]
    (if (nil? rep)
      (msg/get-errors "not_exist")
      (create-vehicle params))))

(defn delete
  [params]
  (repair_use_vehicle/delete-by-repair-num (Long/parseLong (:id params)) (:repair_num params))
  {:vehicles (repair_use_vehicle/find-by-repair-num (:repair_num params))
   :cost (if (nil? (:cost (get-vehicle-cost (:repair_num params)))) 0 (:cost (get-vehicle-cost (:repair_num params))))})

(defn get-vehicles
  [repair-num]
  (repair_use_vehicle/find-by-repair-num repair-num))

