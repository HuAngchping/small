(ns small_repair.models.repair_sent
  (:require [small_repair.dao.repair_sent :as repair_sent]
            [small_repair.dao.repair_scene_failure :as repair_scene_failure]
            [small_repair.dao.repair_finish :as repair_finish]
            [small_repair.models.repair :as repair]
            [small_repair.models.repair_vehicle :as repair_vehicle]
            [small_repair.models.repair_other_cost :as repair_other_cost]
            [small_repair.models.repair_use_vehicle :as repair_use_vehicle]
            [small_repair.models.repair_use_worker :as repair_use_worker]
            [small_repair.models.repair_parts :as repair_parts]
            [small_repair.models.repair_process_mode :as repair_process_mode]
            [small_repair.utils.messages :as msg]
            [small_repair.utils.common :as common])
  (:require [small_repair.lucene.core :as luc])
  (:require [clojure.data.json :as json]
            [clojure.tools.logging :as log])
  (:use [small_repair.init.statistics]
        [korma.db]))

(defn get-with-repair-num
  [repair-num]
  (first (repair_sent/find-by-repair-num repair-num)))

(defn get-sent-repairs
  [params]
  (let [page-index (if (<= (Integer/parseInt (:page_index params)) 0) 1 (Integer/parseInt (:page_index params)))
        page-size (if (<= (Integer/parseInt (:page_size params)) 0) 10 (Integer/parseInt (:page_size params)))
        order-by (:order_by params)
        order-type (if (= (:order_type params) "a") false true)
        t (if (or (nil? (:t params)) (= (:t params) "")) "*:*" (:t params))
        manager (:manager params)]
    (if (or (nil? manager) (= manager ""))
      (msg/get-errors "manager_name_nil")
      (let [index-dir (luc/create-directory "/tmp/repair_index/repair_sent")
            analyzer (luc/create-analyzer)
            reader (luc/create-index-reader index-dir)
            q1 (luc/create-query "fulltext" t analyzer)
            q2 (luc/add-filter-to-query q1 (luc/create-filter-of-exist "manager" manager))
            results (luc/search-paging q2 page-index page-size reader order-by order-type)
            docs (luc/get-docs reader (:docs results))
            rs (luc/convert-docs2-map docs)]
        {:page_index (Integer/parseInt (:page_index params)) :page_size (Integer/parseInt (:page_size params)) :results rs}))))

(defn create-vehicle
  [params]
  (repair_vehicle/drop-vehicle (:repair_num params))
  (repair_vehicle/save params))

(defn update-vehicle
  [params]
  (transaction
    (let [repair-num (:repair_num params)
          rep (repair/get-repair (:repair_num params))]
    (if (nil? rep)
      (msg/get-errors "not_exist")
      (create-vehicle params)))))

(defn get-replace-process
  [scene-failure-id repair-num]
  (first (repair_scene_failure/find-process-replace-by-scene-id scene-failure-id repair-num)))

(defn get-other-process
  [scene-failure-id repair-num]
  (first (repair_scene_failure/find-process-other-by-scene-id scene-failure-id repair-num)))

(defn get-indirect-process-failures
  [indirect-failure]
  (let [process (if (= (:process_mode indirect-failure) "replace")
                  (first (repair_scene_failure/find-indirect-process-replace-by-indirect-id (:id indirect-failure) (:repair_num indirect-failure)))
                  (first (repair_scene_failure/find-indirect-process-other-by-indirect-id (:id indirect-failure) (:repair_num indirect-failure))))]
    (conj indirect-failure {:process process})))

(defn get-indirect-faliures
  [scene-failure-id repair-num]
  (let [indirect-failures (repair_scene_failure/find-indirect-failure-by-scene-id {:id scene-failure-id :repair_num repair-num})]
    (let [new-indirect-failures (into [] (for [indirect-failure indirect-failures]
                                           (get-indirect-process-failures indirect-failure)))]
      new-indirect-failures)))

(defn get-failure
  [id repair-num]
  (let [failure (first (repair_scene_failure/find-scene-failure-by-id {:id id :repair_num repair-num}))
        process (if (= (:process_mode failure) "replace") (get-replace-process id repair-num) (get-other-process id repair-num))
        indirect-failures (if (true? (:indirect failure)) (get-indirect-faliures id repair-num) [])]
    (conj failure {:process process} {:indirect_failures indirect-failures})))

