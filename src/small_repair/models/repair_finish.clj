(ns small_repair.models.repair_finish
  (:require [small_repair.dao.repair_finish :as repair_finish]
            [small_repair.dao.repair_forgo :as repair_forgo]
            [small_repair.dao.repair_scene_failure :as repair_scene_failure]
            [small_repair.models.repair :as repair]
            [small_repair.models.repair_n_sent :as repair_n_sent]
            [small_repair.models.repair_sent :as repair_sent]
            [small_repair.models.repair_vehicle :as repair_vehicle]
            [small_repair.models.repair_other_cost :as repair_other_cost]
            [small_repair.models.repair_use_vehicle :as repair_use_vehicle]
            [small_repair.models.repair_use_worker :as repair_use_worker]
            [small_repair.models.repair_parts :as repair_parts]
            [small_repair.models.repair_process_mode :as repair_process_mode]
            [small_repair.utils.messages :as msg]
            [small_repair.utils.common :as common])
  (:require [small_repair.lucene.core :as luc]
            [clojure.data.json :as json])
  (:use [small_repair.init.statistics]
        [korma.db]))

(defn get-with-repair-num
  [repair-num]
  (first (repair_finish/find-by-repair-num repair-num)))

(defn update-statistics-add-finish
  [status]
  (if (= status "n_sent")
    (let [old-statistics (get-statistics)
          minus-statistics (merge-with - old-statistics {:n_sent 1})
          new-statistics (merge-with + minus-statistics {:finish 1})]
      (set-statistics new-statistics)))
  (if (= status "sent")
    (let [old-statistics (get-statistics)
          minus-statistics (merge-with - old-statistics {:sent 1})
          new-statistics (merge-with + minus-statistics {:finish 1})]
      (set-statistics new-statistics))))

(defn repair-forgo
  [params]
  (transaction
    (let [repair-num (:repair_num params)
          rep (repair/get-repair repair-num)
          status (:status rep)
          manager (:manager rep)]
    (if-not (= manager (:manager params))
      (msg/get-errors "not_forgo_manager")
      (if (or (= status "finish") (= status "forgo"))
        (msg/get-errors "not_forgo_status")
        (if (= status "n_sent")
          (let [repa (repair_n_sent/get-with-repair-num repair-num)]
            (if-not (= (:status repa) "n_sent")
              (msg/get-errors "processed")
              (let [delete-dir (luc/create-directory "/tmp/repair_index/repair_n_sent")
                    analyzer (luc/create-analyzer)
                    q1 (luc/create-query "fulltext" repair-num analyzer)
                    _ (luc/del-index-with-query delete-dir q1)
                    index-dir (luc/create-directory "/tmp/repair_index/repair_finish")]
                (repair_finish/create "forgo" manager repa)
                (repair/update repair-num "forgo" manager)
                (repair_n_sent/update-sus repair-num "forgo")
                (let [index-value (get-with-repair-num repair-num)]
                  (common/write-index index-value index-dir))
                (repair_forgo/create params)
                (repair_parts/revoke-all (:repair_num params))
                (update-statistics-add-finish status)
                (common/update-service-repair-status repair-num "close")
                {:success true})))
          (let [repa (repair_sent/get-with-repair-num repair-num)]
            (if-not (= (:status repa) "sent")
              (msg/get-errors "processed")
              (if-not (nil? (first (repair_scene_failure/find-by-repair-num repair-num)))
                (msg/get-errors "scene_failure_exist")
                (let [delete-dir (luc/create-directory "/tmp/repair_index/repair_sent")
                      analyzer (luc/create-analyzer)
                      q1 (luc/create-query "fulltext" repair-num analyzer)
                      _ (luc/del-index-with-query delete-dir q1)
                      index-dir (luc/create-directory "/tmp/repair_index/repair_finish")]
                  (repair_finish/create "forgo" manager repa)
                  (repair/update repair-num "forgo" manager)
                  (repair_sent/update-sus repair-num "forgo")
                  (let [index-value (get-with-repair-num repair-num)]
                    (common/write-index index-value index-dir))
                  (repair_forgo/create params)
                  (repair_parts/revoke-all-save (:repair_num params))
                  (update-statistics-add-finish status)
                  (common/update-service-repair-status repair-num "close")
                  {:success true}))))))))))

(defn get-finish-repair-results
  [q index size reader order type]
  (let [results (luc/search-paging q index size reader order type)
        docs (luc/get-docs reader (:docs results))
        rs (luc/convert-docs2-map docs)]
    rs))

(defn get-finish-repairs
  [params]
  (let [page-index (if (<= (Integer/parseInt (:page_index params)) 0) 1 (Integer/parseInt (:page_index params)))
        page-size (if (<= (Integer/parseInt (:page_size params)) 0) 10 (Integer/parseInt (:page_size params)))
        order-by (:order_by params)
        order-type (if (= (:order_type params) "a") false true)
        t (if (or (nil? (:t params)) (= (:t params) "")) "*:*" (:t params))
        manager (:manager params)
        status (:status params)]
    (if (or (nil? manager) (= manager ""))
      (msg/get-error "manager_name_nil")
      (let [index-dir (luc/create-directory "/tmp/repair_index/repair_finish")
            analyzer (luc/create-analyzer)
            reader (luc/create-index-reader index-dir)
            q1 (luc/create-query "fulltext" t analyzer)
            q2 (luc/add-filter-to-query q1 (luc/create-filter-of-exist "manager" manager))
            rs (if (or (nil? status) (= "" status)) (get-finish-repair-results q2 page-index page-size reader order-by order-type) (get-finish-repair-results (luc/add-filter-to-query q2 (luc/create-filter-of-exist "status" status)) page-index page-size reader order-by order-type))]
        {:page_index (Integer/parseInt (:page_index params)) :page_size (Integer/parseInt (:page_size params)) :results rs}))))

