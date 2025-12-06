package Soichiro.plugin.SoichiroMechanic.SMmchanism;

import org.bukkit.Location;
import org.bukkit.Material;

import Soichiro.plugin.SoichiroMechanic.utils.CustomBlock;

public class BasicGenerator {
    private CustomBlock generatorBlock;
    private int powerOutput;
    private int maxPowerOutput;
    private boolean isRunning;
    private GeneratorFuel fuelType;
    private int fuelLevel;
    private int maxFuelLevel;
    private long lastUpdateTime;

    public enum GeneratorFuel {
        COAL(100, 1000),
        REDSTONE(150, 800),
        LAVA(200, 500),
        SOLAR(50, 0);

        private final int powerPerSecond;
        private final int maxFuel;

        GeneratorFuel(int powerPerSecond, int maxFuel) {
            this.powerPerSecond = powerPerSecond;
            this.maxFuel = maxFuel;
        }

        public int getPowerPerSecond() {
            return powerPerSecond;
        }

        public int getMaxFuel() {
            return maxFuel;
        }
    }

    public BasicGenerator(Location location, GeneratorFuel fuelType) {
        this.generatorBlock = new CustomBlock(location, Material.BLAST_FURNACE, "BasicGenerator", "generator");
        this.generatorBlock.place();
        this.fuelType = fuelType;
        this.maxFuelLevel = fuelType.getMaxFuel();
        this.fuelLevel = 0;
        this.maxPowerOutput = fuelType.getPowerPerSecond();
        this.powerOutput = 0;
        this.isRunning = false;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void addFuel(int amount) {
        if (fuelType == GeneratorFuel.SOLAR) {
            return;
        }
        fuelLevel = Math.min(fuelLevel + amount, maxFuelLevel);
    }

    public void start() {
        if ((fuelLevel > 0 || fuelType == GeneratorFuel.SOLAR) && !isRunning) {
            isRunning = true;
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    public void stop() {
        isRunning = false;
        powerOutput = 0;
    }

    public void update() {
        if (!isRunning) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        if (fuelType == GeneratorFuel.SOLAR) {
            powerOutput = maxPowerOutput;
        } else {
            if (fuelLevel > 0) {
                int fuelConsumed = (int) (deltaTime / 1000.0);
                fuelLevel = Math.max(fuelLevel - fuelConsumed, 0);
                powerOutput = maxPowerOutput;

                if (fuelLevel <= 0) {
                    stop();
                }
            } else {
                stop();
            }
        }
    }

    public int getPowerOutput() {
        return isRunning ? powerOutput : 0;
    }

    public int getFuelLevel() {
        return fuelLevel;
    }

    public int getMaxFuelLevel() {
        return maxFuelLevel;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public GeneratorFuel getFuelType() {
        return fuelType;
    }

    public CustomBlock getGeneratorBlock() {
        return generatorBlock;
    }

    public void remove() {
        stop();
        generatorBlock.remove();
    }

    public GeneratorStatus getStatus() {
        return new GeneratorStatus(
            fuelType,
            isRunning,
            powerOutput,
            fuelLevel,
            maxFuelLevel,
            generatorBlock.getLocation()
        );
    }

    public static class GeneratorStatus {
        private GeneratorFuel fuelType;
        private boolean running;
        private int powerOutput;
        private int fuelLevel;
        private int maxFuelLevel;
        private Location location;

        public GeneratorStatus(GeneratorFuel fuelType, boolean running, int powerOutput, int fuelLevel, int maxFuelLevel, Location location) {
            this.fuelType = fuelType;
            this.running = running;
            this.powerOutput = powerOutput;
            this.fuelLevel = fuelLevel;
            this.maxFuelLevel = maxFuelLevel;
            this.location = location;
        }

        public GeneratorFuel getFuelType() {
            return fuelType;
        }

        public boolean isRunning() {
            return running;
        }

        public int getPowerOutput() {
            return powerOutput;
        }

        public int getFuelLevel() {
            return fuelLevel;
        }

        public int getMaxFuelLevel() {
            return maxFuelLevel;
        }

        public Location getLocation() {
            return location;
        }

        public double getFuelPercentage() {
            return maxFuelLevel > 0 ? (fuelLevel * 100.0) / maxFuelLevel : 0;
        }

        @Override
        public String toString() {
            return "GeneratorStatus{" +
                    "fuelType=" + fuelType +
                    ", running=" + running +
                    ", powerOutput=" + powerOutput +
                    ", fuelLevel=" + fuelLevel +
                    ", maxFuelLevel=" + maxFuelLevel +
                    ", fuelPercentage=" + String.format("%.1f", getFuelPercentage()) + "%" +
                    ", location=" + location +
                    '}';
        }
    }
}
