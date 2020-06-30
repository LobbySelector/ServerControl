package dev.ivex.serverdata.commands;

import dev.ivex.serverdata.ServerData;
import dev.ivex.serverdata.data.ServerManager;
import dev.ivex.serverdata.jedis.JedisPublisher;
import dev.ivex.serverdata.utilites.Color;
import dev.ivex.serverdata.utilites.TimeUtil;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ServerDataCommand extends BukkitCommand {


    public ServerDataCommand() {
        super("serverstatus");
        setAliases(Arrays.asList("serverstats", "server"));

        ServerData.getInstance().getCommandMap().register("serverstatus", this);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (args.length == 0 || args.length == 2) {
            sender.sendMessage(usage);
            return false;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                ServerManager.getServers().values().stream().filter(Objects::nonNull).forEach((data) -> {
                    sender.sendMessage(Color.translate("&eName&7: &b" + data.getName()+ " "  + "&eOnline&7: &b" + data.getOnlinePlayers() + "/" + data.getMaxPlayers()+ " "  + "&eMotd&7: &b" + data.getMotd()+ " "  + "&eStatus&7: &b" + data.getStatus()+ " "  + "&eTps&7: &b" + Math.round((data.getTps() > 20 ? 20 : data.getTps())) + " " + "&eUptime&7: &b" + TimeUtil.millisToRoundedTime(data.getUpTime())));
                });
                return true;
            }
            ServerManager data = ServerManager.getByName(args[0]);

            if (data == null) {
                sender.sendMessage(Color.translate("&cThat server does cannot be found in our database."));
                return false;
            }

            sender.sendMessage(Color.translate("&7&m------------------------------"));
            sender.sendMessage(Color.translate("Server Status"));
            sender.sendMessage("");
            sender.sendMessage(Color.translate(" &eName&7: &b" + data.getName()));
            sender.sendMessage(Color.translate(" &eOnline&7: &b" + data.getOnlinePlayers() + "/" + data.getMaxPlayers()));
            sender.sendMessage(Color.translate(" &eMotd&7: &b" + data.getMotd()));
            sender.sendMessage(Color.translate(" &eStatus&7: &b" + data.getStatus()));
            sender.sendMessage(Color.translate(" &eTPS&7: &b" + Math.round((data.getTps() > 20 ? 20 : data.getTps()))));
            sender.sendMessage(Color.translate(" &eUptime&7: &b" + TimeUtil.millisToRoundedTime(data.getUpTime())));
            sender.sendMessage(Color.translate("&7&m------------------------------"));

        }
            return true;
    }


    public final String usage = Color.translate(
            "&cServer Commands - Help"
                    + "\n/server list"
                    + "\n/server <name>");
}