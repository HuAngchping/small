(ns small_repair.dao.repair_scene_failure
  (:use [korma.core]
        [korma.db])
  (:use [small_repair.db.config])
  (:require [clojure.tools.logging :as log]))

(defn create-scene-failure
  [params]
  (log/info "saving repair scene failure.")
  (exec-raw ["insert into repair_scene_failure (repair_num, describe, analysis, failure_parts_name, failure_parts_factory,
  failure_parts_num, failure_parts_amount, warranty, process_mode, parts_cost, worker_cost, total_cost, manager, worker, indirect) values
  (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) returning id" [(:repair_num params) (:describe params) (:analysis params) (:failure_parts_name params)
                                                 (:failure_parts_factory params) (:failure_parts_num params) (:failure_parts_amount params)
                                                 (:warranty params) (:process_mode params) (:parts_cost params) (:worker_cost params) (:total_cost params)
                                                 (:manager params) (:worker params) (:indirect params)]] :results))

(defn update-scene-failure
  [params]
  (log/info "updating repair scene failure.")
  (exec-raw ["update repair_scene_failure set describe = ?, analysis = ?, failure_parts_name = ?, failure_parts_factory = ?,
  failure_parts_num = ?, failure_parts_amount = ?, warranty = ?, process_mode = ?, parts_cost = ?, worker_cost = ?, total_cost = ?, manager = ?, worker = ?,
  indirect = ? where id = ? and repair_num = ? returning id" [(:describe params) (:analysis params) (:failure_parts_name params)
                                                              (:failure_parts_factory params) (:failure_parts_num params) (:failure_parts_amount params)
                                                              (:warranty params) (:process_mode params) (:parts_cost params) (:worker_cost params) (:total_cost params)
                                                              (:manager params) (:worker params) (:indirect params) (:id params) (:repair_num params)]] :results))

(defn find-by-repair-num
  [repair-num]
  (log/info "get repair scene failure with repair_num:" repair-num)
  (exec-raw ["select * from repair_scene_failure where repair_num = ?" [repair-num]] :results))

(defn find-indirect-by-repair-num
  [repair-num]
  (log/info "get repair scene failure with repair_num:" repair-num)
  (exec-raw ["select * from repair_scene_indirect_failure where repair_num = ?" [repair-num]] :results))

(defn find-scene-failure-by-id
  [params]
  (log/info "get repair scene failure with id:" (:id params))
  (exec-raw ["select * from repair_scene_failure where id = ? and repair_num = ?" [(:id params), (:repair_num params)]] :results))

(defn find-indirect-failure-by-scene-id
  [params]
  (log/info "get indirect failure with scene failure id:" (:id params))
  (exec-raw ["select * from repair_scene_indirect_failure where scene_failure_id = ? and repair_num = ?" [(:id params), (:repair_num params)]] :results))

(defn create-process-replace
  [failure-id params]
  (log/info "saving repair scene failure process mode with replace.")
  (exec-raw ["insert into repair_scene_replace (scene_failure_id, repair_num, factory, name, num, amount, price) values (?,?,?,?,?,?,?)"
             [failure-id (:repair_num params) (:factory params) (:name params) (:num params) (:amount params) (:price params)]]))

(defn update-process-replace
  [scene-failure-id params]
  (log/info "updating repair scene failure process mode with replace.")
  (exec-raw ["update repair_scene_replace set factory = ?, name = ?, num = ?, amount = ?, price = ? where id = ? and scene_failure_id = ?"
             [(:factory params) (:name params) (:num params) (:amount params) (:price params) (:id params) scene-failure-id]]))

(defn find-process-replace-by-scene-id
  [id repair-num]
  (log/info "get repair scene process replace with scene_failure_id:" id)
  (exec-raw ["select * from repair_scene_replace where scene_failure_id = ? and repair_num = ?" [id repair-num]] :results))

(defn create-process-other
  [failure-id params]
  (log/info "saving repair scene failure process mode with overhaul or adjust.")
  (exec-raw ["insert into repair_scene_overhaul_adjust (scene_failure_id, repair_num, describe) values (?,?,?)" [failure-id (:repair_num params) (:describe params)]]))

(defn update-process-other
  [failure-id params]
  (log/info "updating repair scene failure process mode with overhaul or adjust.")
  (exec-raw ["update repair_scene_overhaul_adjust set describe = ? where id = ? and scene_failure_id = ?" [(:describe params) (:id params) failure-id]]))

