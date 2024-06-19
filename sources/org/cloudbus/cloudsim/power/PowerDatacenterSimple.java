package org.cloudbus.cloudsim.power;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.core.predicates.PredicateType;
import org.cloudbus.cloudsim.power.PowerDatacenter;

/**
 * PowerDatacenter is a class that enables simulation of power-aware data centers.
 * 
 * If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * 
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 * 
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class PowerDatacenterSimple extends PowerDatacenter {

	/** The power. */
	private double power;

	/** The disable migrations. */
	private boolean disableMigrations;

	/** The cloudlet submited. */
	private double cloudletSubmitted;

	/** The migration count. */
	private int migrationCount;

	/**
	 * Instantiates a new datacenter.
	 * 
	 * @param name the name
	 * @param characteristics the res config
	 * @param schedulingInterval the scheduling interval
	 * @param utilizationBound the utilization bound
	 * @param vmAllocationPolicy the vm provisioner
	 * @param storageList the storage list
	 * @throws Exception the exception
	 */
	public PowerDatacenterSimple(
			String name,
			DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy,
			List<Storage> storageList,
			double schedulingInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
	}
	
	@Override
	protected void updateCloudletProcessing() {
	    System.out.println("========== PowerDatacenter: updateCloudletProcessing ==========");
	    
	    double currentTime = CloudSim.clock();
	    double cloudletSubmitted = getCloudletSubmitted();

	    System.out.println("Current Time: " + currentTime);
	    System.out.println("Cloudlet Submitted: " + cloudletSubmitted);

	    // Initial scheduling check
	    if (cloudletSubmitted == -1) {
	        System.out.println("No cloudlets submitted yet. Scheduling next event.");
	        CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.VM_DATACENTER_EVENT));
	        schedule(getId(), getSchedulingInterval(), CloudSimTags.VM_DATACENTER_EVENT);
	        return;
	    }

	    // Avoid looping by ensuring it proceeds with processing after initial submission
	    if (cloudletSubmitted == currentTime) {
	        System.out.println("Cloudlets submitted at the current time. Scheduling next event.");
	        CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.VM_DATACENTER_EVENT));
	        schedule(getId(), getSchedulingInterval(), CloudSimTags.VM_DATACENTER_EVENT);
	        return;
	    }

	    // If some time has passed since the last processing
	    if (currentTime > getLastProcessTime()) {
	        System.out.print(currentTime + " ");
	        double minTime = updateCloudetProcessingWithoutSchedulingFutureEventsForce();

	        if (!isDisableMigrations()) {
	            List<Map<String, Object>> migrationMap = getVmAllocationPolicy().optimizeAllocation(getVmList());

	            if (migrationMap != null) {
	                for (Map<String, Object> migrate : migrationMap) {
	                    Vm vm = (Vm) migrate.get("vm");
	                    PowerHost targetHost = (PowerHost) migrate.get("host");
	                    PowerHost oldHost = (PowerHost) vm.getHost();

	                    if (oldHost == null) {
	                        Log.formatLine(
	                            "%.2f: Migration of VM #%d to Host #%d is started",
	                            currentTime,
	                            vm.getId(),
	                            targetHost.getId()
	                        );
	                    } else {
	                        Log.formatLine(
	                            "%.2f: Migration of VM #%d from Host #%d to Host #%d is started",
	                            currentTime,
	                            vm.getId(),
	                            oldHost.getId(),
	                            targetHost.getId()
	                        );
	                    }

	                    targetHost.addMigratingInVm(vm);
	                    incrementMigrationCount();

	                    // VM migration delay = RAM / bandwidth
	                    // we use BW / 2 to model BW available for migration purposes, the other
	                    // half of BW is for VM communication
	                    // around 16 seconds for 1024 MB using 1 Gbit/s network
	                    send(
	                        getId(),
	                        vm.getRam() / ((double) targetHost.getBw() / (2 * 8000)),
	                        CloudSimTags.VM_MIGRATE,
	                        migrate
	                    );
	                }
	            }
	        }

	        // Schedules an event to the next time
	        if (minTime != Double.MAX_VALUE) {
	            CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.VM_DATACENTER_EVENT));
	            send(getId(), getSchedulingInterval(), CloudSimTags.VM_DATACENTER_EVENT);
	        }

	        setLastProcessTime(currentTime);
	    }
	}
	
}