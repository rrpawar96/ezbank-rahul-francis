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



-- create new table to record  transactions resulting due to interswitch authorization

CREATE TABLE `idt_interswitch_transactions` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`session_id` VARCHAR(50) NOT NULL,
--	`authorization_number` VARCHAR(50) NOT NULL,
--	`application_transaction_id` BIGINT(20) NOT NULL,
	`transaction_amount` DECIMAL(10,0) NOT NULL,
	`transaction_date` DATE NOT NULL,
	`is_reversed` TINYINT(4) NOT NULL,
	`is_adviced` TINYINT(4) NOT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

-- create new table to record  interswitch authorization messages

CREATE TABLE `idt_interswitch_authorization_requests` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
--	`session_id` VARCHAR(50) NOT NULL,
	`transaction_id` BIGINT(20) NULL DEFAULT NULL,
--	`authorization_amount` DECIMAL(10,0) NOT NULL,
	`settlement_amount` DECIMAL(10,0) NULL DEFAULT NULL,
--	`settlement_currency` INT(11) NULL DEFAULT NULL,
--	`settlement_currency_rate` DECIMAL(10,0) NULL DEFAULT NULL,
--	`transaction_currency` INT(11) NULL DEFAULT NULL,
	`transaction_date` DATE NOT NULL,
	`settlement_date` DATE NOT NULL,
	`is_settled` TINYINT(4) NOT NULL,
	`is_reversed` TINYINT(4) NOT NULL,
	`is_adviced` TINYINT(4) NOT NULL,
	`settled_on` DATE NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_interswitch_auth_transation` (`transaction_id`),
	CONSTRAINT `FK_interswitch_auth_transation` FOREIGN KEY (`transaction_id`) REFERENCES `idt_interswitch_transactions` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

-- permissions

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'AUTHORIZE_TRANSACTION', 'TRANSACTION', 'AUTHORIZE', 1);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'EXECUTE_TRANSACTION', 'TRANSACTION', 'EXECUTE', 1);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'UNDO_INTERSWITCHTRANSACTION', 'INTERSWITCHTRANSACTION', 'UNDO', 1);

