package v0;

import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerDatacenter;
//import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.Datacenter;

//import org.cloudbus.cloudsim.Host;
//import org.cloudbus.cloudsim.Datacenter;
//import org.cloudbus.cloudsim.DatacenterBroker;
//import org.cloudbus.cloudsim.power.PowerDatacenterBroker;
//import org.cloudbus.cloudsim.power.models.CustomPowerModel;
//import org.cloudbus.cloudsim.power.PowerVm;
//package org.cloudbus.cloudsim;public class HostDynamicWorkload extends Host;

public class v3 {
	//
	private static final int HOST_NUM = 3;
	private static final int HOST_PES = 10;
	private static final int HOST_MIPS = 1000;
	//private static final int HOST_RAM = 2048;
	private static final int HOST_RAM = 8192;
	private static final int HOST_BW = 10000;
	
	//
	private static int BROKER_ID = 0;
	
	//
	private static final int VM_NUM = 20;
	private static final int VM_MIPS = 200;
	private static final int VM_PE = 4;
	private static final int VM_RAM = 512;
	private static final int VM_BW = 1000;
	private static final int VM_SIZE = 100;
	
	//
	private static final int CLOUDLET_NUM = 2;
	private static final int CLOUDLET_LEN = 10;
	private static final int CLOUDLET_PES = 1;
	
	//
	private DatacenterBroker broker0;
	//private List<PowerVm> vmList;
	private List<Vm> vmList;
	private List<Cloudlet> cloudletList;
	
	public static void main(String[] args) throws Exception {
		new v3();
	}
	
	public v3() throws Exception {
		// Initialize CloudSim
        int numUser = 1;
        Calendar calendar = Calendar.getInstance();
        boolean traceFlag = false;
        CloudSim.init(numUser, calendar, traceFlag);
        
        // Create Datacenter
        //Datacenter datacenter0 = DatacenterCreator.createDatacenter(HOST_NUM, HOST_PES, HOST_MIPS, 
        //	HOST_RAM, HOST_BW);
    	PowerDatacenter datacenter0 = PowerDatacenterCreator.createDatacenter(HOST_NUM, HOST_PES, 
    			HOST_MIPS, HOST_RAM, HOST_BW);
    	
        // Create Broker
        broker0 = BrokerCreator.createBroker();
        BROKER_ID = broker0.getId();
        
        // Create VMs
        //vmList = PowerVMCreator.createVMs(BROKER_ID, VM_NUM, VM_MIPS, VM_PE, VM_RAM, VM_BW, VM_SIZE);
        vmList = VMCreator.createVMs(BROKER_ID, VM_NUM, VM_MIPS, VM_PE, VM_RAM, VM_BW, VM_SIZE);
        broker0.submitVmList(vmList);
        
        // Schedule VMs using VMScheduler
        double initialCpuThreshold = 0.8;
        VMScheduler.scheduleVMs1(datacenter0, vmList, initialCpuThreshold);
        //VMScheduler.scheduleVMsRoundRobin(datacenter0, vmList);
        //VMScheduler.scheduleVMsMaxmin(datacenter0, vmList);
        
        //PowerVMScheduler.scheduleVMs(datacenter0, vmList, initialCpuThreshold);
        
        // Create Cloudlets
        cloudletList = CloudletCreator.createCloudlets(BROKER_ID, CLOUDLET_NUM, CLOUDLET_LEN, 
        		CLOUDLET_PES);
        
        // Submit Cloudlet list to broker
        broker0.submitCloudletList(cloudletList);
        
        /*
        // Print Cloudlet Details before simulation
        System.out.println("Cloudlet Details before simulation:");
        for (Cloudlet cl : cloudletList) {
            System.out.println("Cloudlet ID: " + cl.getCloudletId());
            System.out.println("Length: " + cl.getCloudletLength());
            System.out.println("Number of PEs: " + cl.getNumberOfPes());
            System.out.println("File Size: " + cl.getCloudletFileSize());
            System.out.println("Output Size: " + cl.getCloudletOutputSize());
        }

        // Print PowerVm Details before simulation
        System.out.println("PowerVm Details before simulation:");
        for (Vm v : vmList) {
            System.out.println("PowerVm ID: " + v.getId());
            System.out.println("MIPS: " + v.getMips());
            System.out.println("Number of PEs: " + v.getNumberOfPes());
            System.out.println("RAM: " + v.getRam());
            System.out.println("Bandwidth: " + v.getBw());
            System.out.println("Size: " + v.getSize());
            System.out.println("VMM: " + v.getVmm());
        }
        */
        
        for (Vm v : vmList) {
        	System.out.println("PowerVm : " + v);
        }

		// Start the simulation
        CloudSim.startSimulation();
        // Stop the simulation
        CloudSim.stopSimulation();

        // Print results
        List<Cloudlet> newList = broker0.getCloudletReceivedList();
        printCloudletList(newList);
        
        // Print VM CPU utilization
       VMScheduler.printVmCpuUtilization(vmList);
        
        System.out.println("DONE");
	}
	
	 private static void printCloudletList(List<Cloudlet> list) {
        String indent = "    ";
        System.out.println();
        System.out.println("========== OUTPUT ==========");
        System.out.println("Cloudlet ID" + indent + "STATUS" + indent +
                "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" 
        		+ indent + "Finish Time");

        for (Cloudlet cloudlet : list) {
            System.out.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
                System.out.print("SUCCESS");

                System.out.println(indent + indent + cloudlet.getResourceId() + indent + indent 
                		+ indent + cloudlet.getVmId() + indent + indent + cloudlet.getActualCPUTime() 
                		+ indent + indent + cloudlet.getExecStartTime() + indent + indent 
                		+ cloudlet.getFinishTime());
            }
        }
	 }
}




