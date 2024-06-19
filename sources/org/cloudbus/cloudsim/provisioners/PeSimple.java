package org.cloudbus.cloudsim.provisioners;
import org.cloudbus.cloudsim.Pe;

public class PeSimple extends Pe {

    private long capacity;

    public PeSimple(int id, PeProvisioner peProvisioner, long capacity) {
        super(id, peProvisioner);
        this.capacity = capacity;
    }

    //@Override
    //public long getCapacity() {
    //    return capacity;
    //}
}
