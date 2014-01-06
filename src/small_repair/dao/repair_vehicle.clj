(ns small_repair.dao.repair_vehicle
  (:use [korma.core]
        [korma.db])
  (:use [small_repair.db.config])
  (:require [clojure.tools.logging :as log]))

(defn create
  [{:keys [repair_num engine_type engine_num engine_order_num gearbox_type gearbox_num gearbox_user_num radiators_num radiators_vendor_code
           laminated_spring_num laminated_spring_vendor_code first_drive_num first_drive_vendor_code second_drive_num second_drive_vendor_code
           third_drive_num thrid_drive_vendor_code first_axle first_axle_vendor_code second_axle second_axle_vendor_code third_axle
           third_axle_vendor_code]}]
  (log/info "saving repair vehicle.")
  (exec-raw ["insert into repair_vehicle (repair_num, engine_type, engine_num, engine_order_num, gearbox_type, gearbox_num,
  gearbox_user_num, radiators_num, radiators_vendor_code, laminated_spring_num, laminated_spring_vendor_code, first_drive_num,
  first_drive_vendor_code, second_drive_num, second_drive_vendor_code, third_drive_num, thrid_drive_vendor_code,
  first_axle, first_axle_vendor_code, second_axle, second_axle_vendor_code, third_axle, third_axle_vendor_code) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,
  ?,?,?,?,?,?,?,?) returning id" [repair_num (if (nil? engine_type) "" engine_type) (if (nil? engine_num) "" engine_num) (if (nil? engine_order_num) "" engine_order_num)
                                  (if (nil? gearbox_type) "" gearbox_type) (if (nil? gearbox_num) "" gearbox_num) (if (nil? gearbox_user_num) "" gearbox_user_num) (if (nil? radiators_num) "" radiators_num)
                                  (if (nil? radiators_vendor_code) "" radiators_vendor_code) (if (nil? laminated_spring_num) "" laminated_spring_num) (if (nil? laminated_spring_vendor_code) "" laminated_spring_vendor_code)
                                  (if (nil? first_drive_num) "" first_drive_num) (if (nil? first_drive_vendor_code) "" first_drive_vendor_code) (if (nil? second_drive_num) "" second_drive_num) (if (nil? second_drive_vendor_code) "" second_drive_vendor_code)
                                  (if (nil? third_drive_num) "" third_drive_num) (if (nil? thrid_drive_vendor_code) "" thrid_drive_vendor_code) (if (nil? first_axle) "" first_axle) (if (nil? first_axle_vendor_code) "" first_axle_vendor_code)
                                  (if (nil? second_axle) "" second_axle) (if (nil? second_axle_vendor_code) "" second_axle_vendor_code) (if (nil? third_axle) "" third_axle) (if (nil? third_axle_vendor_code) "" third_axle_vendor_code)]] :results))

(defn save
  [id params]
  (log/info "updating repair vehicle.")
  (exec-raw [(str "update repair_vehicle set engine_type = " (:engine_type params) ", engine_num = " (:engine_num params) ",
   engine_order_num = " (:engine_order_num params) ", gearbox_type = " (:gearbox_type params) ",
   gearbox_num = " (:gearbox_num params) ",gearbox_user_num = " (:gearbox_user_num params) ", radiators_num = " (:radiators_num params) ",
   radiators_vendor_code = " (:radiators_vendor_code params) ", laminated_spring_num = " (:laminated_spring_num params) ",
   laminated_spring_vendor_code = " (:laminated_spring_vendor_code params) ", first_drive_num = " (:first_drive_num params) ",
   first_drive_vendor_code = " (:first_drive_vendor_code params) ", second_drive_num = " (:second_drive_num params) ",
   second_drive_vendor_code = " (:second_drive_vendor_code params) ", third_drive_num = " (:third_drive_num params) ",
   thrid_drive_vendor_code = " (:thrid_drive_vendor_code params) ", first_axle = " (:first_axle params) ", first_axle_vendor_code = " (:first_axle_vendor_code params) ",
   second_axle = " (:second_axle params) ", second_axle_vendor_code = " (:second_axle_vendor_code params) ",
   third_axle = " (:third_axle params) ", third_axle_vendor_code = " (:third_axle_vendor_code params) " where id = ? returning id") [id]] :results))

(defn find-all-by-repair-num
  [repair-num]
  (log/info "get all reapri vehicle with repair_num:" repair-num)
  (exec-raw ["select * from repair_vehicle where repair_num = ?" [repair-num]] :results))

(defn find-max-id
  [repair-num]
  (log/info "get repair vehicle max id with repair_num:" repair-num)
  (exec-raw ["select max(id) from repair_vehicle where repair_num = ?" [repair-num]] :results))

(defn find-by-id
  [id]
  (log/info "get repair vehicle with id:" id)
  (exec-raw ["select engine_type, engine_num, engine_order_num, gearbox_type, gearbox_num,
    gearbox_user_num, radiators_num, radiators_vendor_code, laminated_spring_num, laminated_spring_vendor_code, first_drive_num,
    first_drive_vendor_code, second_drive_num, second_drive_vendor_code, third_drive_num, thrid_drive_vendor_code,
    first_axle, first_axle_vendor_code, second_axle, second_axle_vendor_code, third_axle, third_axle_vendor_code,
    create_at as vehicle_create_at, update_at as vehicle_update_at from repair_vehicle where id = ?" [id]] :results))

(defn find-by-max-id
  [repair-num]
  (log/info "get repair vehicle with repair_num:" repair-num)
  (exec-raw ["select coalesce(engine_type, '') as engine_type, engine_num, engine_order_num, gearbox_type, gearbox_num,
    gearbox_user_num, radiators_num, radiators_vendor_code, laminated_spring_num, laminated_spring_vendor_code, first_drive_num,
    first_drive_vendor_code, second_drive_num, second_drive_vendor_code, third_drive_num, thrid_drive_vendor_code,
    first_axle, first_axle_vendor_code, second_axle, second_axle_vendor_code, third_axle, third_axle_vendor_code,
    create_at as vehicle_create_at, update_at as vehicle_update_at from repair_vehicle where id = (select max(id) from repair_vehicle where repair_num = ?)" [repair-num]] :results))

(defn find-by-min-id
  [repair-num]
  (log/info "get repair vehicle with repair_num:" repair-num)
  (exec-raw ["select engine_type, engine_num, engine_order_num, gearbox_type, gearbox_num,
    gearbox_user_num, radiators_num, radiators_vendor_code, laminated_spring_num, laminated_spring_vendor_code, first_drive_num,
    first_drive_vendor_code, second_drive_num, second_drive_vendor_code, third_drive_num, thrid_drive_vendor_code,
    first_axle, first_axle_vendor_code, second_axle, second_axle_vendor_code, third_axle, third_axle_vendor_code,
    create_at as vehicle_create_at, update_at as vehicle_update_at from repair_vehicle where id = (select min(id) from repair_vehicle where repair_num = ?)" [repair-num]] :results))

(defn delete-by-repair-num
  [repair-num]
  (log/info "deleting repair vehicle with repair_num:" repair-num)
  (exec-raw ["delete from repair_vehicle where id = (select min(id) from repair_vehicle where repair_num = ?)" [repair-num]]))