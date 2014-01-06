(ns small_repair.dao.repair_n_sent
  (:use [korma.core]
        [korma.db])
  (:use [small_repair.db.config]
        [small_repair.utils.common])
  (:require [clojure.tools.logging :as log]))

(defn create
  [params manager]
  (log/info "saving repair not sent.")
  (exec-raw ["insert into repair_n_sent (manager, repair_num, repair_type, customer_name, customer_gender, customer_tel,
  customer_backup_tel, province, city, county, address, owner_name, owner_tel, frame_num, vehicle_type, plate_num, km,
  purchase_date, failure_desc, service_username, service_name, service_upload_at, status, upload_at) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,
  ?,?,?,?,?,?,?,?,?,?)" [manager (:repair_num params) (:repair_type params)
                         (:customer_name params) (:customer_gender params) (:customer_tel params)
                         (:customer_backup_tel params) (:province params) (:city params) (:county params)
                         (:address params) (:owner_name params) (:owner_tel params) (:frame_num params)
                         (:vehicle_type params) (:plate_num params) (:km params) (:purchase_date params)
                         (:failure_desc params) (:service_username params) (:service_name params)
                         (:service_upload_at params) "n_sent" (:create_at params)]]))

(defn find-by-repair-num
  [repair-num]
  (log/info "get repair not sent with repair_num:" repair-num)
  (exec-raw ["select manager,repair_num,repair_type,customer_name,customer_gender,customer_tel,customer_backup_tel,province,city,county,
  address,owner_name,owner_tel,frame_num,vehicle_type,plate_num,km,purchase_date,failure_desc,service_username,
  service_name,service_upload_at,status,upload_at,update_at,create_at from repair_n_sent where repair_num = ?" [repair-num]] :results))

(defn update-repair-customer
  [params]
  (log/info "updating repair not sent customer with repair_num:" (:repair_num params))
  (exec-raw ["update repair_n_sent set customer_name = ?, customer_gender = ?, customer_backup_tel = ?, province = ?, city = ?,
  county = ?, address = ? where repair_num = ?" [(:customer_name params) (:customer_gender params) (:customer_backup_tel params)
                                                 (:province params) (:city params) (:county params) (:address params) (:repair_num params)]]))

(defn update-repair-vehicle
  [params]
  (log/info "updating repair not sent vehicle with repair_num:" (:repair_num params))
  (exec-raw ["update repair_n_sent set owner_name = ?, owner_tel = ?, frame_num = ?, vehicle_type = ?, plate_num = ?,
  km = ?, purchase_date = ? where repair_num = ?" [(:owner_name params) (:owner_tel params) (:frame_num params)
                                                   (:vehicle_type params) (:plate_num params) (:km params) (:purchase_date params) (:repair_num params)]]))

(defn update-repair-failure
  [params]
  (log/info "updating repair not sent failure_desc with repair_num:" (:repair_num params))
  (exec-raw ["update repair_n_sent set failure_desc = ? where repair_num = ?" [(:failure_desc params) (:repair_num params)]]))

(defn update-status
  [repair-num status]
  (log/info "updating repair not sent status with repair_num:" repair-num)
  (exec-raw ["select * from repair_n_sent where repair_num = ? for update" [repair-num]] :results)
  (exec-raw ["update repair_n_sent set status = ? where repair_num = ?" [status repair-num]]))

(defn find-n-sent-repairs-count
  [status]
  (log/info "get count repair not sent with status:" status)
  (exec-raw ["select count(id) as num from repair_n_sent where status = ?" [status]] :results))
