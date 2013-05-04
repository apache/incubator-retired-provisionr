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

package org.apache.provisionr.api.network;

import org.apache.provisionr.api.util.BuilderWithOptions;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Set;

public class NetworkBuilder extends BuilderWithOptions<NetworkBuilder> {

    private String type = "default";
    private ImmutableSet.Builder<Rule> ingress = ImmutableSet.builder();

    @Override
    protected NetworkBuilder getThis() {
        return this;
    }

    public NetworkBuilder type(String type) {
        this.type = checkNotNull(type, "type is null");
        return this;
    }

    public NetworkBuilder ingress(Set<Rule> incoming) {
        this.ingress = ImmutableSet.<Rule>builder().addAll(incoming);
        return this;
    }

    public NetworkBuilder addRules(Rule... rules) {
        return addRules(Lists.newArrayList(rules));
    }

    public NetworkBuilder addRules(Iterable<Rule> rules) {
        this.ingress.addAll(rules);
        return this;
    }

    public Network createNetwork() {
        return new Network(type, ingress.build(), buildOptions());
    }
}