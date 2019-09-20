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

--  These types are added to cater for cashier management

INSERT INTO r_enum_value
(enum_name, enum_id, enum_message_property, enum_value, enum_type)
VALUES
('entity_account_type_enum', 101, 'Allocate Cashier', 'Allocate Cashier', 0),
('entity_account_type_enum', 102, 'Settle Cashier', 'Settle Cashier', 0),
('entity_account_type_enum', 103, 'Inward Cash Transaction', 'Inward Cash Transaction', 0),
('entity_account_type_enum', 104, 'Outward Cash Transaction', 'Outward Cash Transaction', 0)