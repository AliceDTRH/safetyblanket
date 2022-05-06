package xyz.alicedtrh.safetyblanket;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class ExpireSafetyBlanketTask extends BukkitRunnable {

    // It's not precise, but it doesn't really need to be.
    // We just want to give them a decent amount of food and health to start with.
    private static final float PLAYER_PROBABLY_MAX_FOOD = 20;
    private static final double PLAYER_PROBABLY_MAX_HEALTH = 20.0;
    private final Player player;

    public ExpireSafetyBlanketTask(Player player) {
        this.player = player;
    }

    /**
     * Remove blanket from player :(
     * Also gives them some advice on how to stay safe and resets their health and food.
     */
    @Override
    public void run() {
        if(!player.isValid()) { return; }
        if(!player.getPersistentDataContainer().has(Safetyblanket.HAS_NEW_PLAYER_EFFECTS)) { return; }
        player.setAffectsSpawning(true);
        player.removePotionEffect(PotionEffectType.REGENERATION);
        if(player.getHealth() < PLAYER_PROBABLY_MAX_HEALTH) {
            player.setHealth(PLAYER_PROBABLY_MAX_HEALTH);
        }
        if(player.getSaturation() < PLAYER_PROBABLY_MAX_FOOD) {
            player.setSaturation(PLAYER_PROBABLY_MAX_FOOD);
        }
        player.sendMessage(Component.text("Your new player protection has expired.", TextColor.color(255, 0, 0)));
        Audience.audience(player).sendActionBar(Component.text("Your new player protection has expired.", TextColor.color(255, 0, 0)));
        player.sendMessage("In order to stay alive, make sure to light up your base and don't go outside at night.");
        player.sendMessage("Staying well fed will help you regenerate your health and weapons can be used to defend yourself.");
        player.sendMessage("For more info, feel free to ask people on the server or check the wiki at https://minecraft.fandom.com/");
        player.sendMessage("Good luck!");
        player.getPersistentDataContainer().remove(Safetyblanket.HAS_NEW_PLAYER_EFFECTS);
    }
}
