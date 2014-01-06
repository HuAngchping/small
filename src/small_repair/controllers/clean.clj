(ns small_repair.controllers.clean
  (:require [small_repair.models.clean :as clean])
  (:require [ring.util.response :as resp]
            [clojure.tools.logging :as log])
  (:use [small_repair.utils.common]
        [small_repair.utils.web]))

(defhandler clean
  [req]
  (let [is_clean (getParam "is_clean" "Y")]
    (if (= is_clean "Y")
      (clean/clean-data))
    (resp/response {:success true})))