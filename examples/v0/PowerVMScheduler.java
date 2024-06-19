package v0;

import java.util.List;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Host;
//import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVm;

public class PowerVMScheduler {

    public static void scheduleVMs(PowerDatacenter datacenter, List<PowerVm> vmList, double cpuThreshold) {
        List<PowerHost> hostList = datacenter.getHostList();

        // Sort hosts based on current CPU utilization
        hostList.sort((h1, h2) -> Double.compare(getUtilizationOfCpu(h1), getUtilizationOfCpu(h2)));

        // Iterate through each VM and allocate to the best host
        for (PowerVm vm : vmList) {
            PowerHost bestHost = findBestHost(hostList, vm, cpuThreshold);

            if (bestHost != null) {
                bestHost.vmCreate(vm); // Allocate VM to the best host
                vm.setHost(bestHost); // Set VM's host attribute
            }
        }
    }

    private static PowerHost findBestHost(List<PowerHost> hostList, PowerVm vm, double cpuThreshold) {
        // Iterate through hosts and find the first suitable host based on CPU utilization
        for (PowerHost host : hostList) {
            if (host.isSuitableForVm(vm) && getUtilizationOfCpu(host) < cpuThreshold) {
                return host; // Return the first suitable host with CPU utilization below threshold
            }
        }
        return null; // No suitable host found
    }

    private static double getUtilizationOfCpu(PowerHost host) {
        // Example: Calculate CPU utilization based on VMs currently running on the host
        double totalMips = 0;
        double usedMips = 0;

        //for (PowerVm vm : host.getVmList()) {
        //    totalMips += vm.getMips();
        //    usedMips += vm.getTotalUtilizationOfCpuMips(CloudSim.clock());
        //}

        if (totalMips == 0) {
            return 0;
        }

        return usedMips / totalMips;
    }

    public static void printVmCpuUtilization(List<PowerVm> vmList) {
        String indent = "    ";
        System.out.println();
        System.out.println("========== VM CPU UTILIZATION ==========");
        System.out.println("VM ID" + indent + "CPU Utilization");

        double currentTime = CloudSim.clock();
        for (PowerVm vm : vmList) {
            double cpuUtilization = vm.getTotalUtilizationOfCpu(currentTime);
            System.out.println(vm.getId() + indent + cpuUtilization);
        }
    }
    
    private static PowerHost findBestHost(List<PowerHost> hostList, PowerVm vm) {
        for (PowerHost host : hostList) {
            if (host.isSuitableForVm(vm)) {
                return host;
            }
        }
        return null;
    }
}
