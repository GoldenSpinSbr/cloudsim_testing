package v0;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
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
import org.cloudbus.cloudsim.core.SimEvent;
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

public class v11 {
	private static final int SCHEDULING_INTERNAL = 10;
	private static final int HOSTS = 1;
	private static final int HOST_PES = 1;//4;
	
	private static final int VMS = 1;
	//private static final int USER_ID = 0;
	private static final int VM_PES = 4;
	private static final int CLOUDLETS = 5;
	private static final int CLOUDLET_PES = 2;
	private static final int CLOUDLET_LENGTH = 2000;
	
	private static final double STATIC_POWER = 35;
	
	private static final int MAX_POWER = 50;
	private static final double MIPS_PER_PE = 2000;
	
	private static List<Double> utilizationList = new ArrayList<>();
    private static List<Double> allocatedMipsList = new ArrayList<>();
	
	//private final CloudSim simulation;
	private DatacenterBroker broker0;
	private List<Vm> vmList;
	private List<Cloudlet> cloudletList;
	private Datacenter datacenter0;
	private final List<Host> hostList = new ArrayList<>();
	
	public static void main(String[] args) throws Exception {
		new v11();
	}
	
	public v11() throws Exception {
		// Initialize CloudSim
        int numUser = 1; // Number of cloud users
        Calendar calendar = Calendar.getInstance();
        boolean traceFlag = false; // Mean trace events

        CloudSim.init(numUser, calendar, traceFlag);
		datacenter0 = createDatacenterSimple();
		broker0 = BrokerCreator.createBroker();
		//broker0.setDatacenter(datacenter0);
		vmList = createVms();
		
		// Initial CPU threshold for scheduling
        double initialCpuThreshold = 1;

        // Schedule VMs initially
        VMScheduler.scheduleVMs(datacenter0, vmList, initialCpuThreshold);

        // Print initial VM placements
        printVmPlacement(vmList);
        
        System.out.println("passed");
		
		
		cloudletList = createCloudlets();
		//broker0.submitVmList(vmList);
		broker0.submitCloudletList(cloudletList);
		
		// Check VM allocation status
		for (Vm vm : vmList) {
		    if (vm.getHost() != null) {
		        System.out.println("VM #" + vm.getId() + " is allocated to Host #" + vm.getHost().getId());
		    } else {
		        System.out.println("VM #" + vm.getId() + " is not allocated to any host.");
		    }
		}
		
		CloudSim.startSimulation();
		// Access simulation time
        //double currentTimex = CloudSim.clock();
        //System.out.println("xzaCurrent simulation time: " + currentTimex);
		/*
		final List<Cloudlet> finishedCloudlets = broker0.getCloudletReceivedList();	
		
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
        */
		
        // Display the sorted Cloudlets in a tabular format
        //printCloudletList(finishedCloudlets, vmMap);
		//CloudSim.stopSimulation();
        //alternative();
        //for (final Host host: hostList) {
        //	double utilizedMean = calculateUtilizationMeanx(host);
        //}
        //CloudSim.pauseSimulation();
		//CloudSim.terminateSimulation();
		//printHostCpuUtilizationAndPowerConsumption();
		
		// Data structure to store VM utilization over time

		//double simulationTime = 0;
        //double loggingInterval = 1.0; // Set logging interval as needed

        //while (true) {
        //    // Advance simulation time using the minimum time between events
        //    simulationTime += CloudSim.getMinTimeBetweenEvents();
        //    CloudSim.resumeSimulation();

        //    // Check if the simulation has ended
        //    if (CloudSim.clock() >= 10 || broker0.getCloudletReceivedList().size() == cloudletList.size()) {
        //        break;
        //    }

        //    // Log metrics at regular intervals
        //    if (simulationTime % loggingInterval == 0) {
        //        logMetrics(vmList);
        //    }
        //}
        // Main simulation loop
 
        // Main simulation loop
        /*
        double simulationTime = 0;
        double endTime = 5; // Duration of simulation in seconds
        

        while (simulationTime < endTime) {
            // Handle events during simulation (e.g., process events, monitor VMs)
            handleEvents(broker0, vmList);

            // Optionally log metrics during simulation at regular intervals
            //if (simulationTime >= 5) {
            logMetrics(vmList);
            //    break;
            //}

            // Advance simulation time using the minimum time between events
            simulationTime += CloudSim.getMinTimeBetweenEvents();
        }
		*/
        /*
        while (!broker0.getCloudletReceivedList().containsAll(cloudletList)) {
            handleEvents(broker0, vmList);
            CloudSim.resumeSimulation();
        }
        */
        /*
        while (broker0.getCloudletReceivedList().size() < CLOUDLETS) {
            // Handle events during simulation (e.g., process events, monitor VMs)
            handleEvents(broker0, vmList);

            // Optionally log metrics during simulation at regular intervals
            if (simulationTime % 1 == 0) {
                logMetrics(vmList);
            }

            // Advance simulation time using the minimum time between events
            simulationTime += CloudSim.getMinTimeBetweenEvents();
            CloudSim.resumeSimulation();
        }
		*/
        /*
        boolean allCloudletsExecuted = false;
        while (!allCloudletsExecuted) {
            CloudSim.pauseSimulation(); // Pause to handle events
            handleEvents(broker0); // Process events during the simulation
            allCloudletsExecuted = checkIfAllCloudletsExecuted(broker0); // Check if all cloudlets are executed
            logMetrics(vmList);
            break;
            //CloudSim.resumeSimulation(); // Resume simulation after handling events
        }
        */
		// Simulation loop
		/*
		double currentTime = 0;
        while (currentTime < 5) {
            // Update VMs
            for (Vm vm : vmList) {
                double cpuUtilization = vm.getTotalUtilizationOfCpu(currentTime);
                Log.printLine("Time: " + currentTime + " - VM #" + vm.getId() + " CPU Utilization: " + cpuUtilization);
            }

            // Advance simulation time
            //CloudSim.pauseSimulation();
            //double timeToAdvance = currentTime + 0.01;
            //CloudSim.resumeSimulation();
            currentTime += 0.01;
            CloudSim.pauseSimulation();
            CloudSim.resumeSimulation();
        }
        */

        //evaluateMetrics();
        
        //List<Cloudlet> newList = broker0.getCloudletReceivedList();
        //printCloudletList(newList);

        // Verify VM allocation
        //System.out.println("okk");
		CloudSim.pauseSimulation(); // Pause to demonstrate rescheduling

        // New CPU threshold for rescheduling (example)
        //double newCpuThreshold = 0.8;

        // Reschedule VMs based on updated conditions
        //VMScheduler.rescheduleVMs(datacenter0, vmList, newCpuThreshold);

        // Print updated VM placements after rescheduling

        // Resume and continue simulation
        CloudSim.resumeSimulation();
        //verifyVmAllocation(vmList);
        
        CloudSim.stopSimulation();
        verifyVmAllocation(vmList);
        System.out.println("end");
	}
	
