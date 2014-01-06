(ns small_repair.dao.repair_use_worker
  (:use [korma.core]
        [korma.db])
  (:use [small_repair.db.config]
        [small_repair.utils.common])
  (:require [clojure.tools.logging :as log]))

(defn create
  [repair-num params]
  (log/info "saving repair use worker.")
  (exec-raw ["insert into repair_use_worker (repair_num, name, headman, tel)
  values (?,?,?,?)" [repair-num (:name params) (:headman params) (:tel params)]]))

(defn delete-all
  [repair-num]
  (log/info "deleting all repair use worker with repair_num:" repair-num)
  (exec-raw ["delete from repair_use_worker where repair_num = ?" [repair-num]]))

(defn find-by-repair-num
  [repair-num]
  (log/info "get repair use workers with repair_num:" repair-num)
  (exec-raw ["select id, repair_num, name, headman, tel, create_at from repair_use_worker where repair_num = ?" [repair-num]] :results))

(defn delete-by-repair-num
  [id repair-num]
  (log/info "deleting repair use worker with id:" id "and repair_num:" repair-num)
  (exec-raw ["delete from repair_use_worker where id = ? and repair_num = ?" [id repair-num]]))
