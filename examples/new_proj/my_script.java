package new_proj;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.PeSimple;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.CustomDatacenter;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicySimple;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

import org.cloudbus.cloudsim.HostSimple;
import threshold_based.BrokerCreator;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.UtilizationModelNull;

public class my_script {
	private static final int SCHEDULING_INTERNAL = 10;
	private static final int HOSTS = 1;
	private static final int HOST_PES = 1;//4;
	
	private static final int VMS = 1;
	//private static final int USER_ID = 0;
	private static final int VM_PES = 4;
	private static final int CLOUDLETS = 10;
	private static final int CLOUDLET_PES = 2;
	private static final int CLOUDLET_LENGTH = 50000;
	
	private static final double STATIC_POWER = 35;
	
	private static final int MAX_POWER = 50;
	private static final double MIPS_PER_PE = 2000;
	
	//private final CloudSim simulation;
	private DatacenterBroker broker0;
	private List<Vm> vmList;
	private List<Cloudlet> cloudletList;
	private Datacenter datacenter0;
	private final List<Host> hostList = new ArrayList<>();
	
	public static void main(String[] args) throws Exception {
		new my_script();
	}
	
	public my_script() throws Exception {
		// Initialize CloudSim
        int numUser = 1; // Number of cloud users
        Calendar calendar = Calendar.getInstance();
        boolean traceFlag = false; // Mean trace events

        CloudSim.init(numUser, calendar, traceFlag);
		datacenter0 = createDatacenterSimple();
		broker0 = BrokerCreator.createBroker();
		//broker0.setDatacenter(datacenter0);
		vmList = createVms();
		cloudletList = createCloudlets();
		broker0.submitVmList(vmList);
		broker0.submitCloudletList(cloudletList);
		
		CloudSim.startSimulation();
		final List<Cloudlet> finishedCloudlets = broker0.getCloudletReceivedList();	
		
		/*
		for (Vm vm : vmList) {
		    System.out.println("VM ID: " + vm.getId());
		    System.out.println("User ID: " + vm.getUserId());
		    System.out.println("MIPS: " + vm.getMips());
		    System.out.println("Number of PEs: " + vm.getNumberOfPes());
		    System.out.println("RAM: " + vm.getRam());
		    System.out.println("Bandwidth: " + vm.getBw());
		    System.out.println("Size: " + vm.getSize());
		    System.out.println("VMM: " + vm.getVmm());
		    System.out.println("------------------------------");
		}
		*/
		
		/*
		System.out.println("Finished Cloudlets:");
		System.out.println("---------------------------------------------------------------------------");
		System.out.printf("%-10s %-15s %-15s %-15s %-15s %-15s%n",
		        "Cloudlet ID", "Status", "VM ID", "Length", "Start Time", "Finish Time");
		System.out.println("---------------------------------------------------------------------------");

		for (Cloudlet cloudlet : finishedCloudlets) {
		    System.out.printf("%-10d %-15s %-15d %-15d %-15.2f %-15.2f%n",
		            cloudlet.getCloudletId(),
		            Cloudlet.getStatusString(cloudlet.getStatus()),
		            cloudlet.getVmId(),
		            cloudlet.getCloudletLength(),
		            cloudlet.getExecStartTime(),
		            cloudlet.getFinishTime());
		}

		System.out.println("---------------------------------------------------------------------------");
		*/
		
		// Create a map to look up VMs by their ID
        Map<Integer, Vm> vmMap = vmList.stream()
                .collect(Collectors.toMap(Vm::getId, vm -> vm));

        // Sort cloudlets based on their associated VM's host ID, then by cloudlet ID
        finishedCloudlets.sort(
                Comparator.comparingLong((Cloudlet c) -> {
                    Vm vm = vmMap.get(c.getVmId());
                    return vm != null && vm.getHost() != null ? vm.getHost().getId() : Long.MAX_VALUE;
                })
                .thenComparingLong(Cloudlet::getVmId)
        );
        
        // Display the sorted Cloudlets in a tabular format
        printCloudletList(finishedCloudlets, vmMap);
        
		//CloudSim.stopSimulation();
		CloudSim.terminateSimulation();
		printHostCpuUtilizationAndPowerConsumption();
		
	}
	
