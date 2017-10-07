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


-- MODIFY m_savings_account TP ACCOMODATE RETAIL RANGE COLUMNS


ALTER TABLE  m_savings_account
ADD COLUMN `transaction_upper_limit` bigint(20) DEFAULT NULL after autogenerate_transaction_id,
ADD COLUMN  `transaction_lower_limit` bigint(20) DEFAULT NULL after transaction_upper_limit,
ADD COLUMN  `current_transaction_id_used` bigint(20) DEFAULT NULL after transaction_lower_limit;


-- ENSURE THAT TRANSACTION RANGE COLUMNS ARE FILLED WHEN A SAVINGS ACCOUNT IS RETAIL ACCOUNT

	ALTER TABLE m_savings_account
	ADD CONSTRAINT retail_check CHECK
							( 
							autogenerate_transaction_id=0
							or ( transaction_upper_limit IS NOT NULL AND transaction_lower_limit IS NOT NULL AND current_transaction_id_used )
										
							);