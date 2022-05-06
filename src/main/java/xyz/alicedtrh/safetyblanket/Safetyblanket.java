package xyz.alicedtrh.safetyblanket;

import com.google.errorprone.annotations.CheckReturnValue;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import com.google.errorprone.*;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;


public final class Safetyblanket extends JavaPlugin {

    public static final @NotNull NamespacedKey HAS_NEW_PLAYER_EFFECTS = NamespacedKey.minecraft("xyz.alicedtrh.safetyblanket.newplayer");

    public static @NotNull Logger Log() {
        return Safetyblanket.getPlugin(Safetyblanket.class).getLogger();
    }

    @Override
    public void onEnable() {
        //Register events
        Bukkit.getPluginManager().registerEvents(new SafetyblanketEvents(), this);

    }

}
