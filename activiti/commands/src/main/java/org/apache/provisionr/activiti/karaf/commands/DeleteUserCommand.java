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

package org.apache.provisionr.activiti.karaf.commands;

import org.activiti.engine.IdentityService;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

@Command(scope = "activiti", name = "delete-user", description = "Delete existing Activiti user")
public class DeleteUserCommand extends ActivitiCommand {

    @Option(name = "--id", description = "User ID")
    private String id;

    @Override
    protected Object doExecute() {
        if (getProcessEngine() == null) {
            throw new NullPointerException("Please configure a processEngine instance for this command");
        }
        IdentityService identityService = getProcessEngine().getIdentityService();
        identityService.deleteUser(id);
        return null;
    }
}
