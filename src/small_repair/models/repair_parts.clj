(ns small_repair.models.repair_parts
  (:require [small_repair.dao.repair_parts :as repair_parts]
            [small_repair.models.repair :as repair]
            [small_repair.utils.common :as common]
            [small_repair.utils.messages :as msg])
  (:require [clojure.data.json :as json])
  (:use [korma.db]))

(defn parts-error
  [err]
  (rollback)
  err)

(defn search-parts
  [params]
  (let [factory (if (nil? (:factory params)) "" (:factory params))
        brand (if (nil? (:brand params)) "" (:brand params))
        page-index (if (<= (Integer/parseInt (:page_index params)) 0) 1 (Integer/parseInt (:page_index params)))
        page-size (if (<= (Integer/parseInt (:page_size params)) 0) 10 (Integer/parseInt (:page_size params)))
        order-by (:order_by params)
        order-type (if (= (:order_type params) "a") false true)
        t (if (or (nil? (:t params)) (= (:t params) "")) "*:*" (:t params))]
    (let [body (json/read-str (:body (common/request-get (str "http://" (common/getParam "parts_address" "192.168.2.231:6001") "/pieces/stock/all") (str "?factory=" factory "&brand=" brand "&page_num=" page-index
                                                                       "&page_size=" page-size "&sort=" order-type "&sort_field=" order-by
                                                                       "&keyword=" t))) :key-fn keyword)
          results (:stocks body)]
      (if (nil? results)
        body
        {:page_index page-index :page_size page-size :results results}))))

(defn create-parts
  [repair-num result]
  (repair_parts/delete-by-repair-num repair-num)
  (doseq [rs result]
    (repair_parts/create rs))
  {:borrows (repair_parts/find-all repair-num)})

(defn save-parts
  [result params code]
  (doseq [rs result]
    (let [num (:borrow_count rs)]
      (repair_parts/save (:repair_num params) code num)))
  {:borrows (repair_parts/find-all (:repair_num params))})

(defn apply-parts
  [params]
  (transaction
    (let [require-id (common/get-random-id 15)
          repair-num (:repair_num params)
          rep (repair/get-repair repair-num)
          code (:code params)
          borrow-count (:count params)
          value {:requireID require-id :repair_num repair-num :code code :borrow_count borrow-count}]
    (if (nil? rep)
      (msg/get-errors "not_exist")
      (let [body (json/read-str (:body (common/request-post (str "http://" (common/getParam "parts_address" "192.168.2.231:6001") "/pieces/borrow") value)) :key-fn keyword)
            result (:borrows body)]
        (if (nil? result)
          (parts-error body)
          (create-parts repair-num result)))))))

(defn delete-all-parts
  [repair-num]
  (repair_parts/delete-by-repair-num repair-num)
  {:borrows (repair_parts/find-all repair-num)})

(defn save-revoke-parts
  [result params code]
  (doseq [rs result]
    (let [num (:borrow_count rs)]
        (repair_parts/save (:repair_num params) code num)
        (repair_parts/delete-by-repair-num-code (:repair_num params) code)))
  {:borrows (repair_parts/find-all (:repair_num params))})

(defn revoke-parts
  [params]
  (transaction
    (let [require-id (common/get-random-id 15)
          repair-num (:repair_num params)
          rep (repair/get-repair repair-num)
          code (:code params)
          borrow-count (:count params)
          value {:requireID require-id :repair_num repair-num :code code :borrow_count borrow-count}]
    (if (nil? rep)
      (msg/get-errors "not_exist")
      (let [parts (first (repair_parts/find-by-repair-num repair-num code))]
        (if (nil? parts)
          (msg/get-errors "client_parts_nil")
          (let [body (json/read-str (:body (common/request-put (str "http://" (common/getParam "parts_address" "192.168.2.231:6001") "/pieces/borrow") value)) :key-fn keyword)
                result (:borrows body)]
            (if (nil? result)
              (parts-error body)
              (if (nil? (first result))
                (delete-all-parts repair-num)
                (create-parts repair-num result))))))))))

(defn revoke-all-save
  [repair-num]
  (let [body (json/read-str (:body (common/request-delete (str "http://" (common/getParam "parts_address" "192.168.2.231:6001") "/pieces/borrow") (str "?repair_num=" repair-num))) :key-fn keyword)
        results (:borrows body)]
    (if (nil? results)
      (parts-error body)
      (doseq [borrow results]
        (if (= (:status borrow) "2")
          (repair_parts/create-lend repair-num nil nil borrow)
          (if (= (:status borrow) "3")
            (repair_parts/create-back repair-num nil borrow)))))))

