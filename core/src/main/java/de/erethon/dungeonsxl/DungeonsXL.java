/*
 * Copyright (C) 2012-2021 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl;

import de.erethon.dungeonsxl.api.DungeonModule;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.mob.DungeonMob;
import de.erethon.dungeonsxl.api.player.GroupAdapter;
import de.erethon.dungeonsxl.api.player.PlayerCache;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.api.world.EditWorld;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;

/**
 * @author Frank Baumann, Tobias Schmitz, Daniel Saukel
 */
public class DungeonsXL extends DREPlugin implements DungeonsAPI {

    /* Plugin & lib instances */
    private static DungeonsXL instance;

    /* Constants */
    public static final String LATEST_IXL = "1.0.2";
    public static final String[] EXCLUDED_FILES = {"config.yml", "uid.dat", "DXLData.data", "data"};

    /* Folders of internal features */
    public static final File ANNOUNCERS = new File(SCRIPTS, "announcers");
    public static final File SIGNS = new File(SCRIPTS, "signs");
    public static final File COMMANDS = new File(SCRIPTS, "commands");

    /* Legacy */
    public static final Map<String, Class<? extends DungeonSign>> LEGACY_SIGNS = new HashMap<>();

    /* Caches & registries */
    private Set<DungeonModule> modules = new HashSet<>();
    private Collection<GroupAdapter> groupAdapters = new HashSet<>();
    private PlayerCache playerCache;
    private Collection<Game> gameCache;

    /* Global state variables */
    private boolean loaded, loadingWorld;

    @Override
    public void onEnable() {
        super.onEnable();
        String ixlVersion = manager.isPluginEnabled("ItemsXL") ? manager.getPlugin("ItemsXL").getDescription().getVersion() : "";
        if (ixlVersion.startsWith("0.") || ixlVersion.matches("1.0[\\.]?[1]?")) {
            getLogger().log(Level.SEVERE, "DungeonsXL requires ItemsXL v" + LATEST_IXL + " or higher to run.");
            manager.disablePlugin(this);
            return;
        }

        instance = this;
        initFolders();
        registerModule(new DXLModule());
        checkState();
        loaded = true;
    }

    @Override
    public void onDisable() {
        if (!loaded) {
            return;
        }
        loaded = false;
        deleteAllInstances();
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
    }

