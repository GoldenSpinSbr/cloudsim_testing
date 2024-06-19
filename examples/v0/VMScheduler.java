package v0;

import java.util.List;
//import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

public class VMScheduler {

    public static void scheduleVMsBestHost(PowerDatacenter datacenter, List<Vm> vmList, 
    		double cpuThreshold) {
        List<PowerHost> hostList = datacenter.getHostList();

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

    private static Host findBestHost(List<PowerHost> hostList, Vm vm, double cpuThreshold) {
        // Iterate through hosts and find the first suitable host based on CPU utilization
        for (PowerHost host : hostList) {
            if (host.isSuitableForVm(vm) && getUtilizationOfCpu(host) < cpuThreshold) {
                return host; // Return the first suitable host with CPU utilization below threshold
            }
        }
        return null; // No suitable host found
    }
    
    public static void scheduleVMsRoundRobin(PowerDatacenter datacenter, List<Vm> vmList) {
        List<PowerHost> hostList = datacenter.getHostList();
        int hostIndex = 0;  // Start with the first host

        // Iterate through each VM and assign it to the next host in round-robin fashion
        for (Vm vm : vmList) {
            Host host = hostList.get(hostIndex);
            
            // Assign VM to the current host
            host.vmCreate(vm);
            vm.setHost(host);
            
            // Move to the next host in a circular manner
            hostIndex = (hostIndex + 1) % hostList.size();
        }
    }
    
    public static void scheduleVMsMaxmin(PowerDatacenter datacenter, List<Vm> vmList) {
    	List<PowerHost> hostList = datacenter.getHostList();
        // Sort VMs based on MIPS (descending order)
    	vmList.sort((vm1, vm2) -> Double.compare(vm2.getMips(), vm1.getMips()));

        for (Vm vm : vmList) {
            Host bestHost = findMaxmin(hostList, vm);
            if (bestHost != null) {
                bestHost.vmCreate(vm); // Allocate VM to the best host
                vm.setHost(bestHost);   // Set VM's host attribute
            }
        }
    }

    
    private static Host findMaxmin(List<PowerHost> hostList, Vm vm) {
        Host selectedHost = null;
        double maxRemainingCapacity = Double.MIN_VALUE;

        for (PowerHost host : hostList) {
            if (host.isSuitableForVm(vm)) { // getUtilizationOfCpu(host)
                double remainingCapacity = host.getRemainingCapacity();
                if (remainingCapacity > maxRemainingCapacity) {
                    maxRemainingCapacity = remainingCapacity;
                    selectedHost = host;
                }
            }
        }
        return selectedHost;
    }

    private static double getUtilizationOfCpu(Host host) {
        // Example: Calculate CPU utilization based on VMs currently running on the host
        double totalMips = 0;
        double usedMips = 0;

        for (Vm vm : host.getVmList()) {
            totalMips += vm.getMips();
            usedMips += vm.getTotalUtilizationOfCpuMips(CloudSim.clock());
        }

        if (totalMips == 0) {
            return 0;
        }

        return usedMips / totalMips;
    }

    /*
    //Estimation
    public static void printVmCpuUtilization(List<Vm> vmList) {
        String indent = "    ";
        System.out.println();
        System.out.println("========== CLOUDLETS TUILIZATIONNS ==========");
        System.out.println("VM ID" + indent + "CLOUDLETS Utilization");

        double currentTime = CloudSim.clock();
        for (Vm vm : vmList) {
            double cpuUtilization = vm.getTotalUtilizationOfCpu(currentTime);
            System.out.println(vm.getId() + indent + cpuUtilization);
        }
        System.out.println("========== VM MIPS REQUEST ==========");
        System.out.println("VM ID" + indent + "MIPS Request");
        for (Vm vm : vmList) {
            double mipsUtilization = vm.getTotalUtilizationOfCpuMips(currentTime);
            System.out.println(vm.getId() + indent + mipsUtilization);
        }
    }
    */
}
