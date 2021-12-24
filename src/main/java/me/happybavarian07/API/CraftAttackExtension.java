package me.happybavarian07.API;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.happybavarian07.commands.afkcommand;
import me.happybavarian07.main.Prefix;
import me.happybavarian07.main.Utils;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CraftAttackExtension extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "ca";
    }

    @Override
    public String getAuthor() {
        return "HappyBavarian07";
    }

    @Override
    public String getVersion() {
        return "6.0.4";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if(player == null) {
            return "";
        }

        // Returns Online Player quantity
        if(params.equals("onlineplayers")) {
            int onlineplayers = 0;
            for(Player online : Bukkit.getOnlinePlayers()) {
                if(CAPluginMain.getPlugin().getConfig().getStringList("CA.world.CraftAttack_Players").contains(online.getName())) {
                    onlineplayers += 1;
                }
            }
            return String.valueOf(onlineplayers);
        }

        // Returns Player quantity
        if(params.equals("players")) {
            int players = 0;
            players = CAPluginMain.getPlugin().getConfig().getStringList("CA.world.CraftAttack_Players").size();
            return String.valueOf(players);
        }

        // Returns Cam Player quantity
        if(params.equals("cams")) {
            int cams = 0;
            cams = CAPluginMain.getPlugin().getConfig().getStringList("CA.world.Cam_Players").size();
            return String.valueOf(cams);
        }

        // Returns Online Cam Player quantity
        if(params.equals("onlinecams")) {
            int onlinecams = 0;
            for(Player online : Bukkit.getOnlinePlayers()) {
                if(CAPluginMain.getPlugin().getConfig().getStringList("CA.world.Cam_Players").contains(online.getName())) {
                    onlinecams += 1;
                }
            }
            return String.valueOf(onlinecams);
        }

        if(params.equals("playersinlobby")) {
            int playersinlobby = 0;
            for(Player online : Bukkit.getOnlinePlayers()) {
                if(CAPluginMain.getPlugin().getConfig().getStringList("Lobby-Worlds").contains(online.getWorld().getName())) {
                    playersinlobby += 1;
                }
            }
            return String.valueOf(playersinlobby);
        }

        // Returns the Prefix
        if(params.equals("prefix")) {
            return CAPluginMain.getPlugin().getPrefix();
        }

        // Returns the Prefix from someone
        if(params.startsWith("playerprefix_")) {
            Player target = Bukkit.getPlayer(params.substring(13));
            if(target == null) return Prefix.EMPTY.getInGamePrefix();

            return Utils.getPrefixFromConfig(target).getInGamePrefix();
        }

        // Returns the Suffix from someone
        if(params.startsWith("playersuffix_")) {
            Player target = Bukkit.getPlayer(params.substring(13));
            if(target == null) return Prefix.EMPTY.getInGameSuffix();

            return Utils.getPrefixFromConfig(target).getInGameSuffix();
        }
        if(params.endsWith("_prefix")) {
            String[] args = params.split("_");
            if(args.length < 2) return null;
            if(args[1].equalsIgnoreCase("prefix")) {

                try {
                    Prefix prefix = Prefix.valueOf(args[0].toUpperCase());
                    return prefix.getInGamePrefix();
                } catch (NullPointerException ignored) {}
            }
        }
        if(params.endsWith("_suffix")) {
            String[] args = params.split("_");
            if(args.length < 2) return null;
            if(args[1].equalsIgnoreCase("suffix")) {
                try {
                    Prefix prefix = Prefix.valueOf(args[0].toUpperCase());
                    return prefix.getInGameSuffix();
                } catch (NullPointerException ignored) {}
            }
        }
        if(params.endsWith("_config")) {
            String[] args = params.split("_");
            if(args.length < 2) return null;
            if(args[1].equalsIgnoreCase("config")) {
                try {
                    Prefix prefix = Prefix.valueOf(args[0].toUpperCase());
                    return prefix.getConfigName();
                } catch (NullPointerException ignored) {
                    return null;
                }
            }
        }
        if(params.contains("afk_")) {
            String[] args = params.split("_");
            if(args[0].equalsIgnoreCase("afk")) {
                Player target = Bukkit.getPlayer(args[1]);
                if(target == null) return null;
                return String.valueOf(afkcommand.getAfkplayers().containsKey(target));
            }
        }

        // Returns Rules Activated Boolean
        if(params.equals("rulesactivated")) {
            return String.valueOf(CAPluginMain.getPlugin().isRulesActivated());
        }

        // Returns World Change Check Boolean
        if(params.equals("worldchangecheck")) {
            return String.valueOf(CAPluginMain.getPlugin().isWorldChangeCheck());
        }
        return null;
    }
}
