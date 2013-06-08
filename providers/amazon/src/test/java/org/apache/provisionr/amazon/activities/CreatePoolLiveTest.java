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

package org.apache.provisionr.amazon.activities;

import java.util.ArrayList;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.provisionr.api.access.AdminAccess;
import org.apache.provisionr.api.hardware.BlockDevice;
import org.apache.provisionr.api.hardware.Hardware;
import org.apache.provisionr.api.network.Network;
import org.apache.provisionr.api.network.Rule;
import org.apache.provisionr.api.pool.Pool;
import org.apache.provisionr.api.software.Software;
import org.apache.provisionr.core.CoreProcessVariables;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class CreatePoolLiveTest<T extends AmazonActivity> extends AmazonActivityLiveTest<T> {

    protected DelegateExecution execution;
    protected Pool pool;
    protected Hardware hardware;
    protected Software software;

    @SuppressWarnings("unchecked")
    @Override
    public void setUp() throws Exception {
        super.setUp();
        execution = mock(DelegateExecution.class);
        pool = mock(Pool.class);

        final AdminAccess adminAccess = AdminAccess.builder()
            .username("admin")
            .publicKey(getResourceAsString("/org/apache/provisionr/test/id_rsa_test.pub"))
            .privateKey(getResourceAsString("/org/apache/provisionr/test/id_rsa_test"))
            .createAdminAccess();

        final Network network = Network.builder().addRules(
            Rule.builder().anySource().tcp().port(22).createRule()).createNetwork();

        hardware = mock(Hardware.class);
        when(hardware.getType()).thenReturn("t1.micro");
        when(hardware.getBlockDevices()).thenReturn(new ArrayList<BlockDevice>());

        software = mock(Software.class);

        when(pool.getProvider()).thenReturn(provider);
        when(pool.getAdminAccess()).thenReturn(adminAccess);
        when(pool.getNetwork()).thenReturn(network);

        when(pool.getMinSize()).thenReturn(1);
        when(pool.getExpectedSize()).thenReturn(1);

        when(pool.getHardware()).thenReturn(hardware);

        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);
        when(execution.getVariable(CoreProcessVariables.POOL)).thenReturn(pool);

        executeActivitiesInSequence(execution,
            EnsureKeyPairExists.class,
            EnsureSecurityGroupExists.class
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void tearDown() throws Exception {
        executeActivitiesInSequence(execution,
            DeleteSecurityGroup.class,
            DeleteKeyPair.class
        );
        super.tearDown();
    }
}
