SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 库区表 wms_zone
-- ----------------------------
DROP TABLE IF EXISTS `wms_zone`;
CREATE TABLE `wms_zone` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `zone_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '库区编码',
  `zone_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '库区名称',
  `warehouse_id` bigint(20) NULL DEFAULT NULL COMMENT '所属仓库',
  `zone_type` int(11) NULL DEFAULT NULL COMMENT '库区类型(1存储区 2拣货区 3收货区 4发货区 5退货区 6质检区)',
  `capacity` int(11) NULL DEFAULT NULL COMMENT '容量(库位数)',
  `order_num` bigint(20) NULL DEFAULT 0 COMMENT '显示顺序',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '库区' ROW_FORMAT = Dynamic;

INSERT INTO `wms_zone` VALUES (1, 'A01', 'A区-存储区', 1828364609002311682, 1, 200, 1, '常温存储区', 'admin', '2026-09-05 10:00:00.000', 'admin', '2026-09-05 10:00:00.000');
INSERT INTO `wms_zone` VALUES (2, 'A02', 'B区-拣货区', 1828364609002311682, 2, 100, 2, '拣货作业区', 'admin', '2026-09-05 10:00:00.000', 'admin', '2026-09-05 10:00:00.000');
INSERT INTO `wms_zone` VALUES (3, 'C01', '冷链区', 1828364740028174337, 1, 50, 1, '冷链存储', 'admin', '2026-09-05 10:00:00.000', 'admin', '2026-09-05 10:00:00.000');

-- ----------------------------
-- 库位表 wms_location
-- ----------------------------
DROP TABLE IF EXISTS `wms_location`;
CREATE TABLE `wms_location` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `location_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '库位编码',
  `location_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '库位名称',
  `zone_id` bigint(20) NULL DEFAULT NULL COMMENT '所属库区',
  `warehouse_id` bigint(20) NULL DEFAULT NULL COMMENT '所属仓库',
  `location_type` int(11) NULL DEFAULT NULL COMMENT '库位类型(1货架位 2地堆位 3托盘位)',
  `max_weight` decimal(10,2) NULL DEFAULT NULL COMMENT '承重(kg)',
  `length` decimal(10,2) NULL DEFAULT NULL COMMENT '长(cm)',
  `width` decimal(10,2) NULL DEFAULT NULL COMMENT '宽(cm)',
  `height` decimal(10,2) NULL DEFAULT NULL COMMENT '高(cm)',
  `status` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态(0空闲 1占用 2禁用)',
  `order_num` bigint(20) NULL DEFAULT 0 COMMENT '显示顺序',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '库位' ROW_FORMAT = Dynamic;

INSERT INTO `wms_location` VALUES (1, 'A01-01-01', 'A区01排01列', 1, 1828364609002311682, 1, 500.00, 120.00, 100.00, 180.00, '0', 1, NULL, 'admin', '2026-09-05 10:00:00.000', 'admin', '2026-09-05 10:00:00.000');
INSERT INTO `wms_location` VALUES (2, 'A01-01-02', 'A区01排02列', 1, 1828364609002311682, 1, 500.00, 120.00, 100.00, 180.00, '0', 2, NULL, 'admin', '2026-09-05 10:00:00.000', 'admin', '2026-09-05 10:00:00.000');
INSERT INTO `wms_location` VALUES (3, 'B01-01-01', 'B区拣货位01', 2, 1828364609002311682, 1, 300.00, 100.00, 80.00, 150.00, '0', 1, NULL, 'admin', '2026-09-05 10:00:00.000', 'admin', '2026-09-05 10:00:00.000');

-- ----------------------------
-- 波次表 wms_wave
-- ----------------------------
DROP TABLE IF EXISTS `wms_wave`;
CREATE TABLE `wms_wave` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `wave_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '波次号',
  `warehouse_id` bigint(20) NULL DEFAULT NULL COMMENT '所属仓库',
  `status` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态(0待拣货 1拣货中 2已完成 3已取消)',
  `total_quantity` decimal(18,2) NULL DEFAULT NULL COMMENT '总数量',
  `total_sku` int(11) NULL DEFAULT NULL COMMENT 'SKU种类数',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '波次' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 波次明细表 wms_wave_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_wave_detail`;
CREATE TABLE `wms_wave_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `wave_id` bigint(20) NULL DEFAULT NULL COMMENT '波次ID',
  `shipment_order_id` bigint(20) NULL DEFAULT NULL COMMENT '出库单ID',
  `shipment_order_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '出库单号',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '波次明细' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 批次表 wms_batch
