package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.util.List;

public class HostSimple extends Host {

	public HostSimple(int id, long ram, long bw, long storage, List<Pe> peList, VmScheduler vmScheduler) {
        super(id,
                new RamProvisionerSimple((int) ram),  // Use RamProvisionerSimple for RAM provisioning
                new BwProvisionerSimple(bw),         // Use BwProvisionerSimple for bandwidth provisioning
                storage,
                peList,
                vmScheduler);

        // Additional initialization or configuration if needed
    }

    // Additional methods or overrides as needed
}