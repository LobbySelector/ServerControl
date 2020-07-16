package dev.ivex.serverdata.discord;

import dev.ivex.serverdata.ServerData;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Server;

import java.awt.*;

public class DiscordManager {
    
    public void serveronline() {
        try {
            DiscordWebhook webhook = new DiscordWebhook(ServerData.getInstance().getConfig().getString("DISCORD.WEBHOOK"));
            webhook.setUsername(ServerData.getInstance().getConfig().getString("DISCORD.TITLE"));
            webhook.setAvatarUrl(ServerData.getInstance().getConfig().getString("DISCORD.AVATAR"));
            webhook.setTts(false);
            webhook.addEmbed(
                    new DiscordWebhook.EmbedObject()
                            .setDescription(ServerData.getInstance().getConfig().getString("DISCORD.ONLINE").replace("%server%", ServerData.getServerName())).setColor(Color.GREEN));
            webhook.execute();
        }
        catch (Exception e) {
            System.out.println("&cDiscord failed to announce starting!");
        }
    }

    public void serveroffline() {
        try {
            DiscordWebhook webhook = new DiscordWebhook(ServerData.getInstance().getConfig().getString("DISCORD.WEBHOOK"));
            webhook.setContent(ServerData.getInstance().getConfig().getString("DISCORD.CONTENT"));
            webhook.setUsername(ServerData.getInstance().getConfig().getString("DISCORD.TITLE"));
            webhook.setAvatarUrl(ServerData.getInstance().getConfig().getString("DISCORD.AVATAR"));
            webhook.setTts(false);
            webhook.addEmbed(
                    new DiscordWebhook.EmbedObject()
                            .setDescription(ServerData.getInstance().getConfig().getString("DISCORD.OFFLINE").replace("%server%", ServerData.getServerName())).setColor(Color.RED));
            webhook.execute();
        }
        catch (Exception e) {
            System.out.println("&cDiscord failed to announce starting!");
        }
    }
}
