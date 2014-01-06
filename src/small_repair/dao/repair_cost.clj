(ns small_repair.dao.repair_cost
  (:use [korma.core]
        [korma.db])
  (:use [small_repair.db.config])
  (:require [clojure.tools.logging :as log]))


(defn create-cost
  [repair-num cost type]
  (log/info "saving repair use vehicle cost.")
  (exec-raw ["insert into repair_cost (repair_num, type, cost) values (?,?,?)" [repair-num type cost]]))

(defn delete-cost
  [repair-num type]
  (log/info "deleting repair use vehicle cost with repair_num:" repair-num)
  (exec-raw ["delete from repair_cost where repair_num = ? and type = ?" [repair-num type]]))

(defn find-by-repair-num
  [repair-num type]
  (log/info "get repair use vehicle cost with repair_num:" repair-num)
  (exec-raw ["select cost from repair_cost where repair_num = ? and type = ?" [repair-num type]] :results))