/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.provisionr.core;

import static org.fest.assertions.api.Assertions.assertThat;

import org.apache.provisionr.api.pool.Pool;

import org.junit.Test;

public class ProvisionrSupportTest {

    @Test
    public void testConvertTimeout() {
        ProvisionrSupport provisionr = new ProvisionrSupportTestable();
        assertThat(provisionr.convertTimeoutToISO8601TimeDuration(600)).isEqualTo("PT10M");
        assertThat(provisionr.convertTimeoutToISO8601TimeDuration(601)).isEqualTo("PT601S");
        assertThat(provisionr.convertTimeoutToISO8601TimeDuration(300)).isEqualTo("PT5M");
        assertThat(provisionr.convertTimeoutToISO8601TimeDuration(42)).isEqualTo("PT42S");
    }
}

class ProvisionrSupportTestable extends ProvisionrSupport {
    @Override
    public String getId() {
        return null;
    }
    @Override
    public String startPoolManagementProcess(String businessKey, Pool pool) {
        return null;
    }
    @Override
    public void destroyPool(String businessKey) {}
};
