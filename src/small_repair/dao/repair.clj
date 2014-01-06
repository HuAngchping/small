(ns small_repair.dao.repair
  (:use [korma.core]
        [korma.db])
  (:use [small_repair.db.config])
  (:require [clojure.tools.logging :as log]))

(defn create
  [repair-num status]
  (log/info "saving repair.")
  (exec-raw ["insert into repair (repair_num, status) values (?,?)" [repair-num status]]))

(defn update-status-manager
  [repair-num status manager]
  (log/info "updating repair.")
  (exec-raw ["select * from repair where repair_num = ? for update" [repair-num]] :results)
  (exec-raw ["update repair set status = ?, manager = ?, update_at = current_timestamp where repair_num = ?" [status manager repair-num]]))

(defn find-by-repair-num
  [repair-num]
  (log/info "get repair with repair_num:" repair-num)
  (exec-raw ["select * from repair where repair_num = ? for update" [repair-num]] :results))

(defn find-count-all-repair
  []
  (log/info "get count all repair.")
  (exec-raw ["select count(id) as num from repair"] :results))

(defn find-repair-status
  [repair-num]
  (log/info "get repair status with repair_num:" repair-num)
  (exec-raw ["select status from repair where repair_num = ?" [repair-num]] :results))