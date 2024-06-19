package org.cloudbus.cloudsim;

public class VmSimple extends Vm {

    public VmSimple(int id, int userId, double mips, int numberOfPes, int ram, long bw, long size, String vmm, CloudletScheduler cloudletScheduler) {
        super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
        setInMigration(true); // Set inMigration to true to indicate VM is being instantiated
    }

    // Add more constructors if needed
}