(defn get-sent-total-cost
  [repair-num]
  (let [parts (if (nil? (:total (first (repair_scene_failure/find-failure-parts-total-cost repair-num)))) 0 (:total (first (repair_scene_failure/find-failure-parts-total-cost repair-num))))
        worker (if (nil? (:total (first (repair_scene_failure/find-failure-worker-total-cost repair-num)))) 0 (:total (first (repair_scene_failure/find-failure-worker-total-cost repair-num))))
        i-parts (if (nil? (:total (first (repair_scene_failure/find-indirect-failure-parts-total-cost repair-num)))) 0 (:total (first (repair_scene_failure/find-indirect-failure-parts-total-cost repair-num))))
        i-worker (if (nil? (:total (first (repair_scene_failure/find-indirect-failure-worker-total-cost repair-num)))) 0 (:total (first (repair_scene_failure/find-indirect-failure-worker-total-cost repair-num))))
        other (if (nil? (:total (repair_other_cost/get-total-cost repair-num "sent"))) 0 (:total (repair_other_cost/get-total-cost repair-num "sent")))]
    {:sent_total_cost {:parts_cost (+ parts i-parts) :worker_cost (+ worker i-worker)  :other_cost other :total (+ parts i-parts worker i-worker other)}}))

(defn get-n-sent-total-cost
  [repair-num]
  (let [other (repair_other_cost/get-total-cost repair-num "n_sent")
        parts (repair_parts/get-total-cost repair-num)
        vehicle (repair_use_vehicle/get-vehicle-cost repair-num)
        worker (repair_process_mode/get-total-cost repair-num)]
    (+ (if (nil? (:total other)) 0 (:total other)) (if (nil? (:total parts)) 0 (:total parts))
      (if (nil? (:cost vehicle)) 0 (:cost vehicle)) (if (nil? (:total worker)) 0 (:total worker)))))

(defn get-parts-cost
  [parts]
  (apply + (into [] (for [part parts] (* (:borrow_count part) (:price part))))))

(defn get-finish-repair
  [params]
  (let [repair-num (:repair_num params)
        parts (:borrows (json/read-str (:body (common/request-get (str "http://" (common/getParam "parts_address" "192.168.2.231:6001") "/pieces/borrow/all") (str "?repair_num=" repair-num))) :key-fn keyword))
        repair (get-with-repair-num repair-num)
        failures (repair_sent/get-failures params)
        process-modes {:process_modes {:process (repair_process_mode/get-process-modes repair-num) :total_cost (if (nil? (:total (repair_process_mode/get-total-cost repair-num))) 0 (:total (repair_process_mode/get-total-cost repair-num)))}}
        ovehicle {:old_vehicle (repair_vehicle/get-min-vehicle-with-repair-num repair-num)}
        nvehicle {:new_vehicle (repair_vehicle/get-vehicle-with-repair-num repair-num)}
        vehicles {:vehicles {:vehicles (repair_use_vehicle/get-vehicles repair-num) :cost (if (nil? (:cost (repair_use_vehicle/get-vehicle-cost repair-num))) 0 (:cost (repair_use_vehicle/get-vehicle-cost repair-num)))}}
        workers {:workers {:workers (repair_use_worker/get-workers repair-num) :cost (if (nil? (:total (repair_process_mode/get-total-cost repair-num))) 0 (:total (repair_process_mode/get-total-cost repair-num)))}}
        n-costs {:n_sent_other_cost {:other_cost (repair_other_cost/get-costs repair-num "n_sent") :total_cost (if (nil? (:total (repair_other_cost/get-total-cost repair-num "n_sent"))) 0 (:total (repair_other_cost/get-total-cost repair-num "n_sent")))}}
        nparts {:sent_parts {:parts parts :total_cost (common/decimal-format (get-parts-cost parts))}}
        ntotal {:n_sent_total_cost (get-n-sent-total-cost repair-num)}
        s-costs {:sent_other_cost {:other_cost (repair_other_cost/get-costs repair-num "sent") :total_cost (if (nil? (:total (repair_other_cost/get-total-cost repair-num "sent"))) 0 (:total (repair_other_cost/get-total-cost repair-num "sent")))}}
        stotal (get-sent-total-cost repair-num)
        forgo {:forgo (first (repair_forgo/find-by-repair-num repair-num))}]
    (if-not (nil? repair)
      (if (= (:status repair) "forgo")
        (conj repair failures ovehicle nvehicle vehicles workers process-modes nparts n-costs s-costs ntotal stotal forgo)
        (conj repair failures ovehicle nvehicle vehicles workers process-modes nparts n-costs s-costs ntotal stotal))
      {})))

(defn get-status
  [params]
  (let [status (repair/get-repair-status (:repair_num params))]
    (if (nil? status)
      {}
      status)))
