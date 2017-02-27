package com.daansander.gamecore.events.player;

import com.daansander.gamecore.Game;
import org.bukkit.entity.Player;

/**
 * Event that is called when a player joins a game
 *
 * @author Daan Meijer
 * @since 1.0
 */
public class PlayerJoinGameEvent extends PlayerGameEvent {

    /**
     * Constructor for a {@link PlayerJoinGameEvent} object
     *
     * @param game   the game where the event has been executed from
     * @param player the player that is involved with the event
     * @see PlayerGameEvent
     */
    public PlayerJoinGameEvent(Game game, Player player) {
        super(game, player);
    }
}