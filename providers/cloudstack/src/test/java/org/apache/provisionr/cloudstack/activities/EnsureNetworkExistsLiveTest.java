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

package org.apache.provisionr.cloudstack.activities;

import java.util.NoSuchElementException;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.provisionr.api.pool.Pool;
import org.apache.provisionr.cloudstack.NetworkOptions;
import org.apache.provisionr.cloudstack.ProcessVariables;
import org.apache.provisionr.cloudstack.core.Networks;
import org.apache.provisionr.core.CoreProcessVariables;
import org.apache.provisionr.test.ProcessVariablesCollector;
import static org.fest.assertions.api.Assertions.assertThat;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.features.NetworkClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsureNetworkExistsLiveTest extends CloudStackActivityLiveTest<EnsureNetworkExists> {

    private final static Logger LOG = LoggerFactory.getLogger(EnsureNetworkExistsLiveTest.class);
    private final String networkName = Networks.formatNameFromBusinessKey(BUSINESS_KEY);
    private DelegateExecution execution;
    private Pool pool;
    private ProcessVariablesCollector collector;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        logNetworks();
        initMocks();
    }

    private void initMocks() {
        execution = mock(DelegateExecution.class);
        pool = mock(Pool.class);

        when(pool.getProvider()).thenReturn(provider);
        when(execution.getVariable(CoreProcessVariables.POOL)).thenReturn(pool);
        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);
        when(pool.getNetwork()).thenReturn(org.apache.provisionr.api.network.Network.builder()
            .type("provided")
            .createNetwork());
        collector = new ProcessVariablesCollector();
        collector.install(execution);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        deleteNetworkIfExists();
        logNetworks();
        super.tearDown();
    }

    private void deleteNetworkIfExists() {
        try {
            Network network = Networks.getByName(context.getApi(), networkName);
            // this will not wait for the network to be deleted. operation will fail if network is used.
            context.getApi().getNetworkClient().deleteNetwork(network.getId());
        } catch (NoSuchElementException e) {
            LOG.info("Network {} does not exist", networkName);
        }
    }

    @Test
    public void testEnsureNetworkExistsByCreatingTheNetwork() throws Exception {
        activity.execute(execution);

        assertThat(collector.getVariable(ProcessVariables.NETWORK_ID)).isNotNull();

        Network network = Networks.getByName(context.getApi(), networkName);
        assertThat(network.getName()).isEqualToIgnoringCase(networkName);
        String networkId = network.getId();
        // second run should not create a new network
        activity.execute(execution);

        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);
        network = Networks.getByName(context.getApi(), networkName);
        assertThat(network.getId()).isEqualTo(networkId);
    }

    @Test
    public void testEnsureNetworkExistsWithProvidedExistingNetwork() throws Exception {
        final String networkId = "network-id-0123";
        final CloudStackClient mockClient = mock(CloudStackClient.class);
        final NetworkClient mockNetworkClient = mock(NetworkClient.class);
        final Network mockNetwork = mock(Network.class);
        final org.apache.provisionr.api.network.Network network = org.apache.provisionr.api.network.Network.builder()
            .option(NetworkOptions.EXISTING_NETWORK_ID, networkId).createNetwork();

        execution.setVariable(ProcessVariables.NETWORK_ID, networkId);

        when(pool.getNetwork()).thenReturn(network);
        when(mockClient.getNetworkClient()).thenReturn(mockNetworkClient);
        when(mockNetworkClient.getNetwork(networkId)).thenReturn(mockNetwork);
        when(mockNetwork.getId()).thenReturn(networkId);

        activity.execute(mockClient, pool, execution);
        assertThat(collector.getVariable(ProcessVariables.NETWORK_ID)).isEqualTo(networkId);
    }
}