	private static void verifyVmAllocation(List<Vm> vmList) {
        for (Vm vm : vmList) {
            if (vm.getHost() != null) {
                System.out.println("VM #" + vm.getId() + " is allocated to Host #" + vm.getHost().getId());
            } else {
                System.out.println("VM #" + vm.getId() + " is not allocated to any host.");
            }
        }
    }
	
	 private static void printCloudletList(List<Cloudlet> list) {
	        String indent = "    ";
	        System.out.println();
	        System.out.println("========== OUTPUT ==========");
	        System.out.println("Cloudlet ID" + indent + "STATUS" + indent +
	                "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

	        for (Cloudlet cloudlet : list) {
	            System.out.print(indent + cloudlet.getCloudletId() + indent + indent);

	            if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
	                System.out.print("SUCCESS");

	                System.out.println(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
	                        indent + indent + cloudlet.getActualCPUTime() + indent + indent + cloudlet.getExecStartTime() + indent + indent + cloudlet.getFinishTime());
	            }
	        }
	 }
	
	private static boolean checkIfAllCloudletsExecuted(DatacenterBroker broker) {
	    List<Cloudlet> submittedCloudlets = broker.getCloudletList(); // Get list of submitted cloudlets

	    for (Cloudlet cloudlet : submittedCloudlets) {
	        if (cloudlet.getStatus() != Cloudlet.SUCCESS) {
	            return false; // If any cloudlet is not finished, return false
	        }
	    }

	    return true; // All cloudlets are finished
	}
	
