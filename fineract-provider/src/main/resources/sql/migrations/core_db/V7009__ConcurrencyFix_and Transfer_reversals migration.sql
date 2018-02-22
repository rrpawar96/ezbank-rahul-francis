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

--adding new column application id which holds unique id

ALTER TABLE `glim_accounts` ADD COLUMN `application_id` DECIMAL(10,0) NULL DEFAULT '0' AFTER `loan_status_id`;

ALTER TABLE `gsim_accounts` ADD COLUMN `application_id` DECIMAL(10,0) NULL DEFAULT '0' AFTER `savings_status_id`;

-- permissions added for reversal
INSERT INTO `m_permission` ( `grouping`, `code`, `entity_name`, `action_name`) VALUES ( 'portfolio', 'UNDOSAVINGSTRANSFERTRANSACTION_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'UPDATE');

-- global configuration for enabling reversals
INSERT INTO `c_configuration` ( `name`,`enabled`,`description`) VALUES ( 'allow-undo-transfer-transaction',0,'this configuration if enabled allows the user to undo transfer transaction');