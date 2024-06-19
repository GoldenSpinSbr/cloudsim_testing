package v0;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.PeSimple;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.power.models.CustomPowerModel;

public class DatacenterCreator {
	public static Datacenter createDatacenter(int host_num, int host_pes, int host_mips, int host_ram, 
		int host_bw) throws Exception {
    	System.out.println("========== Create Datacenter Simple ==========");
    	List<Host> hostList = new ArrayList<>();
    	for (int i=0; i<host_num; i++) {
	    	List<Pe> peList = new ArrayList<>();
	        for (int j=0; j<host_pes; j++) {
	            peList.add(new PeSimple((int) j, (double) host_mips));
	        }
	        int ram = host_ram;
	        int bw = host_bw;
	        long storage = 100000;
	        Host host = new Host(
	            i,
	            new RamProvisionerSimple(ram),
	            new BwProvisionerSimple(bw),
	            storage,
	            peList,
	            new VmSchedulerTimeShared(peList)
	        );
	        hostList.add(host);
    	}

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            "x86", "Linux", "Xen", hostList, 10.0, 1.0, 0.05, 0.001, 0.0);
        
        //CustomPowerModel customPowerModel = new CustomPowerModel();
        //characteristics.setPowerModel(customPowerModel); // Set power model for the datacenter
        
        VmAllocationPolicySimple vmAllocationPolicy = new VmAllocationPolicySimple(hostList);
        List<Storage> storageList = new ArrayList<>();

        Datacenter dc = new Datacenter(
			"Datacenter0",
	        characteristics,
	        vmAllocationPolicy,
	        storageList,
	        0);//SCHEDULING_INTERNAL);
        return dc;
    }
}



