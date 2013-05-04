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

package org.apache.provisionr.api.pool;

import static org.apache.provisionr.api.AssertSerializable.assertSerializable;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class MachineTest {

    @Test
    public void testSerialization() {
        final Machine machine = Machine.builder()
            .externalId("i-12345")
            .publicDnsName("machine1.example.com")
            .publicIp("62.231.75.237")
            .privateDnsName("something.internal")
            .privateIp("10.0.3.45")
            .option("key", "value")
            .createMachine();

        assertThat(machine.getPrivateIp()).startsWith("10.0");
        assertThat(machine.getSshPort()).isEqualTo(22);
        assertThat(machine.toBuilder().createMachine()).isEqualTo(machine);

        assertSerializable(machine, Machine.class);
    }
}
