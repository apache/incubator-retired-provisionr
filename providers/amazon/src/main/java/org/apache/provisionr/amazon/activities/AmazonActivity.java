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
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.provisionr.amazon.core.ProviderClientCache;
import org.apache.provisionr.api.pool.Pool;
import org.apache.provisionr.core.CoreProcessVariables;

public abstract class AmazonActivity implements JavaDelegate {

    private final ProviderClientCache providerClientCache;

    protected AmazonActivity(ProviderClientCache providerClientCache) {
        this.providerClientCache = checkNotNull(providerClientCache, "providerClientCache is null");
    }

    /**
     * Amazon specific activity implementation
     *
     * @param client    Amazon client created using the pool provider
     * @param pool      Virtual machines pool description
     * @param execution Activiti execution context
     */
    public abstract void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception;

    /**
     * Wrap the abstract {@code execute} method with the logic that knows how to create the Amazon client
     */
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Pool pool = (Pool) execution.getVariable(CoreProcessVariables.POOL);
        checkNotNull(pool, "Please add the pool description as a process " +
            "variable with the name '%s'.", CoreProcessVariables.POOL);

        execute(providerClientCache.getUnchecked(pool.getProvider()), pool, execution);
    }

    protected List<Instance> collectInstancesFromReservations(List<Reservation> reservation) {
        /* Make a copy as an ArrayList to force lazy collection evaluation */
        List<List<Instance>> allInstances = Lists.transform(reservation, new Function<Reservation, List<Instance>>() {
            @Override
            public List<Instance> apply(Reservation reservation) {
                return reservation.getInstances();
            }
        });

        return Lists.newArrayList(Iterables.concat(allInstances));
    }
}
