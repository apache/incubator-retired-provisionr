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
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.provisionr.api.Provisionr;
import org.apache.provisionr.api.hardware.BlockDevice;
import org.apache.provisionr.api.hardware.Hardware;
import org.apache.provisionr.api.pool.Pool;
import org.apache.provisionr.api.provider.Provider;
import org.apache.provisionr.api.software.Software;
import org.apache.provisionr.core.templates.PoolTemplate;

/**
 * A typical call looks like this:
 * <p/>
 * $ provisionr:create --id amazon --key web-1 --size 5 --hardware-type m1.small \
 * --port 80 --port 443 --package nginx --package gunicorn --package python-pip
 */
@Command(scope = "provisionr", name = "create", description = "Create pool of virtual machines")
public class CreatePoolCommand extends CreateCommand {

    @Option(name = "-k", aliases = "--key", description = "Unique business identifier for this pool", required = true)
    private String key;

    @Option(name = "-s", aliases = "--size", description = "Expected pool size")
    private int size = 1;

    @Option(name = "-h", aliases = "--hardware-type", description = "Virtual machine hardware type")
    private String hardwareType = "t1.micro";

    @Option(name = "--port", description = "Firewall ports that need to be open for any TCP traffic " +
        "(multi-valued). SSH (22) is always open by default.", multiValued = true)
    private List<Integer> ports = Lists.newArrayList();

    @Option(name = "--volume", description = "Block devices that will be attached to each instance. " +
        "(multi-valued) Expects the following format: [mapping]:[size in GB]. ", multiValued = true)
    private List<String> blockDeviceOptions = Lists.newArrayList();

    @Option(name = "-o", aliases = "--provider-options", description = "Provider-specific options (multi-valued). " +
        "Expects either the key=value format or just plain key. If value is not specified, defaults to 'true'. " +
        "Supported values: spotBid=x.xxx (Amazon).", multiValued = true)
    private List<String> providerOptions = Lists.newArrayList();

    @Option(name = "--image-id", description = "The id of the OS image with which the machines will be created.")
    private String imageId = "";

    @Option(name = "--cached-image", description = "If the machines will have their packages and files downloaded " +
        "or not. If creating the machines from an existent image, software might already be installed.")
    private boolean cachedImage = false;

    public CreatePoolCommand(List<Provisionr> services, List<PoolTemplate> templates,
                             String publicKeyPath, String privateKeyPath) {
        super(services, templates, publicKeyPath, privateKeyPath);
    }

    @Override
    protected Object doExecute() {
        checkArgument(size > 0, "size should be a positive integer");

        Provisionr service = getService();
        final Pool pool = createPoolFromArgumentsAndServiceDefaults(service);
        final String processInstanceId = service.startPoolManagementProcess(key, pool);
        return String.format("Pool management process started (id: %s)", processInstanceId);
    }

    Pool createPoolFromArgumentsAndServiceDefaults(Provisionr service) {
        final Optional<Provider> defaultProvider = getDefaultProvider(service);

        /* append the provider options that were passed in and rebuild the default provider */
        // TODO: this currently does not support overriding default options, it will throw an exception
        Map<String, String> options = ImmutableMap.<String, String>builder()
            .putAll(defaultProvider.get().getOptions())     // default options
            .putAll(parseProviderOptions(providerOptions))  // options added by the user
            .build();
        Provider provider = defaultProvider.get().toBuilder().options(options).createProvider();

        final Software software = Software.builder()
            .packages(packages)
            .imageId(imageId)
            .cachedImage(cachedImage)
            .createSoftware();

        final Hardware hardware = Hardware.builder()
            .type(hardwareType)
            .blockDevices(parseBlockDeviceOptions(blockDeviceOptions))
            .createHardware();

        final Pool pool = Pool.builder()
            .provider(provider)
            .hardware(hardware)
            .software(software)
            .network(buildNetwork(ports))
            .adminAccess(collectCurrentUserCredentialsForAdminAccess())
            .minSize(size)
            .expectedSize(size)
            .bootstrapTimeInSeconds(bootstrapTimeout)
            .createPool();

        return template != null ? applyTemplate(pool) : pool;
    }

    private List<BlockDevice> parseBlockDeviceOptions(List<String> options) {
        List<BlockDevice> result = Lists.newArrayList();
        for (String option : options) {
            String[] parts = option.split(":");
            checkArgument(parts.length == 2, "The arguments for the --volume option must be mapping:size");
            result.add(BlockDevice.builder().name(parts[0]).size(Integer.parseInt(parts[1])).createBlockDevice());
        }
        return result;
    }

    private Map<String, String> parseProviderOptions(List<String> providerOptions) {
        Map<String, String> result = Maps.newHashMap();
        for (String option : providerOptions) {
            String[] parts = option.split("=");
            String value = parts.length > 1 ? parts[1] : "true";
            result.put(parts[0], value);
        }
        return result;
    }

    @VisibleForTesting
    void setKey(String key) {
        this.key = checkNotNull(key, "key is null");
    }

    @VisibleForTesting
    void setSize(int size) {
        checkArgument(size > 0, "size should be a positive number");
        this.size = size;
    }

    @VisibleForTesting
    void setHardwareType(String hardwareType) {
        this.hardwareType = checkNotNull(hardwareType, "hardwareType is null");
    }

    @VisibleForTesting
    void setPorts(List<Integer> ports) {
        this.ports = ImmutableList.copyOf(ports);
    }

    @VisibleForTesting
    void setProviderOptions(List<String> providerOptions) {
        this.providerOptions = ImmutableList.copyOf(providerOptions);
    }

    @VisibleForTesting
    void setBlockDeviceOptions(List<String> blockDeviceOptions) {
        this.blockDeviceOptions = ImmutableList.copyOf(blockDeviceOptions);
    }

    @VisibleForTesting
    void setCachedImage(boolean cachedImage) {
        this.cachedImage = cachedImage;
    }
}