	private void printHostCpuUtilizationAndPowerConsumption() {
		System.out.println();
		for (final Host host: hostList) {
			printHostCpuUtilizationAndPowerConsumption(host);
		}
		System.out.println();
	}
	private void printHostCpuUtilizationAndPowerConsumption(final Host host) {
		final double utilization = calculateUtilizationMean(host);
		final double watts = calculatePowerConsumption(host, utilization);
		System.out.printf("Host %2d CPU Usage mean: %6.1f%% | Power Consumption: %.2f watts%n",
                host.getId(), utilization * 100, watts);
		
	}
    private Datacenter createDatacenterSimple() throws Exception {
    	System.out.println("========== Create Datacenter Simple ==========");
        for (int i=0; i<HOSTS; i++) {
            Host host = createPowerHost(i);
            hostList.add(host);
        }
        
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                "x86", "Linux", "Xen", hostList, 10.0, 1.0, 0.05, 0.001, 0.0);
        //PowerVmAllocationPolicySimple vmAllocationPolicy = new PowerVmAllocationPolicySimple(hostList);
        VmAllocationPolicySimple vmAllocationPolicy = new VmAllocationPolicySimple(hostList);

        // Create a list of storage devices (if needed)
        List<Storage> storageList = new ArrayList<>();

        // Create a DatacenterSimple with the specified characteristics
        final Datacenter dc = new Datacenter(
			"Datacenter1",
	        characteristics,
	        vmAllocationPolicy,
	        storageList,
	        0);//SCHEDULING_INTERNAL);
        
