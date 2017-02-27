package com.daansander.gamecore;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.daansander.gamecore.events.game.GameStartEvent;
import com.daansander.gamecore.events.game.GameStopEvent;
import com.daansander.gamecore.events.player.PlayerJoinGameEvent;
import com.daansander.gamecore.events.player.PlayerLeaveGameEvent;

/**
 * A superclass that is meant for easy creation of games
 *
 * @author Daan Meijer
 * @since 1.0
 */
public abstract class Game {

    public final int ID;
    public final String TAG;
    public final int MIN_PLAYES, MAX_PLAYERS;
    public final long GAME_DURATION;
    public final Plugin PLUGIN;
    private GameState gameState = GameState.WAITING;
    private int currentLobbyTime = 0;

    private List<Player> players = new ArrayList<>();
    private static List<Game> games = new ArrayList<>();

    /**
     * Constructor for an {@link Game} object that eases the creation of games
     *
     * @param plugin        the plugin that uses the api
     * @param tag           name of the game
     * @param game_duration how many seconds the {@link Game} will last set it to -1 for no duration
     * @param min_players   how many {@link Player}'s are needed to start the {@link Game}
     * @param max_players   the maximum amount of {@link Player}'s in a {@link Game}
     */
    public Game(Plugin plugin, String tag, long game_duration, int min_players, int max_players) {
        this.PLUGIN = plugin;
        this.ID = games.size();
        this.TAG = tag + "-" + ID;
        this.GAME_DURATION = game_duration;
        this.MIN_PLAYES = min_players;
        this.MAX_PLAYERS = max_players;

        games.add(this);
        startLobbyTimer(15);
    }

