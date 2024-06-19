package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.Pe;

public class PeSimple extends Pe {

    // Constructor accepting int id and double mips
    public PeSimple(int id, double mips) {
        super(id, new PeProvisionerSimple(mips)); // Initialize with PeProvisionerSimple
    }
}
