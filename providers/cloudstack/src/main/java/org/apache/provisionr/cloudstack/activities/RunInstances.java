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

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.provisionr.api.pool.Pool;
import org.apache.provisionr.cloudstack.ProviderOptions;
import org.apache.provisionr.cloudstack.core.KeyPairs;
import org.apache.provisionr.cloudstack.core.Networks;
import org.apache.provisionr.cloudstack.core.VirtualMachines;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunInstances extends CloudStackActivity {

    public static final Logger LOG = LoggerFactory.getLogger(RunInstances.class);

    @Override
    public void execute(CloudStackClient cloudStackClient, Pool pool, DelegateExecution execution) {
        final String businessKey = execution.getProcessBusinessKey();

        final String keyPairName = KeyPairs.formatNameFromBusinessKey(businessKey);

        final String zoneId = pool.getOptions().get(ProviderOptions.ZONE_ID);
        final String templateId = pool.getSoftware().getImageId();
        final String serviceOffering = pool.getHardware().getType();

        LOG.info("Starting instances!");

        cloudStackClient.getVirtualMachineClient().deployVirtualMachineInZone(
            zoneId, serviceOffering, templateId,
            DeployVirtualMachineOptions.Builder
                .displayName(businessKey)
                .group(businessKey)
                .networkId(Networks.formatNameFromBusinessKey(businessKey))
                .keyPair(keyPairName)
                .name(businessKey));

        VirtualMachines.waitForVMtoStart(cloudStackClient, businessKey);
    }
}
