--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership. The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License. You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied. See the License for the
-- specific language governing permissions and limitations
-- under the License.
--


	
	-- midifications to savings account and savings transaction

 	ALTER TABLE `m_savings_account`
	ADD COLUMN `is_retail` TINYINT(1) NOT NULL DEFAULT '0' AFTER `product_id`;
	
	ALTER TABLE `m_savings_account_transaction`
	ADD COLUMN `external_id` VARCHAR(50) NULL DEFAULT NULL AFTER `payment_detail_id`;
	
	ALTER TABLE `m_savings_account`
	ADD COLUMN `autogenerate_transaction_id` TINYINT(1) NOT NULL DEFAULT '0' AFTER `is_retail`;
	
	ALTER TABLE `m_savings_account_transaction` 
	ADD CONSTRAINT `unique_retail_transaction` UNIQUE(`savings_account_id`,`external_id`);
	
	-- permissions
	
	INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATE_RETAILACCOUNTENTRIES', 'RETAILACCOUNTENTRIES', 'CREATE', 0);
	  
	INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'UPDATE_RETAILACCOUNTENTRIES', 'RETAILACCOUNTENTRIES', 'UPDATE', 0);
	
	
	-- new retail transaction table
	
	CREATE TABLE `idt_retail_transaction_range` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `retail_savings_id` bigint(20) NOT NULL,
  `transaction_upper_limit` bigint(20) NOT NULL,
  `transaction_lower_limit` bigint(20) NOT NULL,
  `current_transaction_id_used` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_retail_savings_id` (`retail_savings_id`),
  CONSTRAINT `FK_retail_savings_id` FOREIGN KEY (`retail_savings_id`) REFERENCES `m_savings_account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;


-- new retail account type entry table

CREATE TABLE `retail_account_entry_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `data_type` varchar(30) NOT NULL,
  `retail_account_id` bigint(20) NOT NULL,
  `is_constant` tinyint(1) NOT NULL DEFAULT '0',
  `constant_value` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_retail_account_id` (`retail_account_id`),
  CONSTRAINT `FK_retail_account_id` FOREIGN KEY (`retail_account_id`) REFERENCES `m_savings_account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


-- new retail account entry data table

CREATE TABLE `retail_account_entry_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `retail_account_entry_type_id` bigint(20) NOT NULL,
  `retail_account_entry_value` varchar(100) NOT NULL,
  `retail_account_transaction_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_retail_entry` (`retail_account_entry_type_id`,`retail_account_transaction_id`),
  KEY `FK_retail_transactions` (`retail_account_transaction_id`),
  CONSTRAINT `FK_retail_entries` FOREIGN KEY (`retail_account_entry_type_id`) REFERENCES `retail_account_entry_types` (`id`),
  CONSTRAINT `FK_retail_transactions` FOREIGN KEY (`retail_account_transaction_id`) REFERENCES `m_savings_account_transaction` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
