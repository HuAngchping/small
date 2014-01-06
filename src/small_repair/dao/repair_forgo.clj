(ns small_repair.dao.repair_forgo
  (:use [korma.core]
        [korma.db])
  (:use [small_repair.db.config]
        [small_repair.utils.common])
  (:require [clojure.tools.logging :as log]))

(defn create
  [params]
  (log/info "saving repair forgo.")
  (exec-raw ["insert into repair_forgo (repair_num, reason) values (?,?)" [(:repair_num params) (:reason params)]]))

(defn find-by-repair-num
  [repair-num]
  (log/info "get repair forgo with repair_num:" repair-num)
  (exec-raw ["select * from repair_forgo where repair_num = ?" [repair-num]] :results))