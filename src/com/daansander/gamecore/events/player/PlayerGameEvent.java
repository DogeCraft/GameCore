package com.daansander.gamecore.events.player;

import com.daansander.gamecore.Game;
import com.daansander.gamecore.events.GameEvent;
import org.bukkit.entity.Player;

/**
 * Superclass for all {@link Game} event's that involve {@link Player}'s
 *
 * @author Daan Meijer
 * @since 1.0
 */
public abstract class PlayerGameEvent extends GameEvent {

    private Player player;

    /**
     * Constructor for a {@link PlayerGameEvent} object
     *
     * @param game   the game where the event has been executed from
     * @param player the player that is involved with the event
     * @see Game
     * @see GameEvent
     */
    public PlayerGameEvent(Game game, Player player) {
        super(game);
        this.player = player;
    }

    /**
     * Getter for the {@link PlayerGameEvent#player} object
     *
     * @return the player that's involved with the event
     */
    public Player getPlayer() {
        return player;
    }
}