/*
 * Written from 2015-2021 by Daniel Saukel
 *
 * To the extent possible under law, the author(s) have dedicated all
 * copyright and related and neighboring rights to this software
 * to the public domain worldwide.
 *
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC0 Public Domain Dedication
 * along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */
package de.erethon.dungeonsxl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The custom JavaPlugin class. It provides simplified registration of and access to features of Vault, Metrics and DRECommons.
 *
 * @author Daniel Saukel
 */
public abstract class DREPlugin extends JavaPlugin {

    private static DREPlugin instance;
    protected static PluginManager manager;

    @Override
    public void onEnable() {
        instance = this;
        manager = getServer().getPluginManager();
    }

    @Override
    public void onDisable() {
        instance = null;
        manager = null;
    }

    /**
     * @return the plugin instance
     */
    public static DREPlugin getInstance() {
        return instance;
    }

    /**
     * Attempts to save a resource.
     * <p>
     * See {@link org.bukkit.plugin.Plugin#saveResource(java.lang.String, boolean)}. This does not throw an exception.
     * <p>
     * Updates the file if it lacks configuration paths the resource has.
     *
     * @param resource the path to the resource to save
     * @param replace  if the resource shall be replaced
     * @return if the resource was saved or updated
     */
    public boolean attemptToSaveResource(String resource, boolean replace) {
        File file = new File(getDataFolder(), resource);
        if (replace || !file.exists()) {
            try {
                saveResource(resource, replace);
                return true;
            } catch (IllegalArgumentException exception) {
                return false;
            }

        } else {
            boolean updated = false;
            InputStream is = getResource(resource);
            if (is == null) {
                return false;
            }
            YamlConfiguration resourceCfg = YamlConfiguration.loadConfiguration(new InputStreamReader(is, Charset.forName("UTF-8")));
            YamlConfiguration fileCfg = YamlConfiguration.loadConfiguration(file);
            for (String key : resourceCfg.getKeys(true)) {
                if (!fileCfg.contains(key)) {
                    fileCfg.set(key, resourceCfg.get(key));
                    updated = true;
                }
            }
            if (updated) {
                try {
                    fileCfg.save(file);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            return updated;
        }
    }

    protected void setDataFolder(File dataFolder) {
        try {
            Field field = JavaPlugin.class.getDeclaredField("dataFolder");
            field.setAccessible(true);
            field.set(this, dataFolder);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
        }
    }
}
