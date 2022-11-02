package me.happybavarian07.discord;

import me.happybavarian07.language.LanguageManager;
import me.happybavarian07.language.PlaceholderType;
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
    private LanguageManager lgm;

    public DiscordVerify(JDA bot) {
        this.bot = bot;
        this.plugin = CAPluginMain.getPlugin();
        this.mySQLHandler = plugin.getMySQLHandler();
        this.uuidCodeMap = new HashMap<>();
        this.uuidIdMap = new HashMap<>();
        this.verifiedMembers = new ArrayList<>();
        this.lgm = plugin.getLanguageManager();
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

    // TODO Fertig machen (Language Messages, Errors beheben)
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("Console.ExecutesPlayerCommand", null, false));
                return true;
            }
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("verify")) {
                if (mySQLHandler.isVerified(player.getUniqueId().toString())) {
                    player.sendMessage(lgm.getMessage("Player.Discord.LinkAccount.AlreadyVerified", player, false));
                    return true;
                }
                if (!uuidCodeMap.containsKey(player.getUniqueId())) {
                    player.sendMessage(lgm.getMessage("Player.Discord.LinkAccount.NoPendingVerifications", player, false));
                    return true;
                }
                if (args.length != 1) {
                    player.sendMessage(plugin.getLanguageManager().getMessage(args.length > 1 ? "Player.Commands.TooManyArguments" : "Player.TooFewArguments", player, false));
                    return true;
                }
                String actualCode = uuidCodeMap.get(player.getUniqueId());
                if (!actualCode.equals(args[0])) {
                    player.sendMessage(lgm.getMessage("Player.Discord.LinkAccount.CodeNotValid", player, false));
                    return true;
                }
                String discordId = uuidIdMap.get(player.getUniqueId());
                Member target = guild.getMemberById(discordId);
                if (target == null) {
                    uuidCodeMap.remove(player.getUniqueId());
                    uuidIdMap.remove(player.getUniqueId());
                    player.sendMessage(lgm.getMessage("Player.Discord.LinkAccount.LeftDiscordServer", player, false));
                    return true;
                }
                uuidCodeMap.remove(player.getUniqueId());
                uuidIdMap.remove(player.getUniqueId());
                verifiedMembers.add(player.getUniqueId());
                Role verifiedRole = guild.getRolesByName("Verified", false).get(0);
                guild.addRoleToMember(target, verifiedRole).queue();
                target.getUser().openPrivateChannel().complete().sendMessage(":white_check_mark: **|** Verification successfully, you have linked your account with MC Account: " + player.getName()).queue();
                target.getUser().openPrivateChannel().complete().sendMessage(":x: **|** This is not You! Contact the Server Administrator for Help! ").queue();
                lgm.addPlaceholder(PlaceholderType.MESSAGE, "%discord_name%", target.getUser().getName() + "#" + target.getUser().getDiscriminator(), false);
                player.sendMessage(lgm.getMessage("Player.Discord.LinkAccount.SuccessfullyLinked", player, true));
                mySQLHandler.setVerified(player.getUniqueId().toString(), true, discordId);
                return true;
            }
            if (command.getName().equalsIgnoreCase("unlink")) {
                if (!mySQLHandler.isVerified(player.getUniqueId().toString())) {
                    player.sendMessage(lgm.getMessage("Player.Discord.UnlinkAccount.NotVerified", player, false));
                    return true;
                }
                Member target = guild.getMemberById(mySQLHandler.getDiscordID(player.getUniqueId().toString()));
                Role verifiedRole = guild.getRolesByName("Verified", false).get(0);
                guild.removeRoleFromMember(target, verifiedRole).queue();
                mySQLHandler.setVerified(player.getUniqueId().toString(), false, "");
                lgm.addPlaceholder(PlaceholderType.MESSAGE, "%discord_name%", target.getUser().getName() + "#" + target.getUser().getDiscriminator(), false);
                player.sendMessage(lgm.getMessage("Player.Discord.UnlinkAccount.SuccessfullyUnlinked", player, true));
                target.getUser().openPrivateChannel().complete().sendMessage(":x: **|** Your Account successfully has been unlinked!").queue();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