(defn find-process-other-by-scene-id
  [id repair-num]
(log/info "get repair scene process other with scene_failure_id:" id)
(exec-raw ["select * from repair_scene_overhaul_adjust where scene_failure_id = ? and repair_num = ?" [id repair-num]] :results))

(defn create-scene-indirect-failure
  [scene-failure-id params]
  (log/info "saving repair scene indirect failure.")
  (exec-raw ["insert into repair_scene_indirect_failure (scene_failure_id, repair_num, describe, analysis, failure_parts_name, failure_parts_factory,
  failure_parts_num, failure_parts_amount, warranty, process_mode, parts_cost, worker_cost, total_cost, manager, worker) values
  (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) returning id" [scene-failure-id (:repair_num params) (:describe params) (:analysis params) (:failure_parts_name params)
                                                 (:failure_parts_factory params) (:failure_parts_num params) (:failure_parts_amount params)
                                                 (:warranty params) (:process_mode params) (:parts_cost params) (:worker_cost params) (:total_cost params)
                                                 (:manager params) (:worker params)]] :results))

(defn update-scene-indirect-failure
  [params]
  (log/info "updating repair scene indirect failure.")
  (exec-raw ["update repair_scene_indirect_failure set describe = ?, analysis = ?, failure_parts_name = ?, failure_parts_factory = ?,
  failure_parts_num = ?, failure_parts_amount = ?, warranty = ?, process_mode = ?, parts_cost = ?, worker_cost = ?, total_cost = ?, manager = ?, worker = ?
  where id = ? and repair_num = ? returning id" [(:describe params) (:analysis params) (:failure_parts_name params)
                                                 (:failure_parts_factory params) (:failure_parts_num params) (:failure_parts_amount params)
                                                 (:warranty params) (:process_mode params) (:parts_cost params) (:worker_cost params) (:total_cost params)
                                                 (:manager params) (:worker params) (:id params) (:repair_num params)]] :results))


(defn find-scene-indirect-failure-by-id
  [id]
  (log/info "get repair scene failure with id:" id)
  (exec-raw ["select * from repair_scene_indirect_failure where id = ?" [id]] :results))

(defn create-indirect-process-replace
  [indirect-failure-id params]
  (log/info "saving repair scene indirect failure process mode with replace.")
  (exec-raw ["insert into repair_scene_indirect_replace (indirect_failure_id, repair_num, factory, name, num, amount, price) values (?,?,?,?,?,?,?)"
             [indirect-failure-id (:repair_num params) (:factory params) (:name params) (:num params) (:amount params) (:price params)]]))

(defn update-indirect-process-replace
  [indirect-failure-id params]
  (log/info "updating repair scene indirect failure process mode with replace.")
  (exec-raw ["update repair_scene_indirect_replace set factory = ?, name = ?, num = ?, amount = ?, price = ? where id = ? and indirect_failure_id = ?"
             [(:factory params) (:name params) (:num params) (:amount params) (:price params) (:id params) indirect-failure-id]]))

(defn find-indirect-process-replace-by-indirect-id
  [indirect-id repair-num]
  (log/info "get repair scene indirect replace with indirect_failure_id:" indirect-id)
  (exec-raw ["select * from repair_scene_indirect_replace where indirect_failure_id = ? and repair_num = ?" [indirect-id repair-num]] :results))

(defn create-indirect-process-other
  [indirect-failure-id params]
  (log/info "saving repair scene indirect failure process mode with overhaul or adjust.")
  (exec-raw ["insert into repair_scene_indirect_overhaul_adjust (indirect_failure_id, repair_num, describe) values (?,?,?)" [indirect-failure-id (:repair_num params) (:describe params)]]))

(defn update-indirect-process-other
  [indirect-failure-id params]
  (log/info "updating repair scene indirect failure process mode with overhaul or adjust.")
  (exec-raw ["update repair_scene_indirect_overhaul_adjust set describe = ? where id = ? and indirect_failure_id = ?" [(:describe params) (:id params) indirect-failure-id]]))

(defn find-indirect-process-other-by-indirect-id
  [indirect-id repair-num]
  (log/info "get repair scene indirect overhaul adjust with indirect_failure_id:" indirect-id)
  (exec-raw ["select * from repair_scene_indirect_overhaul_adjust where indirect_failure_id = ? and repair_num = ?" [indirect-id repair-num]] :results))

