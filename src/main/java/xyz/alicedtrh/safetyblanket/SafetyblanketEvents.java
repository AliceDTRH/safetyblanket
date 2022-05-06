package xyz.alicedtrh.safetyblanket;

import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.framework.qual.Unused;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH;
import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.*;


public class SafetyblanketEvents implements Listener {
    Safetyblanket plugin = Safetyblanket.getPlugin(Safetyblanket.class);
    /**
     * The amount of time in milliseconds that a player is considered new to the server.
     */
    public static final int NEW_PLAYER_DURATION = 900000;

    /**
     * Reasons entities should NOT ignore
     */
    final EntityTargetEvent.TargetReason[] reasons = new EntityTargetEvent.TargetReason[]{
            TARGET_ATTACKED_ENTITY, TARGET_ATTACKED_OWNER, TARGET_ATTACKED_NEARBY_ENTITY, COLLISION, CUSTOM
    };

    /**
     * Prevent enemies from targeting players under blanket
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTargetLivingEntityEvent(@NotNull EntityTargetLivingEntityEvent event) {
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
     * Decrease falldamage by 35% if the falldamage isn't too high for players under blanket
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageEvent(@NotNull EntityDamageEvent event) {
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

        double playerMaxHealth = 20.0;
        AttributeInstance playerMaxHealthAttribute = player.getAttribute(GENERIC_MAX_HEALTH);

        if (playerMaxHealthAttribute != null) {
            playerMaxHealth = playerMaxHealthAttribute.getValue();
        }

        if (event.getFinalDamage() <= (playerMaxHealth / 2)) {
            event.setDamage(Math.floor(event.getFinalDamage() * 0.65));
            player.sendMessage("You took less fall damage because you're new to the server. Please be careful.");
        }
    }

    /**
     * Ensure monsters attack players under blanket, when attacked by said player.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntityEvent(@NotNull EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Monster)) {
            return;
        }
        Monster monster = (Monster) event.getEntity();

        if(event.getDamager() instanceof Player && isPlayerNew((Player) event.getDamager())) {
            monster.setTarget((Player)event.getDamager());
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
        new ExpireSafetyBlanketTask(player).runTaskLater(plugin, timeUntilRegular(player, TimeUnit.TICKS));
        player.getPersistentDataContainer().set(Safetyblanket.HAS_NEW_PLAYER_EFFECTS, PersistentDataType.SHORT, (short) 1);
        if(!player.hasPlayedBefore()) {
            player.sendMessage("Enemies won't target you for "+timeUntilRegular(player, TimeUnit.MINUTES)+" minutes, unless you attack them first.");
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, timeUntilRegular(player, TimeUnit.TICKS), 0, true, true, false));
        }
        player.sendMessage("You have received a REGENERATION boost because you're new to this server.");
        player.sendMessage("Use this time to get a base with a bed and a source of food going.");

        player.setAffectsSpawning(false);
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
            Safetyblanket.Log().warning("Overflow when trying to calculate player time: "+e.getLocalizedMessage());
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
