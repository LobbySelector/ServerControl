package dev.ivex.serverdata.utilites;

import dev.ivex.serverdata.ServerData;
import lombok.Getter;
import org.bukkit.configuration.file.*;


import java.io.*;

@Getter
public class ConfigFile
{
    @Getter
    public static ConfigFile instance;

    private YamlConfiguration config;
    private File file;

    public ConfigFile() {
        instance = this;

        file = new File(ServerData.getInstance().getDataFolder(), "config.yml");

        if (!file.exists()) {
            ServerData.getInstance().saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void load() {
        file = new File(ServerData.getInstance().getDataFolder(), "config.yml");
        if (!file.exists()) {
            ServerData.getInstance().saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}