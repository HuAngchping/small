(ns small_repair.models.repair
  (:require [small_repair.dao.repair :as repair]))

(defn save
  [repair-num status]
    (repair/create repair-num status))

(defn update
  [repair-num status manager]
    (repair/update-status-manager repair-num status manager))


(defn get-repair
  [repair-num]
  (first (repair/find-by-repair-num repair-num)))

(defn get-repair-status
  [repair-num]
  (first (repair/find-repair-status repair-num)))