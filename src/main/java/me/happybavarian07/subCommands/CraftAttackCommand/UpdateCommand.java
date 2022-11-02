package me.happybavarian07.subCommands.CraftAttackCommand;/*
 * @Author HappyBavarian07
 * @Date 24.12.2021 | 14:19
 */

import de.happybavarian07.adminpanel.utils.NewUpdater;
import de.happybavarian07.adminpanel.utils.Utils;
import me.happybavarian07.commandmanagement.CommandData;
import me.happybavarian07.commandmanagement.SubCommand;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@CommandData()
public class UpdateCommand extends SubCommand {
    private final NewUpdater updater = plugin.getUpdater();

    public UpdateCommand(String mainCommandName) {
        super(mainCommandName);
    }

    public boolean handle(CommandSender sender, String[] args) {
        if (args.length != 1) {
            return false;
        }
        boolean check = updater.updateAvailable();
        if (args[0].equalsIgnoreCase("check")) {
            if (check) {
                updater.getMessages().sendUpdateMessage(sender);
            } else {
                updater.getMessages().sendNoUpdateMessage(sender);
            }
        } else if (args[0].equalsIgnoreCase("download")) {
            if (check) {
                try {
                    updater.downloadLatestUpdate(false, true, true);
                    sender.sendMessage(Utils.chat(
                            "&aNew Version now available in the downloaded-update Folder! (Further Actions required)"));
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(Utils.chat(CAPluginMain.getPrefix() + " &cSomething went completely wrong!"));
                }
            } else {
                updater.getMessages().sendNoUpdateMessage(sender);
            }
        } else if (args[0].equalsIgnoreCase("forcedownload")) {
            try {
                updater.downloadLatestUpdate(false, true, true);
                sender.sendMessage(Utils.chat(CAPluginMain.getPrefix() + "&aForce Download finished!"));
                sender.sendMessage(Utils.chat(CAPluginMain.getPrefix() +
                        "&aNew Version now available in the downloaded-update Folder! (Further Actions required)"));
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(Utils.chat(CAPluginMain.getPrefix() + " &cSomething went completely wrong!"));
            }
        } else if (args[0].equalsIgnoreCase("replace")) {
            if (check) {
                try {
                    updater.downloadLatestUpdate(true, true, true);
                    sender.sendMessage(Utils.chat(CAPluginMain.getPrefix() + "&aNew Version now available to play! (No further Actions required)"));
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(Utils.chat(CAPluginMain.getPrefix() + " &cSomething went completely wrong!"));
                }
            } else {
                updater.getMessages().sendNoUpdateMessage(sender);
            }
        } else if (args[0].equalsIgnoreCase("forcereplace")) {
            try {
                updater.downloadLatestUpdate(true, true, true);
                sender.sendMessage(Utils.chat(CAPluginMain.getPrefix() + "&aForce Replace finished!"));
                sender.sendMessage(Utils.chat(CAPluginMain.getPrefix() + "&aNew Version now available to play! (No further Actions required)"));
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(Utils.chat(CAPluginMain.getPrefix() + " &cSomething went completely wrong!"));
            }
        } else if (args[0].equalsIgnoreCase("getlatest")) {
            try {
                JSONObject websiteData = updater.getObjectFromWebsite("https://api.spiget.org/v2/resources/" + updater.getResourceID() + "/updates/latest");
                String currentVersion = updater.getPluginVersion();
                String versionName = updater.getLatestVersionName();
                String versionID = String.valueOf(websiteData.getInt("id"));
                String versionTitle = websiteData.getString("title");
                String versionDescriptionEncoded = websiteData.getString("description");
                String versionDescriptionDecoded = updater.html2text(new String(Base64.getDecoder().decode(versionDescriptionEncoded)));
                String versionLikes = String.valueOf(websiteData.getInt("likes"));
                String versionDate = String.valueOf(websiteData.getInt("date"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&bCurrent Version: &c" + currentVersion + "&r\n" +
                                "&bNew Version Name: &c" + versionName + "&r\n" +
                                "&bNew Version ID: &c" + versionID + "&r\n" +
                                "&bNew Version Title: &c" + versionTitle + "&r\n" +
                                "&bNew Version Description: &c" + versionDescriptionDecoded + "&r\n" +
                                "&bNew Version Likes: &c" + versionLikes + "&r\n" +
                                "&bNew Version Date: &c" + versionDate));
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(Utils.chat(CAPluginMain.getPrefix() + " &cSomething went completely wrong!"));
            }
        }
        return true;
    }

    @Override
    public boolean onPlayerCommand(Player player, String[] args) {
        return handle(player, args);
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender sender, String[] args) {
        return handle(sender, args);
    }

    @Override
    public String name() {
        return "update";
    }

    @Override
    public String info() {
        return "The Update Command";
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }

    @Override
    public Map<Integer, String[]> subArgs() {
        Map<Integer, String[]> subArgs = new HashMap<>();
        subArgs.put(1, new String[]{"check", "download", "forcedownload", "replace", "forcereplace", "getlatest"});
        return subArgs;
    }

    @Override
    public String syntax() {
        return "/ca update <check|download|forcedownload|replace|forcereplace|getlatest>";
    }

    @Override
    public String permission() {
        return "ca.admin.update";
    }
}
