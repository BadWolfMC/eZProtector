/*
 * eZProtector - Copyright (C) 2018-2020 DoNotSpamPls
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.donotspampls.ezprotector.paper.utilities;

import com.github.donotspampls.ezprotector.paper.Main;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class TabCompletionPolicy {

    public static final String BYPASS_PERMISSION = "ezprotector.bypass.command.tabcomplete";

    private final Main plugin;
    private boolean blocked;
    private boolean whitelist;
    private Set<String> commands = Set.of();

    public TabCompletionPolicy(Main plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        blocked = plugin.getConfig().getBoolean("tab-completion.blocked");
        whitelist = plugin.getConfig().getBoolean("tab-completion.whitelist");
        commands = plugin.getConfig().getStringList("tab-completion.commands").stream()
                .map(TabCompletionPolicy::normalizeCommand)
                .filter(command -> !command.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean isEnabled() {
        return blocked;
    }

    public boolean hasGlobalBypass(Player player) {
        return player.hasPermission(BYPASS_PERMISSION);
    }

    public boolean shouldHide(Player player, String command) {
        if (!blocked || hasGlobalBypass(player)) return false;

        String normalized = normalizeCommand(command);
        if (normalized.isEmpty() || hasCommandBypass(player, normalized)) return false;

        boolean listed = commands.contains(normalized);
        return whitelist ? !listed : listed;
    }

    public static String commandFromBuffer(String buffer) {
        if (buffer == null) return "";

        String trimmed = buffer.trim();
        if (trimmed.startsWith("/")) trimmed = trimmed.substring(1);
        if (trimmed.isEmpty()) return "";

        int firstSpace = trimmed.indexOf(' ');
        return normalizeCommand(firstSpace < 0 ? trimmed : trimmed.substring(0, firstSpace));
    }

    private boolean hasCommandBypass(Player player, String command) {
        if (player.hasPermission(BYPASS_PERMISSION + "." + command)) return true;

        int namespaceSeparator = command.indexOf(':');
        return namespaceSeparator >= 0
                && namespaceSeparator + 1 < command.length()
                && player.hasPermission(BYPASS_PERMISSION + "." + command.substring(namespaceSeparator + 1));
    }

    private static String normalizeCommand(String command) {
        if (command == null) return "";

        String normalized = command.trim();
        while (normalized.startsWith("/")) normalized = normalized.substring(1);

        int firstSpace = normalized.indexOf(' ');
        if (firstSpace >= 0) normalized = normalized.substring(0, firstSpace);

        return normalized.toLowerCase(Locale.ROOT);
    }
}
