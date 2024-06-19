package power_efficient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.UtilizationModelStochastic;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.CustomDatacenter;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicySimple;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.power.models.PowerModel;

public class my_class {

    public static void main(String[] args) {
        try {
            // Initialize CloudSim
            CloudSim.init(1, Calendar.getInstance(), false);

            // Create a DatacenterBroker
            DatacenterBroker broker = new DatacenterBroker("Broker");

            // Create a list to hold VMs
            List<Vm> vmList = new ArrayList<>();
            // Create a VM
            int vmId = 2;
            int mips = 100; // Million Instructions Per Second
            int pesNumber = 2; // Number of Processing Elements (cores)
            int ram = 256; // VM memory (MB)
            long bw = 100; // Bandwidth (Mbps)
            long size = 1000; // Image size (MB)
            String vmm = "Xen"; // VMM name
            Vm vm = new Vm(vmId, broker.getId(), mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            // Add the VM to the list
            vmList.add(vm);
            /*
            System.out.println("VM List:");
            for (Vm v : vmList) {
                System.out.println("VM ID: " + v.getId());
                System.out.println("Broker ID: " + v.getUserId());
                System.out.println("MIPS: " + v.getMips());
                System.out.println("PEs: " + v.getNumberOfPes());
                System.out.println("RAM: " + v.getRam());
                System.out.println("Bandwidth: " + v.getBw());
                System.out.println("Size: " + v.getSize());
                System.out.println("VMM: " + v.getVmm());
                System.out.println("Cloudlet Scheduler: " + v.getCloudletScheduler().getClass().getSimpleName());
                System.out.println(); // Add a blank line for readability
            }
			*/
            // Create a list to hold Hosts
            List<PowerHost> hostList = createHostList();
            System.out.println("Host List:");
            
            PowerHost host1 = hostList.get(0); // Assuming hostList has at least one host

            // Print host details
	         System.out.println("Host ID: " + host1.getId());
	         System.out.println("RAM Capacity: " + host1.getRamProvisioner().getRam()); // Assuming getRamProvisioner() returns RamProvisionerSimple
	         System.out.println("BW Capacity: " + host1.getBwProvisioner().getBw()); // Assuming getBwProvisioner() returns BwProvisionerSimple
	         System.out.println("Storage Capacity: " + host1.getStorage());
	         // Print details of Pe instances
	         List<Pe> peList = host1.getPeList();
	         System.out.println("Number of Pe: " + peList.size());
	         for (Pe pe : peList) {
	             System.out.println(" - Pe ID: " + pe.getId() + ", MIPS Capacity: " + pe.getMips());
	         }

	         // Print details of VM Scheduler and Power Model
	         System.out.println("VmScheduler: " + host1.getVmScheduler().getClass().getSimpleName()); // Assuming getVmScheduler() returns VmSchedulerTimeShared
	         System.out.println("Power Model: " + host1.getPowerModel().getClass().getSimpleName()); // Assuming getPowerModel() returns PowerModelLinear
	         // Check if the host has VM
	         /*
            for (PowerHost host : hostList) {
                System.out.println("Host ID: " + host.getId());
                System.out.println("RAM Capacity: " + host.getRamProvisioner().getRam());
                System.out.println("BW Capacity: " + host.getBwProvisioner().getBw());
                System.out.println("Storage Capacity: " + host.getStorage());
                
                // Print details of Pe instances
                List<Pe> peList = host.getPeList();
                System.out.println("Number of Pe: " + peList.size());
                for (Pe pe : peList) {
                    System.out.println(" - Pe ID: " + pe.getId() + ", MIPS Capacity: " + pe.getMips());
                }
                
                // Print details of VM Scheduler and Power Model
                System.out.println("VmScheduler: " + host.getVmScheduler().getClass().getSimpleName());
                System.out.println(); // Add a blank line for readability
            }
            */
            // Create a PowerDatacenter
            DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                    "x86", "Linux", "Xen", hostList, 10.0, 0.0, 0.7, 0.5, 0.1);
            PowerVmAllocationPolicySimple vmAllocationPolicy = new PowerVmAllocationPolicySimple(hostList);
            
            String name = "Datacenter";
            //PowerDatacenter datacenter = new PowerDatacenter(
            //        name,
            //        characteristics,
            //        vmAllocationPolicy,
            //        new ArrayList<>(),
            //        0.0);
            CustomDatacenter datacenter = new CustomDatacenter(
                    name,
                    characteristics,
                    vmAllocationPolicy,
                    new ArrayList<>(),
                    0.0);
            
            // Register the Datacenter with CloudSim
            CloudSim.addEntity(datacenter);

            // Set the DatacenterBroker
            broker.submitVmList(vmList);

            // Submit a cloudlet
            UtilizationModel utilizationModelCpu = new UtilizationModelFull();
            UtilizationModel utilizationModelRam = new UtilizationModelFull();
            UtilizationModel utilizationModelBw = new UtilizationModelFull();
            
            Cloudlet cloudlet = new Cloudlet(1, 5, 1, 0, 0, utilizationModelCpu, utilizationModelRam, utilizationModelBw);
            //broker.submitCloudletList(List.of(cloudlet));
            
            System.out.println("Cloudlet ID: " + cloudlet.getCloudletId());
            //System.out.println("Length: " + cloudlet.getLength());
            System.out.println("PEs Number: " + cloudlet.getNumberOfPes());
            //System.out.println("File Size: " + cloudlet.getFileSize());
            //System.out.println("Output Size: " + cloudlet.getOutputSize());
            
            //cloudlet.setUserId(broker.getId());
            //cloudlet.setVmId(vmId); // Set the correct VM ID
            //broker.submitCloudletList(List.of(cloudlet));
            //cloudlet.setUserId(broker.getId());
	        //cloudlet.setVmId(vmId); // Set the correct VM ID
	        broker.submitCloudletList(List.of(cloudlet));
	        
	        CloudSim.terminateSimulation(10000);

            // Start the simulation
            Log.printLine("Starting simulation...");
            CloudSim.startSimulation();

            // Stop the simulation
            Log.printLine("Stopping simulation...");
            CloudSim.stopSimulation();

            // Print results
            //List<Cloudlet> cloudletList = broker.getCloudletReceivedList();
            //printCloudletList(cloudletList);

            // Print power usage for hosts
            Log.printLine("ok");
            
            List<Vm> vmList1 = host1.getVmList();
	         if (vmList1.isEmpty()) {
	             System.out.println("No VMs assigned to this host.");
	         } else {
	             System.out.println("VMs assigned to this host:");
	             for (Vm vm1 : vmList1) {
	                 System.out.println(" - VM ID: " + vm1.getId());
	                 System.out.println("   MIPS: " + vm1.getMips());
	                 System.out.println("   PEs Number: " + vm1.getNumberOfPes());
	                 // Add more details about the VM as needed
	             }
	         }
	         
            for (PowerHost h : hostList) {
                Log.printLine("Power usage for Host #" + h.getId() + ": " + h.getPower());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
                "Data center ID" + indent + "VM ID" + indent + indent + "Time" + indent + "Start Time" + indent + "Finish Time" + indent + "Execution Time" + indent + "Submission Time" + indent + "Resource ID" + indent + "Cost");
        Log.printLine("========== CLOUDLET ==========");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);
            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");
                Log.printLine(
                        indent + indent +
                        cloudlet.getResourceId() + indent + indent +
                        cloudlet.getVmId() + indent + indent +
                        indent + cloudlet.getActualCPUTime() + indent + indent +
                        cloudlet.getExecStartTime() + indent + indent +
                        cloudlet.getFinishTime() + indent + indent +
                        (cloudlet.getFinishTime() - cloudlet.getExecStartTime()) + indent + indent +
                        indent + cloudlet.getSubmissionTime() + indent + indent +
                        cloudlet.getResourceId() + indent + indent +
                        cloudlet.getCostPerSec()
                );
            }
        }
    }

    private static List<PowerHost> createHostList() {
        List<PowerHost> hostList = new ArrayList<>();

        // Example 1: Creating a PowerHost with multiple Pe instances
        int hostId1 = 0;
        int ramCapacity1 = 2048; // in MB
        long bwCapacity1 = 10000; // in Mbps
        long storage1 = 1000000; // in MB
        List<Pe> peList1 = createPeList(4, 1000); // Creating 4 Pe instances with 1000 MIPS each
        VmSchedulerTimeShared vmScheduler1 = new VmSchedulerTimeShared(peList1);
        PowerModelLinear powerModel1 = new PowerModelLinear(100, 0); // Example of PowerModelLinear

        PowerHost host1 = new PowerHost(
                hostId1,
                new RamProvisionerSimple(ramCapacity1),
                new BwProvisionerSimple(bwCapacity1),
                storage1,
                peList1,
                vmScheduler1,
                powerModel1);

        hostList.add(host1);

        // Example 2: Creating another PowerHost with different configuration
        int hostId2 = 1;
        int ramCapacity2 = 4096; // in MB
        long bwCapacity2 = 20000; // in Mbps
        long storage2 = 2000000; // in MB
        List<Pe> peList2 = createPeList(8, 1500); // Creating 8 Pe instances with 1500 MIPS each
        VmSchedulerTimeShared vmScheduler2 = new VmSchedulerTimeShared(peList2);
        PowerModelLinear powerModel2 = new PowerModelLinear(150, 0); // Example of PowerModelLinear

        PowerHost host2 = new PowerHost(
                hostId2,
                new RamProvisionerSimple(ramCapacity2),
                new BwProvisionerSimple(bwCapacity2),
                storage2,
                peList2,
                vmScheduler2,
                powerModel2);

        hostList.add(host2);

        // Add more PowerHost instances as needed

        return hostList;
    }

    private static List<Pe> createPeList(int num, int mipsCapacity) {
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            peList.add(new Pe(i, new PeProvisionerSimple(mipsCapacity)));
        }
        return peList;
    }
}
