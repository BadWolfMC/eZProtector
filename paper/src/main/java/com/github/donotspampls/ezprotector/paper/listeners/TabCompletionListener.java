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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

public class TabCompletionListener implements Listener {

    private final TabCompletionPolicy policy;

    public TabCompletionListener(TabCompletionPolicy policy) {
        this.policy = policy;
    }

    /**
     * Blocks server-generated argument suggestions for configured commands.
     * Top-level command names are filtered separately by {@link BrigadierListener}.
     *
     * @param event the tab-completion request and generated suggestions
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTabComplete(TabCompleteEvent event) {
        if (!event.isCommand() || !(event.getSender() instanceof Player player)) return;
        if (!policy.isEnabled() || policy.hasGlobalBypass(player)) return;

        String command = TabCompletionPolicy.commandFromBuffer(event.getBuffer());
        if (policy.shouldHide(player, command)) {
            event.setCancelled(true);
            event.getCompletions().clear();
        }
    }
}
