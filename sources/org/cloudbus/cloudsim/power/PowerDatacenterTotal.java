package org.cloudbus.cloudsim.power;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.core.predicates.PredicateType;

public class PowerDatacenterTotal extends PowerDatacenter {

	public PowerDatacenterTotal(
			String name,
			DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy,
			List<Storage> storageList,
			double schedulingInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
	}
	
	@Override
	protected double updateCloudetProcessingWithoutSchedulingFutureEventsForce() {
		System.out.println("========== PowerDatacenter: updateCloudetProcessingWithoutSchedulingFutureEventsForce ==========");
		double currentTime = CloudSim.clock();
		double minTime = Double.MAX_VALUE;
		double timeDiff = currentTime - getLastProcessTime();
		double timeFrameDatacenterEnergy = 0.0;

		Log.printLine("\n\n--------------------------------------------------------------\n\n");
		Log.formatLine("New resource usage for the time frame starting at %.2f:", currentTime);

		for (Host host : this.<Host> getHostList()) {
		//for (PowerHost host : this.<PowerHost> getHostList()) {
			Log.printLine();

			double time = host.updateVmsProcessing(currentTime); // inform VMs to update processing
			if (time < minTime) {
				minTime = time;
			}
			
			// Calculate static energy (assume it's a constant value for illustration)
	        double staticEnergy = getStaticEnergy(); // Get static energy consumption of the host

	        // Calculate dynamic energy based on utilization
	        double dynamicEnergy = 0.0; // Calculate dynamic energy based on utilization (not shown here)

	        // Total energy is the sum of static and dynamic energy
	        double totalEnergy = staticEnergy + dynamicEnergy;

	        /*
			Log.formatLine(
					"%.2f: [Host #%d] utilization is %.2f%%",
					currentTime,
					host.getId(),
					host.getUtilizationOfCpu() * 100);
			*/
			
			Log.formatLine(
	            "%.2f: [Host #%d] total energy is %.2f W*sec (static: %.2f, dynamic: %.2f)",
	            currentTime,
	            host.getId(),
	            totalEnergy,
	            staticEnergy,
	            dynamicEnergy);

	        timeFrameDatacenterEnergy += totalEnergy;
		}

		if (timeDiff > 0) {
			Log.formatLine(
					"\nEnergy consumption for the last time frame from %.2f to %.2f:",
					getLastProcessTime(),
					currentTime);

			for (PowerHost host : this.<PowerHost> getHostList()) {
				double previousUtilizationOfCpu = host.getPreviousUtilizationOfCpu();
				double utilizationOfCpu = host.getUtilizationOfCpu();
				double timeFrameHostEnergy = host.getEnergyLinearInterpolation(
						previousUtilizationOfCpu,
						utilizationOfCpu,
						timeDiff);
				timeFrameDatacenterEnergy += timeFrameHostEnergy;

				Log.printLine();
				Log.formatLine(
						"%.2f: [Host #%d] utilization at %.2f was %.2f%%, now is %.2f%%",
						currentTime,
						host.getId(),
						getLastProcessTime(),
						previousUtilizationOfCpu * 100,
						utilizationOfCpu * 100);
				Log.formatLine(
		                "%.2f: [Host #%d] energy is %.2f W*sec (static: %.2f, dynamic: %.2f)",
		                currentTime,
		                host.getId(),
		                timeFrameHostEnergy,
		                getStaticEnergy(),
		                timeFrameHostEnergy - getStaticEnergy()); // Dynamic energy is total minus static
			}
			
			Log.formatLine(
		            "\n%.2f: Data center's total energy is %.2f W*sec\n",
		            currentTime,
		            timeFrameDatacenterEnergy);
		}

		setPower(getPower() + timeFrameDatacenterEnergy);

		checkCloudletCompletion();

		/** Remove completed VMs **/
		for (PowerHost host : this.<PowerHost> getHostList()) {
			for (Vm vm : host.getCompletedVms()) {
				getVmAllocationPolicy().deallocateHostForVm(vm);
				getVmList().remove(vm);
				Log.printLine("VM #" + vm.getId() + " has been deallocated from host #" + host.getId());
			}
		}

		Log.printLine();

		setLastProcessTime(currentTime);
		return minTime;
	}
	
	protected double getStaticEnergy() {
		return 30;	
	}
}