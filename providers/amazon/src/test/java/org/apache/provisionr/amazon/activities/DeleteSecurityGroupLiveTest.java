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
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.provisionr.amazon.core.SecurityGroups;
import org.apache.provisionr.api.pool.Pool;
import org.apache.provisionr.core.CoreProcessVariables;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import org.junit.After;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeleteSecurityGroupLiveTest extends AmazonActivityLiveTest<DeleteSecurityGroup> {

    private final String SECURITY_GROUP_NAME = SecurityGroups.formatNameFromBusinessKey(BUSINESS_KEY);

    @After
    public void tearDown() throws Exception {
        quietlyDeleteSecurityGroupIfExists(SECURITY_GROUP_NAME);
        super.tearDown();
    }

    @Test
    public void testDeleteSecurityGroup() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        Pool pool = mock(Pool.class);

        when(pool.getProvider()).thenReturn(provider);
        when(execution.getVariable(CoreProcessVariables.POOL)).thenReturn(pool);
        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);

        client.createSecurityGroup(new CreateSecurityGroupRequest()
            .withGroupName(SECURITY_GROUP_NAME).withDescription("Just for test"));

        activity.execute(execution);

        try {
            client.describeSecurityGroups(new DescribeSecurityGroupsRequest()
                .withGroupNames(SECURITY_GROUP_NAME));
            fail("Did not throw AmazonServiceException as expected");

        } catch (AmazonServiceException e) {
            assertThat(e.getErrorCode()).isEqualTo("InvalidGroup.NotFound");
        }
    }
}
