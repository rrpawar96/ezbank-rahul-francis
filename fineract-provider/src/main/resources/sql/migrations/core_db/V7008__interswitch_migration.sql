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

-- Create new table to store card details


CREATE TABLE `idt_interswitch_card_details` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`primary_account_number` BIGINT(20) NOT NULL,
	`savings_account_id` BIGINT(20) NOT NULL,
	`cvv` INT(11) NOT NULL,
	`valid_from` DATE NOT NULL,
	`valid_through` DATE NOT NULL,
	`pin` VARCHAR(50) NOT NULL,
	INDEX `InterSwitchCard_savings_FK` (`savings_account_id`),
	INDEX `primary_key` (`id`),
	CONSTRAINT `InterSwitchCard_savings_FK` FOREIGN KEY (`savings_account_id`) REFERENCES `m_savings_account` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

-- create new table to record  transactions resulting due to interswitch authorization

CREATE TABLE `idt_interswitch_transactions` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`session_id` VARCHAR(50) NOT NULL,
	`authorization_number` VARCHAR(50) NOT NULL,
	`application_transaction_id` BIGINT(20) NOT NULL,
	`transaction_amount` DECIMAL(10,0) NOT NULL,
	`transaction_date` DATE NOT NULL,
	`is_reversed` TINYINT(4) NOT NULL,
	`is_adviced` TINYINT(4) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_idt_interswitch_transactions_m_savings_account_transaction` (`application_transaction_id`),
	CONSTRAINT `FK_idt_interswitch_transactions_m_savings_account_transaction` FOREIGN KEY (`application_transaction_id`) REFERENCES `m_savings_account_transaction` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

-- create new table to record  interswitch authorization messages

CREATE TABLE `idt_interswitch_authorization_requests` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`session_id` VARCHAR(50) NOT NULL,
	`transaction_id` BIGINT(20) NULL DEFAULT NULL,
	`authorization_amount` DECIMAL(10,0) NOT NULL,
	`settlement_amount` DECIMAL(10,0) NULL DEFAULT NULL,
	`settlement_currency` VARCHAR(50) NULL DEFAULT NULL,
	`settlement_currency_rate` DECIMAL(10,0) NULL DEFAULT NULL,
	`transaction_currency` VARCHAR(50) NULL DEFAULT NULL,
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

-- create new table to map iso currency codes

CREATE TABLE `idt_interswitch_currency_codes` (
	`Entity` VARCHAR(50) NULL DEFAULT NULL,
	`Currency` VARCHAR(50) NULL DEFAULT NULL,
	`AlphabeticCode` VARCHAR(50) NULL DEFAULT NULL,
	`NumericCode` INT(11) NULL DEFAULT NULL,
	`MinorUnit` VARCHAR(50) NULL DEFAULT NULL,
	`WithdrawalDate` DATE NULL DEFAULT NULL
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;


