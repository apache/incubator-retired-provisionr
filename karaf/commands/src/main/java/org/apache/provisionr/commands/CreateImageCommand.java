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

package org.apache.provisionr.commands;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import org.apache.felix.gogo.commands.Command;
import org.apache.provisionr.api.Provisionr;
import org.apache.provisionr.api.hardware.Hardware;
import org.apache.provisionr.api.pool.Pool;
import org.apache.provisionr.api.software.Software;
import org.apache.provisionr.core.templates.PoolTemplate;

@Command(scope = "provisionr", name = "cache", description = "Create a cached golden image.")
public class CreateImageCommand extends CreateCommand {

    // TODO: remove this and use a provided parameter
    private static final String HARDWARE_TYPE = "t1.micro";

    public CreateImageCommand(List<Provisionr> services, List<PoolTemplate> templates,
                              String publicKeyPath, String privateKeyPath) {
        super(services, templates, publicKeyPath, privateKeyPath);
    }

    @Override
    protected Object doExecute() throws Exception {
        // Provisionr service = getService();
        // final Pool pool = createPoolOfOne(service);
        // TODO: create service.startCachingProcess(uuid, pool) in the Provisionr class

        return null;
    }

    @VisibleForTesting
    Pool createPoolOfOne(Provisionr service) {

        final Software software = Software.builder().packages(packages).createSoftware();
        final Hardware hardware = Hardware.builder().type(HARDWARE_TYPE).createHardware();

        final Pool pool = Pool.builder()
            .provider(getDefaultProvider(service).get())
            .hardware(hardware)
            .software(software)
            .network(buildNetwork(new ArrayList<Integer>()))
            .adminAccess(collectCurrentUserCredentialsForAdminAccess())
            .minSize(1)
            .expectedSize(1)
            .bootstrapTimeInSeconds(bootstrapTimeout)
            .createPool();

        return template != null ? applyTemplate(pool) : pool;

    }

}
