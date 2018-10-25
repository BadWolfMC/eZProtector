/*
 * eZProtector - Copyright (C) 2018 DoNotSpamPls
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.donotspampls.ezprotector;

import com.github.donotspampls.ezprotector.commands.EZPCommand;
import com.github.donotspampls.ezprotector.commands.EZPTabCompleter;
import com.github.donotspampls.ezprotector.listeners.*;

import org.bstats.bukkit.Metrics;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static com.github.donotspampls.ezprotector.utilities.MessageUtil.color;

public class Main extends JavaPlugin {

    // Variables
    public static String ZIG = "5zig_Set";
    public static String BSM = "BSM";
    public static String MCBRAND;
    public static String SCHEMATICA = "schematica";
    public static String WDLINIT;
    public static String WDLCONTROL;
    private static String prefix;
    private static Main plugin;

    /**
     * Gets the plugin variable from the main class.
     *
     * @return The plugin variable.
     */
    public static Main getPlugin() {
        return plugin;
    }

    /**
     * Gets the plugin's message prefix from the main class.
     *
     * @return The plugin variable.
     */
    public static String getPrefix() {
        return prefix;
    }

    @Override
    public void onEnable() {
        plugin = this;
        prefix = color(getConfig().getString("prefix"));

        // Save the default config
        saveDefaultConfig();

        ByteMessageListener pluginMessageListener = new ByteMessageListener();

        // Set mod channels (Forge 1.13 doesn't exist yet so we don't bother with most 1.13 mods)
        if (!getServer().getVersion().contains("1.13")) {
            MCBRAND = "MC|Brand";
            WDLINIT = "WDL|INIT";
            WDLCONTROL = "WDL|CONTROL";

            getServer().getMessenger().registerIncomingPluginChannel(this, ZIG, pluginMessageListener);
            getServer().getMessenger().registerIncomingPluginChannel(this, BSM, pluginMessageListener);
            getServer().getMessenger().registerIncomingPluginChannel(this, MCBRAND, pluginMessageListener);
            getServer().getMessenger().registerIncomingPluginChannel(this, SCHEMATICA, pluginMessageListener);
            getServer().getMessenger().registerIncomingPluginChannel(this, WDLINIT, pluginMessageListener);

            getServer().getMessenger().registerOutgoingPluginChannel(this, ZIG);
            getServer().getMessenger().registerOutgoingPluginChannel(this, BSM);
            getServer().getMessenger().registerOutgoingPluginChannel(this, SCHEMATICA);
            getServer().getMessenger().registerOutgoingPluginChannel(this, WDLCONTROL);
        } else {
            MCBRAND = "minecraft:brand";
            WDLINIT = "wdl:init";
            WDLCONTROL = "wdl:control";

            getServer().getMessenger().registerIncomingPluginChannel(this, MCBRAND, pluginMessageListener);
            getServer().getMessenger().registerIncomingPluginChannel(this, WDLINIT, pluginMessageListener);

            getServer().getMessenger().registerOutgoingPluginChannel(this, WDLCONTROL);
        }

        PluginCommand command = getCommand("ezp");
        command.setExecutor(new EZPCommand());
        command.setTabCompleter(new EZPTabCompleter());

        getServer().getPluginManager().registerEvents(new CommandEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new TabCompletionListener(), this);

        // Register the metrics class and add custom charts
        Metrics metrics = new Metrics(this);

        registerMetricsChart(metrics, "updater_enabled", "updater");
        registerMetricsChart(metrics, "tab_completion", "tab-completion.blocked");
        registerMetricsChart(metrics, "hidden_syntaxes", "hidden-syntaxes.blocked");
        registerMetricsChart(metrics, "custom_plugins", "custom-plugins.enabled");
        registerMetricsChart(metrics, "custom_version", "custom-version.enabled");
        registerMetricsChart(metrics, "custom_commands", "custom-commands.blocked");

        // Initiate a (very) simple check to see if the plugin has an update!
        if (getConfig().getBoolean("updater")) checkVersion();
    }

    private void registerMetricsChart(Metrics metrics, String metricsId, String configKey) {
        metrics.addCustomChart(new Metrics.SimplePie(metricsId, () -> String.valueOf(getConfig().getBoolean(configKey))));
    }

    private void checkVersion() {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {
                HttpsURLConnection con = (HttpsURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=12663").openConnection();
                con.setRequestMethod("GET");
                String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();

                if (!(version.equals(this.getDescription().getVersion()))) {
                    getLogger().info("An update for eZProtector is available! Download it now at https://bit.ly/eZProtector");
                }
            } catch (IOException ignored) {
                // For some reason the update check failed, this is very rare so the exception isn't printed.
                getLogger().warning("Failed to check for an update!");
            }
        });
    }

}
