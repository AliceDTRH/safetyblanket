package xyz.alicedtrh.safetyblanket;

import lol.hyper.githubreleaseapi.GitHubReleaseAPI;
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

import static xyz.alicedtrh.safetyblanket.SafetyBlanketConfig.CHECK_FOR_UPDATES;


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

        /*ConfigManager config = */
        ConfigManager.create(this).target(SafetyBlanketConfig.class).saveDefaults().load();
        new CommandParser(this.getResource("command.rdcml")).parse().register("safetyblanket", this);

        if (CHECK_FOR_UPDATES) {

            try {
                GitHubReleaseAPI api = new GitHubReleaseAPI("safetyblanket", "AliceDTRH");
                int buildsBehind = api.getBuildsBehind(api.getReleaseByTag(this.getDescription().getVersion()));
                if (buildsBehind > 0) {
                    getLogger().warning("This version of " + getDescription().getFullName() + " is " + buildsBehind + "versions behind the latest released version. Please consider updating.");
                }
            } catch (Exception e) {
                getLogger().warning("Failed to check for plugin updates. Please check manually. Reason: " + e.getLocalizedMessage());
            }

        }
    }

    @Override
    public void onDisable() {
        metrics.addCustomChart(new SingleLineChart("amount_of_people_blanketed", () -> blankets));
    }

    @CommandHook("disableuserblanket")
    public void onCommandDisableSafetyBlanket(CommandSender sender, Player player) {
        new ExpireSafetyBlanketTask(player).run();
        sender.sendMessage("Forcefully expired user blanket.");
    }

    @CommandHook("enableuserblanket")
    public void onCommandEnableSafetyBlanket(CommandSender sender, Player player, Integer ticks) {
        sender.sendMessage("Warning! This is only used for testing. When the user logs out and back in, the blanket will disappear.");
        new SafetyblanketEvents().addSafetyBlanket(player, ticks);
        sender.sendMessage(String.format("%s has been given test blanket.", player.getName()));
    }

}
