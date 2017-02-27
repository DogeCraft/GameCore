package com.daansander.gamecore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * A class that manages all the join {@link Sign}'s for the {@link Game} objects
 *
 * @author Daan Meijer
 * @since 1.0
 */
public final class GameSign {

    public final String GAME_TAG;
    public List<Sign> signs = new ArrayList<>();

    private static List<GameSign> gameSigns = new ArrayList<>();

    /**
     * Constructor for a {@link GameSign} object
     *
     * @param game_tag tag of the game to create je join {@link Sign} for
     */
    protected GameSign(String game_tag) {
        this.GAME_TAG = game_tag;
    }

    /**
     * Constructor for a {@link GameSign} object
     *
     * @param game_tag tag of the game to create je join {@link Sign} for
     * @param signs    all the join {@link Sign}'s
     */
    protected GameSign(String game_tag, final List<Sign> signs) {
        this.GAME_TAG = game_tag;
        this.signs = signs;
        this.gameSigns.add(this);
    }

    /**
     * Get a {@link GameSign} object of the {@link Game} that has been defined to join
     *
     * @param game_tag name of the {@link Game} that has been defined to join
     * @return a {@link GameSign} object with the corresponding {@link Game} tag that has been defined to join
     */
    public static GameSign getGameSign(String game_tag) {
        for (int i = 0; i < gameSigns.size(); i++) {
            GameSign gameSing = gameSigns.get(i);
            if (gameSing.GAME_TAG.equalsIgnoreCase(game_tag))
                return gameSing;
        }
        return null;
    }

    /**
     * Adds a {@link Sign} object to be able to join a game if the {@link Sign} isn't already added
     *
     * @param sign the {@link Sign} object to add
     */
    public static void addSign(Sign sign) {
        GameSign gameSign = GameSign.getGameSign(sign.getLine(1));

        if (gameSign == null)
            new GameSign(sign.getLine(1), new ArrayList<>(Arrays.asList(sign)));
        else {
            if (!gameSign.signs.contains(sign))
                gameSign.signs.add(sign);
        }
    }

    /**
     * Checks if the {@link Game} join {@link Sign} object is already registered
     *
     * @param sign the {@link Sign} object to check if it's already registered
     * @return if the {@link Sign} already is registered
     */
    public static boolean signExists(Sign sign) {
        GameSign gameSign = GameSign.getGameSign(sign.getLine(1));

        if (gameSign == null) return false;
        return gameSign.signs.contains(sign);
    }

    /**
     * Loads all the registered {@link Game} join {@link Sign}'s from the {@link World}
     *
     * @param plugin the {@link JavaPlugin} that owns/manages the {@link Sign}'s
     * @param world  the {@link World} to load the {@link Game} join {@link Sign}'s from
     */
    public static void loadSigns(JavaPlugin plugin, final World world) {
        if (!world.hasMetadata("signs")) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Map<String, List<Location>> sign_locations = (Map<String, List<Location>>) world.getMetadata("signs").get(0).value();

                System.out.println(world.getMetadata("signs").get(0).value());
                if (sign_locations == null) {
                    System.out.println("ERRNO");
                    return;
                }
                List<String> game_tags = new ArrayList<>(sign_locations.keySet());

                for (int i = 0; i < game_tags.size(); i++) {
                    String game_tag = game_tags.get(i);
                    List<Location> locations = sign_locations.get(game_tag);
                    List<Sign> signs = new ArrayList<>();

                    for (int s = 0; s < locations.size(); s++)
                        signs.add((Sign) world.getBlockAt(locations.get(s)).getState());

                    new GameSign(game_tag, signs);
                }
            }
        });
    }

    /**
     * Saves all the {@link Game} join {@link Sign}'s into the {@link World}'s metadata
     *
     * @param plugin the {@link JavaPlugin} that owns/manages the {@link Sign}'s
     * @param world  the {@link World} to save the {@link Game} join {@link Sign}'s to
     */
    public static void saveSigns(final JavaPlugin plugin, final World world) {
        final Map<String, List<Location>> sign_locations = new HashMap<>();

        for (int i = 0; i < gameSigns.size(); i++) {
            GameSign gameSign = gameSigns.get(i);

            List<Sign> signs = gameSign.signs;
            List<Location> locations = new ArrayList<>();

            for (int l = 0; l < signs.size(); l++)
                locations.add(signs.get(l).getLocation());

            sign_locations.put(gameSign.GAME_TAG, locations);
        }

        Bukkit.broadcastMessage("SAVING " + sign_locations.size() + " SIGNS!");
        world.removeMetadata("signs", plugin);
        world.setMetadata("signs", new MetadataValue() {
            @Override
            public Object value() {
                return sign_locations;
            }

            @Override
            public int asInt() {
                return 0;
            }

            @Override
            public float asFloat() {
                return 0;
            }

            @Override
            public double asDouble() {
                return 0;
            }

            @Override
            public long asLong() {
                return 0;
            }

            @Override
            public short asShort() {
                return 0;
            }

            @Override
            public byte asByte() {
                return 0;
            }

            @Override
            public boolean asBoolean() {
                return false;
            }

            @Override
            public String asString() {
                return null;
            }

            @Override
            public Plugin getOwningPlugin() {
                return plugin;
            }

            @Override
            public void invalidate() {

            }
        });
        //world.save();
    }

    /**
     * Starts the timer that manages the {@link Game} join {@link Sign}'s to change to empty {@link Game}
     *
     * @param plugin the {@link JavaPlugin} that owns/manages the {@link Sign}'s
     */
    public static void startUpdater(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < gameSigns.size(); i++) {
                    GameSign gameSign = gameSigns.get(i);
                    List<Game> games = (gameSign.GAME_TAG.equalsIgnoreCase("all")) ?
                            Game.getGameInState(Game.GameState.WAITING) : Game.getGameInState(Game.GameState.WAITING, gameSign.GAME_TAG);

                    if (games == null || games.size() == 0) continue;

                    List<Sign> signs = gameSign.signs;

                    for (int s = 0; s < signs.size(); s++) {
                        Game game = games.get(0);

                        Sign sign = signs.get(s);
                        sign.setLine(2, game.TAG);
                        sign.update();

                        games.remove(game);
                    }
                }
            }
        }, 0, 10);
    }
}