	/*
	private static void handleEvents(DatacenterBroker broker) {
        // Example event handling logic
        List<Cloudlet> receivedCloudlets = broker.getCloudletReceivedList();
        for (Cloudlet cloudlet : receivedCloudlets) {
            System.out.println("Received Cloudlet #" + cloudlet.getCloudletId() +
                    " for VM #" + cloudlet.getVmId() +
                    " at time " + CloudSim.clock());
            // Add your event processing logic here
        }
    }
    */
	/*
	private static void handleEvents(DatacenterBroker broker) {
	    // Example event handling logic
	    List<Cloudlet> receivedCloudlets = broker.getCloudletReceivedList();
	    for (Cloudlet cloudlet : receivedCloudlets) {
	        System.out.println("Received Cloudlet #" + cloudlet.getCloudletId() +
	                " for VM #" + cloudlet.getVmId() +
	                " at time " + CloudSim.clock());

	        // Add logic to check if cloudlet has started execution
	        if (cloudlet.getExecStartTime() > 0) {
	            System.out.println("Cloudlet #" + cloudlet.getCloudletId() + " has started execution at " + cloudlet.getExecStartTime());
	        }

	        // Add your event processing logic here
	    }
	}
	*/
	/*
	private static void handleEvents(DatacenterBroker broker, List<Vm> vmList) {
        // Example event handling logic
        List<Cloudlet> receivedCloudlets = broker.getCloudletReceivedList();
        for (Cloudlet cloudlet : receivedCloudlets) {
            System.out.println("Received Cloudlet #" + cloudlet.getCloudletId() +
                    " for VM #" + cloudlet.getVmId() +
                    " at time " + CloudSim.clock());
            // Add your event processing logic here
            //logMetrics(vmList);
        }
    }
	*/
	
	private static void handleEvents(DatacenterBroker broker, List<Vm> vmList) {
	    // Example event handling logic
	    List<Cloudlet> receivedCloudlets = broker.getCloudletReceivedList();
	    for (Cloudlet cloudlet : receivedCloudlets) {
	        System.out.println("Received Cloudlet #" + cloudlet.getCloudletId() +
	                " for VM #" + cloudlet.getVmId() +
	                " at time " + CloudSim.clock());

	        // Get the VM associated with the cloudlet
	        Vm vm = getVmById(vmList, cloudlet.getVmId());
	        
	        // If the VM is found, log its metrics
	        if (vm != null) {
	            logMetricsForVm(vm);
	        } else {
	            System.out.println("VM #" + cloudlet.getVmId() + " not found in the list.");
	        }
	    }
	}
	
	private static Vm getVmById(List<Vm> vmList, int vmId) {
	    for (Vm vm : vmList) {
	        if (vm.getId() == vmId) {
	            return vm;
	        }
	    }
	    return null;
	}

	private static void logMetricsForVm(Vm vm) {
	    double utilization = getUtilization(vm);
	    double allocatedMips = getTotalAllocatedMips(vm);
	    System.out.println("VM #" + vm.getId() + " Utilization: " + utilization + ", Allocated MIPS: " + allocatedMips);
	}
	
	private static void logMetrics(List<Vm> vmList) {
		// Clear previous metrics if needed
        utilizationList.clear();
        allocatedMipsList.clear();
        
	    // Implement logging logic to capture VM metrics (e.g., utilization, MIPS)
        System.out.println("check");
	    for (Vm vm : vmList) {
	    	System.out.println("done");
	        double utilization = getUtilization(vm);
	        double allocatedMips = getTotalAllocatedMips(vm);
	        System.out.println("VM #" + vm.getId() + " Utilization: " + utilization + ", Allocated MIPS: " + allocatedMips);
	        // You can log this information to a file, database, or store in a data structure for analysis
	        // Store metrics in lists
            utilizationList.add(utilization);
            allocatedMipsList.add(allocatedMips);
	    }
	}
	
	private static void evaluateMetrics() {
        // Example evaluation logic
        System.out.println("Evaluation Results:");
        for (int i = 0; i < utilizationList.size(); i++) {
            double utilization = utilizationList.get(i);
            double allocatedMips = allocatedMipsList.get(i);
            System.out.println("VM #" + i + " Utilization: " + utilization + ", Allocated MIPS: " + allocatedMips);
            // Add your evaluation logic here
        }
    }

    private static double getUtilization(Vm vm) {
        // Example method to calculate VM utilization
        double totalRequestedMips = vm.getCurrentRequestedTotalMips();
        //double totalAllocatedMips = vm.getTotalAllocatedMips();
        double totalAllocatedMips = getTotalAllocatedMips(vm);

        if (totalAllocatedMips == 0) {
            return 0;
        }

        return totalRequestedMips / totalAllocatedMips;
    }
    
    private static double getTotalAllocatedMips(Vm vm) {
        double totalAllocatedMips = 0.0;

        // Check if the VM is allocated to a host
        Host host = vm.getHost();
        if (host != null) {
            // Get the list of PEs allocated to this VM's host
            List<Pe> peList = host.getPeList();
            
            // Iterate through each PE and sum up its allocated MIPS for the VM
            for (Pe pe : peList) {
            	List<Double> allocatedMipsList = pe.getPeProvisioner().getAllocatedMipsForVm(vm);
                // Sum up all values in the list
                for (double allocatedMips : allocatedMipsList) {
                    totalAllocatedMips += allocatedMips;
                }
            }
        } else {
            System.out.println("VM #" + vm.getId() + " is not allocated to any host.");
            // Optionally handle this case (e.g., log, throw exception)
        }

        return totalAllocatedMips;
    }

