/*
 * SoichiroMechanic - 基本ジェネレータクラス
 * BasicGenerator は燃料を消費して電力を生成するシンプルなジェネレータ実装です。
 * 作者: soichiro0520
 */
package soichiro.plugin.soichiromechanic.smmchanism;

import org.bukkit.Location;
import org.bukkit.Material;

import soichiro.plugin.soichiromechanic.utils.CustomBlock;

public class BasicGenerator {
    // ジェネレータを表すカスタムブロック
    private CustomBlock generatorBlock;
    // 現在の出力
    private int powerOutput;
    // 燃料種別ごとの最大出力
    private int maxPowerOutput;
    // 実行中フラグ
    private boolean isRunning;
    // 使用する燃料タイプ
    private GeneratorFuel fuelType;
    // 現在の燃料量
    private int fuelLevel;
    // 最大燃料量
    private int maxFuelLevel;
    // 最終更新時間（ミリ秒） - update() の経過時間計算に使用
    private long lastUpdateTime;

    public enum GeneratorFuel {
        COAL(100, 1000),
        REDSTONE(150, 800),
        LAVA(200, 500),
        SOLAR(50, 0);

        private final int powerPerSecond;
        private final int maxFuel;

        // powerPerSecond: 1秒あたりの発電量（任意単位）
        // maxFuel: 燃料の最大値（ジェネレータに入る量の上限）
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
        // 太陽光タイプは燃料不要のため無視
        if (fuelType == GeneratorFuel.SOLAR) {
            return;
        }
        // 燃料を追加（上限を超えないようにする）
        fuelLevel = Math.min(fuelLevel + amount, maxFuelLevel);
    }

    public void start() {
        // 燃料があるか、または太陽光発電の場合に起動する
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
        // 定期的に呼び出して発電/燃料消費を計算する
        if (!isRunning) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        if (fuelType == GeneratorFuel.SOLAR) {
            // 太陽光は燃料を消費せず、常に最大出力を返す想定
            powerOutput = maxPowerOutput;
        } else {
            if (fuelLevel > 0) {
                // 秒数に応じて燃料を消費
                int fuelConsumed = (int) (deltaTime / 1000.0);
                fuelLevel = Math.max(fuelLevel - fuelConsumed, 0);
                powerOutput = maxPowerOutput;

                if (fuelLevel <= 0) {
                    // 燃料切れで停止
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
