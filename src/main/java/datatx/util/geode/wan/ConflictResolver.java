package datatx.util.geode.wan;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.Operation;
import org.apache.geode.cache.util.GatewayConflictHelper;
import org.apache.geode.cache.util.GatewayConflictResolver;
import org.apache.geode.cache.util.TimestampedEntryEvent;
import org.apache.geode.pdx.PdxInstance;

public class ConflictResolver implements GatewayConflictResolver, Declarable {
	private final Logger LOG = LogManager.getLogger(ConflictResolver.class);

	@Override
	public void onEvent(TimestampedEntryEvent event, GatewayConflictHelper helper) {
		if (event.getOperation() == Operation.UPDATE
				&& event.getNewDistributedSystemID() != event.getOldDistributedSystemID()) {
			if (event.getNewTimestamp() > event.getOldTimestamp()) {
				helper.changeEventValue(event.getNewValue());
				LOG.warn("Update conflict resolved for Region: " + event.getRegion().getName()
						+ " Key: " + event.getKey() + "/n"
						+ " New Timestamp: " + new Date(event.getNewTimestamp()) 
						+ " New Value: " + ((PdxInstance) event.getNewValue()).toString()
						+ "/n"
						+ " Old Timestamp: " + new Date(event.getOldTimestamp())  
						+ " Old Value: " + ((PdxInstance) event.getOldValue()).toString());
			}
		}
	}

}
