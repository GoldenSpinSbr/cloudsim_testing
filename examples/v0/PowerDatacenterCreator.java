package v0;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G4Xeon3040;

import org.cloudbus.cloudsim.Datacenter;
//import org.cloudbus.cloudsim.DatacenterCharacteristics;

//import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerDatacenterTotal;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicySimple;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.PeSimple;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.power.models.CustomPowerModel;
import org.cloudbus.cloudsim.power.models.PowerModel;

public class PowerDatacenterCreator {
	public static PowerDatacenterTotal createDatacenter(int host_num, int host_pes, int host_mips, 
			int host_ram, 
		int host_bw) throws Exception {
    	System.out.println("========== Create Datacenter Simple ==========");
    	/*
    	List<PowerHost> hostList = new ArrayList<>();
    	//CustomPowerModel customPowerModel = new CustomPowerModel();
    	
    	for (int i=0; i<host_num; i++) {
	    	List<Pe> peList = new ArrayList<>();
	        for (int j=0; j<host_pes; j++) {
	            peList.add(new PeSimple((int) j, (double) host_mips));
	        }
	        int ram = host_ram;
	        int bw = host_bw;
	        long storage = 100000;
	        PowerHost host = new PowerHost(
	            i,
	            new RamProvisionerSimple(ram),
	            new BwProvisionerSimple(bw),
	            storage,
	            peList,
	            new VmSchedulerTimeShared(peList),
	            //customPowerModel
	            new PowerModelSpecPowerHpProLiantMl110G4Xeon3040()
	            //null
	        );
	        hostList.add(host);
    	}
    	*/
    	
    	List<Host> hostList = new ArrayList<>();
    	//CustomPowerModel customPowerModel = new CustomPowerModel();
    	
    	for (int i=0; i<host_num; i++) {
	    	List<Pe> peList = new ArrayList<>();
	        for (int j=0; j<host_pes; j++) {
	            peList.add(new PeSimple((int) j, (double) host_mips));
	        }
	        int ram = host_ram;
	        int bw = host_bw;
	        long storage = 100000;
	        Host host = new PowerHost(
	            i,
	            new RamProvisionerSimple(ram),
	            new BwProvisionerSimple(bw),
	            storage,
	            peList,
	            new VmSchedulerTimeShared(peList),
	            //customPowerModel
	            new PowerModelSpecPowerHpProLiantMl110G4Xeon3040()
	            //null
	        );
	        hostList.add(host);
    	}

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            "x86", "Linux", "Xen", hostList, 10.0, 1.0, 0.05, 0.001, 0.0);
        
        ///characteristics.setPowerModel(customPowerModel); // Set power model for the datacenter
        
        VmAllocationPolicySimple vmAllocationPolicy = new VmAllocationPolicySimple(hostList);
        //PowerVmAllocationPolicySimple vmAllocationPolicy = new PowerVmAllocationPolicySimple(hostList);
        
        List<Storage> storageList = new ArrayList<>();

        //Datacenter dc = new Datacenter(
		//	"Datacenter0",
	    //    characteristics,
	    //    vmAllocationPolicy,
	    //    storageList,
	    //    0);//SCHEDULING_INTERNAL);
        
        PowerDatacenterTotal dc = new PowerDatacenterTotal(
    		"Datacenter0",
    	    characteristics,
    	    vmAllocationPolicy,
    	    //null,
    	    storageList,
    	    //0.0
    	    2.0
    	);

        return dc;
    }
}






