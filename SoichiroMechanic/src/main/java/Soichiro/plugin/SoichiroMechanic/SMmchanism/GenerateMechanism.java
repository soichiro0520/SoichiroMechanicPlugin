package Soichiro.plugin.SoichiroMechanic.SMmchanism;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import Soichiro.plugin.SoichiroMechanic.utils.CustomBlock;

public class GenerateMechanism {
    private World world;
    private List<CustomBlock> powerLines;
    private List<ElectricDevice> devices;
    private int totalPower;
    private boolean isActive;

    public GenerateMechanism(World world) {
        this.world = world;
        this.powerLines = new ArrayList<>();
        this.devices = new ArrayList<>();
        this.totalPower = 0;
        this.isActive = false;
    }

    public void generatePowerLine(Location start, Location end) {
        int steps = (int) start.distance(end);
        for (int i = 0; i <= steps; i++) {
            double t = steps > 0 ? (double) i / steps : 0;
            Location loc = new Location(world,
                start.getX() + (end.getX() - start.getX()) * t,
                start.getY() + (end.getY() - start.getY()) * t,
                start.getZ() + (end.getZ() - start.getZ()) * t);
            addPowerLine(loc, Material.COPPER_BLOCK, "PowerLine", "electricity");
        }
    }

    private void addPowerLine(Location loc, Material material, String name, String data) {
        CustomBlock block = new CustomBlock(loc, material, name, data);
        block.place();
        powerLines.add(block);
    }

    public void addDevice(Location location, String deviceType, int powerConsumption) {
        CustomBlock block = new CustomBlock(location, getMaterialForDevice(deviceType), deviceType, "device");
        block.place();
        ElectricDevice device = new ElectricDevice(block, deviceType, powerConsumption);
        devices.add(device);
    }

    public void activateSystem(int powerInput) {
        this.totalPower = powerInput;
        this.isActive = true;
        distributePower();
    }

    private void distributePower() {
        int remainingPower = totalPower;
        for (ElectricDevice device : devices) {
            if (remainingPower >= device.getPowerConsumption()) {
                device.activate();
                remainingPower -= device.getPowerConsumption();
            }
        }
    }

    public void deactivateSystem() {
        this.isActive = false;
        for (ElectricDevice device : devices) {
            device.deactivate();
        }
    }

    public void clearAllBlocks() {
        powerLines.forEach(CustomBlock::remove);
        devices.forEach(d -> d.getBlock().remove());
        powerLines.clear();
        devices.clear();
        totalPower = 0;
    }

    private Material getMaterialForDevice(String deviceType) {
        return switch (deviceType) {
            case "generator" -> Material.BLAST_FURNACE;
            case "motor" -> Material.IRON_BLOCK;
            case "battery" -> Material.GOLD_BLOCK;
            default -> Material.COPPER_BLOCK;
        };
    }

    public boolean isActive() {
        return isActive;
    }

    public int getTotalPower() {
        return totalPower;
    }

    public int getDeviceCount() {
        return devices.size();
    }

    public int getPowerLineCount() {
        return powerLines.size();
    }

    private static class ElectricDevice {
        private CustomBlock block;
        private String deviceType;
        private int powerConsumption;
        private boolean active;

        public ElectricDevice(CustomBlock block, String deviceType, int powerConsumption) {
            this.block = block;
            this.deviceType = deviceType;
            this.powerConsumption = powerConsumption;
            this.active = false;
        }

        public void activate() {
            this.active = true;
        }

        public void deactivate() {
            this.active = false;
        }

        public CustomBlock getBlock() {
            return block;
        }

        public int getPowerConsumption() {
            return powerConsumption;
        }

        public boolean isActive() {
            return active;
        }

        public String getDeviceType() {
            return deviceType;
        }
    }
}