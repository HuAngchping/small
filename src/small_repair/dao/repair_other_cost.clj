(ns small_repair.dao.repair_other_cost
  (:use [korma.core]
        [korma.db])
  (:use [small_repair.db.config]
        [small_repair.utils.common])
  (:require [clojure.tools.logging :as log]))

(defn create
  [params]
  (log/info "saving repair_other_cost.")
  (exec-raw ["insert into repair_other_cost (repair_num, name, price, num, status) values (?,?,?,?,?)"
             [(:repair_num params) (:name params) (:price params) (:num params) (:status params)]]))

(defn update-cost
  [params]
  (log/info "updating repair_other_cost.")
  (exec-raw ["update repair_other_cost set name = ?, price = ?, num = ? where id = ? and repair_num = ?"
             [(:name params) (:price params) (:num params) (:id params) (:repair_num params)]]))

(defn delete-by-id
  [params]
  (log/info "deleting repair_other_cost with id:" (:id params) " and repair_num:" (:repair_num params))
  (exec-raw ["delete from repair_other_cost where id = ? and repair_num = ?" [(Long/parseLong (:id params)) (:repair_num params)]]))

(defn find-by-repair-num
  [repair-num status]
  (log/info "get repair other costs whit repair_num:" repair-num)
  (exec-raw ["select id, repair_num, name, price, num, create_at from repair_other_cost where repair_num = ? and status = ?" [repair-num status]] :results))

(defn find-total-cost-by-repair-num
  [repair-num status]
  (log/info "get repair other costs total with repair_num:" repair-num)
  (exec-raw ["select sum(price * num) as total from repair_other_cost where repair_num = ? and status = ?" [repair-num status]] :results))