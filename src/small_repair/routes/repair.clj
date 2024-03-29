(ns small_repair.routes.repair
  (:use [compojure.core])
  (:require [small_repair.controllers.repair_untreated :as repair_untreated]
            [small_repair.controllers.repair_n_sent :as repair_n_sent]
            [small_repair.controllers.repair_parts :as repair_parts]
            [small_repair.controllers.repair_sent :as repair_sent]
            [small_repair.controllers.repair_finish :as repair_finish]
            [small_repair.controllers.clean :as clean]))

(defroutes app-routes
  (POST "/service/upload" [] repair_untreated/service-upload)
  (GET "/service/search" [] repair_untreated/service-search)
  (GET "/vehicle" [] repair_untreated/view-vehicle)
  (GET "/untreated/search" [] repair_untreated/untreated-search)
  (GET "/untreated" [] repair_untreated/view)
  (POST "/untreated/process" [] repair_untreated/process)
  (GET "/n_sent/search" [] repair_n_sent/search)
  (PUT "/n_sent/c/customer" [] repair_n_sent/customer)
  (PUT "/n_sent/c/vehicle" [] repair_n_sent/vehicle)
  (PUT "/n_sent/c/failure" [] repair_n_sent/failure)
  (POST "/n_sent/process" [] repair_n_sent/add-process)
  (PUT "/n_sent/process" [] repair_n_sent/modify-process)
  (DELETE "/n_sent/process" [] repair_n_sent/drop-process)
  (POST "/n_sent/other/cost" [] repair_n_sent/add-other-cost)
  (PUT "/n_sent/other/cost" [] repair_n_sent/modify-other-cost)
  (GET "/n_sent/other/cost" [] repair_n_sent/view-other-cost)
  (DELETE "/n_sent/other/cost" [] repair_sent/drop-other-cost)
  (GET "/use/vehicle" [] repair_n_sent/vehicles)
  (POST "/n_sent/use/vehicle" [] repair_n_sent/add-use-vehicle)
  (DELETE "/n_sent/use/vehicle" [] repair_n_sent/delete-use-vehicle)
  (GET "/use/worker" [] repair_n_sent/workers)
  (POST "/n_sent/use/worker" [] repair_n_sent/add-use-worker)
  (DELETE "/n_sent/use/worker" [] repair_n_sent/delete-use-worker)
  (GET "/n_sent" [] repair_n_sent/view)
  (POST "/n_sent/upload" [] repair_n_sent/upload)
  (GET "/parts/search" [] repair_parts/search)
  (POST "/parts/apply" [] repair_parts/apply-ps)
  (POST "/parts/lend" [] repair_parts/lend)
  (POST "/parts/back" [] repair_parts/back)
  (PUT "/parts/revoke" [] repair_parts/revoke)
  (PUT "/parts" [] repair_parts/add-all)
  (DELETE "/parts/revoke" [] repair_parts/revoke-all)
  (GET "/parts" [] repair_parts/parts)
  (GET "/parts/factory" [] repair_parts/factory)
  (GET "/parts/brand" [] repair_parts/brand)
  (POST "/sent/other/cost" [] repair_sent/add-other-cost)
  (PUT "/sent/other/cost" [] repair_sent/modify-other-cost)
  (GET "/sent/other/cost" [] repair_sent/view-other-cost)
  (DELETE "/sent/other/cost" [] repair_sent/drop-other-cost)
  (GET "/sent/search" [] repair_sent/search)
  (GET "/sent" [] repair_sent/view)
  (PUT "/sent/vehicle" [] repair_sent/modify-vehicle)
  (PUT "/sent/c/customer" [] repair_sent/customer)
  (PUT "/sent/c/vehicle" [] repair_sent/vehicle)
  (PUT "/sent/c/failure" [] repair_sent/failure)
  (POST "/sent/failure" [] repair_sent/add-failure)
  (PUT "/sent/failure" [] repair_sent/modify-failure)
  (GET "/sent/failure" [] repair_sent/view-failure)
  (DELETE "/sent/failure" [] repair_sent/drop-failure)
  (POST "/sent/upload" [] repair_sent/upload)
  (GET "/finish/search" [] repair_finish/search)
  (GET "/finish" [] repair_finish/view)
  (POST "/forgo" [] repair_finish/forgo)
  (GET "/statistics" [] repair_finish/repair-statistics)
  (GET "/status" [] repair_finish/status)
  (DELETE "/clean" [] clean/clean))