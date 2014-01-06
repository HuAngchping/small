(ns small_repair.models.repair_vehicle
  (:require [small_repair.dao.repair_vehicle :as repair_vehicle])
  (:use [korma.db]))

(defn save
  [params]
  (repair_vehicle/create params)
  {:success true})

(def nil-vehicle {:engine_type "" :engine_num "" :engine_order_num "" :gearbox_type "":gearbox_num ""
                  :gearbox_user_num "" :radiators_num "" :radiators_vendor_code "" :laminated_spring_num ""
                  :laminated_spring_vendor_code "" :first_drive_num "" :first_drive_vendor_code "" :second_drive_num ""
                  :second_drive_vendor_code "" :third_drive_num "" :thrid_drive_vendor_code "":first_axle ""
                  :first_axle_vendor_code "" :second_axle "" :second_axle_vendor_code "" :third_axle "" :third_axle_vendor_code ""
                  :vehicle_update_at "" :vehicle_create_at ""})

(defn get-vehicle
  [id]
  (let [vehicle (first (repair_vehicle/find-by-id id))]
    (if-not (nil? vehicle)
      vehicle
      {})))

(defn get-vehicle-with-repair-num
  [repair-num]
  (let [vehi (first (repair_vehicle/find-by-max-id repair-num))]
    (if-not (nil? vehi)
      vehi
      nil-vehicle)))

(defn get-min-vehicle-with-repair-num
  [repair-num]
  (let [vehi (first (repair_vehicle/find-by-min-id repair-num))]
    (if-not (nil? vehi)
      vehi
      nil-vehicle)))

(defn update
  [id params]
    (repair_vehicle/save id params)
    {:success true})

(defn drop-vehicle
  [repair-num]
  (repair_vehicle/delete-by-repair-num repair-num))

(defn get-max-id
  [repair-num]
  (first (repair_vehicle/find-max-id repair-num)))
