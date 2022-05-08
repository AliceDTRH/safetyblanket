package xyz.alicedtrh.safetyblanket;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import redempt.redlib.config.annotations.Comment;

@SuppressFBWarnings("MS_SHOULD_BE_FINAL") // This is how the library told me to do it, I don't see any issues with it.
public class SafetyBlanketConfig {
    @Comment("(c) AliceDTRH 2022")

    @Comment("This is used to keep track of the configuration file version, please don't touch it.")
    public static String config_version = "1.2.0";

    @Comment("This is used to debug the plugin, I suggest you don't touch this unless I specifically ask you to.")
    public static boolean DEBUG = false;
    @Comment("You can touch things below: ")
    @Comment("The amount of time in milliseconds that a user will be considered new to the server. (Default: 900000 - Which means 15 minutes)")
    public static int NEW_PLAYER_DURATION = 900000;

    @Comment("Should mobs target new users? Enemies will still target new users when attacked in most cases. (Default: true)")
    public static boolean PREVENT_TARGETING = true;

    @Comment("Should mobs spawns be prevented near new users? (Default: true)")
    public static boolean PREVENT_MOB_SPAWNS = true;

    @Comment("Should we decrease fall damage for new users? (Default: true)")
    public static boolean DECREASE_FALL_DAMAGE = true;

    @Comment("How much should we decrease fall damage by? (Default: 0.65 - which means 65%)")
    @Comment("Example: 10 half hearts of damage * 0.65 = 6.5 half hearts of damage")
    public static double FALL_DAMAGE_REDUCTION_PERCENT = 0.65;

    @Comment("Should we give new users an ambient regeneration effect? (Default: true)")
    public static boolean REGEN_BOOST = true;

    @Comment("Should we re-enable mob spawns early? (Default: true)")
    public static boolean EARLY_MOB_SPAWN_DISABLE = true;

    @Comment("How far along the new user path should we re-enable mob spawns? (Default: 0.5 - which means 50%)")
    public static double EARLY_MOB_SPAWN_DISABLE_PERCENT = 0.5;

    @Comment("Disable the nag about using /reload (Default: false)")
    public static boolean I_WONT_COMPLAIN_WHEN_EVERYTHING_BREAKS = false;

    @Comment("Should this plugin check for updates on server start? (Default: True)")
    public static boolean CHECK_FOR_UPDATES = true;
}
