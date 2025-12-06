/*
 * SoichiroMechanic - カスタムブロックユーティリティ
 * CustomBlock は特定位置にブロックを設置/管理する簡易ラッパーです。
 * 作者: soichiro0520
 */
package Soichiro.plugin.SoichiroMechanic.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class CustomBlock {
    // ブロックの設置場所
    private Location location;
    // 使用するブロックの種類（Material）
    private Material baseMaterial;
    // プラグイン上で扱うための任意の名前
    private String customName;
    // 追加データを文字列で保持するためのフィールド（用途は拡張可能）
    private String customData;

    public CustomBlock(Location location, Material baseMaterial, String customName, String customData) {
        this.location = location;
        this.baseMaterial = baseMaterial;
        this.customName = customName;
        this.customData = customData;
    }

    public Location getLocation() {
        // 設置場所を返す
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Material getBaseMaterial() {
        // このカスタムブロックに使用する Material を返す
        return baseMaterial;
    }

    public void setBaseMaterial(Material baseMaterial) {
        this.baseMaterial = baseMaterial;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public Block getBlock() {
        // Location から Bukkit の Block オブジェクトを取得する
        return location != null ? location.getBlock() : null;
    }

    public boolean place() {
        if (location == null || baseMaterial == null) {
            return false;
        }
        // ワールド上にブロックを設置する（単純にタイプを変更）
        getBlock().setType(baseMaterial);
        return true;
    }

    public void remove() {
        if (location != null) {
            // ブロックを空気にして削除する
            getBlock().setType(Material.AIR);
        }
    }

    @Override
    public String toString() {
        return "CustomBlock{location=" + location + ", baseMaterial=" + baseMaterial + 
               ", customName='" + customName + "', customData='" + customData + "'}";
    }
}
