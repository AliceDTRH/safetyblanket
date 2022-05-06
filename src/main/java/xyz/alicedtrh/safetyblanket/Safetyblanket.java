package xyz.alicedtrh.safetyblanket;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;


public final class Safetyblanket extends JavaPlugin {
    Metrics metrics;
    static int blankets = 0;

    public static final @NotNull NamespacedKey HAS_NEW_PLAYER_EFFECTS = NamespacedKey.minecraft("xyz.alicedtrh.safetyblanket.newplayer");

    public static @NotNull Logger Log() {
        return Safetyblanket.getPlugin(Safetyblanket.class).getLogger();
    }

    @Override
    public void onEnable() {
        //Register events
        Bukkit.getPluginManager().registerEvents(new SafetyblanketEvents(), this);
        int pluginId = 15141;
        this.metrics = new Metrics(this, pluginId);

    }

    @Override
    public void onDisable() {
        metrics.addCustomChart(new SingleLineChart("amount_of_people_blanketed", () -> blankets));
    }

}
