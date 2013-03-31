package org.apache.provisionr.amazon.activities;

import org.activiti.engine.delegate.DelegateExecution;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import org.apache.provisionr.amazon.ProcessVariables;
import org.apache.provisionr.amazon.core.ProviderClientCache;
import org.apache.provisionr.api.pool.Pool;
import com.google.common.base.Predicate;

public class CheckAllRequestsAreActive extends AllSpotRequestsMatchPredicate {

    public static class RequestIsActive implements Predicate<SpotInstanceRequest> {

        @Override
        public boolean apply(SpotInstanceRequest request) {
            return "active".equalsIgnoreCase(request.getState());
        }

        @Override
        public String toString() {
            return "RequestIsActive{}";
        }
    }

    public CheckAllRequestsAreActive(ProviderClientCache cache) {
        super(cache, ProcessVariables.ALL_SPOT_INSTANCE_REQUESTS_ACTIVE, new RequestIsActive());
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception {
        super.execute(client, pool, execution);        
    }
}
