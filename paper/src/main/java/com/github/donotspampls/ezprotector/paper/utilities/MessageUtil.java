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
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.regex.Pattern;

public class MessageUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_AMPERSAND = LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.builder()
            .character('\u00A7')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();
    private static final Pattern MINI_MESSAGE_TAG = Pattern.compile(
            "(?i)<(?:/?(?:black|dark_blue|dark_green|dark_aqua|dark_red|dark_purple|gold|gray|grey|dark_gray|dark_grey|blue|green|aqua|red|light_purple|yellow|white|bold|b|italic|i|underlined|u|strikethrough|st|obfuscated|obf|reset|rainbow|gradient|transition|color|colour|c|click|hover|insertion|font|keybind|translatable|selector|score|nbt|newline|br|papi)(?::[^>]*)?|#[0-9a-f]{6,8})>"
    );

    private final Main plugin;
    private final ExecutionUtil execUtil;
    private final boolean papi;

    public MessageUtil(Main plugin, ExecutionUtil execUtil, boolean papi) {
        this.plugin = plugin;
        this.execUtil = execUtil;
        this.papi = papi;
    }

    public Component component(String template, Player player, String errorMessage, String command) {
        String resolved = resolvePlaceholders(template, player, errorMessage, command);
        return deserialize(resolved, messageFormat(template), player);
    }

    public String command(String template, Player player, String errorMessage, String command) {
        String resolved = resolvePlaceholders(template, player, errorMessage, command);
        return LEGACY_SECTION.serialize(deserialize(resolved, messageFormat(resolved), player));
    }

    public void punishPlayers(String module, Player player, String errorMessage, String command) {
        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean(module + ".punish-player.enabled")) return;

        String punishCommand = config.getString(module + ".punish-player.command", "");
        if (!punishCommand.isBlank())
            execUtil.executeConsoleCommand(command(punishCommand, player, errorMessage, command));
    }

    public void notifyAdmins(String module, Player player, String command, String perm) {
        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean(module + ".notify-admins.enabled")) return;

        String message = config.getString(module + ".notify-admins.message", "");
        if (!message.isBlank())
            execUtil.notifyAdmins(component(message, player, null, command), "ezprotector.notify." + perm);
    }

    private Component deserialize(String resolved, MessageFormat format, Player player) {
        if (resolved.isBlank()) return Component.empty();

        try {
            return switch (format) {
                case MINIMESSAGE -> papi && player != null
                        ? MINI_MESSAGE.deserialize(resolved, papiTag(player))
                        : MINI_MESSAGE.deserialize(resolved);
                case LEGACY -> LEGACY_AMPERSAND.deserialize(resolved);
            };
        } catch (RuntimeException exception) {
            plugin.getLogger().warning("Could not parse a configured message; sending it as plain text: "
                    + exception.getMessage());
            return Component.text(resolved);
        }
    }

    private TagResolver papiTag(Player player) {
        return TagResolver.resolver("papi", (arguments, context) -> {
            String placeholder = arguments.popOr("The <papi> tag requires a PlaceholderAPI placeholder name").value();
            String parsed = PlaceholderAPI.setPlaceholders(player, "%" + placeholder + "%");
            Component component = LegacyComponentSerializer.legacySection().deserialize(parsed);
            return Tag.selfClosingInserting(component);
        });
    }

    private String resolvePlaceholders(String template, Player player, String errorMessage, String command) {
        String resolved = (template == null ? "" : template)
                .replace("%player%", player == null ? "" : player.getName())
                .replace("%errormessage%", errorMessage == null ? "" : errorMessage)
                .replace("%command%", command == null ? "" : command);

        if (papi && player != null)
            resolved = PlaceholderAPI.setPlaceholders(player, resolved);

        return resolved;
    }

    private MessageFormat messageFormat(String message) {
        String configuredValue = plugin.getConfig().getString("message-format", "auto");
        String configured = (configuredValue == null ? "auto" : configuredValue)
                .trim()
                .toLowerCase(Locale.ROOT);

        return switch (configured) {
            case "minimessage", "mini-message", "mini_message" -> MessageFormat.MINIMESSAGE;
            case "legacy", "ampersand" -> MessageFormat.LEGACY;
            default -> message != null && MINI_MESSAGE_TAG.matcher(message).find()
                    ? MessageFormat.MINIMESSAGE
                    : MessageFormat.LEGACY;
        };
    }

    private enum MessageFormat {
        MINIMESSAGE,
        LEGACY
    }
}
