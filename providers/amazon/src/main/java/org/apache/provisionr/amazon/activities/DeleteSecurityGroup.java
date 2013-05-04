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

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import org.apache.provisionr.amazon.core.ErrorCodes;
import org.apache.provisionr.amazon.core.ProviderClientCache;
import org.apache.provisionr.amazon.core.SecurityGroups;
import org.apache.provisionr.api.pool.Pool;
import com.google.common.base.Throwables;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteSecurityGroup extends AmazonActivity {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteSecurityGroup.class);

    public DeleteSecurityGroup(ProviderClientCache cache) {
        super(cache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) {
        final String groupName = SecurityGroups.formatNameFromBusinessKey(execution.getProcessBusinessKey());
        try {
            LOG.info(">> Deleting Security Group {}", groupName);

            client.deleteSecurityGroup(new DeleteSecurityGroupRequest().withGroupName(groupName));

        } catch (AmazonServiceException e) {
            if (e.getErrorCode().equals(ErrorCodes.SECURITY_GROUP_NOT_FOUND)) {
                LOG.info("<< Security Group {} not found. Ignoring this error.", groupName);
            } else {
                throw Throwables.propagate(e);
            }
        }
    }
}
