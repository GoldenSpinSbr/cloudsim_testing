package v0;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Vm;

public class VMCreator {
	public static List<Vm> createVMs(int broker_id, int vm_num, int vm_mips, int vm_pe, int vm_ram, 
		int vm_bw, int vm_size) {
    	System.out.println("========== Create Vms ==========");
    	List<Vm> list = new ArrayList<>();//VMS);
        for (int i = 0; i < vm_num; i++) {
            Vm vm = new Vm(
                i+1000, // VM ID
                broker_id, // User ID
                vm_mips, // MIPS
                vm_pe, // Number of PEs
                vm_ram, // RAM in MB
                vm_bw, // Bandwidth in Mbps
                vm_size, // Size in MB
                "Xen", // VMM name
                new CloudletSchedulerSpaceShared()
                //new CloudletSchedulerTimeShared()
            );
            list.add(vm);
        }
        return list;
    }
}