    /**
     * Get a {@link Game} object by it's tag. The tag is defined as TAG-ID
     *
     * @param tag name of the {@link Game} object to get
     * @return the {@link Game} object with the corresponding name
     */
    public static Game getGame(String tag) {
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            if (game.TAG.equalsIgnoreCase(tag.toLowerCase()))
                return game;
        }
        return null;
    }

    /**
     * Get a {@link Game} object from it's id
     *
     * @param id the id of the {@link Game} object
     * @return the {@link Game} object with the corresponding name
     */
    public static Game getGame(int id) {
        if (id < 0 || id > games.size() - 1) return null;
        return games.get(id);
    }

    /**
     * Gets all the {@link Game} objects with this tag without id
     *
     * @param tag name of the {@link Game} object(s)
     * @return all the {@link Game} objects with the defined tag
     */
    public static List<Game> getGamesFor(String tag) {
        List<Game> result = new ArrayList<>();
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            if (game.TAG.toLowerCase().contains(tag.toLowerCase()))
                result.add(game);
        }
        return result;
    }

    /**
     * Gets all the games that are in the defined {@link GameState}
     *
     * @param gameState the {@link GameState} to search for
     * @return all the {@link Game} objects that are in the defined state
     */
    public static List<Game> getGameInState(GameState gameState) {
        List<Game> result = new ArrayList<>();
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);

            if (game.gameState.equals(gameState))
                result.add(game);
        }
        return result;
    }

    /**
     * Gets all the games that are in the defined {@link GameState} and have the defined tag
     *
     * @param gameState the {@link GameState} to search for
     * @param tag       the tag to search for
     * @return all the {@link Game} objects that are in the defined state and have the defined tag
     */
    public static List<Game> getGameInState(GameState gameState, String tag) {
        List<Game> result = new ArrayList<>();
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);

            if (game.gameState.equals(gameState) && game.TAG.contains(tag))
                result.add(game);
        }
        return result;
    }
    
    /**
     * Gets the {@link Game} that the {@link Player} object is is in
     * 
     * @param player the {@link Player} object to get the joined {@link Game} from
     * @return the {@link Game} object that the {@link Player} currently is in will be null if the {@link Player} isn't in a {@link Game}
     */
    public static Game getGameFromPlayer(Player player) {
    	for(int i = 0; i < games.size(); i++) {
    		Game game = games.get(i);
    		
    		if(game.players.contains(player))
    			return game;
    	}
    	return null;
    }

    /**
     * Starts the timer where {@link Player}'s wait till the game starts
     *
     * @param duration how many seconds the timer will count down from
     */
    private final void startLobbyTimer(final int duration) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState != GameState.WAITING) return;
                if (players.size() >= MIN_PLAYES) {
                    currentLobbyTime--;
                    Bukkit.broadcastMessage(currentLobbyTime + "");
                    if (currentLobbyTime <= 0) {
                        onStart();
                        currentLobbyTime = 15;
                        cancel();
                    }
                } else
                    currentLobbyTime = duration;
            }
        }.runTaskTimerAsynchronously(PLUGIN, 0, 20);
    }

    /**
     * Starts the {@link Game} timer and will stop the game when it runs out
     *
     * @param duration how many seconds the game timer will last
     */
    private final void startGameTimer(long duration) {
        final BukkitRunnable updateRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState != GameState.INGAME) return;
                onUpdate();
            }
        };

        updateRunnable.runTaskTimerAsynchronously(PLUGIN, 0, 20);

        Bukkit.getScheduler().runTaskLaterAsynchronously(PLUGIN, new Runnable() {
            @Override
            public void run() {
                onStop();
                updateRunnable.cancel();
            }
        }, 20 * duration);
    }

    /**
     * Checks whether the {@link Game} is full
     *
     * @return if the amount of {@link Player}'s is more or equal to the max players
     */
    public boolean isFull() {
        return players.size() >= MAX_PLAYERS;
    }

    /**
     * Event that is called when a {@link Player} object joins the current {@link Game} object
     *
     * @param player
     */
    public void onJoin(final Player player) {
        players.add(player);
        Bukkit.getPluginManager().callEvent(new PlayerJoinGameEvent(this, player));
    }

    /**
     * Event that is called when a {@link Player} object leaves the current {@link Game} object
     *
     * @param player
     */
    public void onLeave(final Player player) {
        players.remove(player);
        Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(this, player));
    }

    /**
     * Event that is called when the {@link Game} timer has started
     */
    protected void onStart() {
		gameState = GameState.INGAME;
    	startGameTimer(GAME_DURATION);
    	Bukkit.getPluginManager().callEvent(new GameStartEvent(this));
    	
    }
    
    /**
     * Event that is called when the {@link Game} countdown timer starts
     */
    protected void onCountdownStart() {
    	gameState = GameState.STARTING;
    	
    	new BukkitRunnable() {
    		
    		int currentTimer = 4;
    		
			@Override
			public void run() {
				currentTimer--;
				
				onCountdown(currentTimer);
				
				if(currentTimer <= 1) {
					onStart();
		        	cancel();
				}
			}
		}.runTaskTimerAsynchronously(PLUGIN, 0, 20);
    }
    
    /**
     * Event that is called when the {@link Game} countdown goes a second down
     * 
     * @param currentTime the second the countdown is currently at
     */
    protected void onCountdown(int currentTime) {
    	
    }

    /**
     * Event that is called every second while the game timer is running
     */
    protected void onUpdate() {

    }

    /**
     * Event that is called when the {@link Game} timer runs out or when the {@link Game} is forced to stop
     */
    protected void onStop() {
        gameState = GameState.WAITING;
        startLobbyTimer(15);
        players.clear();
        Bukkit.getPluginManager().callEvent(new GameStopEvent(this));
    }

    /**
     * Getter for the {@link Game#players} object
     *
     * @return all the {@link Player}'s that joined the {@link Game}
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Getter for the {@link Game#games} object
     *
     * @return all the created {@link Game}'s
     */
    public static List<Game> getGames() {
        return games;
    }
    
    /**
     * Getter for the {@link Game#gameState} object
     * 
     * @return the {@link GameState} which the {@link Game} is currently in
     */
    public GameState getGameState() {
		return gameState;
	}

    /**
     * An enum that defines the state wich the {@link Game} object is currently in
     * 
     * @author Daan Meijer
     * @since 1.0
     * 
     * {@link GameState#INGAME} means that the {@link Game} is currently ingame
     * {@link GameState#WAITING} means that the {@link Game} is waiting for {@link Player}'s to start
     * {@link GameState#STARTING} means that the {@link Game} is counting down to start
     */
    public enum GameState {
        INGAME, WAITING, STARTING
    }
}