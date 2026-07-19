/*
 * eZProtector - Copyright (C) 2018-2020 DoNotSpamPls
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.donotspampls.ezprotector.paper.listeners;

import com.github.donotspampls.ezprotector.paper.utilities.TabCompletionPolicy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class BrigadierListener implements Listener {

    private final TabCompletionPolicy policy;

    public BrigadierListener(TabCompletionPolicy policy) {
        this.policy = policy;
    }

    /**
     * Removes configured top-level commands from the command tree sent to a player.
     *
     * @param event the command-send event whose mutable command collection is filtered
     */
    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent event) {
        if (!policy.isEnabled()) return;

        Player player = event.getPlayer();
        if (policy.hasGlobalBypass(player)) return;

        event.getCommands().removeIf(command -> policy.shouldHide(player, command));
    }
}
