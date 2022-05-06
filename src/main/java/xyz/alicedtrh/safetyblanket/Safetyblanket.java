package xyz.alicedtrh.safetyblanket;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.config.ConfigManager;

import java.util.logging.Logger;


public final class Safetyblanket extends JavaPlugin {
    Metrics metrics;
    static int blankets = 0;

    public static final @NotNull NamespacedKey HAS_NEW_PLAYER_EFFECTS = NamespacedKey.minecraft("xyz.alicedtrh.safetyblanket.newplayer");

    public static Logger log() {
        return Safetyblanket.getPlugin(Safetyblanket.class).getLogger();
    }

    @Override
    public void onEnable() {
        //Register events
        Bukkit.getPluginManager().registerEvents(new SafetyblanketEvents(), this);

        int pluginId = 15141;
        this.metrics = new Metrics(this, pluginId);

        ConfigManager config = ConfigManager.create(this).target(SafetyBlanketConfig.class).saveDefaults().load();
        new CommandParser(this.getResource("command.rdcml")).parse().register("safetyblanket", this);
    }

    @Override
    public void onDisable() {
        metrics.addCustomChart(new SingleLineChart("amount_of_people_blanketed", () -> blankets));
    }

    @CommandHook("disableuserblanket")
    public void onCommandSafetyBlanket(CommandSender sender, Player arg1) {
        new ExpireSafetyBlanketTask(arg1).run();
        sender.sendMessage("Forcefully expired user blanket.");
    }

}
