(ns small_repair.init.statistics
  (:require [small_repair.dao.repair_finish :as repair_finish]
            [small_repair.dao.repair_n_sent :as repair_n_sent]
            [small_repair.dao.repair_sent :as repair_sent]
            [small_repair.dao.repair_untreated :as repair_untreated]
            [small_repair.dao.repair :as repair]))

(defn init
  []
  (let [all (first (repair/find-count-all-repair))
        finishs (first (repair_finish/find-finish-repairs-count))
        untreateds (first (repair_untreated/find-untreated-repairs-count "untreated"))
        n_sents (first (repair_n_sent/find-n-sent-repairs-count "n_sent"))
        sents (first (repair_sent/find-sent-repairs-count "sent"))]
    (atom {:all (:num all) :untreated (:num untreateds) :n_sent (:num n_sents) :sent (:num sents) :finish (:num finishs)})))

(def cache (init))

(defn get-statistics
  []
  @cache)

(defn set-statistics
  [statistics]
  (reset! cache statistics))