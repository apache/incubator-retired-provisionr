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

import org.apache.provisionr.api.access.AdminAccess;
import org.apache.provisionr.api.pool.Pool;
import org.apache.provisionr.core.activities.PuppetActivity;
import org.apache.provisionr.test.TestConstants;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SetupAdminAccessTest {

    @Test
    public void testCreatePuppetScript() throws Exception {
        Pool pool = mock(Pool.class);

        final AdminAccess adminAccess = AdminAccess.builder()
            .privateKey(TestConstants.PRIVATE_KEY)
            .publicKey(TestConstants.PUBLIC_KEY)
            .username(System.getProperty("user.name"))
            .createAdminAccess();

        when(pool.getAdminAccess()).thenReturn(adminAccess);

        PuppetActivity activity = new SetupAdminAccess();
        String content = activity.createPuppetScript(pool, null);

        final String username = adminAccess.getUsername();

        assertThat(content).contains(username)
            .contains(adminAccess.getPublicKey().split(" ")[1])
            .contains(String.format("user { \"%s\":", username))
            .contains(String.format("file { \"/home/%s/.ssh\":", username));
    }
}
