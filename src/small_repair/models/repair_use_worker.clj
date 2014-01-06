(ns small_repair.models.repair_use_worker
  (:require [small_repair.dao.repair_use_worker :as repair_use_worker]
            [small_repair.dao.repair_cost :as repair_cost]
            [small_repair.models.repair :as repair]
            [small_repair.utils.messages :as msg]))

(defn save-worker
  [params]
  (let [repair-num (:repair_num params)
        workers (:workers params)]
    (repair_use_worker/delete-all repair-num)
    (doseq [worker workers]
      (repair_use_worker/create repair-num worker))
    {:workers (repair_use_worker/find-by-repair-num (:repair_num params))}))

(defn save
  [params]
  (let [rep (repair/get-repair (:repair_num params))
        status (:status rep)]
    (if (nil? rep)
      (msg/get-errors "not_exist")
      (if (= status "n_sent")
        (save-worker params)
        (msg/get-errors "add_workers")))))

(defn delete
  [params]
  (repair_use_worker/delete-by-repair-num (Long/parseLong (:id params)) (:repair_num params))
  {:workers (repair_use_worker/find-by-repair-num (:repair_num params))})

(defn get-workers
  [repair-num]
  (repair_use_worker/find-by-repair-num repair-num))

