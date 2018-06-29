SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE `basic_attention` DROP INDEX `ba_peopleid`;

ALTER TABLE `basic_attention` ADD INDEX `ba_people_id`(`ba_people_id`) USING BTREE;

#ALTER TABLE `basic_column` DROP INDEX `fk_basic_column_1`;

#ALTER TABLE `basic_column` DROP FOREIGN KEY `fk_basic_column`;

#ALTER TABLE `basic_column` DROP FOREIGN KEY `fk_basic_column_1`;

#ALTER TABLE `basic_column` ADD INDEX `fk_basic_column_cm_id`(`column_cm_id`) USING BTREE;

#ALTER TABLE `basic_column` ADD CONSTRAINT `fk_basic_column_category_id` FOREIGN KEY (`column_category_id`) REFERENCES `category` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

#ALTER TABLE `basic_column` ADD CONSTRAINT `fk_basic_column_cm_id` FOREIGN KEY (`column_cm_id`) REFERENCES `mdiy_content_model` (`cm_id`) ON DELETE SET NULL ON UPDATE NO ACTION;

ALTER TABLE `basic_log` ROW_FORMAT = Dynamic;

ALTER TABLE `category` DROP FOREIGN KEY `fk_category`;

ALTER TABLE `category` DROP FOREIGN KEY `fk_category_1`;

ALTER TABLE `category` ADD CONSTRAINT `fk_category_app_id` FOREIGN KEY (`category_appid`) REFERENCES `app` (`app_id`) ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE `category` ADD CONSTRAINT `fk_category_category_id` FOREIGN KEY (`category_categoryid`) REFERENCES `category` (`category_id`) ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE `manager_model_page` ROW_FORMAT = Dynamic;

ALTER TABLE `mdiy_content_mode_field` MODIFY COLUMN `create_date` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) AFTER `create_by`;

ALTER TABLE `mdiy_content_mode_field` MODIFY COLUMN `update_date` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) AFTER `update_by`;

ALTER TABLE `mdiy_content_model` MODIFY COLUMN `creaet_date` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) AFTER `create_by`;

ALTER TABLE `mdiy_content_model` MODIFY COLUMN `update_date` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) AFTER `update_by`;

ALTER TABLE `mdiy_dict` COLLATE = utf8_bin;

ALTER TABLE `mdiy_dict` MODIFY COLUMN `dict_value` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '数据值' AFTER `app_id`;

ALTER TABLE `mdiy_dict` MODIFY COLUMN `dict_label` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '标签名' AFTER `dict_value`;

ALTER TABLE `mdiy_dict` MODIFY COLUMN `dict_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '类型' AFTER `dict_label`;

ALTER TABLE `mdiy_dict` MODIFY COLUMN `dict_description` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '描述' AFTER `dict_type`;

ALTER TABLE `mdiy_dict` MODIFY COLUMN `dict_parent_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '0' COMMENT '父级编号' AFTER `dict_sort`;

ALTER TABLE `mdiy_dict` MODIFY COLUMN `dict_remarks` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '备注信息' AFTER `dict_parent_id`;

ALTER TABLE `mdiy_dict` ADD INDEX `dict_value`(`dict_value`) USING BTREE;

ALTER TABLE `mdiy_dict` ADD INDEX `dict_label`(`dict_label`) USING BTREE;

ALTER TABLE `mdiy_form` MODIFY COLUMN `create_date` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) AFTER `create_by`;

ALTER TABLE `mdiy_form` MODIFY COLUMN `update_date` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) AFTER `update_by`;

ALTER TABLE `people` MODIFY COLUMN `people_datetime` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间' AFTER `people_password`;

ALTER TABLE `role` ADD INDEX `role_managerid`(`role_managerid`) USING BTREE;

ALTER TABLE `role` ADD INDEX `fk_role_app_id`(`app_id`) USING BTREE;

ALTER TABLE `role` ADD CONSTRAINT `fk_role_app_id` FOREIGN KEY (`app_id`) REFERENCES `app` (`app_id`) ON DELETE CASCADE ON UPDATE RESTRICT;

ALTER TABLE `model` DROP FOREIGN KEY `model_ibfk_1`;

