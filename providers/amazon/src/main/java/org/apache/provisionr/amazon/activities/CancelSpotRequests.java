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

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.CancelSpotInstanceRequestsRequest;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.provisionr.amazon.AmazonProvisionr;
import org.apache.provisionr.amazon.ProcessVariables;
import org.apache.provisionr.amazon.core.ProviderClientCache;
import org.apache.provisionr.api.pool.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CancelSpotRequests extends AmazonActivity {

    public static final Logger LOG = LoggerFactory.getLogger(AmazonProvisionr.class);

    public CancelSpotRequests(ProviderClientCache providerClientCache) {
        super(providerClientCache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) {
        @SuppressWarnings("unchecked")
        List<String> requests = (List<String>) execution.getVariable(ProcessVariables.SPOT_INSTANCE_REQUEST_IDS);
        checkNotNull(requests, "process variable '{}' not found", ProcessVariables.SPOT_INSTANCE_REQUEST_IDS);
        if (requests.size() > 0) {
            client.cancelSpotInstanceRequests(new CancelSpotInstanceRequestsRequest()
                .withSpotInstanceRequestIds(requests));
        }
    }
}