(defn delete-all-parts
  [repair-num]
  (repair_parts/delete-by-repair-num repair-num)
  {:success true})

(defn revoke-all
  [repair-num]
  (let [body (json/read-str (:body (common/request-delete (str "http://" (common/getParam "parts_address" "192.168.2.231:6001") "/pieces/borrow") (str "?repair_num=" repair-num))) :key-fn keyword)
        results (:borrows body)]
    (if (nil? results)
      (parts-error body)
      (delete-all-parts repair-num))))

(defn revoke-all-parts
  [params]
  (transaction
    (let [repair-num (:repair_num params)
          rep (repair/get-repair repair-num)]
    (if (nil? rep)
      (msg/get-errors "not_exist")
      (revoke-all repair-num)))))

(defn add-all
  [repair-num parts]
  (doseq [part parts]
    (repair_parts/save-borrower repair-num (:id part) (:borrower part)))
  (let [require-id (common/get-random-id 15)
        ps (into [] (for [part parts] {:code (:code part) :username (:borrower part)}))
        value {:requireID require-id :repair_num repair-num :borrows ps}
        body (json/read-str (:body (common/request-put (str "http://" (common/getParam "parts_address" "192.168.2.231:6001") "/pieces/borrow/user") value)) :key-fn keyword)
        result (:borrows body)]
    (if (nil? result)
      body
      {:borrows (repair_parts/find-all repair-num)})))

(defn add-all-parts
  [params]
  (transaction
    (let [repair-num (:repair_num params)
          parts (:parts params)
          rep (repair/get-repair repair-num)]
    (if (nil? rep)
      (msg/get-errors "not_exist")
      (add-all repair-num parts)))))

(defn get-parts
  [params]
  (let [repair-num (:repair_num params)
        parts (repair_parts/find-all repair-num)]
    parts))

(defn get-total-cost
  [repair-num]
  (first (repair_parts/find-total-cost-by-repair-num repair-num)))

(defn get-parts-by-code
  [factory name picture-num repair-num]
  (first (repair_parts/find-by-code factory name picture-num repair-num)))

(defn parts-lend
  [params]
  (transaction
    (let [repair-num (:repair_num params)
          lends (:lends params)
          _ (repair_parts/delete-lend-by-repair-num repair-num)]
      (doseq [lend lends]
        (let [parts (repair_parts/find-by-repair-num repair-num (:code lend))
              borrower (:borrower lend)
              count (:count lend)]
          (repair_parts/create-lend repair-num count borrower parts)))
      {:parts (repair_parts/find-all-lend repair-num)})))

(defn parts-back
  [params]
  (transaction
    (let [repair-num (:repair_num params)
          backs (:backs params)
          _ (repair_parts/delete-back-by-repair-num repair-num)]
      (doseq [back backs]
        (let [parts (repair_parts/find-by-repair-num repair-num (:code back))
              count (:count back)]
        (repair_parts/create-back repair-num count parts)))
      {:parts (repair_parts/find-all-back repair-num)})))

(defn save-parts-use
  [params]
  (transaction
    (let [repair-num (:repair_num params)
          parts (:parts params)
          _ (repair_parts/delete-use-by-repair-num repair-num)]
      (doseq [part parts]
        (repair_parts/create-use repair-num part)))))

(defn get-parts-lend
  [params]
  (let [repair-num (:repair_num params)
        parts (repair_parts/find-all-lend repair-num)]
    parts))

(defn get-parts-back
  [params]
  (let [repair-num (:repair_num params)
        parts (repair_parts/find-all-back repair-num)]
    parts))

(defn get-parts-use
  [params]
  (let [repair-num (:repair_num params)
        parts (repair_parts/find-all-use repair-num)]
    parts))

(defn get-repair-code
  [factory name picture-num repair-num]
  (first (repair_parts/find-by-code factory name picture-num repair-num)))

(defn parts-factory
  []
  (let [body (json/read-str (:body (common/request-get (str "http://" (common/getParam "parts_address" "192.168.2.231:6001") "/pieces/stock/factory/all") "")) :key-fn keyword)
        results (:factory body)]
    (if (nil? results)
      body
      {:factory results})))

(defn parts-brand
  [params]
  (let [name (:name params)
        body (json/read-str (:body (common/request-get (str "http://" (common/getParam "parts_address" "192.168.2.231:6001") "/pieces/stock/brand") (str "?factory=" name))) :key-fn keyword)
        brand (:brand body)]
    (if (nil? brand)
      body
      {:factory name :brand brand})))
