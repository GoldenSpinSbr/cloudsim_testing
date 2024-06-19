package power_efficient;

import org.cloudbus.cloudsim.Vm;
import java.util.List;

public class VmList {
    /**
     * Gets a VM by its ID from a given list of VMs.
     *
     * @param vmList the list of VMs
     * @param vmId the VM ID
     * @return the VM with the specified ID, or null if not found
     */
    public static Vm getById(List<Vm> vmList, int vmId) {
        for (Vm vm : vmList) {
            if (vm.getId() == vmId) {
                return vm;
            }
        }
        return null;
    }
}

