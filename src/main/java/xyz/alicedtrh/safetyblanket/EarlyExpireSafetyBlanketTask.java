package xyz.alicedtrh.safetyblanket;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EarlyExpireSafetyBlanketTask extends BukkitRunnable {

    private final Player player;

    public EarlyExpireSafetyBlanketTask(Player player) {
        this.player = player;
    }

    /**
     * Re-enable enemy spawns.
     */
    @Override
    public void run() {
        if (player.isValid() && player.isOnline() && player.getAffectsSpawning()) {
            player.sendMessage("Enemy spawns now occur like normal.");
            player.setAffectsSpawning(false);
        }
    }
}
