(ns small_repair.dao.repair_parts
  (:use [korma.core]
        [korma.db])
  (:use [small_repair.db.config])
  (:require [clojure.tools.logging :as log]))

(defn create
  [params]
  (log/info "saving repair parts apply.")
  (exec-raw ["insert into repair_parts_apply (repair_num, factory, brand, name, code, picture_num, price, count) values (?,?,?,?,?,?,?,?)"
             [(:repair_num params) (:factory params) (:brand params) (:name params) (:code params) (:picture_num params)
              (:price params) (:borrow_count params)]]))

(defn save
  [repair-num code num]
  (log/info "updating repair parts apply.")
  (exec-raw ["select * from repair_parts_apply where repair_num = ? and code = ? for update" [repair-num code]] :results)
  (exec-raw ["update repair_parts_apply set count = ? where repair_num = ? and code = ?" [num repair-num code]]))

(defn save-borrower
  [repair-num id borrower]
  (log/info "updating repair parts apply.")
  (exec-raw ["select * from repair_parts_apply where id = ? for update" [id]] :results)
  (exec-raw ["update repair_parts_apply set borrower = ? where repair_num = ? and id = ?" [borrower repair-num id]]))


(defn find-all
  [repair-num]
  (log/info "get repair parts apply with repair_num:" repair-num)
  (exec-raw ["select * from repair_parts_apply where repair_num = ?" [repair-num]] :results))

(defn find-by-repair-num
  [repair-num code]
  (log/info "get repair parts apply with repair_num:" repair-num "and code:" code)
  (exec-raw ["select * from repair_parts_apply where repair_num = ? and code = ?" [repair-num code]] :results))

(defn delete-by-repair-num
  [repair-num]
  (log/info "deleting repair parts apply with repair_num:" repair-num)
  (exec-raw ["delete from repair_parts_apply where repair_num = ?" [repair-num]]))

(defn delete-by-repair-num-code
  [repair-num code]
  (log/info "deleting repair parts apply with repair_num:" repair-num "and code:" code)
  (exec-raw ["delete from repair_parts_apply where repair_num = ? and code = ?" [repair-num code]]))

(defn find-total-cost-by-repair-num
  [repair-num]
  (log/info "get repair parts apply cost total with repair_num:" repair-num)
  (exec-raw ["select sum(price * count) as total from repair_parts_apply where repair_num = ?" [repair-num]] :results))

(defn find-by-code
  [factory name picture-num repair-num]
  (log/info "get repair parts apply with factory:" factory "and name:" name "and picture_num:" picture-num "and repair_num:" repair-num)
  (exec-raw ["select code from repair_parts_apply where factory = ? and name = ? and picture_num = ? and repair_num = ?" [factory name picture-num repair-num]] :results))

(defn create-lend
  [repair-num count borrower params]
  (log/info "saving repair parts lend.")
  (exec-raw ["insert into repair_parts_lend (repair_num, borrower, factory, brand, name, code, picture_num, price, count) values
  (?,?,?,?,?,?,?,?,?)" [repair-num (if (nil? borrower) (:username params) borrower) (:factory params) (:brand params) (:name params) (:code params)
                        (:picture_num params) (:price params) (if (nil? count) (:borrow_count params) count)]]))

(defn create-back
  [repair-num count params]
  (log/info "saving repair parts back.")
  (exec-raw ["insert into repair_parts_back (repair_num, factory, brand, name, code, picture_num, price, count) values
  (?,?,?,?,?,?,?,?)" [repair-num (:factory params) (:brand params) (:name params) (:code params)
                      (:picture_num params) (:price params) (if (nil? count) (:borrow_count params) count)]]))

(defn create-use
  [repair-num params]
  (log/info "saving repair parts use.")
  (exec-raw ["insert into repair_parts_use (repair_num, factory, brand, name, code, picture_num, price, count) values
  (?,?,?,?,?,?,?,?)" [repair-num (:factory params) (:brand params) (:name params) (:code params)
                      (:picture_num params) (:price params) (:borrow_count params)]]))

(defn find-all-lend
  [repair-num]
  (log/info "get repair parts lend with repair_num:" repair-num)
  (exec-raw ["select repair_num, borrower, factory, brand, name, code, picture_num, price, count, status, create_at from repair_parts_lend where repair_num = ?" [repair-num]] :results))

(defn find-all-back
  [repair-num]
  (log/info "get repair parts back with repair_num:" repair-num)
  (exec-raw ["select repair_num, factory, brand, name, picture_num, price, count, status, create_at from repair_parts_back where repair_num = ?" [repair-num]] :results))

(defn find-all-use
  [repair-num]
  (log/info "get repair parts use with repair_num:" repair-num)
  (exec-raw ["select repair_num, factory, brand, name, picture_num, price, count, status, create_at from repair_parts_use where repair_num = ?" [repair-num]] :results))

(defn delete-lend-by-repair-num
  [repair-num]
  (log/info "deleting repair parts lend with repair_num:" repair-num)
  (exec-raw ["delete from repair_parts_lend where repair_num = ?" [repair-num]]))

(defn delete-back-by-repair-num
  [repair-num]
  (log/info "deleting repair parts back with repair_num:" repair-num)
  (exec-raw ["delete from repair_parts_back where repair_num = ?" [repair-num]]))

(defn delete-use-by-repair-num
  [repair-num]
  (log/info "deleting repair parts use with repair_num:" repair-num)
  (exec-raw ["delete from repair_parts_use where repair_num = ?" [repair-num]]))

