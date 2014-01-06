(ns small_repair.dao.repair_process_mode
  (:use [korma.core]
        [korma.db])
  (:use [small_repair.db.config]
        [small_repair.utils.common])
  (:require [clojure.tools.logging :as log]))

(defn create
  [params]
  (log/info "saving repair_process_mode.")
  (exec-raw ["insert into repair_process_mode (repair_num, diagnosis, analysis, process_mode, warranty, person, cost) values (?,
  ?,?,?,?,?,?) returning id, repair_num" [(:repair_num params) (:diagnosis params) (:analysis params) (:process_mode params) (:warranty params)
                 (:person params) (:cost params)]] :results))

(defn update-process
  [params]
  (log/info "updating repair_process_mode with id:" (:id params))
  (exec-raw ["update repair_process_mode set diagnosis = ?, analysis = ?, process_mode = ?, warranty = ?,
  person = ?, cost = ? where id = ? and repair_num = ? returning id, repair_num" [(:diagnosis params) (:analysis params) (:process_mode params)
                                      (:warranty params) (:person params) (:cost params) (:id params) (:repair_num params)]] :results))
(defn delete-by-id
  [id repair-num]
  (log/info "deleting repair_process_mode with id:" id "and repair_num:" repair-num)
  (exec-raw ["delete from repair_process_mode where id = ? and repair_num = ?" [id repair-num]]))

(defn find-by-repair-num
  [repair-num]
  (log/info "get repair process modes whit repair_num:" repair-num)
  (exec-raw ["select id, repair_num, diagnosis, analysis, process_mode, warranty, person, cost, create_at from repair_process_mode where repair_num = ?" [repair-num]] :results))

(defn find-total-cost
  [repair-num]
  (log/info "get repair use worker total cost with repair_num:" repair-num)
  (exec-raw ["select sum(cost) as total from repair_process_mode where repair_num = ?" [repair-num]] :results))
