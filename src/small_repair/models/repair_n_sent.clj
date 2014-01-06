(ns small_repair.models.repair_n_sent
  (:require [small_repair.dao.repair_n_sent :as repair_n_sent]
            [small_repair.dao.repair_sent :as repair_sent]
            [small_repair.models.repair :as repair]
            [small_repair.models.repair_process_mode :as repair_process_mode]
            [small_repair.models.repair_use_vehicle :as repair_use_vehicle]
            [small_repair.models.repair_use_worker :as repair_use_worker]
            [small_repair.models.repair_other_cost :as repair_other_cost]
            [small_repair.models.repair_vehicle :as repair_vehicle]
            [small_repair.models.repair_parts :as repair_parts]
            [small_repair.utils.messages :as msg]
            [small_repair.utils.common :as common])
  (:require [small_repair.lucene.core :as luc]
            [clojure.data.json :as json])
  (:use [small_repair.init.statistics]
        [korma.db]))

(defn save
  [params manager]
  (repair_n_sent/create params manager))

(defn get-with-repair-num
  [repair-num]
  (first (repair_n_sent/find-by-repair-num repair-num)))

(defn get-n-sent-reapirs
  [params]
  (let [page-index (if (<= (Integer/parseInt (:page_index params)) 0) 1 (Integer/parseInt (:page_index params)))
        page-size (if (<= (Integer/parseInt (:page_size params)) 0) 10 (Integer/parseInt (:page_size params)))
        order-by (:order_by params)
        order-type (if (= (:order_type params) "a") false true)
        t (if (or (nil? (:t params)) (= (:t params) "")) "*:*" (:t params))
        manager (:manager params)]
    (if (or (nil? manager) (= manager ""))
      (msg/get-errors "manager_name_nil")
      (let [index-dir (luc/create-directory "/tmp/repair_index/repair_n_sent")
            analyzer (luc/create-analyzer)
            reader (luc/create-index-reader index-dir)
            q1 (luc/create-query "fulltext" t analyzer)
            q2 (luc/add-filter-to-query q1 (luc/create-filter-of-exist "manager" manager))
            results (luc/search-paging q2 page-index page-size reader order-by order-type)
            docs (luc/get-docs reader (:docs results))
            rs (luc/convert-docs2-map docs)]
        {:page_index (Integer/parseInt (:page_index params)) :page_size (Integer/parseInt (:page_size params)) :results rs}))))

