package me.happybavarian07.discord;

import me.happybavarian07.main.CAPluginMain;
import me.happybavarian07.main.Utils;
import me.happybavarian07.mysql.MySQLHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DiscordVerify implements CommandExecutor, Listener {
    public final HashMap<UUID, String> uuidCodeMap;
    public final HashMap<UUID, String> uuidIdMap;
    public final List<UUID> verifiedMembers;
    private final CAPluginMain plugin;
    private final MySQLHandler mySQLHandler;
    private final JDA bot;
    private Guild guild;

    public DiscordVerify(JDA bot) {
        this.bot = bot;
        this.plugin = CAPluginMain.getPlugin();
        this.mySQLHandler = plugin.getMySQLHandler();
        this.uuidCodeMap = new HashMap<>();
        this.uuidIdMap = new HashMap<>();
        this.verifiedMembers = new ArrayList<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("verify").setExecutor(this);
        plugin.getCommand("unlink").setExecutor(this);
        Bukkit.getScheduler().runTaskLater(plugin, () -> guild = bot.getGuilds().get(0), 100L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (mySQLHandler.isVerified(e.getPlayer().getUniqueId().toString())) {
            verifiedMembers.add(e.getPlayer().getUniqueId());
        }
    }

    // TODO Fertig machen
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("Console.ExecutesPlayerCommand", null));
                return true;
            }
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("verify")) {
                if (mySQLHandler.isVerified(player.getUniqueId().toString())) {
                    player.sendMessage(Utils.format(player, "&cSorry! You are already verified!", CAPluginMain.getPrefix()));
                    return true;
                }
                if (!uuidCodeMap.containsKey(player.getUniqueId())) {
                    player.sendMessage(Utils.format(player, "&cNot pending verification process, %player_name%!", CAPluginMain.getPrefix()));
                    return true;
                }
                if (args.length != 1) {
                    player.sendMessage(plugin.getLanguageManager().getMessage(args.length > 1 ? "Player.Commands.TooManyArguments" : "Player.TooFewArguments", player));
                    return true;
                }
                String actualCode = uuidCodeMap.get(player.getUniqueId());
                if (!actualCode.equals(args[0])) {
                    player.sendMessage(Utils.format(player, "&cCode is not valid! Check again, %player_name%!", CAPluginMain.getPrefix()));
                    return true;
                }
                String discordId = uuidIdMap.get(player.getUniqueId());
                Member target = guild.getMemberById(discordId);
                if (target == null) {
                    uuidCodeMap.remove(player.getUniqueId());
                    uuidIdMap.remove(player.getUniqueId());
                    player.sendMessage(Utils.format(player, "&cError! It seems that you left our Discord Server!", CAPluginMain.getPrefix()));
                    return true;
                }
                uuidCodeMap.remove(player.getUniqueId());
                uuidIdMap.remove(player.getUniqueId());
                verifiedMembers.add(player.getUniqueId());
                Role verifiedRole = guild.getRolesByName("Verified", false).get(0);
                guild.addRoleToMember(target, verifiedRole).queue();
                target.getUser().openPrivateChannel().complete().sendMessage(":white_check_mark: **|** Verification successfully, you have linked your account with MC Account: " + player.getName()).queue();
                target.getUser().openPrivateChannel().complete().sendMessage(":x: **|** This is not You! Contact the Server Administrator for Help! ").queue();
                player.sendMessage(Utils.format(player, "&aYou have been verified correctly!\n&aYou linked your Account with " +
                        "&a" + target.getUser().getName() + "#" + target.getUser().getDiscriminator() +
                        "\n&aCheck your Discord!", CAPluginMain.getPrefix()));
                mySQLHandler.setVerified(player.getUniqueId().toString(), true, discordId);
                return true;
            }
            if (command.getName().equalsIgnoreCase("unlink")) {
                if (!mySQLHandler.isVerified(player.getUniqueId().toString())) {
                    player.sendMessage(Utils.format(player, "&cSorry! You are not verified!", CAPluginMain.getPrefix()));
                    return true;
                }
                Member target = guild.getMemberById(mySQLHandler.getDiscordID(player.getUniqueId().toString()));
                Role verifiedRole = guild.getRolesByName("Verified", false).get(0);
                guild.removeRoleFromMember(target, verifiedRole).queue();
                mySQLHandler.setVerified(player.getUniqueId().toString(), false, "");
                player.sendMessage(Utils.format(player, "&aYou successfully unlinked your Account!", CAPluginMain.getPrefix()));
                target.getUser().openPrivateChannel().complete().sendMessage(":x: **|** Your Account successfully has been unlinked!").queue();

            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
