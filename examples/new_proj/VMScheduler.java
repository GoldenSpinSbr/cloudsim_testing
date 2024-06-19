package new_proj;

import java.util.List;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

public class VMScheduler {

    /**
     * Schedule VMs onto hosts based on CPU utilization.
     * 
     * @param datacenter The datacenter where VMs will be scheduled.
     * @param vmList     The list of VMs to be scheduled.
     * @param cpuThreshold The CPU utilization threshold for host selection.
     */
    public static void scheduleVMs(Datacenter datacenter, List<Vm> vmList, double cpuThreshold) {
        List<Host> hostList = datacenter.getHostList();

        // Sort hosts based on current CPU utilization
        hostList.sort((h1, h2) -> Double.compare(getUtilizationOfCpu(h1), getUtilizationOfCpu(h2)));

        // Iterate through each VM and allocate to the best host
        for (Vm vm : vmList) {
            Host bestHost = findBestHost(hostList, vm, cpuThreshold);

            if (bestHost != null) {
                bestHost.vmCreate(vm); // Allocate VM to the best host
                vm.setHost(bestHost); // Set VM's host attribute
            }
        }
    }

    /**
     * Find the best host for a VM based on current CPU utilization and CPU threshold.
     * 
     * @param hostList     The list of hosts available in the datacenter.
     * @param vm           The VM to be scheduled.
     * @param cpuThreshold The CPU utilization threshold for host selection.
     * @return The best host for the VM, or null if no suitable host is found.
     */
    private static Host findBestHost(List<Host> hostList, Vm vm, double cpuThreshold) {
        // Iterate through hosts and find the first suitable host based on CPU utilization
        for (Host host : hostList) {
            if (host.isSuitableForVm(vm) && getUtilizationOfCpu(host) < cpuThreshold) {
                return host; // Return the first suitable host with CPU utilization below threshold
            }
        }
        return null; // No suitable host found
    }

    /**
     * Calculate the current CPU utilization of a host.
     * 
     * @param host The host for which CPU utilization is calculated.
     * @return The CPU utilization (as a fraction between 0 and 1).
     */
    private static double getUtilizationOfCpu(Host host) {
        double totalMips = 0;
        double usedMips = 0;

        // Calculate total and used MIPS for all VMs on the host
        for (Vm vm : host.getVmList()) {
            totalMips += vm.getMips();
            usedMips += vm.getTotalUtilizationOfCpuMips(CloudSim.clock());
        }

        // Calculate CPU utilization as the ratio of used MIPS to total MIPS
        if (totalMips == 0) {
            return 0; // No VMs allocated on the host
        } else {
            return usedMips / totalMips; // CPU utilization as a fraction
        }
    }
}
