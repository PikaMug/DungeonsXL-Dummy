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

import de.erethon.dungeonsxl.api.Reward;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.world.GameWorld;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.entity.Player;

/**
 * Represents a group of players provided by DungeonsXL.
 *
 * @author Daniel Saukel
 */
// Implementation-specific methods: setDungeon, setPlaying, [color, unplayed floor, floor count methods], isEmpty, isCustom, teleport,
// finish, finishFloor, startGame, winGame, requirements methods
public interface PlayerGroup {

    /**
     * Returns the ID.
     *
     * @return the ID
     */
    int getId();

    /**
     * Returns the formatted name.
     * <p>
     * This is the name used e.g. in messages.
     *
     * @return the formatted name
     */
    String getName();

    /**
     * Returns the raw, unformatted name.
     * <p>
     * This is the name used e.g. in command arguments.
     *
     * @return the raw, unformatted name
     */
    String getRawName();

    /**
     * Sets the name.
     *
     * @param name the name
     */
    void setName(String name);

    /**
     * Sets the name to a default value taken from the color.
     * <p>
     * In the default implementation, this is nameOfTheColor#{@link #getId()}
     *
     * @param color the color
     */
    default void setName(Color color) {
        setName(color.toString() + "#" + getId());
    }

    /**
     * The player who has permission to manage the group.
     *
     * @return the player who has permission to manage the group
     */
    Player getLeader();

    /**
     * Sets the leader to another group member.
     *
     * @param player the new leader
     */
    void setLeader(Player player);

    /**
     * Returns a PlayerCollection of the group members
     *
     * @return a PlayerCollection of the group members
     */
    PlayerCollection getMembers();

    /**
     * Adds a player to the group.
     * <p>
     * The default implemenation calls {@link #addMember(Player, boolean)} with messages set to true.
     *
     * @param player the player to add
     */
    default void addMember(Player player) {
        addMember(player, true);
    }

    /**
     * Adds a player to the group.
     *
     * @param player  the player to add
     * @param message if messages shall be sent
     */
    void addMember(Player player, boolean message);

    /**
     * Removes a player from the group.
     * <p>
     * The default implemenation calls {@link #removeMember(Player, boolean)} with messages set to true.
     *
     * @param player the player to add
     */
    default void removeMember(Player player) {
        addMember(player, true);
    }

    /**
     * Removes a player from the group.
     *
     * @param player  the player to add
     * @param message if messages shall be sent
     */
    void removeMember(Player player, boolean message);

    /**
     * Returns a PlayerCollection of the players who are invited to join the group but did not yet do so.
     *
     * @return a PlayerCollection of the players who are invited to join the group but did not yet do so
     */
    PlayerCollection getInvitedPlayers();

    /**
     * Invites a player to join the group.
     *
     * @param player  the player to invite
     * @param message if messages shall be sent
     */
    void addInvitedPlayer(Player player, boolean message);

    /**
     * Removes an invitation priviously made for a player to join the group.
     *
     * @param player  the player to uninvite
     * @param message if messages shall be sent
     */
    void removeInvitedPlayer(Player player, boolean message);

    /**
     * Removes all invitations for players who are not online.
     */
    void clearOfflineInvitedPlayers();

    /**
     * Returns the game of the game world the group is in.
     *
     * @return the game of the game world the group is in.
     */
    Game getGame();

    /**
     * Returns the game world the group is in.
     *
     * @return the game world the group is in
     */
    default GameWorld getGameWorld() {
        return getGame() != null ? getGame().getWorld() : null;
    }

    /**
     * Returns the dungeon the group is playing or has remembered to play next.
     * <p>
     * The latter is for example used when a group is created by a group sign sothat a portal or the auto-join function knows where to send the group.
     *
     * @return the dungeon the group is playing or has remembered to play next
     */
    Dungeon getDungeon();

    /**
     * Returns if the group is already playing its remembered {@link #getDungeon() dungeon}.
     *
     * @return if the group is already playing its remembered {@link #getDungeon() dungeon}
     */
    boolean isPlaying();

    /**
     * Returns the rewards that are memorized for the group. These are given when the game is finished.
     *
     * @return the rewards
     */
    List<Reward> getRewards();

    /**
     * Memorizes the given reward for the group. These are given when the game is finished.
     *
     * @param reward the reward
     */
    void addReward(Reward reward);

    /**
     * Removes the given reward.
     *
     * @param reward the reward
     */
    void removeReward(Reward reward);

    /**
     * Returns the score number, which is used for capture the flag and similar game types.
     *
     * @return the score number
     */
    int getScore();

    /**
     * Sets the score of this group to a new value.
     *
     * @param score the value
     */
    void setScore(int score);

    /**
     * Returns the initial amount of lives or -1 if group lives are not used.
     *
     * @return the initial amount of lives or -1 if group lives are not used
     */
    int getInitialLives();

    /**
     * Sets the initial amount of lives.
     * <p>
     * The value must be &gt;=0 or -1, which means unlimited lives.
     *
     * @param lives the new amount of lives known as the initial amount
     */
    void setInitialLives(int lives);

    /**
     * Returns the amount of lives the group currently has left or -1 if group lives are not used.
     *
     * @return the amount of lives the group currently has left or -1 if group lives are not used
     */
    int getLives();

    /**
     * Sets the amount of lives the group currently has left.
     * <p>
     * The value must be &gt;=0 or -1, which means unlimited lives.
     *
     * @param lives the amount of lives the group currently has left
     */
    void setLives(int lives);

    /**
     * Returns true if all players of the group have finished the game; false if not.
     *
     * @return true if all players of the group have finished the game; false if not
     */
    boolean isFinished();

    /**
     * Disbands the group.
     */
    void delete();

    /**
     * Sends a message to all players in the group.
     * <p>
     * Supports color codes.
     *
     * @param message the message to sent
     * @param except  Players who shall not receive the message
     */
    default void sendMessage(String message, Player... except) {
        members:
        for (Player player : getMembers().getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }
            for (Player nope : except) {
                if (player == nope) {
                    continue members;
                }
            }
            player.sendMessage(message);
        }
    }

}