        System.out.println("========== Datacenter complete ==========");
        return dc;
    }
    
    
	private Host createPowerHost(final int id) {
		System.out.println("========== Create Power Host ==========");
		int powerPES = 1;
        final List<Pe> peList = new ArrayList<>();
        //final List<Host> hostList = new ArrayList<>();
        
        for (int i=0; i<powerPES; i++) {
            peList.add(new PeSimple((int) i, (double) MIPS_PER_PE)); // PeSimple
            //peList.add(new Pe(0, new PeProvisionerSimple(MIPS_PER_PE)));
        }
        
        int ram = 4096; // Host memory (MB)
        long storage = 1000000; // Host storage
        int bw = 10000;
                
        //final RamProvisioner ramProvisioner = new RamProvisionerSimple((int) ram); 
        //final BwProvisioner bwProvisioner = new BwProvisionerSimple((int) bw); 
        
        //VmScheduler vmScheduler = new VmSchedulerTimeShared(peList);
        
        //PowerModelHost powerModel = new PowerModelHost(maxPower, staticPower);
        /*
        double totalMips = 0.0;
        for (Pe pe : peList) {
            totalMips += pe.getMips();
        }

        System.out.println("Total MIPS of PowerHost: " + totalMips);\
        */
        /*
        hostList.add(new Host
                id,
                new RamProvisionerSimple(2048),
                new BwProvisionerSimple(10000),
                1000000,
                peList,
                new VmSchedulerTimeShared(peList),
                new PowerModelLinear(10, 2) // This is just an example, adjust to your needs
        );
        */
        
        //hostList.add(
        return new Host(
                0,
                new RamProvisionerSimple(ram),
                new BwProvisionerSimple(bw),
                storage,
                peList,
                new VmSchedulerTimeShared(peList)
            );
        //);

        /*
        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

        hostList.add(
            new Host(
                0,
                new RamProvisionerSimple(ram),
                new BwProvisionerSimple(bw),
                storage,
                peList,
                new VmSchedulerTimeShared(peList)
            )
        );
        */
    }
    
    private List<Vm> createVms() {
    	System.out.println("========== Create Vms ==========");
    	final List<Vm> list = new ArrayList<>();//VMS);
    	int brokerId = broker0.getId();
        for (int i = 0; i < VMS; i++) {
            Vm vm = new Vm(
                i, // VM ID
                brokerId, // User ID
                100, // MIPS
                VM_PES, // Number of PEs
                512, // RAM in MB
                1000, // Bandwidth in Mbps
                10000, // Size in MB
                "Xen", // VMM name
                new CloudletSchedulerSpaceShared() // Cloudlet Scheduler
                //new CloudletSchedulerTimeShared()
            );
            list.add(vm);
        }
        return list;
    }
    
    private List<Cloudlet> createCloudlets() {
    	System.out.println("========== Create Cloudlets ==========");
        final List<Cloudlet> list = new ArrayList<> (CLOUDLETS);
        final UtilizationModel utilizationModelNull = new UtilizationModelNull();
        final UtilizationModel utilizationModelFull = new UtilizationModelFull();
        int brokerId = broker0.getId();
        for (int i=0; i<CLOUDLETS; i++) {
            final long length = i < CLOUDLETS / 2 ? CLOUDLET_LENGTH : CLOUDLET_LENGTH * 2;
            
            Cloudlet cloudlet = new Cloudlet(
                    i, // ID
                    length, // Length
                    CLOUDLET_PES, // Number of PEs
                    300, // File size (bytes)
                    300, // Output size (bytes)
                    utilizationModelFull, // Utilization model CPU
                    utilizationModelNull, // Utilization model RAM
                    utilizationModelNull // Utilization model BW
                );
            cloudlet.setUserId(brokerId);

            list.add(cloudlet);
        }
        return list;
    }
    
    private static void printCloudletList(List<Cloudlet> list, Map<Integer, Vm> vmMap) {
        String indent = "    ";
        System.out.println();
        System.out.println("========== OUTPUT ==========");
        System.out.println("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Host ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");
        
        for (Cloudlet cloudlet : list) {
            System.out.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
                System.out.print("SUCCESS");

                Vm vm = vmMap.get(cloudlet.getVmId());

                if (vm != null && vm.getHost() != null) {
                    Host host = vm.getHost();
                    System.out.println(
                            indent + indent + cloudlet.getResourceId() + indent + indent +
                            cloudlet.getVmId() + indent + indent + host.getId() + indent + indent +
                            String.format("%.2f", cloudlet.getActualCPUTime()) + indent + indent +
                            String.format("%.2f", cloudlet.getExecStartTime()) + indent + indent +
                            String.format("%.2f", cloudlet.getFinishTime())
                    );
                } else {
                    System.out.println(indent + indent + "N/A" + indent + indent + cloudlet.getVmId() + indent + indent + "N/A" + indent + indent + "N/A" + indent + indent + "N/A");
                }
            } else {
                System.out.print("FAILED");
                System.out.println();
            }
        }
    }
    
    private static double calculateUtilizationMean(Host host) {
    	System.out.println("========== Calculate Utilization Mean ==========");
        // Example: Calculate mean utilization of all PEs
        double totalUtilization = 0;
        for (int peIndex = 0; peIndex < host.getPeList().size(); peIndex++) {
            totalUtilization += host.getPeList().get(peIndex).getPeProvisioner().getUtilization();
        }
        return totalUtilization / host.getPeList().size();
    }

    private static double calculatePowerConsumption(Host host, double utilizationPercentMean) {
    	System.out.println("========== Calculate Power Consumption ==========");
        // Example: Implementing a basic power model
        // This is a hypothetical example; actual power models may vary
        // You may need to replace this with a more accurate model based on your requirements
        double powerModelCoefficient = 100; // Example coefficient
        return powerModelCoefficient * utilizationPercentMean;
    }
}