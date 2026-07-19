<p align="center"><img src="https://owo.whats-th.is/b120fb.png"></p>

# eZProtector [![Build Status](https://github.com/BadWolfMC/eZProtector/workflows/Java%20CI/badge.svg)](https://github.com/BadWolfMC/eZProtector/actions) [![Discord](https://img.shields.io/discord/390942438061113344.svg)](https://discord.gg/UGhVcBB)

eZProtector is an advanced server protection plugin, made with simplicity and speed in mind. It has many useful features, such as changing the plugin list to a custom one, blocking many mods which give an advantage and more! If you'd like to download it, you can do so [here](https://papermc.io/forums/t/1-12-2-1-14-x-ezprotector/1361)

If you'd like to get more information on how the plugin works, you can read the [wiki](https://github.com/BadWolfMC/eZProtector/wiki)

## Building

#### Requirements
* Java 25 JDK or newer
* Maven 3.x
* Git

#### Compiling from source
```sh
git clone https://github.com/BadWolfMC/eZProtector.git
cd eZProtector/
mvn clean package
```

You can find the output jars in the `target` directories of the modules.

## Message formatting

eZProtector sends user-facing messages through Paper's Adventure API. Configuration messages support MiniMessage, including RGB colors and gradients:

```yaml
hidden-syntaxes:
  error-message: "<gradient:#17B4E6:#5555FF><bold>eZProtector</bold></gradient> <white>That command syntax is forbidden!"
```

The default `message-format: auto` setting also keeps existing legacy `&` color-code messages working. Each individual message should use either MiniMessage or legacy formatting, not both. Set `message-format` to `minimessage` or `legacy` to enforce one format globally.

When PlaceholderAPI is installed, ordinary `%placeholder%` values remain supported. In MiniMessage values, `<papi:placeholder_name>` is also available and safely converts legacy-formatted expansion output into an Adventure component; for example, `<papi:luckperms_prefix>`.

## Tab-completion filtering

Modern clients receive top-level command names through Brigadier and request some argument suggestions from the server separately. eZProtector filters both paths. `ezprotector.bypass.command.tabcomplete` bypasses all filtering; `ezprotector.bypass.command.tabcomplete.<command>` bypasses one command, including its namespaced form.

## License
eZProtector is licensed under the GPLv3 license. Please see [`LICENSE`](https://github.com/BadWolfMC/eZProtector/blob/master/LICENSE) for more information.
