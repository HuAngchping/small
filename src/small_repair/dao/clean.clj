(ns small_repair.dao.clean
  (:use [korma.core]
        [korma.db])
  (:use [small_repair.db.config])
  (:require [clojure.tools.logging :as log]))

(defn clean
  []
  (log/info "clean database.")
  (transaction
    (exec-raw ["delete from repair"])
    (exec-raw ["delete from repair_finish"])
    (exec-raw ["delete from repair_forgo"])
    (exec-raw ["delete from repair_n_sent"])
    (exec-raw ["delete from repair_other_cost"])
    (exec-raw ["delete from repair_parts_apply"])
    (exec-raw ["delete from repair_parts_use"])
    (exec-raw ["delete from repair_parts_back"])
    (exec-raw ["delete from repair_parts_lend"])
    (exec-raw ["delete from repair_process_mode"])
    (exec-raw ["delete from repair_scene_failure"])
    (exec-raw ["delete from repair_scene_indirect_failure"])
    (exec-raw ["delete from repair_scene_indirect_overhaul_adjust"])
    (exec-raw ["delete from repair_scene_indirect_replace"])
    (exec-raw ["delete from repair_scene_overhaul_adjust"])
    (exec-raw ["delete from repair_scene_replace"])
    (exec-raw ["delete from repair_sent"])
    (exec-raw ["delete from repair_untreated"])
    (exec-raw ["delete from repair_use_vehicle"])
    (exec-raw ["delete from repair_use_worker"])
    (exec-raw ["delete from repair_vehicle"])))