(defn get-failures
  [params]
  (let [scene-failures (repair_scene_failure/find-by-repair-num (:repair_num params))
        failures (into [] (for [scene-failure scene-failures]
                            (get-failure (:id scene-failure) (:repair_num params))))]
    {:failures failures}))

(defn check-indirect-parts
  [indirect indirect-failures]
  (if (true? indirect)
    (into [] (for [indirect-failure indirect-failures :when
                   (and (= (:process_mode indirect-failure) "replace")
                     (nil? (repair_parts/get-parts-by-code (:factory (:process indirect-failure)) (:name (:process indirect-failure)) (:num (:process indirect-failure)) (:repair_num indirect-failure))))] indirect-failure))))

(defn create-process
  [failure-id process-mode indirect process params]
  (if (= process-mode "replace")
    (repair_scene_failure/create-process-replace failure-id process)
    (repair_scene_failure/create-process-other failure-id process))
  (if (true? indirect)
    (let [indirect-failures (:indirect_failures params)]
      (doseq [indirect-failure indirect-failures]
        (let [indirect-failure-id (:id (first (repair_scene_failure/create-scene-indirect-failure failure-id indirect-failure)))
              indirect-process-mode (:process_mode indirect-failure)
              indirect-process (:process indirect-failure)]
          (if (= indirect-process-mode "replace")
            (repair_scene_failure/create-indirect-process-replace indirect-failure-id indirect-process)
            (repair_scene_failure/create-indirect-process-other indirect-failure-id indirect-process))))
      (get-failure failure-id (:repair_num params)))
    (get-failure failure-id (:repair_num params))))

(defn save-failure
  [params]
  (transaction
    (let [failure-id (:id (first (repair_scene_failure/create-scene-failure params)))
          process-mode (:process_mode params)
          indirect (:indirect params)
          process (:process params)]
      (if (= process-mode "replace")
        (let [a (repair_parts/get-parts-by-code (:factory process) (:name process) (:num process) (:repair_num process))]
          (if (nil? a)
            (msg/get-errors "scene_failure")
            (let [errs (check-indirect-parts indirect (:indirect_failures params))]
              (if-not (nil? (first errs))
                (msg/get-errors "scene_indirect_failure")
                (create-process failure-id process-mode indirect process params)))))
        (let [errs (check-indirect-parts indirect (:indirect_failures params))]
          (if-not (nil? (first errs))
            (msg/get-errors "scene_indirect_failure")
            (create-process failure-id process-mode indirect process params)))))))

(defn update-process-and-delete-other
  [scene-failure-id process]
  (repair_scene_failure/delete-process-other (:id process) (:repair_num process))
  (repair_scene_failure/create-process-replace scene-failure-id process))

(defn process-replace
  [scene-failure-id process process-mode old-process-mode]
  (if (= old-process-mode "replace")
    (repair_scene_failure/update-process-replace scene-failure-id process)
    (update-process-and-delete-other scene-failure-id process)))

(defn update-process-and-delete-replace
  [scene-failure-id process]
  (repair_scene_failure/delete-process-replace (:id process) (:repair_num process))
  (repair_scene_failure/create-process-other scene-failure-id process))

(defn process-other
  [scene-failure-id process process-mode old-process-mode]
  (if (or (= old-process-mode "overhaul") (= old-process-mode "adjust"))
    (repair_scene_failure/update-process-other scene-failure-id process)
    (update-process-and-delete-replace scene-failure-id process)))

(defn update-indirect-process-delete-other
  [indirect-failure-id process]
  (repair_scene_failure/delete-indirect-process-other (:id process) (:repair_num process))
  (repair_scene_failure/create-indirect-process-replace indirect-failure-id process))

(defn indirect-process-replace
  [indirect-failure-id process indirect-process-mode old-indirect-process-mode]
  (if (= old-indirect-process-mode "replace")
    (repair_scene_failure/update-indirect-process-replace indirect-failure-id process)
    (update-indirect-process-delete-other indirect-failure-id process)))

(defn update-indirect-process-delete-replace
  [indirect-failure-id process]
  (repair_scene_failure/delete-indirect-process-replace (:id process) (:repair_num process))
  (repair_scene_failure/create-indirect-process-other indirect-failure-id process))

(defn indirect-process-other
  [indirect-failure-id process indirect-process-mode old-indirect-process-mode]
  (if (or (= old-indirect-process-mode "overhaul") (= old-indirect-process-mode "adjust"))
    (repair_scene_failure/update-indirect-process-other indirect-failure-id process)
    (update-indirect-process-delete-replace indirect-failure-id process)))

