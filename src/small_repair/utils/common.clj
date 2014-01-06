(ns small_repair.utils.common
  (:use [small_repair.lucene.core])
  (:require [clj-http.client :as client]))

(defn write-index
  [{:keys [repair_num manager repair_type customer_name customer_gender customer_tel customer_backup_tel province city county
           address owner_name owner_tel frame_num vehicle_type plate_num km purchase_date failure_desc service_username
           service_name service_upload_at status upload_at update_at create_at]} dir]
  (write-index-with-doc! dir (create-document-with-field
                               (create-field-of-index "fulltext" (str repair_num " " customer_name " " customer_tel " " frame_num))
                               (create-field-of-stored "repair_num" repair_num)
                               (create-field-of-stored "manager" (if (nil? manager) "" manager))
                               (create-field-of-stored "frame_num" frame_num)
                               (create-field-of-stored "customer_name" customer_name)
                               (create-field-of-stored "customer_tel" customer_tel)
                               (create-field-of-stored "status" status)
                               (create-field-of-stored "upload_at" upload_at)
                               (create-field-of-stored "create_at" create_at))))

(defn request-post
  [address value]
  (client/post address
    {:content-type :json
     :accept :json
     :form-params value}))

(defn request-put
  [address value]
  (client/put address
    {:content-type :json
     :accept :json
     :form-params value}))

(defn request-get
  [address value]
  (client/get (str address value)))

(defn request-delete
  [address value]
  (client/delete (str address value)))

(defn check-repair-required?
  [v]
  (or (nil? v) (= v "")))

(defn get-empty-str
  [v]
  (if (nil? v)
    ("")
    (v)))

(defn decimal-format
  [deci]
  (.doubleValue (.setScale (new java.math.BigDecimal deci) 2 4)))

(defn get-nil-kyes
  "check-keys: 需要验证的参数名称数组.
   params: 请求参数.
   return: 为空的参数名称数组."
  [check-keys params]
  (into [] (for [k check-keys :when (check-repair-required? (get params (keyword k)))] k)))

(defn getParam [param default]
  (if-let [value (System/getProperty param)]
    value
    default
    ))

(defn update-service-repair-status
  [repair-num status]
  (let [value {:repair_num repair-num :status status}]
    (request-post (str "http://" (getParam "service_address" "192.168.2.231:7777") "/customer/api/status/repair-process") value)))

(defn get-format-date
  [date]
  (let [f (java.text.SimpleDateFormat "yyyy-MM-dd hh:mm:ss")]
    (.format f date)))

(defn md5-sum
  "Generate a md5 checksum for the given string"
  [token]
  (let [hash-bytes
        (doto (java.security.MessageDigest/getInstance "MD5")
          (.reset)
          (.update (.getBytes token)))]
    (.toString
      (new java.math.BigInteger 1 (.digest hash-bytes))
      16))) ;Use base16 i.e. hex

(defn now
  "Generate current time"
  []
  (.getTime (java.util.Calendar/getInstance)))

(defn pretty-date
  "format date time -> MMM dd, yyyy"
  [date]
  (.format (java.text.SimpleDateFormat. "MMM dd, yyyy")
    (date)))

(defn mdy-date
  "Generate now -> pretty date"
  []
  (pretty-date (now)))

(defn rfc822-date
  [date]
  (let [f (java.text.SimpleDateFormat "EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z")]
    (.format f date)))

(defn get-random-id
  [length]
  (let [alpha-numeric "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"]
    (apply str (repeatedly length #(rand-nth alpha-numeric)))))

(defn currenttime
  []
  (System/currentTimeMillis))

(definline now-seconds
  []
  `(quot (System/currentTimeMillis) 1000))


(defn pid
  "Generate the current process's PID, as a String"
  []
  (or
    (System/getProperty "pid")
    (first
      (->
        (.getName (java.lang.management.ManagementFactory/getRuntimeMXBean))
        (.split "@")))))

(defn device-mac-bytes
  "deivce mac bytes array"
  []
  (if-let [ni (java.net.NetworkInterface/getByInetAddress (java.net.InetAddress/getLocalHost))]
    (.getHardwareAddress ni)))

(defn mac-bytes-to-string
  "mac device string"
  [mac-bytes]
  (let [v (apply vector (map #(Integer/toHexString (bit-and % 0xff)) mac-bytes))]
    (apply str (interpose ":" v))))

(defn string-bytes
  "string to byte array
  Return bytes array."
  [string]
  (.getBytes string))

(defn bytes-short
  "bytes to short integer, 2 btyes to short ,
  Return short integer."
  [bytes]
  (.getShort (java.nio.ByteBuffer/wrap bytes)))

(defn decimal-biginteger
  "convert java.math.Decimal -> java.match.BigInteger"
  [big-dec]
  (.toBigInteger big-dec)
  )
