package dev.ivex.serverdata.commands;

import dev.ivex.serverdata.ServerData;
import dev.ivex.serverdata.data.ServerManager;
import dev.ivex.serverdata.jedis.JedisPublisher;
import dev.ivex.serverdata.utilites.Color;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class RunCmdCommand extends BukkitCommand {


    public RunCmdCommand() {
        super("runcmd");

        ServerData.getInstance().getCommandMap().register("runcmd", this);
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
            if (args[0].equalsIgnoreCase("all")) {
                StringBuilder command = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    command.append(args[i]).append(" ");
                }
                JedisPublisher.handleWrite(ServerData.getInstance().getConfig().getString("DATABASE.REDIS.CHANNEL") + ";", "command;all;" + command);
                sender.sendMessage(Color.translate(ServerData.getInstance().getConfig().getString("MESSAGE.COMMAND_ALL").replace("%command%", String.valueOf(command))));
                return false;
            }
            ServerManager data = ServerData.getInstance().getServerManager().getByName(args[0]);

            if (data == null) {
                sender.sendMessage(Color.translate("&cThat server does cannot be found in our database."));
                return false;
            }
            StringBuilder command = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                command.append(args[i]).append(" ");
            }
            JedisPublisher.handleWrite(ServerData.getInstance().getConfig().getString("DATABASE.REDIS.CHANNEL") + ";", "command;" + data.getName() + ';' + command);
                sender.sendMessage(Color.translate(ServerData.getInstance().getConfig().getString("MESSAGE.COMMAND").replace("%server%", String.valueOf(data.getName())).replace("%command%", String.valueOf(command))));
        }
        return true;
    }


    public final String usage = Color.translate(
            "&cRunCmd Commands - Help"
                    + "\n/runcmd <server> <command>"
                    + "\n/runcmd <all> <command>");
}