(defn delete-scene-process
  [id repair-num]
  (repair_scene_failure/delete-process-other-by-scene-id id repair-num)
  (repair_scene_failure/delete-process-replace-by-scene-id id repair-num)
  (repair_scene_failure/delete-scene-failure-by-id id repair-num))

(defn delete-indirect-process
  [indirect-failure-id repair-num]
  (repair_scene_failure/delete-indirect-process-other-by-scene-id indirect-failure-id repair-num)
  (repair_scene_failure/delete-indirect-process-replace-by-scene-id indirect-failure-id repair-num)
  (repair_scene_failure/delete-indirect-failure-by-scene-id indirect-failure-id repair-num))

(defn update-f
  [old-process-mode old-indirect failure-id process-mode indirect process params]
  (if (= process-mode "replace")
    (process-replace failure-id process process-mode old-process-mode)
    (process-other failure-id process process-mode old-process-mode))
  (if (true? old-indirect)
    (if (true? indirect)
      (let [indirect-failures (:indirect_failures params)]
        (doseq [indirect-failure indirect-failures]
          (let [indirect-scene-failure (first (repair_scene_failure/find-scene-indirect-failure-by-id (:id indirect-failure)))
                old-indirect-process-mode (if (nil? indirect-scene-failure) (:process_mode indirect-failure) (:process_mode indirect-scene-failure))
                indirect-failure-id (if (nil? indirect-scene-failure)
                                      (:id (first (repair_scene_failure/create-scene-indirect-failure failure-id indirect-failure)))
                                      (:id (first (repair_scene_failure/update-scene-indirect-failure indirect-failure))))
                indirect-process-mode (:process_mode indirect-failure)
                indirect-process (:process indirect-failure)]
            (if (= indirect-process-mode "replace")
              (indirect-process-replace indirect-failure-id indirect-process indirect-process-mode old-indirect-process-mode)
              (indirect-process-other indirect-failure-id indirect-process indirect-process-mode old-indirect-process-mode)))))
      (let [indirect-failures (:indirect_failures params)]
        (doseq [indirect-failure indirect-failures]
          (delete-indirect-process (:id indirect-failure) (:repair_num params)))))
    (if (true? indirect)
      (let [indirect-failures (:indirect_failures params)]
        (doseq [indirect-failure indirect-failures]
          (let [indirect-failure-id (:id (first (repair_scene_failure/create-scene-indirect-failure failure-id indirect-failure)))
                indirect-process-mode (:process_mode indirect-failure)
                indirect-process (:process indirect-failure)]
            (if (= indirect-process-mode "replace")
              (repair_scene_failure/create-indirect-process-replace indirect-failure-id indirect-process)
              (repair_scene_failure/create-indirect-process-other indirect-failure-id indirect-process)))))))
  (get-failure failure-id (:repair_num params)))

(defn update-failure
  [params]
  (transaction
    (let [scene-failure (first (repair_scene_failure/find-scene-failure-by-id params))
          old-process-mode (:process_mode scene-failure)
          old-indirect (:indirect scene-failure)
          failure-id (:id (first (repair_scene_failure/update-scene-failure params)))
          process-mode (:process_mode params)
          indirect (:indirect params)
          process (:process params)]
      (if (= process-mode "replace")
        (let [a (repair_parts/get-parts-by-code (:factory process) (:name process) (:num process) (:repair_num process))]
          (if (nil? a)
            (msg/get-errors "scene_failure")
            (let [errs (check-indirect-parts indirect (:indirect_failures params))]
              (if-not (nil? (first errs))
                (msg/get-errors "scene_indirect_failure")
                (update-f old-process-mode old-indirect failure-id process-mode indirect process params)))))
        (let [errs (check-indirect-parts indirect (:indirect_failures params))]
          (if-not (nil? (first errs))
            (msg/get-errors "scene_indirect_failure")
            (update-f old-process-mode old-indirect failure-id process-mode indirect process params)))))))

(defn delete-failure
  [params]
  (transaction
    (let [scene-failure (first (repair_scene_failure/find-scene-failure-by-id {:id (Integer/parseInt (:id params)) :repair_num (:repair_num params)}))
          indirect-failures (repair_scene_failure/find-indirect-failure-by-scene-id {:id (:id scene-failure) :repair_num (:repair_num scene-failure)})]
    (doseq [indirect-failure indirect-failures]
      (delete-indirect-process (:id indirect-failure) (:repair_num scene-failure)))
    (delete-scene-process (:id scene-failure) (:repair_num scene-failure)))
  {:success true}))

