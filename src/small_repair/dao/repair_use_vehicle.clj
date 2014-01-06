(ns small_repair.dao.repair_use_vehicle
  (:use [korma.core]
        [korma.db])
  (:use [small_repair.db.config]
        [small_repair.utils.common])
  (:require [clojure.tools.logging :as log]))

(defn create
  [repair-num params]
  (log/info "saving repair use vehicle.")
  (exec-raw ["insert into repair_use_vehicle (repair_num, name, type, tel, plate_num)
  values (?,?,?,?,?)" [repair-num (:name params) (:type params) (:tel params) (:plate_num params)]]))

(defn delete-all
  [repair-num]
  (log/info "deleting all repair use vehicle with repair_num:" repair-num)
  (exec-raw ["delete from repair_use_vehicle where repair_num = ?" [repair-num]]))

(defn find-by-repair-num
  [repair-num]
  (log/info "get repair use vehicles with repair_num:" repair-num)
  (exec-raw ["select id, repair_num, name, type, tel, plate_num, create_at from repair_use_vehicle where repair_num = ?" [repair-num]] :results))

(defn delete-by-repair-num
  [id repair-num]
  (log/info "deleting repair use vehicle with id:" id "and repair_num:" repair-num)
  (exec-raw ["delete from repair_use_vehicle where id = ? and repair_num = ?" [id repair-num]]))

