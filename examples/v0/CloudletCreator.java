package v0;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.UtilizationModelNull;

public class CloudletCreator {
	public static List<Cloudlet> createCloudlets(int broker_id, int cloudlet_num, int cloudlet_len, int cloudlet_pes) {
    	System.out.println("========== Create Cloudlets ==========");
        List<Cloudlet> list = new ArrayList<> ();
        //UtilizationModel utilization_cpu = new UtilizationModelFull();
        //UtilizationModel utilization_ram = new UtilizationModelNull();
        //UtilizationModel utilization_bw = new UtilizationModelNull();
        for (int i=0; i<cloudlet_num; i++) {
        	UtilizationModel utilization_cpu = new UtilizationModelFull();
            UtilizationModel utilization_ram = new UtilizationModelNull();
            UtilizationModel utilization_bw = new UtilizationModelNull();
            long length = i < cloudlet_num / 2 ? cloudlet_len : cloudlet_len * 2;
            //System.out.println("------>");
            //System.out.println(cloudlet_pes);
            Cloudlet cloudlet = new Cloudlet(
                i, // ID
                length, // Length
                cloudlet_pes, // Number of PEs
                100, // File size (bytes)
                100, // Output size (bytes)
                utilization_cpu, // Utilization model CPU
                utilization_ram, // Utilization model RAM
                utilization_bw // Utilization model BW
            );
            cloudlet.setUserId(broker_id);
            //cloudlet.setVmId(1000); 
            list.add(cloudlet);
        }
        
        return list;
    }
}
	
	
	