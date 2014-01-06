(ns small_repair.db.config
  (:use [korma.db])
  (:use [small_repair.utils.common])
  (:require [clojure.string :as string]))

;(defdb db-pg (postgres {:db "small_repair"
;                       :user "huangchunping"
;                       :password "huangchunping"
;                       :host "192.168.2.231"
;                       :port "5432"}))

(defdb db-pg (postgres {:db (getParam "db_name" "small_repair")
                        :user (getParam "db_user" "huangchunping")
                        :password (getParam "db_password" "huangchunping")
                        ;;OPTIONAL KEYS
                        :host (getParam "db_host" "192.168.2.231")
                        :port (getParam "db_port" "5432")
                        :delimiters "" ;; remove delimiters
                        :naming {:keys string/lower-case
                                 ;; set map keys to lower
                                 :fields string/upper-case}}))