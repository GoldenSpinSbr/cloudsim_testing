package v0;

import org.cloudbus.cloudsim.power.PowerDatacenterBroker;

public class PowerBrokerCreator {
    public static PowerDatacenterBroker createBroker() {
        PowerDatacenterBroker broker = null;
        try {
            broker = new PowerDatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return broker;
    }
}



