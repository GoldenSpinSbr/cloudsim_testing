package v0;

import org.cloudbus.cloudsim.DatacenterBroker;

public class BrokerCreator {
    public static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return broker;
    }
}



