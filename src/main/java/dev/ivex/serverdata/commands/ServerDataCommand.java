package dev.ivex.serverdata.commands;

import dev.ivex.serverdata.ServerData;
import dev.ivex.serverdata.data.ServerManager;
import dev.ivex.serverdata.jedis.JedisPublisher;
import dev.ivex.serverdata.utilites.Color;
import dev.ivex.serverdata.utilites.DataUtil;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Arrays;
import java.util.Objects;

public class ServerDataCommand extends BukkitCommand {


    public ServerDataCommand() {
        super("server");
        setAliases(Arrays.asList("serverstats", "serverstatus"));

        ServerData.getCommandMap().register("server", this);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!sender.hasPermission(ServerData.getInstance().getConfig().getString("MESSAGE.PERMISSION"))) {
            sender.sendMessage(Color.translate(ServerData.getInstance().getConfig().getString("MESSAGE.NOPERMISSION")));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(usage);
        } else {
            if (args[0].equalsIgnoreCase("list")) {
                ServerData.getInstance().getServerManager().getAllServersData().stream().filter(Objects::nonNull).forEach(data -> {
                        for (String messagelist : ServerData.getInstance().getConfig().getStringList("MESSAGE.LIST")) {
                            String tps = String.valueOf(Math.round((data.getTps() > 20 ? 20 : data.getTps())));
                            String uptime = DataUtil.millisToRoundedTime(data.getUpTime());
                            String onlineplayers = String.valueOf(Integer.valueOf(data.getOnlinePlayers()));
                            String maxplayers = String.valueOf(Integer.valueOf(data.getMaxPlayers()));
                            String server = String.valueOf(data.getName());
                            String motd = String.valueOf(data.getMotd());
                            String status = String.valueOf(data.getStatus());
                            sender.sendMessage(Color.translate(messagelist.replace("%server%", server).replace("%onlineplayers%", onlineplayers).replace("%maxplayers%", maxplayers).replace("%motd%", motd).replace("%status%", status).replace("%tps%", tps).replace("%uptime%", uptime)));
                        }
                        });
                return false;
            }
            ServerManager data = ServerData.getInstance().getServerManager().getByName(args[0]);

            if (data == null) {
                sender.sendMessage(Color.translate(ServerData.getInstance().getConfig().getString("MESSAGE.NO_DATA")));
                return false;
            }

            for (String message : ServerData.getInstance().getConfig().getStringList("MESSAGE.INFO")) {
                String tps = String.valueOf(Math.round((data.getTps() > 20 ? 20 : data.getTps())));
                String uptime = DataUtil.millisToRoundedTime(data.getUpTime());
                String onlineplayers = String.valueOf(Integer.valueOf(data.getOnlinePlayers()));
                String maxplayers = String.valueOf(Integer.valueOf(data.getMaxPlayers()));
                String server = String.valueOf(data.getName());
                String motd = String.valueOf(data.getMotd());
                String status = String.valueOf(data.getStatus());
                sender.sendMessage(Color.translate(message.replace("%server%", server).replace("%onlineplayers%", onlineplayers).replace("%maxplayers%", maxplayers).replace("%motd%", motd).replace("%status%", status).replace("%tps%", tps).replace("%uptime%", uptime)));
            }
            }
            return true;
        }




    public final String usage = Color.translate(
            "&cServer Commands - Help"
                    + "\n/server list"
                    + "\n/server <name>");
}