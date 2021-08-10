/*
 * Copyright (C) 2014-2021 Daniel Saukel
 *
 * This library is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNULesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.api.player;

import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * @author Daniel Saukel
 */
public class PlayerCache {

    public GlobalPlayer get(UUID uuid) {
        return new GlobalPlayer() {
            @Override
            public PlayerGroup getGroup() {
                return null;
            }

            @Override
            public boolean isInGroupChat() {
                return false;
            }

            @Override
            public void setInGroupChat(boolean groupChat) {

            }

            @Override
            public boolean isInChatSpyMode() {
                return false;
            }

            @Override
            public void setInChatSpyMode(boolean chatSpyMode) {

            }

            @Override
            public boolean hasPermission(String permission) {
                return false;
            }

            @Override
            public List<ItemStack> getRewardItems() {
                return null;
            }

            @Override
            public void setRewardItems(List<ItemStack> rewardItems) {

            }

            @Override
            public boolean hasRewardItemsLeft() {
                return false;
            }

            @Override
            public boolean isInBreakMode() {
                return false;
            }

            @Override
            public void setInBreakMode(boolean breakMode) {

            }

            @Override
            public void reset(boolean gameFinished) {

            }

            @Override
            public void reset(Location tpLoc, boolean keepInventory) {

            }

            @Override
            public boolean checkRequirements(Dungeon dungeon) {
                return false;
            }

            @Override
            public Player getPlayer() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public UUID getUniqueId() {
                return null;
            }
        };
    }

    public InstancePlayer getInstancePlayer(Player player) {
        return getFirstInstancePlayerIf(p -> p.getPlayer() == player);
    }

    public EditPlayer getEditPlayer(Player player) {
        return getFirstEditPlayerIf(p -> p.getPlayer() == player);
    }

    public GamePlayer getGamePlayer(Player player) {
        return getFirstGamePlayerIf(p -> p.getPlayer() == player);
    }

    /**
     * Returns the first InstancePlayer that satisfies the given predicate.
     *
     * @param predicate the predicate to check
     * @return the first InstancePlayer that satisfies the given predicate
     */
    public InstancePlayer getFirstInstancePlayerIf(Predicate<InstancePlayer> predicate) {
        return null;
    }

    /**
     * Returns the first EditPlayer that satisfies the given predicate.
     *
     * @param predicate the predicate to check
     * @return the first EditPlayer that satisfies the given predicate
     */
    public EditPlayer getFirstEditPlayerIf(Predicate<EditPlayer> predicate) {
        return null;
    }

    /**
     * Returns the first GamePlayer that satisfies the given predicate.
     *
     * @param predicate the predicate to check
     * @return the first GamePlayer that satisfies the given predicate
     */
    public GamePlayer getFirstGamePlayerIf(Predicate<GamePlayer> predicate) {
        return null;
    }

    /**
     * Returns all InstancePlayers that satisfy the given predicate.
     *
     * @param predicate the predicate to check
     * @return all InstancePlayers that satisfy the given predicate
     */
    public Collection<InstancePlayer> getAllInstancePlayersIf(Predicate<InstancePlayer> predicate) {
        return null;
    }

    /**
     * Returns all EditPlayer that satisfy the given predicate.
     *
     * @param predicate the predicate to check
     * @return all EditPlayer that satisfy the given predicate
     */
    public Collection<EditPlayer> getAllEditPlayersIf(Predicate<EditPlayer> predicate) {
        return null;
    }

    /**
     * Returns all GamePlayer that satisfy the given predicate.
     *
     * @param predicate the predicate to check
     * @return all GamePlayer that satisfy the given predicate
     */
    public Collection<GamePlayer> getAllGamePlayersIf(Predicate<GamePlayer> predicate) {
        return null;
    }

    /**
     * Returns all InstancePlayers.
     *
     * @return all InstancePlayers
     */
    public Collection<InstancePlayer> getAllInstancePlayers() {
        return null;
    }

    /**
     * Returns all EditPlayers.
     *
     * @return all EditPlayers
     */
    public Collection<EditPlayer> getAllEditPlayers() {
        return null;
    }

    /**
     * Returns all GamePlayers.
     *
     * @return all GamePlayers
     */
    public Collection<GamePlayer> getAllGamePlayers() {
        return null;
    }
}
