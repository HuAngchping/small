(ns small_repair.models.clean
  (:require [small_repair.dao.clean :as clean]
            [me.raynes.fs :as fs])
  (:use [small_repair.init.statistics]))

(defn clear-all!! []
  (fs/delete-dir "/tmp/repair_index"))

(defn clean-data
  []
  (clean/clean)
  (clear-all!!)
  (set-statistics {:all 0 :untreated 0 :n_sent 0 :sent 0 :finish 0}))