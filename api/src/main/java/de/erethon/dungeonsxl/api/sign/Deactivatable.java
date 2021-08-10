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
package de.erethon.dungeonsxl.api.sign;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * A {@link DungeonSign} that changes its state when triggered.
 *
 * @author Daniel Saukel
 */
public abstract class Deactivatable extends AbstractDSign {

    protected boolean active;

    protected Deactivatable(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    /**
     * Sets the state to active for the given player.
     * <p>
     * <b>Note that the default implementation of this method assumes that the sign does not need player specific behavior and simply calls,
     * while the default implementation of
     *
     * @param player the player
     * @return if the action was successful
     */
    public boolean activate(Player player) {
        return true;
    }
}
