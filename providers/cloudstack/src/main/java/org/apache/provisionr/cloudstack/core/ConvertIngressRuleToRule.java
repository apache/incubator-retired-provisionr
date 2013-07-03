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

package org.apache.provisionr.cloudstack.core;

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkNotNull;
import org.apache.provisionr.api.network.Protocol;
import org.apache.provisionr.api.network.Rule;
import org.jclouds.cloudstack.domain.IngressRule;

public enum ConvertIngressRuleToRule implements Function<IngressRule, Rule> {
    FUNCTION;

    @Override
    public Rule apply(IngressRule ingressRule) {
        checkNotNull(ingressRule, "ingressRule is null");

        Rule rule;
        if (ingressRule.getProtocol().equalsIgnoreCase("icmp")) {
            rule = Rule.builder()
                .anySource()
                .protocol(Protocol.valueOf(ingressRule.getProtocol().toUpperCase()))
                .cidr(ingressRule.getCIDR())
                .createRule();
        } else {
            rule = Rule.builder()
                .anySource()
                .protocol(Protocol.valueOf(ingressRule.getProtocol().toUpperCase()))
                .ports(ingressRule.getStartPort(), ingressRule.getEndPort())
                .cidr(ingressRule.getCIDR())
                .createRule();
        }
        return rule;
    }
}
