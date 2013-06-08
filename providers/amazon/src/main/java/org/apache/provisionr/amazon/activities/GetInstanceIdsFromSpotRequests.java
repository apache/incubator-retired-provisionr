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
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import java.util.ArrayList;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.provisionr.amazon.ProcessVariables;
import org.apache.provisionr.amazon.core.ProviderClientCache;
import org.apache.provisionr.api.pool.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetInstanceIdsFromSpotRequests extends AmazonActivity {

    private static final Logger LOG = LoggerFactory.getLogger(GetInstanceIdsFromSpotRequests.class);

    public GetInstanceIdsFromSpotRequests(ProviderClientCache providerClientCache) {
        super(providerClientCache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception {
        LOG.info(">> retrieving instance Ids from spot request Ids");

        @SuppressWarnings("unchecked")
        List<String> requestIds =
            (List<String>) execution.getVariable(ProcessVariables.SPOT_INSTANCE_REQUEST_IDS);
        DescribeSpotInstanceRequestsResult result = client.describeSpotInstanceRequests(
            new DescribeSpotInstanceRequestsRequest().withSpotInstanceRequestIds(requestIds));
        List<String> instanceIds = new ArrayList<String>();
        for (SpotInstanceRequest spotRequest : result.getSpotInstanceRequests()) {
            if (spotRequest.getInstanceId() != null) {
                instanceIds.add(spotRequest.getInstanceId());
            }
        }
        execution.setVariable(ProcessVariables.INSTANCE_IDS, instanceIds);
    }

}
