(ns small_repair.models.repair_process_mode
  (:require [small_repair.dao.repair_process_mode :as repair_process_mode]
            [small_repair.models.repair :as repair]
            [small_repair.utils.common :as common]
            [small_repair.utils.messages :as msg])
  (:use [small_repair.utils.common]))

(defn save
  [params]
  (common/request-post (str "http://" (getParam "auto_address" "192.168.2.231:9898") "/auto/diagnosis") (:diagnosis params))
  (common/request-post (str "http://" (getParam "auto_address" "192.168.2.231:9898") "/auto/analysis") (:analysis params))
  (repair_process_mode/create params)
  {:process (repair_process_mode/find-by-repair-num (:repair_num params))})

(defn update
  [params]
  (common/request-post (str "http://" (getParam "auto_address" "192.168.2.231:9898") "/auto/diagnosis") (:diagnosis params))
  (common/request-post (str "http://" (getParam "auto_address" "192.168.2.231:9898") "/auto/analysis") (:analysis params))
  (repair_process_mode/update-process params)
  {:process (repair_process_mode/find-by-repair-num (:repair_num params))})

(defn delete
  [params]
  (let [id (Integer/parseInt (:id params))
        repair-num (:repair_num params)]
    (repair_process_mode/delete-by-id id repair-num)
    {:process (repair_process_mode/find-by-repair-num (:repair_num params))}))

(defn get-process-modes
  [repair-num]
  (repair_process_mode/find-by-repair-num repair-num))

(defn get-total-cost
  [repair-num]
  (first (repair_process_mode/find-total-cost repair-num)))

