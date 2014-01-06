(ns small_repair.dao.repair_untreated
  (:use [korma.core]
        [korma.db])
  (:use [small_repair.db.config]
        [small_repair.utils.common])
  (:require [clojure.tools.logging :as log]))

(defn create
  [params]
  (log/info "saving repair untreated.")
  (exec-raw ["insert into repair_untreated (repair_num, repair_type, customer_name, customer_gender, customer_tel,
  customer_backup_tel, province, city, county, address, owner_name, owner_tel, frame_num, vehicle_type, plate_num, km,
  purchase_date, failure_desc, service_username, service_name, service_upload_at, upload_at, status) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,
  ?,?,?,?,?,?,?,?,?) returning repair_num, create_at" [(get params :repair_num) (get params :repair_type)
                                                       (get params :customer_name) (get params :customer_gender) (get params :customer_tel)
                                                       (get params :customer_backup_tel) (get params :province) (get params :city) (get params :county)
                                                       (get params :address) (get params :owner_name) (get params :owner_tel) (get params :frame_num)
                                                       (get params :vehicle_type) (get params :plate_num) (get params :km) (get params :purchase_date)
                                                       (get params :failure_desc) (get params :service_username) (get params :service_name)
                                                       (java.sql.Timestamp. (get params :service_upload_at)) (java.sql.Timestamp. (get params :service_upload_at)) "untreated"]] :results))

(defn save
  [params]
  (log/info "updating repair untreated.")
  (exec-raw ["update repair_untreated set repair_num = ?, repair_type = ?,customer_name = ?, customer_gender = ?,
  customer_tel = ?, customer_backup_tel = ?,province = ?, city = ?, county = ?,address = ?, owner_name = ?, owner_tel = ?,
  frame_num = ?, vehicle_type = ?,plate_num = ?, km = ?, purchase_date = ?,
  failure_desc = ? where id = ? returning id" [(get params :repair_num) (get params :repair_type) (get params :customer_name)
                                               (get params :customer_gender) (get params :customer_tel) (get params :customer_backup_tel)
                                               (get params :province) (get params :city)(get params :county) (get params :address)
                                               (get params :owner_name) (get params :owner_tel) (get params :frame_num) (get params :vehicle_type)
                                               (get params :plate_num) (get params :km) (get params :purchase_date) (get params :failure_desc) (get params :id)]] :results))

(defn delete-all
  []
  (log/info "deleting all repair untreated.")
  (transaction
    (exec-raw ["delete from repair_untreated"])))

(defn find-by-id
  [id]
  (log/info "get repair untreated with id:" id)
  (exec-raw ["select * from repair_untreated where id = ?" [id]] :results))

(defn find-by-coustomer-tel
  [tel]
  (log/info "get repair untreated with coustomer tel:" tel)
  (exec-raw ["select repair_num,repair_type,customer_name,customer_gender,customer_tel,customer_backup_tel,province,city,county,
  address,owner_name,owner_tel,frame_num,vehicle_type,plate_num,km,purchase_date,failure_desc,service_username,
  service_name,service_upload_at,status,update_at,create_at from repair_finish where customer_tel = ? order by id desc limit 5" [tel]] :results))

(defn find-by-repair-num
  [repair-num]
  (log/info "get repair untreated with repair_num:" repair-num)
  (exec-raw ["select repair_num,repair_type,customer_name,customer_gender,customer_tel,customer_backup_tel,province,city,county,
  address,owner_name,owner_tel,frame_num,vehicle_type,plate_num,km,purchase_date,failure_desc,service_username,
  service_name,service_upload_at,status,upload_at,update_at,create_at from repair_untreated where repair_num = ?" [repair-num]] :results))

(defn update-status
  [repair-num status]
  (log/info "updating repair untreated status with repair_num:" repair-num)
  (exec-raw ["select * from repair_untreated where repair_num = ? for update" [repair-num]] :results)
  (exec-raw ["update repair_untreated set status = ? where repair_num = ?" [status repair-num]]))

(defn find-untreated-repairs-count
  [status]
  (log/info "get count repair untreated with status:" status)
  (exec-raw ["select count(id) as num from repair_untreated where status = ?" [status]] :results))
