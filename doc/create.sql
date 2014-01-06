ALTER TABLE "repair"DROP CONSTRAINT "";
ALTER TABLE "repair_vehicle"DROP CONSTRAINT "";
ALTER TABLE "repair_untreated"DROP CONSTRAINT "";
ALTER TABLE "repair_sent"DROP CONSTRAINT "";
ALTER TABLE "repair_finish"DROP CONSTRAINT "";
ALTER TABLE "repair_process_mode"DROP CONSTRAINT "";
ALTER TABLE "repair_use_vehicle"DROP CONSTRAINT "";
ALTER TABLE "repair_use_worker"DROP CONSTRAINT "";
ALTER TABLE "repair_parts_apply"DROP CONSTRAINT "";
ALTER TABLE "repair_other_cost"DROP CONSTRAINT "";
ALTER TABLE "repair_scene_failure"DROP CONSTRAINT "";
ALTER TABLE "repair_scene_replace"DROP CONSTRAINT "";
ALTER TABLE "repair_scene_overhaul_adjust"DROP CONSTRAINT "";
ALTER TABLE "repair_scene_indirect_failure"DROP CONSTRAINT "";
ALTER TABLE "repair_scene_indirect_replace"DROP CONSTRAINT "";
ALTER TABLE "repair_scene_indirect_overhaul_adjust"DROP CONSTRAINT "";
ALTER TABLE "repair_forgo"DROP CONSTRAINT "";
ALTER TABLE "repair_n_sent"DROP CONSTRAINT "";
ALTER TABLE "repair_cost"DROP CONSTRAINT "";
ALTER TABLE "repair_parts_use"DROP CONSTRAINT "";
ALTER TABLE "repair_parts_back"DROP CONSTRAINT "";
ALTER TABLE "repair_parts_lend"DROP CONSTRAINT "";

DROP TABLE "repair";
DROP TABLE "repair_vehicle";
DROP TABLE "repair_untreated";
DROP TABLE "repair_sent";
DROP TABLE "repair_finish";
DROP TABLE "repair_process_mode";
DROP TABLE "repair_use_vehicle";
DROP TABLE "repair_use_worker";
DROP TABLE "repair_parts_apply";
DROP TABLE "repair_other_cost";
DROP TABLE "repair_scene_failure";
DROP TABLE "repair_scene_replace";
DROP TABLE "repair_scene_overhaul_adjust";
DROP TABLE "repair_scene_indirect_failure";
DROP TABLE "repair_scene_indirect_replace";
DROP TABLE "repair_scene_indirect_overhaul_adjust";
DROP TABLE "repair_forgo";
DROP TABLE "repair_n_sent";
DROP TABLE "repair_cost";
DROP TABLE "repair_parts_use";
DROP TABLE "repair_parts_back";
DROP TABLE "repair_parts_lend";

