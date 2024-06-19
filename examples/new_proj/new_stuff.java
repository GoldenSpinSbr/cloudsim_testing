package new_proj;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.hosts.pe.Pe;
import org.cloudbus.cloudsim.hosts.pe.PeSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.PeProvisioner;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

public class new_stuff {

    private static final int HOSTS = 1;
    private static final int HOST_PES = 4;
    private static final double MIPS_PER_PE = 2000;
    private static final int VMS = 1;
    private static final int VM_PES = 2;
    private static final int CLOUDLETS = 5;
    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 10000;

    public static void main(String[] args) {
        // Create CloudSim simulation
        CloudSim simulation = new CloudSim();

        // Create Datacenter
        Datacenter datacenter = createDatacenter(simulation);

        // Create VMs and Cloudlets
        List<Vm> vmList = createVms();
        List<Cloudlet> cloudletList = createCloudlets();

        // Create Broker and submit VMs and Cloudlets
        DatacenterBroker broker = new DatacenterBrokerSimple(simulation);
        broker.submitVmList(vmList);
        broker.submitCloudletList(cloudletList);

        // Add a simulation step listener to print PE properties during simulation
        simulation.addOnClockTickListener(event -> {
            double currentTime = simulation.clock();
            System.out.println("Simulation time: " + currentTime);
            for (Host host : datacenter.getHostList()) {
                printPeProperties(host.getPeList());
            }
        });

        // Start simulation
        simulation.start();

        // Print Cloudlets completion results
        List<Cloudlet> finishedCloudlets = broker.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();

        // Print final PE properties
        System.out.println("Final PE properties after simulation:");
        for (Host host : datacenter.getHostList()) {
            printPeProperties(host.getPeList());
        }
    }

    private static Datacenter createDatacenter(CloudSim simulation) {
        List<Host> hostList = new ArrayList<>();

        for (int i = 0; i < HOSTS; i++) {
            hostList.add(createHost());
        }

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                "x86", "Linux", "Xen", hostList, 10.0, 3.0, 0.05, 0.001, 0.0);

        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    private static Host createHost() {
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(MIPS_PER_PE));
        }

        int ram = 4096;
        long storage = 1000000;
        int bw = 10000;

        return new HostSimple(ram, bw, storage, peList)
                .setVmScheduler(new VmSchedulerTimeShared());
    }

    private static List<Vm> createVms() {
        List<Vm> list = new ArrayList<>(VMS);

        for (int i = 0; i < VMS; i++) {
            Vm vm = new VmSimple(MIPS_PER_PE, VM_PES)
                    .setRam(512).setBw(1000).setSize(10000)
                    .setCloudletScheduler(new CloudletSchedulerTimeShared());
            list.add(vm);
        }

        return list;
    }

    private static List<Cloudlet> createCloudlets() {
        List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < CLOUDLETS; i++) {
            Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }

        return list;
    }

    private static void printPeProperties(List<Pe> peList) {
        for (Pe pe : peList) {
            System.out.println("========== Pe Properties ==========");
            System.out.println("Pe ID: " + pe.getId());
            System.out.println("Pe MIPS: " + pe.getMips());

            PeProvisionerSimple peProvisioner = (PeProvisionerSimple) pe.getPeProvisioner();
            System.out.println("Pe Provisioner Available MIPS: " + peProvisioner.getAvailableMips());
            System.out.println("Pe Provisioner Utilization: " + peProvisioner.getUtilization());
            System.out.println("Pe Provisioner Utilization in Percent: " + peProvisioner.getUtilization() * 100 + "%");
            System.out.println("-----------------------------------");
        }
    }
}