	private void printHostCpuUtilizationAndPowerConsumption() {
		System.out.println();
		for (final Host host: hostList) {
			//printHostCpuUtilizationAndPowerConsumption(host);
			double maxPower = calculateMaxPower(host);
			double staticPower = calculateStaticPower(host);
			double utilizedMean = calculateUtilizationMeanx(host);
			//double utilizedMean = calculateUtilizationMean(host);
			System.out.printf("Max Power: %.3f Static Power: %.3f Utilized Mean: %.3f", 
					maxPower, staticPower, utilizedMean);
		}
		System.out.println();
	}
	private void printHostCpuUtilizationAndPowerConsumption(final Host host) {
		final double utilization = calculateUtilizationMean(host);
		final double watts = calculatePowerConsumption(host, utilization);
		System.out.printf("Host %2d CPU Usage mean: %6.1f%% | Power Consumption: %.2f watts%n",
                host.getId(), utilization * 100, watts);
		
	}
	private double calculateMaxPower(Host host) {
	    List<Pe> peList = host.getPeList();
	    double totalMaxMips = 0;
	    // Calculate the total MIPS capacity of all PEs
	    for (Pe pe : peList) {
	        totalMaxMips += pe.getPeProvisioner().getMips();
	    }
	    // Assuming all PEs are fully utilized, max power could be estimated as total MIPS capacity
	    // divided by an efficiency factor (assuming not all power is consumed when fully utilized)
	    // This is a hypothetical example and should be adjusted based on your actual system characteristics.
	    double efficiencyFactor = 0.8; // Example efficiency factor (adjust as per your system)
	    return totalMaxMips / efficiencyFactor;
	}
	private double calculateStaticPower(Host host) {
	    // Example: Assuming a static power consumption value based on system specifications or estimates
	    return 35; // Example static power consumption (adjust as per your system)
	}
	private double alternative() {
	    // Example: Assuming a static power consumption value based on system specifications or estimates
		double totalUtilization = 0;
        int vmCount = 0;

        for (Host host1 : hostList) {
        	System.out.println("ok");
        	System.out.println(host1);
        	System.out.println(host1.getVmList());
            for (Vm vm : host1.getVmList()) {
            	System.out.println("ako");
                //totalUtilization += vm.getTotalAllocatedMips() / host.getTotalMips();
                //vmCount++;
            	System.out.println("X ID: " + host1.getTotalMips());
            }
        }
	    return 35; // Example static power consumption (adjust as per your system)
	}
	private double calculateUtilizationMeanx(Host host) {
	    List<Pe> peList = host.getPeList();
	    double totalUtilization = 0;

	    // Sum up the utilization of all PEs
	    for (Pe pe : peList) {
	    	//System.out.println("am here");
	    	//System.out.printf("Host %.6f", pe.getPeProvisioner().getUtilization());
	        totalUtilization += pe.getPeProvisioner().getUtilization();
	        System.out.println("========== Pe Properties ==========");
	        System.out.println("Pe ID: " + pe.getId());
	        System.out.println("Pe MIPS: " + pe.getMips());
	        PeProvisionerSimple peProvisioner = (PeProvisionerSimple) pe.getPeProvisioner();
	        System.out.println("Pe Provisioner Available MIPS: " + peProvisioner.getAvailableMips());
	        System.out.println("Pe Provisioner Utilization: " + peProvisioner.getUtilization());
	        System.out.println("Pe Provisioner Utilization in Percent: " + peProvisioner.getUtilization() * 100 + "%");
	        System.out.println("-----------------------------------");
	        
	    }

	    // Calculate the mean utilization
	    return totalUtilization / peList.size();
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
    private static void printVmPlacement(List<Vm> vmList) {
        System.out.println("VM Placement:");
        for (Vm vm : vmList) {
            if (vm.getHost() != null) {
                System.out.println("VM #" + vm.getId() + " -> Host #" + vm.getHost().getId());
            } else {
                System.out.println("VM #" + vm.getId() + " -> Not allocated to any host");
            }
        }
        System.out.println();
        System.out.println("Evaluation Results:");
        for (int i = 0; i < utilizationList.size(); i++) {
            double utilization = utilizationList.get(i);
            double allocatedMips = allocatedMipsList.get(i);
            System.out.println("VM #" + i + " Utilization: " + utilization + ", Allocated MIPS: " + allocatedMips);
            // Add your evaluation logic here
        }
    }
}