    public void initFolders() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        BACKUPS.mkdir();
        MAPS.mkdir();
        PLAYERS.mkdir();
        SCRIPTS.mkdir();
        ANNOUNCERS.mkdir();
        CLASSES.mkdir();
        DUNGEONS.mkdir();
        SIGNS.mkdir();
        COMMANDS.mkdir();
    }

    public void checkState() {

        for (File file : Bukkit.getWorldContainer().listFiles()) {
            if (!file.getName().startsWith("DXL_") || !file.isDirectory()) {
                continue;
            }

            if (file.getName().startsWith("DXL_Edit_")) {
                for (File mapFile : file.listFiles()) {
                    if (!mapFile.getName().startsWith(".id_")) {
                        continue;
                    }

                    String name = mapFile.getName().substring(4);

                    File resource = new File(DungeonsXL.MAPS, name);
                    File backup = new File(DungeonsXL.BACKUPS, resource.getName() + "-" + System.currentTimeMillis() + "_crashbackup");
                }
            }
        }
    }

    /* Getters and loaders */
    /**
     * @return the plugin instance
     */
    public static DungeonsXL getInstance() {
        return instance;
    }

    @Override
    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    @Override
    public Collection<Game> getGameCache() {
        return gameCache;
    }

    @Override
    public void registerModule(DungeonModule module) {
        modules.add(module);
    }

    @Override
    public void registerGroupAdapter(GroupAdapter groupAdapter) {

    }

    @Override
    public PlayerGroup createGroup(Player leader) {
        return null;
    }

    @Override
    public PlayerGroup createGroup(Player leader, Color color) {
        return null;
    }

    @Override
    public PlayerGroup createGroup(Player leader, String name) {
        return null;
    }

    @Override
    public PlayerGroup createGroup(Player leader, Dungeon dungeon) {
        return null;
    }

    @Override
    public PlayerGroup createGroup(Player leader, Collection<Player> members, String name, Dungeon dungeon) {
        return null;
    }

    @Override
    public DungeonMob wrapEntity(LivingEntity entity, GameWorld gameWorld, String triggerId) {
        return null;
    }

    /**
     * Returns a collection of the loadedGroupAdapters
     *
     * @return a collection of GroupAdapters
     */
    public Collection<GroupAdapter> getGroupAdapters() {
        return groupAdapters;
    }

    /**
     * Returns true if the plugin is not currently in the process of enabling or disabling or entirely disabled, otherwise false.
     *
     * @return true if the plugin is not currently in the process of enabling or disabling or entirely disabled, otherwise false
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Returns true if the plugin is currently loading a world, false if not.
     * <p>
     * If the plugin is loading a world, it is locked in order to prevent loading two at once.
     *
     * @return true if the plugin is currently loading a world, false if not
     */
    public boolean isLoadingWorld() {
        return loadingWorld;
    }

    /**
     * Notifies the plugin that a world is being loaded.
     * <p>
     * If the plugin is loading a world, it is locked in order to prevent loading two at once.
     *
     * @param loadingWorld if a world is being loaded
     */
    public void setLoadingWorld(boolean loadingWorld) {
        log("World loading is now " + (loadingWorld ? "LOCKED" : "UNLOCKED"));
        this.loadingWorld = loadingWorld;
    }

    @Deprecated
    private Set<Inventory> guis = new HashSet<>();

    @Deprecated
    public Set<Inventory> getGUIs() {
        return guis;
    }

    /* Getters */
    @Override
    public DungeonMob getDungeonMob(LivingEntity entity) {
        GameWorld gameWorld = getGameWorld(entity.getWorld());
        if (gameWorld == null) {
            return null;
        }
        for (DungeonMob mob : gameWorld.getMobs()) {
            if (mob.getEntity() == entity) {
                return mob;
            }
        }
        return null;
    }

    @Override
    public PlayerGroup getPlayerGroup(Player member) {
        return null;
    }

    @Override
    public Game getGame(Player player) {
        for (Game game : gameCache) {
            if (game.getPlayers().contains(player)) {
                return game;
            }
        }
        return null;
    }

    @Override
    public Game getGame(World world) {
        GameWorld gameWorld = getGameWorld(world);
        return gameWorld != null ? gameWorld.getGame() : null;
    }

    @Override
    public GameWorld getGameWorld(World world) {
        InstanceWorld instance = getInstanceWorld(world);
        return instance instanceof GameWorld ? (GameWorld) instance : null;
    }

    @Override
    public EditWorld getEditWorld(World world) {
        InstanceWorld instance = getInstanceWorld(world);
        return instance instanceof EditWorld ? (EditWorld) instance : null;
    }

    public InstanceWorld getInstanceWorld(World world) {
        return null;
    }

    @Override
    public boolean isInstance(World world) {
        return world.getName().startsWith("DXL_Game_") || world.getName().startsWith("DXL_Edit_");
    }

    @Override
    public boolean isDungeonItem(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return false;
        }
        return itemStack.getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("dungeon_item", this), PersistentDataType.BYTE);
    }

    @Override
    public ItemStack setDungeonItem(ItemStack itemStack, boolean dungeonItem) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return null;
        }
        ItemStack dIStack = itemStack.clone();
        ItemMeta meta = dIStack.getItemMeta();
        NamespacedKey key = NamespacedKey.fromString("dungeon_item", this);
        if (dungeonItem) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        } else {
            meta.getPersistentDataContainer().remove(key);
        }
        dIStack.setItemMeta(meta);
        return dIStack;
    }

    /**
     * Clean up all instances.
     */
    public void deleteAllInstances() {
    }

    /**
     * Checks if an old player wrapper instance of the user exists. If yes, the old Player of the user is replaced with the new object.
     *
     * @param player the player to check
     * @return if the player exists
     */
    public boolean checkPlayer(Player player) {
        return true;
    }

    private boolean xlDevMode = System.getProperty("XLDevMode") != null;

    public void log(String message) {
    }

    public <T> void log(String message, T t, Predicate<T> predicate) {
        if (xlDevMode && !predicate.test(t)) {
            throw new AssertionError(message);
        }
    }

}