ALTER TABLE `model` ADD CONSTRAINT `fk_model_model_id` FOREIGN KEY (`model_modelid`) REFERENCES `model` (`model_id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `mdiy_page` DROP FOREIGN KEY `fk_model_template_app_1`;
ALTER TABLE `mdiy_page` ADD CONSTRAINT `fk_page_app_id` FOREIGN KEY (`page_app_id`) REFERENCES `app` (`app_id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `system_skin` ROW_FORMAT = Dynamic;

ALTER TABLE `system_skin` ADD INDEX `ss_app_id`(`ss_app_id`) USING BTREE;

ALTER TABLE `system_skin` ADD INDEX `ss_category_id`(`ss_category_id`) USING BTREE;

ALTER TABLE `system_skin` DROP FOREIGN KEY `fk_system_skin_app_1`;

ALTER TABLE `system_skin` ADD CONSTRAINT `fk_system_skin_app_id` FOREIGN KEY (`ss_app_id`) REFERENCES `app` (`app_id`) ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE `system_skin` DROP INDEX `fk_system_skin_app_1`;

ALTER TABLE `mdiy_form` MODIFY COLUMN `form_table_name` varchar(30) NOT NULL COMMENT '自定义表单表名' AFTER `form_tips_name`;

ALTER TABLE `mdiy_form_field` MODIFY COLUMN `ff_fieldname` varchar(30) NULL DEFAULT NULL COMMENT '字段名称' AFTER `ff_tipsname`;
ALTER TABLE `basic_column` DROP FOREIGN KEY `fk_basic_column`;
ALTER TABLE `basic_column` ADD CONSTRAINT `fk_column_category_id` FOREIGN KEY (`column_category_id`) REFERENCES `category` (`category_id`) ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE `basic` DROP FOREIGN KEY `fk_basic_app_1`;
ALTER TABLE `basic` ADD CONSTRAINT `fk_basic_app_id` FOREIGN KEY (`basic_appid`) REFERENCES `app` (`app_id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `basic` DROP FOREIGN KEY `fk_basic_category_1`;
ALTER TABLE `basic` ADD CONSTRAINT `fk_basic_category_id` FOREIGN KEY (`basic_categoryid`) REFERENCES `category` (`category_id`) ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE `basic_column` ADD CONSTRAINT `fk_basic_column_id` FOREIGN KEY (`column_cm_id`) REFERENCES `mdiy_content_model` (`cm_id`) ON DELETE SET NULL ON UPDATE NO ACTION;

ALTER TABLE `basic` DROP INDEX `basic_title`;

ALTER TABLE `basic` DROP FOREIGN KEY `fk_basic_category_id`;

ALTER TABLE `basic` ADD CONSTRAINT `fk_basic_categoryid` FOREIGN KEY (`basic_categoryid`) REFERENCES `category` (`category_id`) ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE `basic` ADD INDEX `basic_title`(`basic_title`) USING BTREE;

ALTER TABLE `basic_column` DROP FOREIGN KEY `fk_basic_column_id`;

ALTER TABLE `basic_column` DROP FOREIGN KEY `fk_column_category_id`;

ALTER TABLE `basic_column` ADD CONSTRAINT `fk_basic_column_category_id` FOREIGN KEY (`column_category_id`) REFERENCES `category` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `basic_column` ADD CONSTRAINT `fk_basic_column_cm_id` FOREIGN KEY (`column_cm_id`) REFERENCES `mdiy_content_model` (`cm_id`) ON DELETE SET NULL ON UPDATE NO ACTION;

ALTER TABLE `basic_column` ADD INDEX `fk_basic_column_cm_id`(`column_cm_id`) USING BTREE;

ALTER TABLE `role` DROP FOREIGN KEY `fk_role_app_id`;

ALTER TABLE `role` ADD CONSTRAINT `fk_role_app_id` FOREIGN KEY (`app_id`) REFERENCES `app` (`app_id`) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE `basic_column` ADD INDEX `fk_basic_column_id`(`column_cm_id`) USING BTREE;




ALTER TABLE `basic_column` DROP INDEX `fk_basic_column_1`;

ALTER TABLE `basic_column` DROP INDEX `fk_basic_column_cm_id`;

ALTER TABLE `basic_column` DROP FOREIGN KEY `fk_basic_column_1`;

ALTER TABLE `basic_column` DROP FOREIGN KEY `fk_basic_column_category_id`;

ALTER TABLE `basic_column` DROP FOREIGN KEY `fk_basic_column_cm_id`;


ALTER TABLE `basic_column` ADD CONSTRAINT `fk_column_category_id` FOREIGN KEY (`column_category_id`) REFERENCES `category` (`category_id`) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE `basic_column` ADD CONSTRAINT `fk_basic_column_id` FOREIGN KEY (`column_cm_id`) REFERENCES `mdiy_content_model` (`cm_id`) ON DELETE SET NULL ON UPDATE NO ACTION;
ALTER TABLE `app` ADD COLUMN `app_login_page` varchar(255) NULL COMMENT '自定义登录界面' AFTER `app_mobile_state`;
DROP TABLE `system_skin`;
SET FOREIGN_KEY_CHECKS=1;