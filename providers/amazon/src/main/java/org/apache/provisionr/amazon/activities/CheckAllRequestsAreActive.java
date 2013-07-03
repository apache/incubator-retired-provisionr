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
import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import com.google.common.base.Predicate;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.provisionr.amazon.ProcessVariables;
import org.apache.provisionr.amazon.core.ProviderClientCache;
import org.apache.provisionr.api.pool.Pool;

public class CheckAllRequestsAreActive extends AllSpotRequestsMatchPredicate {

    public static class RequestIsActive implements Predicate<SpotInstanceRequest> {

        @Override
        public boolean apply(SpotInstanceRequest request) {
            return "active".equalsIgnoreCase(request.getState());
        }

        @Override
        public String toString() {
            return "RequestIsActive{}";
        }
    }

    public CheckAllRequestsAreActive(ProviderClientCache cache) {
        super(cache, ProcessVariables.ALL_SPOT_INSTANCE_REQUESTS_ACTIVE, new RequestIsActive());
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) {
        super.execute(client, pool, execution);
    }
}