CREATE TABLE "repair" (
"id" serial8 NOT NULL,
"repair_num" varchar(50) DEFAULT '',
"manager" varchar(100) DEFAULT '',
"status" varchar(10) DEFAULT '',
"update_at" timestamp DEFAULT now(),
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair"."id" IS '维修单ID';
COMMENT ON COLUMN "repair"."repair_num" IS '维修单编号';
COMMENT ON COLUMN "repair"."manager" IS '维修经理';
COMMENT ON COLUMN "repair"."status" IS '维修单状态,未处理(untreated)，未派出(n_sent)，已派出(sent)，正常完成或放弃维修单(finish/forgo)';
COMMENT ON COLUMN "repair"."update_at" IS '更新时间';
COMMENT ON COLUMN "repair"."create_at" IS '创建时间';

CREATE TABLE "repair_vehicle" (
"id" serial8,
"repair_num" varchar(255) DEFAULT '',
"engine_type" varchar(255) DEFAULT '',
"engine_num" varchar(255) DEFAULT '',
"engine_order_num" varchar(255) DEFAULT '',
"gearbox_type" varchar(255) DEFAULT '',
"gearbox_num" varchar(255) DEFAULT '',
"gearbox_user_num" varchar(255) DEFAULT '',
"radiators_num" varchar(255) DEFAULT '',
"radiators_vendor_code" varchar(255) DEFAULT '',
"laminated_spring_num" varchar(255) DEFAULT '',
"laminated_spring_vendor_code" varchar(255) DEFAULT '',
"first_drive_num" varchar(255) DEFAULT '',
"first_drive_vendor_code" varchar(255) DEFAULT '',
"second_drive_num" varchar(255) DEFAULT '',
"second_drive_vendor_code" varchar(255) DEFAULT '',
"third_drive_num" varchar(255) DEFAULT '',
"thrid_drive_vendor_code" varchar(255) DEFAULT '',
"first_axle" varchar(255) DEFAULT '',
"first_axle_vendor_code" varchar(255) DEFAULT '',
"second_axle" varchar(255) DEFAULT '',
"second_axle_vendor_code" varchar(255) DEFAULT '',
"third_axle" varchar(255) DEFAULT '',
"third_axle_vendor_code" varchar(255) DEFAULT '',
"update_at" timestamp DEFAULT now(),
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_vehicle"."id" IS '整车信息ID';
COMMENT ON COLUMN "repair_vehicle"."repair_num" IS '维修单号';
COMMENT ON COLUMN "repair_vehicle"."engine_type" IS '发动机型号';
COMMENT ON COLUMN "repair_vehicle"."engine_num" IS '发动机编号';
COMMENT ON COLUMN "repair_vehicle"."engine_order_num" IS '发动机订货单号';
COMMENT ON COLUMN "repair_vehicle"."gearbox_type" IS '变速箱型号';
COMMENT ON COLUMN "repair_vehicle"."gearbox_num" IS '变速箱编号';
COMMENT ON COLUMN "repair_vehicle"."gearbox_user_num" IS '变速箱用户编号';
COMMENT ON COLUMN "repair_vehicle"."radiators_num" IS '水箱图号';
COMMENT ON COLUMN "repair_vehicle"."radiators_vendor_code" IS '水箱厂家代码';
COMMENT ON COLUMN "repair_vehicle"."laminated_spring_num" IS '板簧图号';
COMMENT ON COLUMN "repair_vehicle"."laminated_spring_vendor_code" IS '板簧厂家代码';
COMMENT ON COLUMN "repair_vehicle"."first_drive_num" IS '第一传动图号';
COMMENT ON COLUMN "repair_vehicle"."first_drive_vendor_code" IS '第一传动厂家代码';
COMMENT ON COLUMN "repair_vehicle"."second_drive_num" IS '第二传动图号';
COMMENT ON COLUMN "repair_vehicle"."second_drive_vendor_code" IS '第二传动厂家代码';
COMMENT ON COLUMN "repair_vehicle"."third_drive_num" IS '第三传动图号';
COMMENT ON COLUMN "repair_vehicle"."thrid_drive_vendor_code" IS '第三传动厂家代码';
COMMENT ON COLUMN "repair_vehicle"."first_axle" IS '第一桥';
COMMENT ON COLUMN "repair_vehicle"."first_axle_vendor_code" IS '第一桥厂家代码';
COMMENT ON COLUMN "repair_vehicle"."second_axle" IS '第二桥';
COMMENT ON COLUMN "repair_vehicle"."second_axle_vendor_code" IS '第二桥厂家代码';
COMMENT ON COLUMN "repair_vehicle"."third_axle" IS '第三桥';
COMMENT ON COLUMN "repair_vehicle"."third_axle_vendor_code" IS '第三桥厂家代码';
COMMENT ON COLUMN "repair_vehicle"."update_at" IS '更新时间';
COMMENT ON COLUMN "repair_vehicle"."create_at" IS '创建时间';

CREATE TABLE "repair_untreated" (
"id" serial8 NOT NULL,
"repair_num" varchar(50) DEFAULT '',
"repair_type" varchar(10) DEFAULT '',
"customer_name" varchar(200) DEFAULT '',
"customer_gender" varchar(10) DEFAULT '',
"customer_tel" varchar(20) DEFAULT '',
"customer_backup_tel" varchar(20) DEFAULT '',
"province" varchar(50) DEFAULT '',
"city" varchar(50) DEFAULT '',
"county" varchar(50) DEFAULT '',
"address" varchar(500) DEFAULT '',
"owner_name" varchar(200) DEFAULT '',
"owner_tel" varchar(20) DEFAULT '',
"frame_num" varchar(100) DEFAULT '',
"vehicle_type" varchar(100) DEFAULT '',
"plate_num" varchar(20) DEFAULT '',
"km" int4 DEFAULT 0,
"purchase_date" varchar(50) DEFAULT '',
"failure_desc" varchar(2000) DEFAULT '',
"service_username" varchar(50) DEFAULT '',
"service_name" varchar(200) DEFAULT '',
"service_upload_at" timestamp DEFAULT now(),
"status" varchar(10) DEFAULT '',
"upload_at" timestamp DEFAULT now(),
"update_at" timestamp DEFAULT now(),
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_untreated"."id" IS '维修单ID';
COMMENT ON COLUMN "repair_untreated"."repair_num" IS '维修单编号';
COMMENT ON COLUMN "repair_untreated"."repair_type" IS '维修单类型';
COMMENT ON COLUMN "repair_untreated"."customer_name" IS '客户姓名';
COMMENT ON COLUMN "repair_untreated"."customer_gender" IS '客户性别';
COMMENT ON COLUMN "repair_untreated"."customer_tel" IS '客户电话';
COMMENT ON COLUMN "repair_untreated"."customer_backup_tel" IS '客户备用电话';
COMMENT ON COLUMN "repair_untreated"."province" IS '省';
COMMENT ON COLUMN "repair_untreated"."city" IS '市';
COMMENT ON COLUMN "repair_untreated"."county" IS '区/县';
COMMENT ON COLUMN "repair_untreated"."address" IS '故障地址';
COMMENT ON COLUMN "repair_untreated"."owner_name" IS '车主姓名';
COMMENT ON COLUMN "repair_untreated"."owner_tel" IS '车主电话';
COMMENT ON COLUMN "repair_untreated"."frame_num" IS '大架号';
COMMENT ON COLUMN "repair_untreated"."vehicle_type" IS '车辆类型';
COMMENT ON COLUMN "repair_untreated"."plate_num" IS '车牌号';
COMMENT ON COLUMN "repair_untreated"."km" IS '行驶里程';
COMMENT ON COLUMN "repair_untreated"."purchase_date" IS '购买日期';
COMMENT ON COLUMN "repair_untreated"."failure_desc" IS '故障描述';
COMMENT ON COLUMN "repair_untreated"."service_username" IS '客服用户名';
COMMENT ON COLUMN "repair_untreated"."service_name" IS '客服姓名';
COMMENT ON COLUMN "repair_untreated"."service_upload_at" IS '客服上传时间';
COMMENT ON COLUMN "repair_untreated"."status" IS '维修单状态,未处理(untreated)，未派出(n_sent)，已派出(sent)，正常完成或放弃维修单(finish/forgo)';
COMMENT ON COLUMN "repair_untreated"."update_at" IS '更新时间';
COMMENT ON COLUMN "repair_untreated"."create_at" IS '创建时间';

CREATE TABLE "repair_sent" (
"id" serial8 NOT NULL,
"manager" varchar(100) DEFAULT '',
"repair_num" varchar(50) DEFAULT '',
"repair_type" varchar(10) DEFAULT '',
"customer_name" varchar(200) DEFAULT '',
"customer_gender" varchar(10) DEFAULT '',
"customer_tel" varchar(20) DEFAULT '',
"customer_backup_tel" varchar(20) DEFAULT '',
"province" varchar(50) DEFAULT '',
"city" varchar(50) DEFAULT '',
"county" varchar(50) DEFAULT '',
"address" varchar(500) DEFAULT '',
"owner_name" varchar(200) DEFAULT '',
"owner_tel" varchar(20) DEFAULT '',
"frame_num" varchar(100) DEFAULT '',
"vehicle_type" varchar(100) DEFAULT '',
"plate_num" varchar(20) DEFAULT '',
"km" int4 DEFAULT 0,
"purchase_date" varchar(50) DEFAULT '',
"failure_desc" varchar(2000) DEFAULT '',
"service_username" varchar(50) DEFAULT '',
"service_name" varchar(200) DEFAULT '',
"service_upload_at" timestamp DEFAULT now(),
"status" varchar(10) DEFAULT '',
"upload_at" timestamp(255) DEFAULT now(),
"update_at" timestamp DEFAULT now(),
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_sent"."id" IS '维修单ID';
COMMENT ON COLUMN "repair_sent"."manager" IS '维修经理用户名';
COMMENT ON COLUMN "repair_sent"."repair_num" IS '维修单编号';
COMMENT ON COLUMN "repair_sent"."repair_type" IS '维修单类型';
COMMENT ON COLUMN "repair_sent"."customer_name" IS '客户姓名';
COMMENT ON COLUMN "repair_sent"."customer_gender" IS '客户性别';
COMMENT ON COLUMN "repair_sent"."customer_tel" IS '客户电话';
COMMENT ON COLUMN "repair_sent"."customer_backup_tel" IS '客户备用电话';
COMMENT ON COLUMN "repair_sent"."province" IS '省';
COMMENT ON COLUMN "repair_sent"."city" IS '市';
COMMENT ON COLUMN "repair_sent"."county" IS '区/县';
COMMENT ON COLUMN "repair_sent"."address" IS '故障地址';
COMMENT ON COLUMN "repair_sent"."owner_name" IS '车主姓名';
COMMENT ON COLUMN "repair_sent"."owner_tel" IS '车主电话';
COMMENT ON COLUMN "repair_sent"."frame_num" IS '大架号';
COMMENT ON COLUMN "repair_sent"."vehicle_type" IS '车辆类型';
COMMENT ON COLUMN "repair_sent"."plate_num" IS '车牌号';
COMMENT ON COLUMN "repair_sent"."km" IS '行驶里程';
COMMENT ON COLUMN "repair_sent"."purchase_date" IS '购买日期';
COMMENT ON COLUMN "repair_sent"."failure_desc" IS '故障描述';
COMMENT ON COLUMN "repair_sent"."service_username" IS '客服用户名';
COMMENT ON COLUMN "repair_sent"."service_name" IS '客服姓名';
COMMENT ON COLUMN "repair_sent"."service_upload_at" IS '客服上传时间';
COMMENT ON COLUMN "repair_sent"."status" IS '维修单状态,未处理(untreated)，未派出(n_sent)，已派出(sent)，正常完成或放弃维修单(finish/forgo)';
COMMENT ON COLUMN "repair_sent"."upload_at" IS '未派出维修单的创建时间';
COMMENT ON COLUMN "repair_sent"."update_at" IS '更新时间';
COMMENT ON COLUMN "repair_sent"."create_at" IS '创建时间';

CREATE TABLE "repair_finish" (
"id" serial8 NOT NULL,
"manager" varchar(100) DEFAULT '',
"repair_num" varchar(50) DEFAULT '',
"repair_type" varchar(10) DEFAULT '',
"customer_name" varchar(200) DEFAULT '',
"customer_gender" varchar(10) DEFAULT '',
"customer_tel" varchar(20) DEFAULT '',
"customer_backup_tel" varchar(20) DEFAULT '',
"province" varchar(50) DEFAULT '',
"city" varchar(50) DEFAULT '',
"county" varchar(50) DEFAULT '',
"address" varchar(500) DEFAULT '',
"owner_name" varchar(200) DEFAULT '',
"owner_tel" varchar(20) DEFAULT '',
"frame_num" varchar(100) DEFAULT '',
"vehicle_type" varchar(100) DEFAULT '',
"plate_num" varchar(20) DEFAULT '',
"km" int4 DEFAULT 0,
"purchase_date" varchar(50) DEFAULT '',
"failure_desc" varchar(2000) DEFAULT '',
"service_username" varchar(50) DEFAULT '',
"service_name" varchar(200) DEFAULT '',
"service_upload_at" timestamp DEFAULT now(),
"status" varchar(10) DEFAULT '',
"upload_at" timestamp DEFAULT now(),
"update_at" timestamp DEFAULT now(),
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_finish"."id" IS '维修单ID';
COMMENT ON COLUMN "repair_finish"."manager" IS '维修经理用户名';
COMMENT ON COLUMN "repair_finish"."repair_num" IS '维修单编号';
COMMENT ON COLUMN "repair_finish"."repair_type" IS '维修单类型';
COMMENT ON COLUMN "repair_finish"."customer_name" IS '客户姓名';
COMMENT ON COLUMN "repair_finish"."customer_gender" IS '客户性别';
COMMENT ON COLUMN "repair_finish"."customer_tel" IS '客户电话';
COMMENT ON COLUMN "repair_finish"."customer_backup_tel" IS '客户备用电话';
COMMENT ON COLUMN "repair_finish"."province" IS '省';
COMMENT ON COLUMN "repair_finish"."city" IS '市';
COMMENT ON COLUMN "repair_finish"."county" IS '区/县';
COMMENT ON COLUMN "repair_finish"."address" IS '故障地址';
COMMENT ON COLUMN "repair_finish"."owner_name" IS '车主姓名';
COMMENT ON COLUMN "repair_finish"."owner_tel" IS '车主电话';
COMMENT ON COLUMN "repair_finish"."frame_num" IS '大架号';
COMMENT ON COLUMN "repair_finish"."vehicle_type" IS '车辆类型';
COMMENT ON COLUMN "repair_finish"."plate_num" IS '车牌号';
COMMENT ON COLUMN "repair_finish"."km" IS '行驶里程';
COMMENT ON COLUMN "repair_finish"."purchase_date" IS '购买日期';
COMMENT ON COLUMN "repair_finish"."failure_desc" IS '故障描述';
COMMENT ON COLUMN "repair_finish"."service_username" IS '客服用户名';
COMMENT ON COLUMN "repair_finish"."service_name" IS '客服姓名';
COMMENT ON COLUMN "repair_finish"."service_upload_at" IS '客服上传时间';
COMMENT ON COLUMN "repair_finish"."status" IS '维修单状态,未处理(untreated)，未派出(n_sent)，已派出(sent)，正常完成或放弃维修单(finish/forgo)';
COMMENT ON COLUMN "repair_finish"."upload_at" IS '已派出维修单的创建时间';
COMMENT ON COLUMN "repair_finish"."update_at" IS '更新时间';
COMMENT ON COLUMN "repair_finish"."create_at" IS '创建时间';

CREATE TABLE "repair_process_mode" (
"id" serial8,
"repair_num" varchar(50) DEFAULT '',
"diagnosis" varchar(500) DEFAULT '',
"analysis" varchar(500) DEFAULT '',
"process_mode" varchar(50) DEFAULT '',
"warranty" bool DEFAULT false,
"person" varchar(100) DEFAULT '',
"cost" decimal(10,2) DEFAULT 0.00,
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_process_mode"."id" IS '故障处理办法ID';
COMMENT ON COLUMN "repair_process_mode"."repair_num" IS '维修单号';
COMMENT ON COLUMN "repair_process_mode"."diagnosis" IS '故障诊断';
COMMENT ON COLUMN "repair_process_mode"."analysis" IS '故障分析';
COMMENT ON COLUMN "repair_process_mode"."process_mode" IS '处理措施';
COMMENT ON COLUMN "repair_process_mode"."warranty" IS '是否三包';
COMMENT ON COLUMN "repair_process_mode"."person" IS '处理人';
COMMENT ON COLUMN "repair_process_mode"."cost" IS '工时费';
COMMENT ON COLUMN "repair_process_mode"."create_at" IS '创建时间';

CREATE TABLE "repair_use_vehicle" (
"id" serial8,
"repair_num" varchar(50) DEFAULT '',
"name" varchar(200) DEFAULT '',
"type" varchar(100) DEFAULT '',
"tel" varchar(20) DEFAULT '',
"plate_num" varchar(20) DEFAULT '',
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_use_vehicle"."id" IS '用车信息ID';
COMMENT ON COLUMN "repair_use_vehicle"."repair_num" IS '维修单号';
COMMENT ON COLUMN "repair_use_vehicle"."name" IS '姓名';
COMMENT ON COLUMN "repair_use_vehicle"."type" IS '车型';
COMMENT ON COLUMN "repair_use_vehicle"."tel" IS '电话';
COMMENT ON COLUMN "repair_use_vehicle"."plate_num" IS '车牌';
COMMENT ON COLUMN "repair_use_vehicle"."create_at" IS '创建时间';

CREATE TABLE "repair_use_worker" (
"id" serial8,
"repair_num" varchar(50) DEFAULT '',
"name" varchar(200) DEFAULT '',
"headman" bool DEFAULT false,
"tel" varchar(20) DEFAULT '',
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_use_worker"."id" IS '用车信息ID';
COMMENT ON COLUMN "repair_use_worker"."repair_num" IS '维修单号';
COMMENT ON COLUMN "repair_use_worker"."name" IS '姓名';
COMMENT ON COLUMN "repair_use_worker"."headman" IS '是否时组长';
COMMENT ON COLUMN "repair_use_worker"."tel" IS '电话';
COMMENT ON COLUMN "repair_use_worker"."create_at" IS '创建时间';

CREATE TABLE "repair_parts_apply" (
"id" serial8,
"borrower" varchar(100) DEFAULT '',
"repair_num" varchar(50) DEFAULT '',
"factory" varchar(200) DEFAULT '',
"brand" varchar(200) DEFAULT '',
"name" varchar(200) DEFAULT '',
"code" varchar(200) DEFAULT '',
"picture_num" varchar(200) DEFAULT '',
"price" decimal(10,2) DEFAULT 0.00,
"count" int4 DEFAULT 0,
"status" varchar(20) DEFAULT 'apply',
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_parts_apply"."borrower" IS '借件人用户名';
COMMENT ON COLUMN "repair_parts_apply"."repair_num" IS '维修单编码';
COMMENT ON COLUMN "repair_parts_apply"."code" IS '配件编码';
COMMENT ON COLUMN "repair_parts_apply"."count" IS '借出数量';
COMMENT ON COLUMN "repair_parts_apply"."status" IS '状态';
COMMENT ON COLUMN "repair_parts_apply"."create_at" IS '创建时间';

CREATE TABLE "repair_other_cost" (
"id" serial8,
"repair_num" varchar(50) DEFAULT '',
"status" varchar(255) DEFAULT '',
"name" varchar(255) DEFAULT '',
"price" decimal(10,2) DEFAULT 0.00,
"num" int4 DEFAULT 0,
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_other_cost"."status" IS '维修单状态';
COMMENT ON COLUMN "repair_other_cost"."name" IS '其他配件名称';
COMMENT ON COLUMN "repair_other_cost"."price" IS '其他配件单价';
COMMENT ON COLUMN "repair_other_cost"."num" IS '使用数量';

CREATE TABLE "repair_scene_failure" (
"id" serial8,
"repair_num" varchar(50) DEFAULT '',
"describe" varchar(500) DEFAULT '',
"analysis" varchar(500) DEFAULT '',
"failure_parts_name" varchar(200) DEFAULT '',
"failure_parts_factory" varchar(200) DEFAULT '',
"failure_parts_num" varchar(200) DEFAULT '',
"failure_parts_amount" int4 DEFAULT 0,
"warranty" bool DEFAULT false,
"process_mode" varchar(20) DEFAULT '',
"parts_cost" decimal(10,2) DEFAULT 0.00,
"worker_cost" decimal(10,2) DEFAULT 0.00,
"total_cost" decimal(10,2) DEFAULT 0.00,
"manager" varchar(50) DEFAULT '',
"worker" varchar(50) DEFAULT '',
"indirect" bool DEFAULT false,
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_scene_failure"."id" IS '故障处理办法ID';
COMMENT ON COLUMN "repair_scene_failure"."repair_num" IS '维修单号';
COMMENT ON COLUMN "repair_scene_failure"."describe" IS '故障描述';
COMMENT ON COLUMN "repair_scene_failure"."analysis" IS '故障分析';
COMMENT ON COLUMN "repair_scene_failure"."failure_parts_name" IS '故障件名称';
COMMENT ON COLUMN "repair_scene_failure"."failure_parts_factory" IS '故障件厂家';
COMMENT ON COLUMN "repair_scene_failure"."failure_parts_num" IS '故障件图号';
COMMENT ON COLUMN "repair_scene_failure"."failure_parts_amount" IS '故障件数量';
COMMENT ON COLUMN "repair_scene_failure"."warranty" IS '是否三包';
COMMENT ON COLUMN "repair_scene_failure"."process_mode" IS '处理措施';
COMMENT ON COLUMN "repair_scene_failure"."parts_cost" IS '用件费';
COMMENT ON COLUMN "repair_scene_failure"."worker_cost" IS '工时费';
COMMENT ON COLUMN "repair_scene_failure"."total_cost" IS '共计';
COMMENT ON COLUMN "repair_scene_failure"."manager" IS '维修经理用户名';
COMMENT ON COLUMN "repair_scene_failure"."worker" IS '维修人用户名';
COMMENT ON COLUMN "repair_scene_failure"."indirect" IS '是否造成间接件损坏';
COMMENT ON COLUMN "repair_scene_failure"."create_at" IS '创建时间';

CREATE TABLE "repair_scene_replace" (
"id" serial8,
"scene_failure_id" int8 DEFAULT 0,
"repair_num" varchar(50) DEFAULT '',
"factory" varchar(200) DEFAULT '',
"name" varchar(200) DEFAULT '',
"num" varchar(200) DEFAULT '',
"amount" int4 DEFAULT 0,
"price" decimal(10,2) DEFAULT 0.00,
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_scene_replace"."scene_failure_id" IS '现场故障ID';
COMMENT ON COLUMN "repair_scene_replace"."factory" IS '厂家';
COMMENT ON COLUMN "repair_scene_replace"."name" IS '名称';
COMMENT ON COLUMN "repair_scene_replace"."num" IS '图号';
COMMENT ON COLUMN "repair_scene_replace"."amount" IS '数量';
COMMENT ON COLUMN "repair_scene_replace"."price" IS '单价';
COMMENT ON COLUMN "repair_scene_replace"."create_at" IS '创建时间';

CREATE TABLE "repair_scene_overhaul_adjust" (
"id" serial8,
"scene_failure_id" int8 DEFAULT 0,
"repair_num" varchar(50) DEFAULT '',
"describe" varchar(2000) DEFAULT '',
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_scene_overhaul_adjust"."scene_failure_id" IS '现场故障ID';
COMMENT ON COLUMN "repair_scene_overhaul_adjust"."describe" IS '描述';
COMMENT ON COLUMN "repair_scene_overhaul_adjust"."create_at" IS '创建时间';

CREATE TABLE "repair_scene_indirect_failure" (
"id" serial8,
"scene_failure_id" int8 DEFAULT 0,
"repair_num" varchar(50) DEFAULT '',
"describe" varchar(500) DEFAULT '',
"analysis" varchar(500) DEFAULT '',
"failure_parts_name" varchar(200) DEFAULT '',
"failure_parts_factory" varchar(200) DEFAULT '',
"failure_parts_num" varchar(200) DEFAULT '',
"failure_parts_amount" int4 DEFAULT 0,
"warranty" bool DEFAULT false,
"process_mode" varchar(20) DEFAULT '',
"parts_cost" decimal(10,2) DEFAULT 0.00,
"worker_cost" decimal(10,2) DEFAULT 0.00,
"total_cost" decimal(10,2) DEFAULT 0.00,
"manager" varchar(50) DEFAULT '',
"worker" varchar(50) DEFAULT '',
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_scene_indirect_failure"."id" IS '故障处理办法ID';
COMMENT ON COLUMN "repair_scene_indirect_failure"."scene_failure_id" IS '现场故障ID';
COMMENT ON COLUMN "repair_scene_indirect_failure"."repair_num" IS '维修单号';
COMMENT ON COLUMN "repair_scene_indirect_failure"."describe" IS '故障描述';
COMMENT ON COLUMN "repair_scene_indirect_failure"."analysis" IS '故障分析';
COMMENT ON COLUMN "repair_scene_indirect_failure"."failure_parts_name" IS '故障件名称';
COMMENT ON COLUMN "repair_scene_indirect_failure"."failure_parts_factory" IS '故障件厂家';
COMMENT ON COLUMN "repair_scene_indirect_failure"."failure_parts_num" IS '故障件图号';
COMMENT ON COLUMN "repair_scene_indirect_failure"."failure_parts_amount" IS '故障件数量';
COMMENT ON COLUMN "repair_scene_indirect_failure"."warranty" IS '是否三包';
COMMENT ON COLUMN "repair_scene_indirect_failure"."process_mode" IS '处理措施';
COMMENT ON COLUMN "repair_scene_indirect_failure"."parts_cost" IS '用件费';
COMMENT ON COLUMN "repair_scene_indirect_failure"."worker_cost" IS '用工费';
COMMENT ON COLUMN "repair_scene_indirect_failure"."total_cost" IS '共计';
COMMENT ON COLUMN "repair_scene_indirect_failure"."manager" IS '维修经理';
COMMENT ON COLUMN "repair_scene_indirect_failure"."worker" IS '维修人';
COMMENT ON COLUMN "repair_scene_indirect_failure"."create_at" IS '创建时间';

CREATE TABLE "repair_scene_indirect_replace" (
"id" serial8,
"indirect_failure_id" int8,
"repair_num" varchar(50),
"factory" varchar(200),
"name" varchar(200),
"num" varchar(200),
"amount" int4,
"price" decimal(10,2),
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_scene_indirect_replace"."indirect_failure_id" IS '现场故障ID';
COMMENT ON COLUMN "repair_scene_indirect_replace"."factory" IS '厂家';
COMMENT ON COLUMN "repair_scene_indirect_replace"."name" IS '名称';
COMMENT ON COLUMN "repair_scene_indirect_replace"."num" IS '图号';
COMMENT ON COLUMN "repair_scene_indirect_replace"."amount" IS '数量';
COMMENT ON COLUMN "repair_scene_indirect_replace"."price" IS '单价';
COMMENT ON COLUMN "repair_scene_indirect_replace"."create_at" IS '创建时间';

CREATE TABLE "repair_scene_indirect_overhaul_adjust" (
"id" serial8,
"indirect_failure_id" int8 DEFAULT 0,
"repair_num" varchar(50) DEFAULT '',
"describe" varchar(2000) DEFAULT '',
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_scene_indirect_overhaul_adjust"."indirect_failure_id" IS '现场故障ID';
COMMENT ON COLUMN "repair_scene_indirect_overhaul_adjust"."describe" IS '描述';
COMMENT ON COLUMN "repair_scene_indirect_overhaul_adjust"."create_at" IS '创建时间';

CREATE TABLE "repair_forgo" (
"id" serial8,
"repair_num" varchar(50) DEFAULT '',
"reason" varchar(2000) DEFAULT '',
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_forgo"."id" IS '弃单原因ID';
COMMENT ON COLUMN "repair_forgo"."reason" IS '弃单原因';
COMMENT ON COLUMN "repair_forgo"."create_at" IS '创建时间';

CREATE TABLE "repair_n_sent" (
"id" serial8 NOT NULL,
"manager" varchar(100) DEFAULT '',
"repair_num" varchar(50) DEFAULT '',
"repair_type" varchar(10) DEFAULT '',
"customer_name" varchar(200) DEFAULT '',
"customer_gender" varchar(10) DEFAULT '',
"customer_tel" varchar(20) DEFAULT '',
"customer_backup_tel" varchar(20) DEFAULT '',
"province" varchar(50) DEFAULT '',
"city" varchar(50) DEFAULT '',
"county" varchar(50) DEFAULT '',
"address" varchar(500) DEFAULT '',
"owner_name" varchar(200) DEFAULT '',
"owner_tel" varchar(20) DEFAULT '',
"frame_num" varchar(100) DEFAULT '',
"vehicle_type" varchar(100) DEFAULT '',
"plate_num" varchar(20) DEFAULT '',
"km" int4 DEFAULT 0,
"purchase_date" varchar(50) DEFAULT '',
"failure_desc" varchar(2000) DEFAULT '',
"service_username" varchar(50) DEFAULT '',
"service_name" varchar(200) DEFAULT '',
"service_upload_at" timestamp DEFAULT now(),
"status" varchar(10) DEFAULT '',
"upload_at" timestamp(255) DEFAULT now(),
"update_at" timestamp DEFAULT now(),
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_n_sent"."id" IS '维修单ID';
COMMENT ON COLUMN "repair_n_sent"."manager" IS '维修经理用户名';
COMMENT ON COLUMN "repair_n_sent"."repair_num" IS '维修单编号';
COMMENT ON COLUMN "repair_n_sent"."repair_type" IS '维修单类型';
COMMENT ON COLUMN "repair_n_sent"."customer_name" IS '客户姓名';
COMMENT ON COLUMN "repair_n_sent"."customer_gender" IS '客户性别';
COMMENT ON COLUMN "repair_n_sent"."customer_tel" IS '客户电话';
COMMENT ON COLUMN "repair_n_sent"."customer_backup_tel" IS '客户备用电话';
COMMENT ON COLUMN "repair_n_sent"."province" IS '省';
COMMENT ON COLUMN "repair_n_sent"."city" IS '市';
COMMENT ON COLUMN "repair_n_sent"."county" IS '区/县';
COMMENT ON COLUMN "repair_n_sent"."address" IS '故障地址';
COMMENT ON COLUMN "repair_n_sent"."owner_name" IS '车主姓名';
COMMENT ON COLUMN "repair_n_sent"."owner_tel" IS '车主电话';
COMMENT ON COLUMN "repair_n_sent"."frame_num" IS '大架号';
COMMENT ON COLUMN "repair_n_sent"."vehicle_type" IS '车辆类型';
COMMENT ON COLUMN "repair_n_sent"."plate_num" IS '车牌号';
COMMENT ON COLUMN "repair_n_sent"."km" IS '行驶里程';
COMMENT ON COLUMN "repair_n_sent"."purchase_date" IS '购买日期';
COMMENT ON COLUMN "repair_n_sent"."failure_desc" IS '故障描述';
COMMENT ON COLUMN "repair_n_sent"."service_username" IS '客服用户名';
COMMENT ON COLUMN "repair_n_sent"."service_name" IS '客服姓名';
COMMENT ON COLUMN "repair_n_sent"."service_upload_at" IS '客服上传时间';
COMMENT ON COLUMN "repair_n_sent"."status" IS '维修单状态,未处理(untreated)，未派出(n_sent)，已派出(sent)，正常完成或放弃维修单(finish/forgo)';
COMMENT ON COLUMN "repair_n_sent"."upload_at" IS '未派出维修单的创建时间';
COMMENT ON COLUMN "repair_n_sent"."update_at" IS '更新时间';
COMMENT ON COLUMN "repair_n_sent"."create_at" IS '创建时间';

CREATE TABLE "repair_cost" (
"id" serial8,
"repair_num" varchar(50) DEFAULT '',
"type" varchar(30) DEFAULT '',
"cost" decimal(10,2) DEFAULT 0.00,
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_cost"."repair_num" IS '维修单号';
COMMENT ON COLUMN "repair_cost"."type" IS '费用类型（用车或用工）';
COMMENT ON COLUMN "repair_cost"."cost" IS '费用';

CREATE TABLE "repair_parts_use" (
"id" serial8,
"repair_num" varchar(50) DEFAULT '',
"factory" varchar(200) DEFAULT '',
"brand" varchar(200) DEFAULT '',
"name" varchar(200) DEFAULT '',
"code" varchar(200) DEFAULT '',
"picture_num" varchar(200) DEFAULT '',
"price" decimal(10,2) DEFAULT 0.00,
"count" int4 DEFAULT 0,
"status" varchar(20) DEFAULT 'use',
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_parts_use"."repair_num" IS '维修单编码';
COMMENT ON COLUMN "repair_parts_use"."code" IS '配件编码';
COMMENT ON COLUMN "repair_parts_use"."count" IS '借出数量';
COMMENT ON COLUMN "repair_parts_use"."status" IS '状态';
COMMENT ON COLUMN "repair_parts_use"."create_at" IS '创建时间';

CREATE TABLE "repair_parts_back" (
"id" serial8,
"repair_num" varchar(50) DEFAULT '',
"factory" varchar(200) DEFAULT '',
"brand" varchar(200) DEFAULT '',
"name" varchar(200) DEFAULT '',
"code" varchar(200) DEFAULT '',
"picture_num" varchar(200) DEFAULT '',
"price" decimal(10,2) DEFAULT 0.00,
"count" int4 DEFAULT 0,
"status" varchar(20) DEFAULT 'back',
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_parts_back"."repair_num" IS '维修单编码';
COMMENT ON COLUMN "repair_parts_back"."code" IS '配件编码';
COMMENT ON COLUMN "repair_parts_back"."count" IS '借出数量';
COMMENT ON COLUMN "repair_parts_back"."status" IS '状态';
COMMENT ON COLUMN "repair_parts_back"."create_at" IS '创建时间';

CREATE TABLE "repair_parts_lend" (
"id" serial8,
"borrower" varchar(100) DEFAULT '',
"repair_num" varchar(50) DEFAULT '',
"factory" varchar(200) DEFAULT '',
"brand" varchar(200) DEFAULT '',
"name" varchar(200) DEFAULT '',
"code" varchar(200) DEFAULT '',
"picture_num" varchar(200) DEFAULT '',
"price" decimal(10,2) DEFAULT 0.00,
"count" int4 DEFAULT 0,
"status" varchar(20) DEFAULT 'lend',
"create_at" timestamp DEFAULT now(),
PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "repair_parts_lend"."borrower" IS '借件人用户名';
COMMENT ON COLUMN "repair_parts_lend"."repair_num" IS '维修单编码';
COMMENT ON COLUMN "repair_parts_lend"."code" IS '配件编码';
COMMENT ON COLUMN "repair_parts_lend"."count" IS '借出数量';
COMMENT ON COLUMN "repair_parts_lend"."status" IS '状态';
COMMENT ON COLUMN "repair_parts_lend"."create_at" IS '创建时间';

