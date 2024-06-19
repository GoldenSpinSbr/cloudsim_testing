package org.cloudbus.cloudsim.power;

import org.cloudbus.cloudsim.core.SimEvent;

import java.util.List;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;

public class CustomDatacenter extends PowerDatacenter {

    public CustomDatacenter(String name, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval) throws Exception {
        super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
    }

    @Override
    protected void processCloudletSubmit(SimEvent ev, boolean ack) {
        Log.printLine(CloudSim.clock() + ": " + getName() + ": Processing cloudlet submit...");
        super.processCloudletSubmit(ev, ack);
        Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet submit processed.");
    }

    @Override
    protected void updateCloudletProcessing() {
        Log.printLine(CloudSim.clock() + ": " + getName() + ": Updating cloudlet processing...");
        super.updateCloudletProcessing();
        Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet processing updated.");
    }
}