(defn save-other-cost
  [params]
  (transaction
    (let [rep (repair/get-repair (:repair_num params))]
    (if (nil? rep)
      (msg/get-errors "not_exist")
      (repair_other_cost/save (conj params {:status "sent"}))))))

(defn update-other-cost
  [params]
  (transaction
    (let [rep (repair/get-repair (:repair_num params))]
    (if (nil? rep)
      (msg/get-errors "not_exist")
      (repair_other_cost/update (conj params {:status "sent"}))))))

(defn get-other-cost
  [params]
  {:costs (repair_other_cost/get-costs (:repair_num params) "sent")})

(defn delete-other-cost
  [params]
  (transaction
    (repair_other_cost/delete (conj params {:status "sent"}))))

(defn update-statistics-add-finish
  []
  (let [old-statistics (get-statistics)
        minus-statistics (merge-with - old-statistics {:sent 1})
        new-statistics (merge-with + minus-statistics {:finish 1})]
    (set-statistics new-statistics)))

(defn save-parts-used
  [repair-num results]
  (repair_parts/save-parts-use {:repair_num repair-num :parts results})
  {:success true})

(defn used-parts
  [repair-num use-parts]
  (let [params {:repair_num repair-num :code_amount use-parts}
        body (json/read-str (:body (common/request-put (str "http://" (common/getParam "parts_address" "192.168.2.231:6001") "/pieces/stock/use") params)) :key-fn keyword)
        results (:borrows body)]
    (if (nil? results)
      body
      (save-parts-used repair-num results))))

(defn get-used-parts
  [repair-num]
  (let [parts (into (repair_scene_failure/find-replace-by-repair-num repair-num) (repair_scene_failure/find-indirect-replace-by-repair-num repair-num))
        use-parts (into [] (for [part parts
                                 :let [p (repair_parts/get-parts-by-code (:factory part) (:name part) (:num part) repair-num)]
                                 :when (not (nil? p))] {:code (:code p) :amount (:amount part)}))]
    use-parts))

(defn upload-repair-sent
  [params]
  (transaction
    (let [repair-num (:repair_num params)
          rep (repair/get-repair repair-num)
          status (:status rep)
          manager (:manager rep)
          failures (first (repair_scene_failure/find-by-repair-num (:repair_num params)))
          ;;借出配件临时处理方式
          ;;配件管理系统完成后该方法删除
          _ (common/request-put (str "http://" (common/getParam "parts_address" "192.168.2.231:6001") "/pieces/stock/borrow") {:repair_num repair-num})        ]
      (if-not (= manager (:manager params))
        (msg/get-errors "not_forgo_manager")
        (if (nil? failures)
          (msg/get-errors "sent_upload_failure")
          (let [repa (get-with-repair-num repair-num)]
            (if (= (:status repa) "finish")
              (msg/get-errors "processed")
              (if-not (= status "sent")
                (msg/get-errors "sent_upload_status")
                (let [delete-dir (luc/create-directory "/tmp/repair_index/repair_sent")
                      analyzer (luc/create-analyzer)
                      q1 (luc/create-query "fulltext" repair-num analyzer)
                      _ (luc/del-index-with-query delete-dir q1)
                      index-dir (luc/create-directory "/tmp/repair_index/repair_finish")]
                  (repair_finish/create "finish" manager repa)
                  (repair/update repair-num "finish" manager)
                  (repair_sent/update-status repair-num "finish")
                  (let [index-value (first (repair_finish/find-by-repair-num repair-num))]
                    (common/write-index index-value index-dir))
                  (update-statistics-add-finish)
                  (common/update-service-repair-status repair-num "close")))))))))
  (let [rs (get-used-parts (:repair_num params))]
    (if (nil? (first rs))
      {:success true}
      (used-parts (:repair_num params) rs))))

(defn update-sus
  [repair-num status]
  (repair_sent/update-status repair-num status))

(def nil-repair
  {:manager "" :repair_num "" :repair_type "" :customer_name "" :customer_gender "" :customer_tel "" :customer_backup_tel ""
   :province "" :city "" :county "" :address "" :owner_name "" :owner_tel "" :frame_num "" :vehicle_type "" :plate_num ""
   :km 0 :purchase_date "" :failure_desc "" :service_username "" :service_name "" :service_upload_at "" :status ""
   :upload_at "" :update_at "" :create_at ""})

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

