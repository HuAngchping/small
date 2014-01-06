(ns small_repair.dao.repair_finish
  (:use [korma.core]
        [korma.db])
  (:use [small_repair.db.config]
        [small_repair.utils.common])
  (:require [clojure.tools.logging :as log]))

(defn create
  [status manager params]
  (log/info "saving repair_finish.")
  (exec-raw ["insert into repair_finish (manager, repair_num, repair_type, customer_name, customer_gender, customer_tel,
  customer_backup_tel, province, city, county, address, owner_name, owner_tel, frame_num, vehicle_type, plate_num, km,
  purchase_date, failure_desc, service_username, service_name, service_upload_at, status, upload_at) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,
  ?,?,?,?,?,?,?,?,?,?)" [manager (:repair_num params) (:repair_type params)
                         (:customer_name params) (:customer_gender params) (:customer_tel params)
                         (:customer_backup_tel params) (:province params) (:city params) (:county params)
                         (:address params) (:owner_name params) (:owner_tel params) (:frame_num params)
                         (:vehicle_type params) (:plate_num params) (:km params) (:purchase_date params)
                         (:failure_desc params) (:service_username params) (:service_name params)
                         (:service_upload_at params) status (:create_at params)]]))

(defn find-by-repair-num
  [repair-num]
  (log/info "get repair_finish with repair_num:" repair-num)
  (exec-raw ["select manager,repair_num,repair_type,customer_name,customer_gender,customer_tel,customer_backup_tel,province,city,county,
  address,owner_name,owner_tel,frame_num,vehicle_type,plate_num,km,purchase_date,failure_desc,service_username,
  service_name,service_upload_at,status,upload_at,update_at,create_at from repair_finish where repair_num = ?" [repair-num]] :results))

(defn find-finish-repairs-count
  []
  (log/info "get count repair finish.")
  (exec-raw ["select count(id) as num from repair_finish"] :results))
