/*
 * SoichiroMechanic - ジェネレータ燃料リスナー
 * プレイヤーの右クリック操作でジェネレータに燃料を入れたり起動/停止を行います。
 * 作者: soichiro0520
 */
package soichiro.plugin.soichiromechanic.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import soichiro.plugin.soichiromechanic.smmchanism.BasicGenerator;
import soichiro.plugin.soichiromechanic.smmchanism.BasicGenerator.GeneratorFuel;

public class GeneratorFuelLisener implements Listener {
    // プレイヤーごとに所有するジェネレータを紐付けるためのマップ
    private Map<UUID, BasicGenerator> generatorMap;
    // ワールド上のロケーションからジェネレータを検索するためのマップ
    private Map<Location, BasicGenerator> generatorLocationMap;

    public GeneratorFuelLisener() {
        this.generatorMap = new HashMap<>();
        this.generatorLocationMap = new HashMap<>();
    }

    public void registerGenerator(BasicGenerator generator, Player owner) {
        // 所有者と位置で登録することで、右クリック時に generator を特定できる
        generatorMap.put(owner.getUniqueId(), generator);
        generatorLocationMap.put(generator.getGeneratorBlock().getLocation(), generator);
    }

    public void unregisterGenerator(Location location) {
        BasicGenerator generator = generatorLocationMap.remove(location);
        if (generator != null) {
            generatorMap.values().remove(generator);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // プレイヤーがジェネレータブロックを右クリックしたときの処理
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        // クリックした位置がジェネレータであれば操作を処理する
        BasicGenerator generator = generatorLocationMap.get(clickedBlock.getLocation());
        if (generator == null) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // 素手（空手）で右クリックした場合は起動/停止トグル
        if (itemInHand.getType() == Material.AIR) {
            handleGeneratorInteraction(player, generator);
            event.setCancelled(true);
            return;
        }

        // 燃料アイテムであれば燃料を追加
        if (isFuelItem(itemInHand, generator.getFuelType())) {
            addFuelToGenerator(player, generator, itemInHand);
            event.setCancelled(true);
        }
    }

    private void handleGeneratorInteraction(Player player, BasicGenerator generator) {
        if (!generator.isRunning()) {
            generator.start();
            player.sendMessage("§aジェネレータが起動しました");
        } else {
            generator.stop();
            player.sendMessage("§cジェネレータが停止しました");
        }

        displayGeneratorStatus(player, generator);
    }

    private void addFuelToGenerator(Player player, BasicGenerator generator, ItemStack fuelItem) {
        int fuelAmount = getFuelAmount(generator.getFuelType(), fuelItem.getType());

        if (fuelAmount <= 0) {
            player.sendMessage("§c無効な燃料です");
            return;
        }

        int currentFuel = generator.getFuelLevel();
        int maxFuel = generator.getMaxFuelLevel();

        if (currentFuel >= maxFuel) {
            player.sendMessage("§cジェネレータの燃料がいっぱいです");
            return;
        }

        // ジェネレータに燃料を追加し、インベントリのアイテム数を減らす
        generator.addFuel(fuelAmount);
        fuelItem.setAmount(fuelItem.getAmount() - 1);

        player.sendMessage("§a燃料を追加しました");
        displayGeneratorStatus(player, generator);
    }

    private void displayGeneratorStatus(Player player, BasicGenerator generator) {
        BasicGenerator.GeneratorStatus status = generator.getStatus();
        player.sendMessage("§6========== ジェネレータ情報 ==========");
        player.sendMessage("§e燃料タイプ: §f" + status.getFuelType());
        player.sendMessage("§e状態: §f" + (status.isRunning() ? "§a起動中" : "§c停止中"));
        player.sendMessage("§e出力電力: §f" + status.getPowerOutput() + "W");

        if (status.getFuelType() != GeneratorFuel.SOLAR) {
            player.sendMessage("§e燃料レベル: §f" + status.getFuelLevel() + "/" + status.getMaxFuelLevel());
            player.sendMessage("§e燃料パーセンテージ: §f" + String.format("%.1f", status.getFuelPercentage()) + "%");
        }

        player.sendMessage("§6=====================================");
    }

    private boolean isFuelItem(ItemStack item, GeneratorFuel fuelType) {
        // 指定した燃料タイプに対応するアイテムか判定する
        return switch (fuelType) {
            case COAL -> item.getType() == Material.COAL;
            case REDSTONE -> item.getType() == Material.REDSTONE;
            case LAVA -> item.getType() == Material.LAVA_BUCKET;
            case SOLAR -> false; // 太陽光はアイテムで追加できない
        };
    }

    private int getFuelAmount(GeneratorFuel fuelType, Material material) {
        return switch (fuelType) {
            case COAL -> material == Material.COAL ? 100 : 0;
            case REDSTONE -> material == Material.REDSTONE ? 150 : 0;
            case LAVA -> material == Material.LAVA_BUCKET ? 200 : 0;
            case SOLAR -> 0;
        };
    }

    public BasicGenerator getGenerator(Location location) {
        return generatorLocationMap.get(location);
    }

    public int getGeneratorCount() {
        return generatorLocationMap.size();
    }

    public void clearAllGenerators() {
        generatorMap.clear();
        generatorLocationMap.clear();
    }
}