(defn delete-process-replace
  [id repair-num]
  (log/info "deleting repair failure process mode with replace." id repair-num)
  (exec-raw ["delete from repair_scene_replace where id = ? and repair_num = ?" [id repair-num]]))

(defn delete-process-other
  [id repair-num]
  (log/info "deleting repair failure process mode with replace.")
  (exec-raw ["delete from repair_scene_overhaul_adjust where id = ? and repair_num = ?" [id repair-num]]))

(defn delete-indirect-process-replace
  [id repair-num]
  (log/info "deleting repair failure mode with replace.")
  (exec-raw ["delete from repair_scene_indirect_replace where id = ? and repair_num = ?" [id repair-num]]))

(defn delete-indirect-process-other
  [id repair-num]
  (log/info "deleting repair failure mode with overhaul or adjust.")
  (exec-raw ["delete from repair_scene_indirect_overhaul_adjust where id = ? and repair_num = ?" [id repair-num]]))

(defn delete-scene-failure-by-id
  [id repair-num]
  (log/info "deleting repair scene failure with id:" id)
  (exec-raw ["delete from repair_scene_failure where id = ? and repair_num = ?" [id repair-num]]))

(defn delete-indirect-failure-by-scene-id
  [indirect-failure-id repair-num]
  (log/info "deleting repair scene indirect failure with repair_num:" repair-num)
  (exec-raw ["delete from repair_scene_indirect_failure where id = ? and repair_num = ?" [indirect-failure-id repair-num]]))

(defn delete-indirect-process-replace-by-scene-id
  [indirect-failure-id repair-num]
  (log/info "deleting repair failure mode replace with repair_num." repair-num)
  (exec-raw ["delete from repair_scene_indirect_replace where indirect_failure_id = ? and repair_num = ?" [indirect-failure-id repair-num]]))

(defn delete-indirect-process-other-by-scene-id
  [indirect-failure-id repair-num]
  (log/info "deleting repair failure mode overhaul or adjust with repair_num." repair-num)
  (exec-raw ["delete from repair_scene_indirect_overhaul_adjust where indirect_failure_id = ? and repair_num = ?" [indirect-failure-id repair-num]]))

(defn delete-process-other-by-scene-id
  [id repair-num]
  (log/info "deleting repair failure process mode overhaul adjust with scene_failure_id." id repair-num)
  (exec-raw ["delete from repair_scene_overhaul_adjust where scene_failure_id = ? and repair_num = ?" [id repair-num]]))

(defn delete-process-replace-by-scene-id
  [id repair-num]
  (log/info "deleting repair failure process mode place with scene_failure_id." id repair-num)
  (exec-raw ["delete from repair_scene_replace where scene_failure_id = ? and repair_num = ?" [id repair-num]]))

(defn find-failure-parts-total-cost
  [repair-num]
  (log/info "get repair failure parts total cost.")
  (exec-raw ["select sum(parts_cost) as total from repair_scene_failure where repair_num = ?" [repair-num]] :results))

(defn find-failure-worker-total-cost
  [repair-num]
  (log/info "get repair failure worker total cost.")
  (exec-raw ["select sum(worker_cost) as total from repair_scene_failure where repair_num = ?" [repair-num]] :results))

(defn find-indirect-failure-parts-total-cost
  [repair-num]
  (log/info "get repair indirect failure parts total cost.")
  (exec-raw ["select sum(parts_cost) as total from repair_scene_indirect_failure where repair_num = ?" [repair-num]] :results))

(defn find-indirect-failure-worker-total-cost
  [repair-num]
  (log/info "get repair indirect failure worker total cost.")
  (exec-raw ["select sum(worker_cost) as total from repair_scene_indirect_failure where repair_num = ?" [repair-num]] :results))


(defn find-replace-by-repair-num
  [repair-num]
  (log/info "get repair scene failure replace with repair_num:" repair-num)
  (exec-raw ["select * from repair_scene_replace where repair_num = ?" [repair-num]] :results))

(defn find-indirect-replace-by-repair-num
  [repair-num]
  (log/info "get repair scene indirect failure replace with repair_num:" repair-num)
  (exec-raw ["select * from repair_scene_indirect_replace where repair_num = ?" [repair-num]] :results))