(defn get-sent-repair
  [params]
  (let [repair-num (:repair_num params)
        repair (first (repair_sent/find-by-repair-num repair-num))
        failures (get-failures params)
        parts (:borrows (json/read-str (:body (common/request-get (str "http://" (common/getParam "parts_address" "192.168.2.231:6001") "/pieces/borrow/all") (str "?repair_num=" repair-num))) :key-fn keyword))
        process-modes {:process_modes {:process (repair_process_mode/get-process-modes repair-num) :total_cost (if (nil? (:total (repair_process_mode/get-total-cost repair-num))) 0 (:total (repair_process_mode/get-total-cost repair-num)))}}
        ovehicle {:old_vehicle (repair_vehicle/get-min-vehicle-with-repair-num repair-num)}
        nvehicle {:new_vehicle (repair_vehicle/get-vehicle-with-repair-num repair-num)}
        vehicles {:vehicles {:vehicles (repair_use_vehicle/get-vehicles repair-num) :cost (if (nil? (:cost (repair_use_vehicle/get-vehicle-cost repair-num))) 0 (:cost (repair_use_vehicle/get-vehicle-cost repair-num)))}}
        workers {:workers {:workers (repair_use_worker/get-workers repair-num) :cost (if (nil? (:total (repair_process_mode/get-total-cost repair-num))) 0 (:total (repair_process_mode/get-total-cost repair-num)))}}
        n-costs {:n_sent_other_cost {:other_cost (repair_other_cost/get-costs repair-num "n_sent") :total_cost (if (nil? (:total (repair_other_cost/get-total-cost repair-num "n_sent"))) 0 (:total (repair_other_cost/get-total-cost repair-num "n_sent")))}}
        nparts {:sent_parts {:parts parts :total_cost (common/decimal-format (get-parts-cost parts))}}
        ntotal {:n_sent_total_cost (get-n-sent-total-cost repair-num)}
        s-costs {:sent_other_cost {:other_cost (repair_other_cost/get-costs repair-num "sent") :total_cost (if (nil? (:total (repair_other_cost/get-total-cost repair-num "sent"))) 0 (:total (repair_other_cost/get-total-cost repair-num "sent")))}}
        stotal (get-sent-total-cost repair-num)]
    (if-not (nil? repair)
      (conj repair failures ovehicle nvehicle vehicles workers process-modes nparts n-costs s-costs ntotal stotal)
      {})))

(defn modify-customer
  [params]
  (transaction
    (let [rep (repair/get-repair (:repair_num params))
          status (:status rep)]
      (if (nil? rep)
        (msg/get-errors "not_exist")
        (if-not (= status "sent")
          (msg/get-errors "not_modify_sent")
          (if (or (= status "finish") (= status "forgo"))
            (msg/get-errors "not_modify_finish_forgo")
            (let [_ (repair_sent/update-repair-customer params)
                  index-dir (luc/create-directory "/tmp/repair_index/repair_sent")
                  analyzer (luc/create-analyzer)
                  q1 (luc/create-query "fulltext" (:repair_num params) analyzer)
                  _ (luc/del-index-with-query index-dir q1)
                  index-value (first (repair_sent/find-by-repair-num (:repair_num params)))]
              (common/write-index index-value index-dir)
              {:success true})))))))

(defn modify-vehicle
  [params]
  (transaction
    (let [rep (repair/get-repair (:repair_num params))
          status (:status rep)]
      (if (nil? rep)
        (msg/get-errors "not_exist")
        (if-not (= status "sent")
          (msg/get-errors "not_modify_sent")
          (if (or (= status "finish") (= status "forgo"))
            (msg/get-errors "not_modify_finish_forgo")
            (let [_ (repair_sent/update-repair-vehicle params)]
              {:success true})))))))

(defn modify-failure
  [params]
  (transaction
    (let [rep (repair/get-repair (:repair_num params))
          status (:status rep)]
      (if (nil? rep)
        (msg/get-errors "not_exist")
        (if-not (= status "sent")
          (msg/get-errors "not_modify_sent")
          (if (or (= status "finish") (= status "forgo"))
            (msg/get-errors "not_modify_finish_forgo")
            (let [_ (repair_sent/update-repair-failure params)]
              {:success true})))))))


