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

import com.github.donotspampls.ezprotector.paper.Main;
import com.github.donotspampls.ezprotector.paper.utilities.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;

public class FakeCommands implements Listener {

    private final Main plugin;

    public FakeCommands(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void execute(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("ezprotector.bypass.command.fake")) return;

        FileConfiguration config = plugin.getConfig();
        MessageUtil msgUtil = plugin.getMsgUtil();
        String command = event.getMessage().split(" ")[0];

        if (command.matches("(?i)/ver|/version") && config.getBoolean("custom-version.enabled")) {
            event.setCancelled(true);

            String version = config.getString("custom-version.version", "Custom Version");
            player.sendMessage(Component.text("This server is running server version ", NamedTextColor.WHITE)
                    .append(msgUtil.component(version, player, null, command)));

            msgUtil.notifyAdmins("custom-version", player, command, "command.version");
            return;
        }

        if (command.matches("(?i)/pl|/plugins") && config.getBoolean("custom-plugins.enabled")) {
            event.setCancelled(true);

            String[] plugins = Arrays.stream(config.getString("custom-plugins.plugins", "").split("\\s*,\\s*"))
                    .filter(pluginName -> !pluginName.isBlank())
                    .toArray(String[]::new);

            TextComponent.Builder message = Component.text()
                    .append(Component.text("Plugins (" + plugins.length + "): ", NamedTextColor.WHITE));

            for (int index = 0; index < plugins.length; index++) {
                if (index > 0) message.append(Component.text(", ", NamedTextColor.WHITE));
                message.append(Component.text(plugins[index], NamedTextColor.GREEN));
            }

            player.sendMessage(message.build());
            msgUtil.notifyAdmins("custom-plugins", player, command, "command.plugins");
        }
    }
}
