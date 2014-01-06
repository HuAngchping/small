(ns small_repair.models.repair_other_cost
  (:require [small_repair.dao.repair_other_cost :as repair_other_cost]))

(defn save
  [params]
  (repair_other_cost/create params)
  (let [costs (repair_other_cost/find-by-repair-num (:repair_num params) (:status params))]
    {:costs costs}))

(defn update
  [params]
  (repair_other_cost/update-cost params)
  (let [costs (repair_other_cost/find-by-repair-num (:repair_num params) (:status params))]
    {:costs costs}))

(defn delete
  [params]
  (repair_other_cost/delete-by-id params)
  (let [costs (repair_other_cost/find-by-repair-num (:repair_num params) (:status params))]
    {:costs costs}))

(defn get-costs
  [repair-num status]
  (let [costs (repair_other_cost/find-by-repair-num repair-num status)]
    costs))

(defn get-total-cost
  [repair-num status]
  (first (repair_other_cost/find-total-cost-by-repair-num repair-num status)))