-- ----------------------------
DROP TABLE IF EXISTS `wms_batch`;
CREATE TABLE `wms_batch` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `batch_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次号',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT '规格ID',
  `quantity` decimal(18,2) NULL DEFAULT NULL COMMENT '批次数量',
  `production_date` date NULL DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date NULL DEFAULT NULL COMMENT '有效期至',
  `supplier` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '供应商/来源',
  `status` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态(0正常 1已过期 2已用完)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '批次' ROW_FORMAT = Dynamic;

INSERT INTO `wms_batch` VALUES (1, 'PC20260905001', 1, 500.00, '2026-09-01', '2027-09-01', '苏州供应商A', '0', NULL, 'admin', '2026-09-05 10:00:00.000', 'admin', '2026-09-05 10:00:00.000');
INSERT INTO `wms_batch` VALUES (2, 'PC20260905002', 2, 300.00, '2026-08-20', '2027-08-20', '常熟供应商B', '0', NULL, 'admin', '2026-09-05 10:00:00.000', 'admin', '2026-09-05 10:00:00.000');

-- ----------------------------
-- 序列号表 wms_serial
-- ----------------------------
DROP TABLE IF EXISTS `wms_serial`;
CREATE TABLE `wms_serial` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `serial_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '序列号',
  `batch_id` bigint(20) NULL DEFAULT NULL COMMENT '所属批次',
  `batch_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '批次号(冗余)',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT '规格ID',
  `status` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态(0在库 1已出库 2报废)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '序列号' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 发货计划表 wms_ship_plan
-- ----------------------------
DROP TABLE IF EXISTS `wms_ship_plan`;
CREATE TABLE `wms_ship_plan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `plan_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '计划单号',
  `status` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态: 0待装车 1已装车 2已取消',
  `customer_id` bigint(20) NULL DEFAULT NULL COMMENT '客户ID(往来单位)',
  `warehouse_id` bigint(20) NULL DEFAULT NULL COMMENT '发货仓库ID',
  `total_quantity` decimal(18,2) NULL DEFAULT NULL COMMENT '总数量',
  `total_sku` int(11) NULL DEFAULT NULL COMMENT 'SKU种类数',
  `total_amount` decimal(18,2) NULL DEFAULT NULL COMMENT '总金额',
  `expect_date` date NULL DEFAULT NULL COMMENT '期望发货日期',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人电话',
  `receiver_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货地址',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '发货计划单' ROW_FORMAT = Dynamic;

INSERT INTO `wms_ship_plan` VALUES (1, 'FH20260905001', '0', 1, 1828364609002311682, 100.00, 2, 12500.00, '2026-09-08', '张三', '13800000001', '上海市浦东新区XX路100号', NULL, 'admin', '2026-09-05 10:00:00.000', 'admin', '2026-09-05 10:00:00.000');

-- ----------------------------
-- 发货计划明细表 wms_ship_plan_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_ship_plan_detail`;
CREATE TABLE `wms_ship_plan_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `plan_id` bigint(20) NULL DEFAULT NULL COMMENT '发货计划ID',
  `shipment_order_id` bigint(20) NULL DEFAULT NULL COMMENT '出库单ID',
  `shipment_order_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '出库单号',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '发货计划明细' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 装车单表 wms_load_order
-- ----------------------------
DROP TABLE IF EXISTS `wms_load_order`;
CREATE TABLE `wms_load_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `load_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '装车单号',
  `status` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态: 0待发车 1在途 2已签收 3异常',
  `vehicle_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '车牌号',
  `driver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '司机姓名',
  `driver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '司机电话',
  `total_quantity` decimal(18,2) NULL DEFAULT NULL COMMENT '总数量',
  `total_plan` int(11) NULL DEFAULT NULL COMMENT '计划单数量',
  `depart_time` datetime NULL DEFAULT NULL COMMENT '发车时间',
  `sign_time` datetime NULL DEFAULT NULL COMMENT '签收时间',
  `sign_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '签收人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '装车单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 装车单明细表 wms_load_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_load_order_detail`;
CREATE TABLE `wms_load_order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `load_order_id` bigint(20) NULL DEFAULT NULL COMMENT '装车单ID',
  `ship_plan_id` bigint(20) NULL DEFAULT NULL COMMENT '发货计划ID',
  `ship_plan_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发货计划单号',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '装车单明细' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
