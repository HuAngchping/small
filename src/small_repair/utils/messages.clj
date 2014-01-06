(ns small_repair.utils.messages
  (:use [korma.db]))

(def errors {:repair_num {:success false :errno 201001 :message "维修接待单号不能为空"}
             :repair_type {:success false :errno 201002 :message "维修类型不能为空"}
             :customer_name {:success false :errno 201003 :message "客户姓名不能为空"}
             :customer_tel {:success false :errno 201004 :message "客户电话不能为空"}
             :customer_gender {:success false :errno 201005 :message "客户性别不能为空"}
             :province {:success false :errno 201006 :message "省不能为空"}
             :city {:success false :errno 201007 :message "市不能为空"}
             :county {:success false :errno 201008 :message "区县不能为空"}
             :address {:success false :errno 201009 :message "故障地址不能为空"}
             :owner_name {:success false :errno 201010 :message "车主姓名不能为空"}
             :owner_tel {:success false :errno 201011 :message "车主电话不能为空"}
             :frame_num {:success false :errno 201012 :message "车辆大架号不能为空"}
             :vehicle_type {:success false :errno 201013 :message "车型不能为空"}
             :km {:success false :errno 201014 :message "行驶里程不能为空"}
             :purchase_date {:success false :errno 201015 :message "购车日期不能为空"}
             :failure_desc {:success false :errno 201016 :message "故障描述不能为空"}
             :service_username {:success false :errno 201017 :message "客服用户名不能为空"}
             :service_name {:success false :errno 201018 :message "客服姓名不能为空"}
             :service_upload_at {:success false :errno 201019 :message "客服上传时间不能为空"}
             :not_exist {:success false :errno 201020 :message "该维修单不存在"}
             :processed {:success false :errno 201021 :message "该维修单已被处理"}
             :not_modify_untreated {:success false :errno 201022 :message "该维修单状态为未处理，不能修改相应信息"}
             :not_modify_finish_forgo {:success false :errno 201023 :message "该维修单状态为已完成或弃单，不能修改相应信息"}
             :process_mode_code {:success false :errno 201024 :message "该处理措施编码不正确"}
             :diagnosis {:success false :errno 201025 :message "故障诊断不能为空"}
             :analysis {:success false :errno 201026 :message "故障分析不能为空"}
             :process_mode {:success false :errno 201027 :message "处理措施不能为空"}
             :warranty {:success false :errno 201028 :message "是否三包不能为空"}
             :person {:success false :errno 201029 :message "处理人不能为空"}
             :cost {:success false :errno 201030 :message "工时费不能为空"}
             :repair_n_sent {:success false :errno 201031 :message "该维修单状态已改变，不能添加处理办法"}
             :add_workers {:success false :errno 201032 :message "不能添加用工信息，该维修单不是未派出状态"}
             :not_forgo_manager {:success false :errno 201033 :message "该维修单不能放弃，与处理人不一致"}
             :not_forgo_status {:success false :errno 201034 :message "该维修单不能放弃，当前状态为完成或弃单"}
             :sent_upload_failure {:success false :errno 201035 :message "该维修单不能上传，现场故障至少一条"}
             :sent_upload_status {:success false :errno 201036 :message "该维修单不能上传，状态不为已派出"}
             :n_sent_process {:success false :errno 201037 :message "该维修单不能上传，处理办法至少一条"}
             :n_sent_worker {:success false :errno 201038 :message "该维修单不能上传，用工信息至少一条"}
             :n_sent_upload_status {:success false :errno 201039 :message "该维修单不能上传，状态不为未派出"}
             :repair_num_repetition {:success false :errno 201040 :message "该维修单编号已存在"}
             :process_mode_code_nil {:success false :errno 201041 :message "处理措施编码不能为空"}
             :client_vehicle_sys {:success false :errno 201042 :message "连接车辆管理系统出现异常"}
             :client_worker_sys {:success false :errno 201043 :message "连接员工管理系统出现异常"}
             :client_parts_sys {:success false :errno 201044 :message "连接配件管理系统出现异常"}
             :client_parts_insufficient {:success false :errno 201045 :message "该配件库存不足"}
             :client_parts_nil {:success false :errno 201046 :message "该配件已被撤销"}
             :manager_name_nil {:success false :errno 201047 :message "维修经理用户名不能为空"}
             :not_modify_sent {:success false :errno 201048 :message "该维修单状态不是已派出，不能修改相应信息"}
             :scene_failure {:success false :errno 201049 :message "所添加更换新件信息与借件信息不一致"}
             :scene_indirect_failure {:success false :errno 201050 :message "所添加间接故障更换新件信息与借件信息不一致"}
             :scene_failure_exist {:success false :errno 201051 :message "该维修单已添加现场故障，不能放弃"}})

(defn get-errors
  [k]
  (rollback)
  (get errors (keyword k)))

(defn get-error
  [k]
  (get errors (keyword k)))