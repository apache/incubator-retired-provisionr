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

package org.apache.provisionr.amazon.functions;

import com.amazonaws.services.ec2.model.IpPermission;
import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import org.apache.provisionr.api.network.Protocol;
import org.apache.provisionr.api.network.Rule;
import org.apache.provisionr.api.network.RuleBuilder;

public enum ConvertIpPermissionToRule implements Function<IpPermission, Rule> {
    FUNCTION;

    @Override
    public Rule apply(IpPermission ipPermission) {
        checkNotNull(ipPermission, "ipPermission is null");

        final RuleBuilder builder = Rule.builder().cidr(getOnlyElement(ipPermission.getIpRanges()))
            .protocol(Protocol.valueOf(ipPermission.getIpProtocol().toUpperCase()));

        if (!ipPermission.getIpProtocol().equalsIgnoreCase("icmp")) {
            builder.ports(ipPermission.getFromPort(), ipPermission.getToPort());
        }

        return builder.createRule();
    }
}
