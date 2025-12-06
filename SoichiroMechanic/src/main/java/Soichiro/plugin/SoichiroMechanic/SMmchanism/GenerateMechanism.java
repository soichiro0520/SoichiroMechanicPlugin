/*
 * SoichiroMechanic - 発電/電力メカニズム生成クラス
 * GenerateMechanism は電力線やデバイスを設置・管理する役割を持ちます。
 * 作者: soichiro0520
 */
package Soichiro.plugin.SoichiroMechanic.SMmchanism;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import Soichiro.plugin.SoichiroMechanic.utils.CustomBlock;

public class GenerateMechanism {
    private World world;
    // この GenerateMechanism が操作するワールド
    // 電力線用の CustomBlock リスト
    private List<CustomBlock> powerLines;
    // 接続されたデバイスのリスト（内部クラス ElectricDevice を使用）
    private List<ElectricDevice> devices;
    // システム全体への入力電力量（任意の単位）
    private int totalPower;
    // システムがアクティブかどうか
    private boolean isActive;

    public GenerateMechanism(World world) {
        this.world = world;
        this.powerLines = new ArrayList<>();
        this.devices = new ArrayList<>();
        this.totalPower = 0;
        this.isActive = false;
    }

    public void generatePowerLine(Location start, Location end) {
        // start から end まで直線上にブロックを設置して電力線を表現する
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
        // 指定位置に電力線ブロックを設置し、リストで管理する
        CustomBlock block = new CustomBlock(loc, material, name, data);
        block.place();
        powerLines.add(block);
    }

    public void addDevice(Location location, String deviceType, int powerConsumption) {
        // 指定位置にデバイスブロックを設置し、消費電力を持つ ElectricDevice として管理する
        CustomBlock block = new CustomBlock(location, getMaterialForDevice(deviceType), deviceType, "device");
        block.place();
        ElectricDevice device = new ElectricDevice(block, deviceType, powerConsumption);
        devices.add(device);
    }

    public void activateSystem(int powerInput) {
        // システムに入力電力を与えて稼働させる
        this.totalPower = powerInput;
        this.isActive = true;
        distributePower();
    }

    private void distributePower() {
        // 単純な順次配分ロジック：残り電力がデバイスの消費電力を満たす場合に起動
        int remainingPower = totalPower;
        for (ElectricDevice device : devices) {
            if (remainingPower >= device.getPowerConsumption()) {
                device.activate();
                remainingPower -= device.getPowerConsumption();
            }
        }
    }

    public void deactivateSystem() {
        // システム停止：全デバイスを停止する
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