/*
 * Copyright (C) 2012-2016 Frank Baumann
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
package io.github.dre2n.dungeonsxl.world;

import io.github.dre2n.commons.util.FileUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.SignData;
import io.github.dre2n.dungeonsxl.config.WorldConfig;
import io.github.dre2n.dungeonsxl.player.DEditPlayer;
import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

/**
 * This class represents unloaded worlds.
 *
 * @author Daniel Saukel
 */
public class ResourceWorld {

    DungeonsXL plugin = DungeonsXL.getInstance();
    Worlds worlds = plugin.getWorlds();

    private File folder;
    private WorldConfig config;
    private SignData signData;

    public ResourceWorld(String name) {
        folder = new File(DungeonsXL.MAPS, name);
        if (!folder.exists()) {
            folder.mkdir();
        }

        File signDataFile = new File(folder, "DXLData.data");
        if (!signDataFile.exists()) {
            try {
                signDataFile.createNewFile();

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        signData = new SignData(signDataFile);
    }

    public ResourceWorld(File folder) {
        this.folder = folder;

        File configFile = new File(folder, "config.yml");
        if (configFile.exists()) {
            config = new WorldConfig(configFile);
        }

        File signData = new File(folder, "DXLData.data");
        if (signData.exists()) {
            this.signData = new SignData(signData);
        }
    }

    /* Getters and setters */
    /**
     * @return the folder that stores the world
     */
    public File getFolder() {
        return folder;
    }

    /**
     * @return the name of the world
     */
    public String getName() {
        return folder.getName();
    }

    /**
     * @param name
     * the name to set
     */
    public void setName(String name) {
        folder.renameTo(new File(folder.getParentFile(), name));
    }

    /**
     * @return the WorldConfig
     */
    public WorldConfig getConfig() {
        return config;
    }

    /**
     * @return the DXLData.data file
     */
    public SignData getSignData() {
        return signData;
    }

    /**
     * @param player
     * the player to invite
     */
    public void addInvitedPlayer(OfflinePlayer player) {
        if (config == null) {
            config = new WorldConfig();
        }

        config.addInvitedPlayer(player.getUniqueId().toString());
        config.save();
    }

    /**
     * @param player
     * the player to uninvite
     */
    public boolean removeInvitedPlayer(OfflinePlayer player) {
        if (config == null) {
            return false;
        }

        config.removeInvitedPlayers(player.getUniqueId().toString(), player.getName().toLowerCase());
        config.save();

        DEditPlayer editPlayer = DEditPlayer.getByName(player.getName());
        if (editPlayer != null) {
            if (EditWorld.getByWorld(editPlayer.getWorld()).getResource() == this) {
                editPlayer.leave();
            }
        }

        return true;
    }

    /**
     * @param player
     * the player to check
     */
    public boolean isInvitedPlayer(OfflinePlayer player) {
        if (config == null) {
            return false;
        }

        return config.getInvitedPlayers().contains(player.getName().toLowerCase()) || config.getInvitedPlayers().contains(player.getUniqueId().toString());
    }

    /* Actions */
    /**
     * @param game
     * whether the instance is a GameWorld
     * @return an instance of this world
     */
    public InstanceWorld instantiate(boolean game) {
        int id = worlds.generateId();
        String name = worlds.generateName(game);
        File instanceFolder = new File(Bukkit.getWorldContainer(), name);
        FileUtil.copyDirectory(folder, instanceFolder, DungeonsXL.EXCLUDED_FILES);

        if (Bukkit.getWorld(name) != null) {
            return null;
        }

        World world = plugin.getServer().createWorld(WorldCreator.name(name));

        InstanceWorld instance = null;
        try {
            if (game) {
                new GameWorld(this, instanceFolder, world, id);
                signData.deserializeSigns((GameWorld) instance);

            } else {
                new EditWorld(this, instanceFolder, world, id);
                signData.deserializeSigns((EditWorld) instance);
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return instance;
    }

    /**
     * @return an instance of this world
     */
    public EditWorld instantiateAsEditWorld() {
        return (EditWorld) instantiate(false);
    }

    /**
     * @return an instance of this world
     */
    public GameWorld instantiateAsGameWorld() {
        return (GameWorld) instantiate(true);
    }

    /**
     * Generate a new ResourceWorld.
     *
     * @return the automatically created EditWorld instance
     */
    public EditWorld generate() {
        String name = worlds.generateName(false);
        WorldCreator creator = WorldCreator.name(name);
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);

        /*EditWorldGenerateEvent event = new EditWorldGenerateEvent(this);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }
         */
        int id = worlds.generateId();
        File folder = new File(Bukkit.getWorldContainer(), name);
        World world = plugin.getServer().createWorld(creator);

        EditWorld editWorld = new EditWorld(this, folder, world, id);
        editWorld.generateIdFile();

        return editWorld;
    }

}
