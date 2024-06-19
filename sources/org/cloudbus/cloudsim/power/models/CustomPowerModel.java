package org.cloudbus.cloudsim.power.models;


public class CustomPowerModel extends PowerModelSpecPower {

    // Example power data (hypothetical values)
    private static final double[] powerData = {
        100, 95, 90, 85, 80, 75, 70, 65, 60, 55, 50
    };

    @Override
    protected double getPowerData(int index) {
        // Implement your logic to retrieve power data based on index
        // In this example, assume powerData[index] represents power consumption
        // for utilization levels from 0.0 to 1.0 (in increments of 0.1)
        return powerData[index];
    }
}