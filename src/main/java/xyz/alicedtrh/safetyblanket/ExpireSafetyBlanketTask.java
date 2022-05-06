package xyz.alicedtrh.safetyblanket;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ExpireSafetyBlanketTask extends BukkitRunnable {

    private final Player player;

    public ExpireSafetyBlanketTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if(!player.isValid()) { return; }
        if(!player.getPersistentDataContainer().has(Safetyblanket.HAS_NEW_PLAYER_EFFECTS)) { return; }
        player.setAffectsSpawning(true);
        player.removePotionEffect(PotionEffectType.REGENERATION);
        player.addPotionEffect(PotionEffectType.REGENERATION.createEffect(20, 2));
        player.sendMessage("Your new player protection has expired. Stay safe!");
        player.getPersistentDataContainer().remove(Safetyblanket.HAS_NEW_PLAYER_EFFECTS);
    }
}
