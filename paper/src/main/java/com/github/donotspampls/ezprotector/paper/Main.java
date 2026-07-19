/*
 * eZProtector - Copyright (C) 2018-2020 DoNotSpamPls
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.donotspampls.ezprotector.paper;

import com.github.donotspampls.ezprotector.paper.listeners.BrigadierListener;
import com.github.donotspampls.ezprotector.paper.listeners.ByteMessageListener;
import com.github.donotspampls.ezprotector.paper.listeners.CustomCommands;
import com.github.donotspampls.ezprotector.paper.listeners.FakeCommands;
import com.github.donotspampls.ezprotector.paper.listeners.HiddenSyntaxes;
import com.github.donotspampls.ezprotector.paper.listeners.PlayerJoinListener;
import com.github.donotspampls.ezprotector.paper.listeners.TabCompletionListener;
import com.github.donotspampls.ezprotector.paper.utilities.ExecutionUtil;
import com.github.donotspampls.ezprotector.paper.utilities.MessageUtil;
import com.github.donotspampls.ezprotector.paper.utilities.TabCompletionPolicy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Main extends JavaPlugin {

    // Mod channels used by supported modern Paper versions.
    public static final String ZIG = "the5zigmod:5zig_set";
    public static final String BSM = "bsm:settings";
    public static final String MCBRAND = "minecraft:brand";
    public static final String SCHEMATICA = "dev:null"; // Schematica has no 1.13+ channel.
    public static final String WDLINIT = "wdl:init";
    public static final String WDLCONTROL = "wdl:control";

    private boolean papi = false; // is PlaceholderAPI available?
    private MessageUtil msgUtil;
    private TabCompletionPolicy tabCompletionPolicy;

    @Override
    public void onEnable() {
        // Paper enforces the minimum supported version through plugin.yml's api-version.
        saveDefaultConfig();

        papi = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

        ExecutionUtil execUtil = new ExecutionUtil(getServer());
        msgUtil = new MessageUtil(this, execUtil, papi);
        tabCompletionPolicy = new TabCompletionPolicy(this);
        ByteMessageListener bml = new ByteMessageListener(this, execUtil, msgUtil);

        // The command tree listener hides root commands; the tab-completion listener
        // blocks server-generated argument suggestions for those commands.
        getServer().getPluginManager().registerEvents(new BrigadierListener(tabCompletionPolicy), this);
        getServer().getPluginManager().registerEvents(new TabCompletionListener(tabCompletionPolicy), this);

        Objects.requireNonNull(getCommand("ezp"), "Command 'ezp' is missing from plugin.yml")
                .setExecutor(this);

        getServer().getMessenger().registerIncomingPluginChannel(this, ZIG, bml);
        getServer().getMessenger().registerIncomingPluginChannel(this, BSM, bml);
        getServer().getMessenger().registerIncomingPluginChannel(this, MCBRAND, bml);
        getServer().getMessenger().registerIncomingPluginChannel(this, SCHEMATICA, bml);
        getServer().getMessenger().registerIncomingPluginChannel(this, WDLINIT, bml);

        getServer().getMessenger().registerOutgoingPluginChannel(this, ZIG);
        getServer().getMessenger().registerOutgoingPluginChannel(this, BSM);
        getServer().getMessenger().registerOutgoingPluginChannel(this, SCHEMATICA);
        getServer().getMessenger().registerOutgoingPluginChannel(this, WDLCONTROL);

        getServer().getPluginManager().registerEvents(new CustomCommands(this), this);
        getServer().getPluginManager().registerEvents(new FakeCommands(this), this);
        getServer().getPluginManager().registerEvents(new HiddenSyntaxes(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length != 1) return false;

        if (args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            tabCompletionPolicy.reload();
            sender.sendMessage(Component.text("Config reloaded!", NamedTextColor.GREEN));
            return true;
        }

        return false;
    }

    public MessageUtil getMsgUtil() {
        return msgUtil;
    }
}
