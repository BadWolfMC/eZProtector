/*
 * eZProtector - Copyright (C) 2018-2019 DoNotSpamPls
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.donotspampls.ezprotector.paper.listeners;

import com.github.donotspampls.ezprotector.paper.Main;
import com.github.donotspampls.ezprotector.paper.utilities.CustomCommands;
import com.github.donotspampls.ezprotector.paper.utilities.FakeCommands;
import com.github.donotspampls.ezprotector.paper.utilities.HiddenSyntaxes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandEventListener implements Listener {

    /**
     * Listener to intercept and check and commands executed by the player.
     * This runs before the actual command in question is executed.
     * If there is no issue, nothing happens, otherwise the command is blocked.
     *
     * @param event The command event from which other information is gathered.
     */
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        FileConfiguration config = Main.getPlugin().getConfig();

        if (config.getBoolean("custom-commands.blocked")) CustomCommands.execute(event);
        if (config.getBoolean("hidden-syntaxes.blocked")) HiddenSyntaxes.execute(event);

        FakeCommands.executeBlock(event);
        FakeCommands.executeCustom(event);
    }

}
