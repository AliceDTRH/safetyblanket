# safetyblanket
This plugin helps people get used to your server by giving them various temporary bonuses, or in other words a safety blanket.
This is especially useful for users on other devices or new to the game.

## Features
* Customizable settings<br>
  All parts of the mod are fully configurable in the configuration file
* Decreased mob spawning<br>
  Disable _natural_ mob spawning around the player
* Prevent mobs from targeting player<br>
  Make mobs leave new players alone (except if they get attacked by the player)
* Decreased fall damage<br>
  Help users get used to fall damage without having to start over
* Give users regeneration<br>
  Want to make sure players have time to setup a base? Give them a regeneration boost!

## Commands
* ```disableuserblanket [user]``` Disable a users blanket early. If they still have time left on their blanket, they will get it back when they rejoin.
* ```enableuserblanket [user] [ticks]``` Give user a forced temporary blanket. This is only for testing and will be removed when they rejoin.

## Metrics
This plugin uses bstats Metrics - the same Metrics system used by all Bukkit implementations.<br>
You can find their privacy policy at https://bstats.org/privacy-policy and can disable it in the ```plugins/bStats/config.yml``` file.<br>
I'd really appreciate it if you kept it on though, it helps me figure out if my plugin is being used by anyone and on what types of systems :)<br>
  
