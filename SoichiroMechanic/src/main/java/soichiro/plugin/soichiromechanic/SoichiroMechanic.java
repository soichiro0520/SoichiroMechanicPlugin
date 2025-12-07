/*
 * SoichiroMechanic プラグイン
 * このファイルはプラグインのエントリポイントです。
 * 作者: soichiro0520
 */
package soichiro.plugin.soichiromechanic;

import org.bukkit.plugin.java.JavaPlugin;


public class SoichiroMechanic extends JavaPlugin {
    
    @Override
    public void onEnable() {
        // プラグインが有効化されたときに呼ばれる処理
        // ここでリスナーの登録や初期化処理を行います（現在はログ出力のみ）
        getLogger().info("SoichiroMechanic has been enabled!");
    }

    @Override
    public void onDisable() {
        // プラグインが無効化されるときに呼ばれる処理
        // リソース解放や保存処理をここに記述します（現在はログ出力のみ）
        getLogger().info("SoichiroMechanic has been disabled!");
    }
    
}