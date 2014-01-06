(ns small_repair.models.repair_untreated
  (:require [small_repair.models.repair :as repair]
            [small_repair.models.repair_n_sent :as repair_n_sent]
            [small_repair.dao.repair_untreated :as repair_untreated]
            [small_repair.models.repair_vehicle :as repair_vehicle]
            [small_repair.utils.common :as common]
            [small_repair.utils.messages :as msg])
  (:require [small_repair.lucene.core :as luc])
  (:use [small_repair.init.statistics]
        [korma.db]))

(def check-upload-keys ["repair_num" "repair_type" "customer_name" "customer_gender" "customer_tel" "province" "city" "county" "address"
                        "owner_name" "owner_tel" "km" "purchase_date" "failure_desc" "service_username" "service_name" "service_upload_at"])

(defn update-statistics-add-untreated
  []
  (let [old-statistics (get-statistics)
        merge-statistics (merge-with + old-statistics {:untreated 1})
        new-statistics (merge-with + merge-statistics {:all 1})]
    (set-statistics new-statistics)))

(defn upload
  [params]
  (transaction
    (let [keys (common/get-nil-kyes check-upload-keys params)
          key (first keys)
          orepair (first (repair_untreated/find-by-repair-num (:repair_num params)))]
    (if-not (nil? orepair)
      (msg/get-errors "repair_num_repetition")
      (if-not (nil? key)
        (msg/get-errors key)
        (let [results (first (repair_untreated/create params))
              index-dir (luc/create-directory "/tmp/repair_index/repair_untreated")
              index-value (first (repair_untreated/find-by-repair-num (:repair_num results)))]
          (repair/save (:repair_num results) "untreated")
          (repair_vehicle/save params)
          (repair_vehicle/save params)
          (common/write-index index-value index-dir)
          (update-statistics-add-untreated)
          results))))))

(def check-update-keys ["repair_num" "repair_type" "customer_name" "customer_gender" "customer_tel" "province" "city" "county" "address"
                        "owner_name" "owner_tel" "km" "purchase_date" "failure_desc"])

(defn update-repair
  [params]
  (transaction
    (let [keys (common/get-nil-kyes check-update-keys params)
        key (first keys)]
    (if-not (nil? key)
      (msg/get-errors key)
      (repair_untreated/save params)))))

(defn update-repair-vehicle
  [params]
  (transaction
    (let [keys (common/get-nil-kyes check-update-keys params)
        key (first keys)]
    (if-not (nil? key)
      (msg/get-errors key))
    (repair_untreated/save params)
    (repair_vehicle/save params))))

(defn get-repairs
  [params]
  (let [t (:t params)
        rs (repair_untreated/find-by-coustomer-tel t)]
    (println rs)
    (into [] (for [r rs]
               (conj (repair_vehicle/get-vehicle-with-repair-num (:repair_num r)) r)))))

(defn get-repair-vehicle
  [params]
  (let [repair-num (:id params)]
    (repair_vehicle/get-vehicle-with-repair-num repair-num)))

(defn get-untreated-repairs
  [params]
  (let [page-index (if (<= (Integer/parseInt (:page_index params)) 0) 1 (Integer/parseInt (:page_index params)))
        page-size (if (<= (Integer/parseInt (:page_size params)) 0) 10 (Integer/parseInt (:page_size params)))
        order-by (:order_by params)
        order-type (if (= (:order_type params) "a") false true)
        t (if (or (nil? (:t params)) (= (:t params) "")) "*:*" (:t params))
        index-dir (luc/create-directory "/tmp/repair_index/repair_untreated")
        analyzer (luc/create-analyzer)
        reader (luc/create-index-reader index-dir)
        q1 (luc/create-query "fulltext" t analyzer)
        results (luc/search-paging q1 page-index page-size reader order-by order-type)
        docs (luc/get-docs reader (:docs results))
        rs (luc/convert-docs2-map docs)]
    {:page_index (Integer/parseInt (:page_index params)) :page_size (Integer/parseInt (:page_size params)) :results rs}))

(defn get-untreated-repair
  [params]
  (let [repair-num (:repair_num params)
        repair (first (repair_untreated/find-by-repair-num repair-num))
        vehicle (repair_vehicle/get-vehicle-with-repair-num repair-num)]
    (if-not (nil? repair)
      (conj repair vehicle)
      {})))

(defn update-statistics-add-n-sent
  []
  (let [old-statistics (get-statistics)
        minus-statistics (merge-with - old-statistics {:untreated 1})
        new-statistics (merge-with + minus-statistics {:n_sent 1})]
    (set-statistics new-statistics)))

(def o (Object.))

(defn process-untreated-repair
  [params]
  (locking o
    (transaction
      (let [repair-num (:repair_num params)
            manager (:manager params)
            rep (repair/get-repair repair-num)
            status (:status rep)]
        (if (nil? rep)
          (msg/get-errors "not_exist")
          (if-not (= status "untreated")
            (msg/get-errors "processed")
            (let [n-sent-repair (first (repair_untreated/find-by-repair-num repair-num))]
              (if-not (= (:status n-sent-repair) "untreated")
                (msg/get-errors "processed")
                (let [index-untreated (luc/create-directory "/tmp/repair_index/repair_untreated")
                      analyzer (luc/create-analyzer)
                      q1 (luc/create-query "fulltext" repair-num analyzer)
                      _ (luc/del-index-with-query index-untreated q1)
                      index-dir (luc/create-directory "/tmp/repair_index/repair_n_sent")]
                  (repair_n_sent/save n-sent-repair manager)
                  (repair/update repair-num "n_sent" manager)
                  (repair_untreated/update-status repair-num "n_sent")
                  (let [index-value (repair_n_sent/get-n-sent repair-num)]
                    (common/write-index index-value index-dir))
                  (update-statistics-add-n-sent)
                  (common/update-service-repair-status repair-num "process")
                  {:success true})))))))))
