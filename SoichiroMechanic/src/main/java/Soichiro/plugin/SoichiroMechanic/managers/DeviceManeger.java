package Soichiro.plugin.SoichiroMechanic.managers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;

import Soichiro.plugin.SoichiroMechanic.SMmchanism.GenerateMechanism;

public class DeviceManeger {
    private Map<String, GenerateMechanism> mechanisms;
    private Map<String, SystemConfig> systemConfigs;

    public DeviceManeger() {
        this.mechanisms = new HashMap<>();
        this.systemConfigs = new HashMap<>();
    }

    public void createMechanism(String id, World world) {
        GenerateMechanism mechanism = new GenerateMechanism(world);
        mechanisms.put(id, mechanism);
        systemConfigs.put(id, new SystemConfig());
    }

    public GenerateMechanism getMechanism(String id) {
        return mechanisms.get(id);
    }

    public void removeMechanism(String id) {
        GenerateMechanism mechanism = mechanisms.get(id);
        if (mechanism != null) {
            mechanism.clearAllBlocks();
            mechanisms.remove(id);
            systemConfigs.remove(id);
        }
    }

    public void activateMechanism(String id, int powerInput) {
        GenerateMechanism mechanism = mechanisms.get(id);
        if (mechanism != null) {
            mechanism.activateSystem(powerInput);
            SystemConfig config = systemConfigs.get(id);
            if (config != null) {
                config.setActive(true);
            }
        }
    }

    public void deactivateMechanism(String id) {
        GenerateMechanism mechanism = mechanisms.get(id);
        if (mechanism != null) {
            mechanism.deactivateSystem();
            SystemConfig config = systemConfigs.get(id);
            if (config != null) {
                config.setActive(false);
            }
        }
    }

    public void addDeviceToMechanism(String mechanismId, Location location, String deviceType, int powerConsumption) {
        GenerateMechanism mechanism = mechanisms.get(mechanismId);
        if (mechanism != null) {
            mechanism.addDevice(location, deviceType, powerConsumption);
        }
    }

    public void generatePowerLine(String mechanismId, Location start, Location end) {
        GenerateMechanism mechanism = mechanisms.get(mechanismId);
        if (mechanism != null) {
            mechanism.generatePowerLine(start, end);
        }
    }

    public SystemStatus getSystemStatus(String id) {
        GenerateMechanism mechanism = mechanisms.get(id);
        if (mechanism == null) {
            return null;
        }
        return new SystemStatus(
            id,
            mechanism.isActive(),
            mechanism.getTotalPower(),
            mechanism.getDeviceCount(),
            mechanism.getPowerLineCount()
        );
    }

    public void clearAllMechanisms() {
        mechanisms.values().forEach(GenerateMechanism::clearAllBlocks);
        mechanisms.clear();
        systemConfigs.clear();
    }

    public int getMechanismCount() {
        return mechanisms.size();
    }

    public static class SystemConfig {
        private boolean active;
        private String name;

        public SystemConfig() {
            this.active = false;
            this.name = "Unnamed";
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class SystemStatus {
        private String mechanismId;
        private boolean active;
        private int totalPower;
        private int deviceCount;
        private int powerLineCount;

        public SystemStatus(String mechanismId, boolean active, int totalPower, int deviceCount, int powerLineCount) {
            this.mechanismId = mechanismId;
            this.active = active;
            this.totalPower = totalPower;
            this.deviceCount = deviceCount;
            this.powerLineCount = powerLineCount;
        }

        public String getMechanismId() {
            return mechanismId;
        }

        public boolean isActive() {
            return active;
        }

        public int getTotalPower() {
            return totalPower;
        }

        public int getDeviceCount() {
            return deviceCount;
        }

        public int getPowerLineCount() {
            return powerLineCount;
        }

        @Override
        public String toString() {
            return "SystemStatus{" +
                    "mechanismId='" + mechanismId + '\'' +
                    ", active=" + active +
                    ", totalPower=" + totalPower +
                    ", deviceCount=" + deviceCount +
                    ", powerLineCount=" + powerLineCount +
                    '}';
        }
    }
}
