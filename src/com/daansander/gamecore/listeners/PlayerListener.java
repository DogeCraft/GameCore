package com.daansander.gamecore.listeners;

import com.daansander.gamecore.Game;
import com.daansander.gamecore.GameCore;
import com.daansander.gamecore.GameSign;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Daan Meijer
 */
public class PlayerListener implements Listener {

    private GameCore plugin;

    public PlayerListener(GameCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Material material = block.getType();
        if (material != Material.SIGN && material != Material.SIGN_POST && material != Material.WALL_SIGN) return;

        Sign sign = (Sign) block.getState();

        if (sign.getLine(0).equalsIgnoreCase("[game]")) {

            Player player = event.getPlayer();

            String gameLine = sign.getLine(1);
            if (Game.getGame(gameLine) == null && !gameLine.equalsIgnoreCase("all")) {
                player.sendMessage(ChatColor.RED + "Couldn't find any game with the tag: " + gameLine);
                return;
            }
            GameSign.addSign(sign);

            player.sendMessage(ChatColor.GREEN + "Successfully created a new join sign for: " + gameLine + "!");
        }
    }
}