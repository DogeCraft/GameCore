package com.daansander.gamecore;

import com.daansander.gamecore.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Daan Meijer
 * @version 1.0
 */
public class GameCore extends JavaPlugin {

    public void onEnable() {
        GameSign.loadSigns(this, Bukkit.getWorlds().get(0));
        GameSign.startUpdater(this);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    public void onDisable() {
        GameSign.saveSigns(this, Bukkit.getWorlds().get(0));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("joingame") && sender instanceof Player) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Insufficient arguments usage: joingame <id>");
                return true;
            }

            Game game = Game.getGame(Integer.parseInt(args[0]));

            if(game == null) {
                sender.sendMessage(ChatColor.RED + "Couldn't find game with id " + args[0]);
                return true;
            }

            Player player = (Player) sender;

            game.onJoin(player);
            player.sendMessage(ChatColor.GREEN + "Successfully joined the game " + game.TAG + "!");
        }
        return true;
    }
    /**
     *  if(command.getName().equalsIgnoreCase("stop")) {
     if(!sender.isOp()) return true;

     GameSign.saveSigns(this, Bukkit.getWorlds().get(0));
     sender.sendMessage(ChatColor.GREEN + "Saved the game signs");
     }
     */
}