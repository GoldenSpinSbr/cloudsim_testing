package v0;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.power.PowerVm;

public class PowerVMCreator {
	public static List<PowerVm> createVMs(int broker_id, int vm_num, int vm_mips, int vm_pe, int vm_ram, 
		int vm_bw, int vm_size) {
    	System.out.println("========== Create Vms ==========");
    	List<PowerVm> list = new ArrayList<>();//VMS);
        for (int i = 0; i < vm_num; i++) {
            PowerVm vm = new PowerVm(
                i+1000, // id
                broker_id, // userId
                vm_mips, // mips
                vm_pe, // pesNumber
                vm_ram, // ram
                vm_bw, // bw
                vm_size, // size
                0, // priority
                "Xen", // vmm
                //new CloudletSchedulerSpaceShared(). 
                new CloudletSchedulerTimeShared(),// cloudletScheduler
                1.0 // schedulingInterval
            );
            
            list.add(vm);
        }
        return list;
    }
}