(defn get-total-cost
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

(defn get-n-sent-reapir
  [params]
  (let [repair-num (:repair_num params)
        all-parts (:borrows (json/read-str (:body (common/request-get (str "http://" (common/getParam "parts_address" "192.168.2.231:6001") "/pieces/borrow/all") (str "?repair_num=" repair-num))) :key-fn keyword))
        repair (first (repair_n_sent/find-by-repair-num repair-num))
        process-total (:total (repair_process_mode/get-total-cost repair-num))
        process-modes {:process_modes {:process (repair_process_mode/get-process-modes repair-num) :total_cost (if (nil? (:total (repair_process_mode/get-total-cost repair-num))) 0 (:total (repair_process_mode/get-total-cost repair-num)))}}
        vehicle (repair_vehicle/get-vehicle-with-repair-num repair-num)
        vehicles {:vehicles {:vehicles (repair_use_vehicle/get-vehicles repair-num) :cost (if (nil? (:cost (repair_use_vehicle/get-vehicle-cost repair-num))) 0 (:cost (repair_use_vehicle/get-vehicle-cost repair-num)))}}
        workers {:workers {:workers (repair_use_worker/get-workers repair-num) :cost (if (nil? (:total (repair_process_mode/get-total-cost repair-num))) 0 (:total (repair_process_mode/get-total-cost repair-num)))}}
        costs {:other_cost {:other_cost (repair_other_cost/get-costs repair-num "n_sent") :total_cost (if (nil? (:total (repair_other_cost/get-total-cost repair-num "n_sent"))) 0 (:total (repair_other_cost/get-total-cost repair-num "n_sent")))}}
        parts {:parts {:parts all-parts :total_cost (common/decimal-format (get-parts-cost all-parts))}}
        total {:total_cost (get-total-cost repair-num)}]
    (if-not (nil? repair)
      (conj repair vehicle vehicles workers process-modes parts costs total)
      {})))

(defn get-n-sent
  [repair-num]
  (let [repair (first (repair_n_sent/find-by-repair-num repair-num))]
    repair))

(defn modify-customer
  [params]
  (transaction
    (let [rep (repair/get-repair (:repair_num params))
        status (:status rep)]
    (if (nil? rep)
      (msg/get-errors "not_exist")
      (if (= status "untreated")
        (msg/get-errors "not_modify_untreated")
        (if (or (= status "finish") (= status "forgo"))
          (msg/get-errors "not_modify_finish_forgo")
          (let [_ (repair_n_sent/update-repair-customer params)
                index-dir (luc/create-directory "/tmp/repair_index/repair_n_sent")
                analyzer (luc/create-analyzer)
                q1 (luc/create-query "fulltext" (:repair_num params) analyzer)
                _ (luc/del-index-with-query index-dir q1)
                index-value (first (repair_n_sent/find-by-repair-num (:repair_num params)))]
            (common/write-index index-value index-dir)
            {:success true})))))))

(defn modify-vehicle
  [params]
  (transaction
    (let [rep (repair/get-repair (:repair_num params))
        status (:status rep)]
    (if (nil? rep)
      (msg/get-errors "not_exist")
      (if (= status "untreated")
        (msg/get-errors "not_modify_untreated")
        (if (or (= status "finish") (= status "forgo"))
          (msg/get-errors "not_modify_finish_forgo")
          (let [_ (repair_n_sent/update-repair-vehicle params)]
            {:success true})))))))

(defn modify-failure
  [params]
  (transaction
    (let [rep (repair/get-repair (:repair_num params))
        status (:status rep)]
    (if (nil? rep)
      (msg/get-errors "not_exist")
      (if (= status "untreated")
        (msg/get-errors "not_modify_untreated")
        (if (or (= status "finish") (= status "forgo"))
          (msg/get-errors "not_modify_finish_forgo")
          (let [_ (repair_n_sent/update-repair-failure params)]
            {:success true})))))))

(def process {:overhaul "overhaul" :replace "replace" :adjust "adjust"})

(def check-process ["repair_num" "diagnosis" "analysis" "process_mode" "warranty" "person" "cost"])

(defn save-process
  [params]
  (transaction
    (let [keys (common/get-nil-kyes check-process params)
        key (first keys)
        rep (repair/get-repair (:repair_num params))
        status (:status rep)
        process_mode (:process_mode params)
        rep (repair/get-repair (:repair_num params))
        status (:status rep)]
    (if (nil? rep)
      (msg/get-errors "not_exist")
      (if-not (nil? key)
        (msg/get-errors key)
        (if-not (= status "n_sent")
          (msg/get-errors "repair_n_sent")
          (if (nil? (get process (keyword process_mode)))
            (msg/get-errors "process_mode_code")
            (if (= status "untreated")
              (msg/get-errors "not_modify_untreated")
              (if (or (= status "finish") (= status "forgo"))
                (msg/get-errors "not_modify_finish_forgo")
                (let [p (repair_process_mode/save params)]
                  p))))))))))

(defn modify-process
  [params]
  (transaction
    (let [keys (common/get-nil-kyes check-process params)
        key (first keys)
        rep (repair/get-repair (:repair_num params))
        status (:status rep)
        process_mode (:process_mode params)
        rep (repair/get-repair (:repair_num params))
        status (:status rep)]
    (if (nil? rep)
      (msg/get-errors "not_exist")
      (if-not (nil? key)
        (msg/get-errors key)
        (if-not (= status "n_sent")
          (msg/get-errors "repair_n_sent")
          (if (nil? (get process (keyword process_mode)))
            (msg/get-errors "process_mode_code")
            (if (= status "untreated")
              (msg/get-errors "not_modify_untreated")
              (if (or (= status "finish") (= status "forgo"))
                (msg/get-errors "not_modify_finish_forgo")
                (let [p (repair_process_mode/update params)]
                  p))))))))))

(defn delete-process
  [params]
  (transaction
    (repair_process_mode/delete params)))

(defn save-vehicle
  [params]
  (transaction
    (repair_use_vehicle/save params)))

(defn drop-vehicle
  [params]
  (transaction
    (repair_use_vehicle/delete params)))

(defn save-worker
  [params]
  (transaction
    (repair_use_worker/save params)))

(defn drop-worker
  [params]
  (transaction
    (repair_use_worker/delete params)))

(defn update-statistics-add-sent
  []
  (let [old-statistics (get-statistics)
        minus-statistics (merge-with - old-statistics {:n_sent 1})
        new-statistics (merge-with + minus-statistics {:sent 1})]
    (set-statistics new-statistics)))

(defn upload-n-sent
  [params]
  (transaction
    (let [repair-num (:repair_num params)
          rep (repair/get-repair repair-num)
          status (:status rep)
          manager (:manager rep)
          process (repair_process_mode/get-process-modes (:repair_num params))
          worker (first (repair_use_worker/get-workers (:repair_num params)))]
    (if (nil? rep)
      (msg/get-errors "not_exist")
      (if-not (= manager (:manager params))
        (msg/get-errors "not_forgo_manager")
        (if (nil? process)
          (msg/get-errors "n_sent_process")
          (if (nil? worker)
            (msg/get-errors "n_sent_worker")
            (if-not (= status "n_sent")
              (msg/get-errors "n_sent_upload_status")
              (let [repa (get-with-repair-num repair-num)]
                (if (= (:status repa) "sent")
                  (msg/get-errors "processed")
                  (let [delete-dir (luc/create-directory "/tmp/repair_index/repair_n_sent")
                        analyzer (luc/create-analyzer)
                        q1 (luc/create-query "fulltext" repair-num analyzer)
                        _ (luc/del-index-with-query delete-dir q1)
                        index-dir (luc/create-directory "/tmp/repair_index/repair_sent")]
                    (repair_sent/create "sent" manager repa)
                    (repair/update repair-num "sent" manager)
                    (repair_n_sent/update-status repair-num "sent")
                    (let [index-value (first (repair_sent/find-by-repair-num repair-num))]
                      (common/write-index index-value index-dir)
                      (update-statistics-add-sent)
                      {:success true}))))))))))))

(defn update-sus
  [repair-num status]
  (repair_n_sent/update-status repair-num status))

(defn get-vehicles
  [params]
  (let [page-index (if (<= (Integer/parseInt (:page_index params)) 0) 1 (Integer/parseInt (:page_index params)))
        page-size (if (<= (Integer/parseInt (:page_size params)) 0) 10 (Integer/parseInt (:page_size params)))
        t (if (or (nil? (:t params)) (= (:t params) "")) "*:*" (:t params))
        value {:keyword t :page page-index :size page-size}
        body (:body (common/request-post (str "http://" (common/getParam "vehicle_address" "192.168.2.231:7788") "/cars/api/search/car-info") value))
        results (:results (json/read-str body :key-fn keyword))]
    (if (nil? results)
      (msg/get-errors "client_vehicle_sys")
      {:page_index page-index :page_size page-size :vehicles results})))

(defn get-workers
  [params]
  (let [page-index (if (<= (Integer/parseInt (:page_index params)) 0) 1 (Integer/parseInt (:page_index params)))
        page-size (if (<= (Integer/parseInt (:page_size params)) 0) 10 (Integer/parseInt (:page_size params)))
        t (if (or (nil? (:t params)) (= (:t params) "")) "*:*" (:t params))
        value {:keyword t :page page-index :size page-size}
        body (:body (common/request-get (str "http://" (common/getParam "worker_address" "192.168.2.231:6000") "/api/user/all") (str "?pagenum=" page-index "&pagesize=" page-size "&keyword=" t)))
        results (:users (json/read-str body :key-fn keyword))]
    (if (nil? results)
      (msg/get-errors "client_worker_sys")
      {:page_index page-index :page_size page-size :workers results})))

(defn save-other-cost
  [params]
  (transaction
    (let [rep (repair/get-repair (:repair_num params))]
      (if (nil? rep)
        (msg/get-errors "not_exist")
        (repair_other_cost/save (conj params {:status "n_sent"}))))))

(defn update-other-cost
  [params]
  (transaction
    (let [rep (repair/get-repair (:repair_num params))]
      (if (nil? rep)
        (msg/get-errors "not_exist")
        (repair_other_cost/update (conj params {:status "n_sent"}))))))

(defn get-other-cost
  [params]
  {:costs (repair_other_cost/get-costs (:repair_num params) "n_sent")})

(defn delete-other-cost
  [params]
  (transaction
    (repair_other_cost/delete (conj params {:status "n_sent"}))))
