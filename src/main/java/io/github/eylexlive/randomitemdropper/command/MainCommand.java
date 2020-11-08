package io.github.eylexlive.randomitemdropper.command;

import io.github.eylexlive.randomitemdropper.RandomItemDropper;
import io.github.eylexlive.randomitemdropper.manager.DropManager;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class MainCommand implements CommandExecutor {
    private final RandomItemDropper plugin;
    private final String[] mainMessage;
    public MainCommand(RandomItemDropper plugin) {
        this.plugin = plugin;
        this.mainMessage = new String[]{
                "§8§m---------------------------------",
                "",
                "§aRandom Item Dropper Help",
                "",
                "/rid drop <drop> <itemSize>",
                "/rid create <drop>",
                "/rid delete <drop>",
                "/rid loc1 <drop>",
                "/rid loc2 <drop>",
                "/rid addItem <drop> <material>",
                "/rid removeItem <drop> <material>",
                "/rid setChance <drop> <material> <chance>",
                "/rid async <drop>",
                "/rid setAsyncDelay <drop> <delay>",
                "/rid info <drop>",
                "/rid reloadConfig",
                "" ,
                "§8§m---------------------------------"
        };
    }
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.isOp()) {
            commandSender.sendMessage(new String[] {
                    "§aRandom Item Dropper §fby EylexLive",
                    "§fVersion: §av" + this.plugin.getDescription().getVersion()
            });
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reloadConfig")) {
            this.plugin.reloadConfig();
            commandSender.sendMessage(new String[] {
                    "§aReload success!",
                    "",
                    "§4Warning: §cAuto dropper mode cannot be turned on with reloading the config, if you opened it with reloading the config, please restart your server."
            });
            return true;
        } else if (args.length == 0) {
            commandSender.sendMessage(this.mainMessage);
            return true;
        }
        final DropManager dropManager = new DropManager(
                args[1], this.plugin.getDatabaseManager().getDatabaseConfiguration()
        );
        switch (args.length) {
            case 2:
                if (args[0].equalsIgnoreCase("create")) {
                    dropManager.create();
                    commandSender.sendMessage(new String[] {
                            "§aYou created new drop named §f" + args[1],
                            "",
                            "§cPlease set the first location with /rid loc1 " + args[1],
                            "§cPlease set the second location with /rid loc2 " + args[1] ,
                            "§cPlease add drop items with /rid addItem " + args[1]
                    });
                } else if (args[0].equalsIgnoreCase("delete")) {
                    dropManager.delete();
                    commandSender.sendMessage("§aYou deleted the drop named §f" + args[1]);
                } else if (args[0].equalsIgnoreCase("loc1")) {
                    if (!(commandSender instanceof  Player))
                        return true;
                    final Player player = (Player) commandSender;
                    dropManager.setLoc(player.getLocation(), 1);
                    commandSender.sendMessage("§aThe first location of the drop named §f" + args[1] + " §ahas been set.");
                } else if (args[0].equalsIgnoreCase("loc2")) {
                    if (!(commandSender instanceof  Player))
                        return true;
                    final Player player = (Player) commandSender;
                    dropManager.setLoc(player.getLocation(), 2);
                    commandSender.sendMessage("§aThe second location of the drop named §f" + args[1] + " §ahas been set.");
                } else if (args[0].equalsIgnoreCase("async")) {
                    final boolean async = dropManager.setAsync();
                    final String msg = (async ? "enabled" : "disabled");
                    commandSender.sendMessage(new String[] {"§aAsync mode §f" + msg});
                    if (async)
                        commandSender.sendMessage("§cIf you have not set delay, please set async delay with /rid setAsyncDelay " + args[1] + " <delay(seconds)>");
                } else if (args[0].equalsIgnoreCase("info")) {
                    commandSender.sendMessage(dropManager.getInfoMessage());
                }
                break;
            case 3:
                if (args[0].equalsIgnoreCase("additem")) {
                    final Material material = Material.getMaterial(args[2]);
                    if (material == null) {
                        commandSender.sendMessage("§cMaterial does not exits. (" + args[2] + ")");
                        return true;
                    }
                    dropManager.addItem(material);
                    final String matName = material.name();
                    commandSender.sendMessage(new String[] {
                            "§aAdded an item named §f" + matName + "§a to §f" + args[1],
                            "§cPlease set the chance of " + matName +" with /rid setChance " + args[1]+ " " + matName + " <chance>"
                    });
                } else if (args[0].equalsIgnoreCase("removeitem")){
                    final Material material = Material.getMaterial(args[2]);
                    if (material == null) {
                        commandSender.sendMessage("§cMaterial does not exits. (" + args[2] + ")");
                        return true;
                    }
                    dropManager.removeItem(material);
                    commandSender.sendMessage("§aRemoved an item named §f" + material.name() + "§a from §f" + args[1]);
                } else if (args[0].equalsIgnoreCase("drop") && NumberUtils.isNumber(args[2])) {
                    final int size = Integer.parseInt(args[2]);
                    commandSender.sendMessage("§aBased on the §f"+ size +" items §achance rates selected, it randomly dropping between the determined locations.");
                    dropManager.dropItems(size);
                    commandSender.sendMessage("§fAll items dropped, if async mode is not enabled.");
                } else if (args[0].equalsIgnoreCase("setasyncdelay") && NumberUtils.isNumber(args[2])) {
                    final int delay = Integer.parseInt(args[2]);
                    dropManager.setAsyncDelay(delay);
                    commandSender.sendMessage("§aDelay set to §f" + delay +" seconds");
                }
                break;
            case 4:
                final Material material = Material.getMaterial(args[2]);
                if (material == null) {
                    commandSender.sendMessage("§cMaterial does not exits. (" + args[2] + ")");
                    return true;
                }
                if (args[0].equalsIgnoreCase("setchance") && NumberUtils.isNumber(args[3])) {
                    final double chance = Double.parseDouble(args[3]);
                    dropManager.setChance(material, chance);
                    commandSender.sendMessage("§aThe chance rate of §f" + material.name() +" §athe has been successfully set to §f"+ chance + "%");
                }
                break;
            default:
                commandSender.sendMessage(this.mainMessage);
                break;
        }
        return true;
    }
}
