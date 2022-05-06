package xyz.alicedtrh.safetyblanket;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.*;
import static xyz.alicedtrh.safetyblanket.SafetyBlanketConfig.*;


public class SafetyblanketEvents implements Listener {
    Safetyblanket plugin = Safetyblanket.getPlugin(Safetyblanket.class);

    /**
     * Reasons entities should NOT ignore
     */
    final EntityTargetEvent.TargetReason[] reasons = new EntityTargetEvent.TargetReason[]{
            TARGET_ATTACKED_ENTITY, TARGET_ATTACKED_OWNER, TARGET_ATTACKED_NEARBY_ENTITY, COLLISION, CUSTOM
    };

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (I_WONT_COMPLAIN_WHEN_EVERYTHING_BREAKS) {
            return;
        }
        if (event.getMessage().equalsIgnoreCase("/reload") || event.getMessage().equalsIgnoreCase("/reload confirm")) {
            Bukkit.getServer().getOperators().forEach(offlinePlayer -> {
                Player player = offlinePlayer.getPlayer();
                if (player != null && player.isOnline() && player.isOp()) {
                    player.sendMessage(Component.text("[SafetyBlanket] Reload is not supported.", TextColor.color(137, 0, 0)));
                }
            });
        }


    }

    /**
     * Prevent enemies from targeting players under blanket
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTargetLivingEntityEvent(@NotNull EntityTargetLivingEntityEvent event) {
        if (!PREVENT_TARGETING) {
            return;
        }
        @Nullable LivingEntity target = event.getTarget();
        if (target == null || !target.isValid()) return;

        if (target.getType() != EntityType.PLAYER || !isPlayerNew((Player) target)) {
            return;
        }

        for (EntityTargetEvent.TargetReason badReason : this.reasons) {
            if (event.getReason() == badReason) {
                continue;
            }
            event.setCancelled(true);
        }
    }

    /**
     * Prevent enemies from targeting players under blanket
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTargetEvent(@NotNull EntityTargetEvent event) {
        if (!PREVENT_TARGETING) {
            return;
        }

        @Nullable Entity target = event.getTarget();
        if (target == null || !target.isValid()) return;

        if (target.getType() != EntityType.PLAYER || !isPlayerNew((Player) target)) {
            return;
        }

        for (EntityTargetEvent.TargetReason badReason : this.reasons) {
            if (event.getReason() == badReason) {
                continue;
            }
            event.setCancelled(true);
        }
    }

    /**
     * Decrease fall damage by 35% if the fall damage isn't too high for players under blanket
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageEvent(@NotNull EntityDamageEvent event) {
        if (!DECREASE_FALL_DAMAGE) {
            return;
        }

        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (!isPlayerNew(player)) {
            return;
        }

        event.setDamage(Math.floor(event.getFinalDamage() * FALL_DAMAGE_REDUCTION_PERCENT));
        player.sendMessage("You took less fall damage because you're new to the server. Please be careful.");

    }

    /**
     * Ensure monsters attack players under blanket, when attacked by said player.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntityEvent(@NotNull EntityDamageByEntityEvent event) {
        if (!PREVENT_TARGETING) {
            return;
        }
        if (!(event.getEntity() instanceof Monster)) {
            return;
        }
        Monster monster = (Monster) event.getEntity();

        if (event.getDamager() instanceof Player && isPlayerNew((Player) event.getDamager())) {
            monster.setTarget((Player) event.getDamager());
        }

    }

    /**
     * Setup and remove blanket
     */
    @EventHandler
    public void onPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
        if (!event.getPlayer().isValid()) {
            return;
        }

        if (!isPlayerNew(event.getPlayer())) {
            if(event.getPlayer().getPersistentDataContainer().has(Safetyblanket.HAS_NEW_PLAYER_EFFECTS)) {
                new ExpireSafetyBlanketTask(event.getPlayer()).runTask(plugin);
            }
            return;
        }

        addSafetyBlanket(event.getPlayer());

    }

    /**
     * Cover player in blanket
     */
    private void addSafetyBlanket(@NotNull Player player) {
        Safetyblanket.blankets++;

        new ExpireSafetyBlanketTask(player).runTaskLater(plugin, timeUntilRegular(player, TimeUnit.TICKS));
        player.getPersistentDataContainer().set(Safetyblanket.HAS_NEW_PLAYER_EFFECTS, PersistentDataType.SHORT, (short) 1);
        if (!player.hasPlayedBefore()) {
            if (PREVENT_TARGETING) {
                player.sendMessage("Enemies won't target you for " + timeUntilRegular(player, TimeUnit.MINUTES) + " minutes, unless you attack them first.");
            }
            if (REGEN_BOOST) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, timeUntilRegular(player, TimeUnit.TICKS), 0, true, true, false));
            }
        }
        if (REGEN_BOOST) {
            player.sendMessage("You have received a REGENERATION boost because you're new to this server.");
        }
        player.sendMessage("Use this time to get a base with a bed and a source of food going.");

        if (PREVENT_MOB_SPAWNS) {
            player.setAffectsSpawning(false);
            if (EARLY_MOB_SPAWN_DISABLE) {
                new EarlyExpireSafetyBlanketTask(player).runTaskLater(plugin, (long) (timeUntilRegular(player, TimeUnit.TICKS) * EARLY_MOB_SPAWN_DISABLE_PERCENT));
            }
        }
    }

    /**
     * Returns whether the player is still considered new to the server.
     */
    private static boolean isPlayerNew(@NotNull Player player) {
        return (System.currentTimeMillis() - player.getFirstPlayed()) < NEW_PLAYER_DURATION;
    }

    /**
     * The amount of time the player has left until they are no longer considered new to the server.
     */
    private int timeUntilRegular(@NotNull Player player, @NotNull TimeUnit timeUnit) {
        int time_in_millis;
        try {
            time_in_millis = Math.toIntExact(
                    NEW_PLAYER_DURATION - (System.currentTimeMillis() - player.getFirstPlayed())
            );
        } catch (ArithmeticException e) {
            time_in_millis = 0;
            Safetyblanket.log().warning("Overflow when trying to calculate player time: " + e.getLocalizedMessage());
        }

        // If it's less than (or equal to) zero, the result will always be zero regardless of timeUnit.
        if(time_in_millis <= 0) {
            return 0;
        }

        switch (timeUnit) {
            default:
            case MILLIS:
                return time_in_millis;
            case SECONDS:
                return time_in_millis / 1000;
            case TICKS:
                return (time_in_millis / 1000) * 20;
            case MINUTES:
                return (time_in_millis / 1000) / 60;

        }

    }


}
