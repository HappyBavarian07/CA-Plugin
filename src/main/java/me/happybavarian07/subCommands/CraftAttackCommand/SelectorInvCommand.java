package me.happybavarian07.subCommands.CraftAttackCommand;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 15:55
 */

import me.happybavarian07.GUIs.SelectorInv;
import me.happybavarian07.SubCommand;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SelectorInvCommand extends SubCommand {
    @Override
    public boolean onCommand(Player player, String[] args) {
        if(args.length != 0) {
            return false;
        }
        SelectorInv.openSelectorInv(player);
        player.sendMessage(lgm.getMessage("Player.OpenSelector", player));
        return true;
    }

    @Override
    public String name() {
        return "selector";
    }

    @Override
    public String info() {
        return "Opens the Selector Inventory";
    }

    @Override
    public String[] aliases() {
        return new String[] {"sel"};
    }

    @Override
    public Map<Integer, String[]> subArgs() {
        return new HashMap<>();
    }

    @Override
    public String syntax() {
        return "/ca selector";
    }

    @Override
    public String permission() {
        return "";
    }
}
