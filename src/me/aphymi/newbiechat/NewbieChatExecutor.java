/*
 * Copyright (c) 2014 Amelia Winters
 * See the file license.txt.
 */
package me.aphymi.newbiechat;

import static me.aphymi.newbiechat.NewbieChat.name;
import static me.aphymi.newbiechat.NewbieChat.nameErr;
import static me.aphymi.newbiechat.NewbieChatListener.leaveRoom;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class NewbieChatExecutor implements CommandExecutor {
	public static final String noPerms = nameErr + "You don't have permission for that.";
	public static final String notPlayer = nameErr + "You need to be a player to do that!";
	protected static final String meta = "newbiechat_room";
	private NewbieChat plugin;
	
	public NewbieChatExecutor(NewbieChat plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (cmd.getName().toLowerCase()) {
			case "ncreload":
				return reload(sender, cmd, args);
			case "newbies":
				return newbies(sender, cmd, args);
			case "staff":
				return staff(sender, cmd, args);
			case "chatters":
				return chatters(sender, cmd, args);
			case "pullnew":
				return pullNew(sender, cmd, args);
			case "pull":
				return pull(sender, cmd, args);
			case "join":
				return join(sender, cmd, args);
			case "leave":
				return leave(sender, cmd, args);
		}
		return false;
	}
	
	private boolean reload(CommandSender sender, Command cmd, String[] args) {
		if (!sender.hasPermission("newbiechat.reload")) {
			sender.sendMessage(noPerms);
			return true;
		}
		plugin.reloadConfig();
		sender.sendMessage(name + "Configuration reloaded.");
		return true;
	}
	
	private boolean newbies(CommandSender sender, Command cmd, String[] args) {
		if (!sender.hasPermission("newbiechat.newbies")) {
			sender.sendMessage(noPerms);
			return true;
		}
		ArrayList<String> newbieList = new ArrayList<String>();
		for (Player player: Bukkit.getOnlinePlayers()) {
			if (!(player.hasPermission("newbiechat.notnewbie"))) {
				newbieList.add(player.getName());
			}
		}
		if (newbieList.size() == 0) {
			sender.sendMessage(name + "There are no newbies online");
			return true;
		}
		sender.sendMessage(name + String.format("There are %s online newbies: %s",
				newbieList.size(), StringUtils.join(newbieList, ", ")));
		return true;
	}
	
	private boolean staff(CommandSender sender, Command cmd, String[] args) {
		if (!sender.hasPermission("newbiechat.staff")) {
			sender.sendMessage(noPerms);
			return true;
		}
		ArrayList<String> staffList = new ArrayList<String>();
		for (Player player: Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("newbiechat.staffmember")) {
				staffList.add(player.getName());
			}
		}
		if (staffList.size() == 0) {
			sender.sendMessage(name + "There are no staff online.");
			return true;
		}
		sender.sendMessage(name + String.format("There are %s online staff: %s",
				staffList.size(), StringUtils.join(staffList, ", ")));
		return true;
	}
	
	private boolean chatters(CommandSender sender, Command cmd, String[] args) {
		if (!sender.hasPermission("newbiechat.chatters")) {
			sender.sendMessage(noPerms);
			return true;
		}
		HashMap<Integer, ArrayList<String>> chatterList = new HashMap<Integer, ArrayList<String>>();
		for (Player player: Bukkit.getOnlinePlayers()) {
			if (player.hasMetadata(meta)) {
				int room = player.getMetadata(meta).get(0).asInt();
				if (!chatterList.containsKey(room)) {
					chatterList.put(room, new ArrayList<String>());
				}
				chatterList.get(room).add(player.getName());
			}
		}
		if (chatterList.isEmpty()) {
			sender.sendMessage(name + "No one is currently in a chat room.");
			return true;
		}
		ArrayList<String> lines = new ArrayList<String>();
		for (Integer room: chatterList.keySet()) {
			lines.add(String.format(
				"§eRoom %s: §f%s", room,
				StringUtils.join(chatterList.get(room), ", ")
			));
		}
		sender.sendMessage(StringUtils.join(lines, "\n"));
		return true;
	}
	
	
	private int getFreeRoom() {
		int freeRoom = 0;
		
		outer:
		while (true) {
			for (Player player: Bukkit.getOnlinePlayers()) {
				if (player.hasMetadata(meta) && player.getMetadata(meta).get(0).asInt() == freeRoom) {
					freeRoom++;
					continue outer;
				}
			}
			break;
		}
		return freeRoom;
	}
	
	private boolean leave(CommandSender sender, Command cmd, String[] args) {
		if (!sender.hasPermission("newbiechat.rooms.leave")) {
			sender.sendMessage(noPerms);
			return true;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(notPlayer);
			return true;
		}
		Player player = (Player) sender;
		if (!player.hasMetadata(meta)) {
			sender.sendMessage(nameErr + "You are not in a room.");
			return true;
		}
		leaveRoom(player);
		player.sendMessage(name + "You are now talking in main chat.");
		return true;
	}
	
	private void setRoom(Player player, int value) {
		player.setMetadata(meta, new FixedMetadataValue(plugin, value));
	}
	
	//Join a new room and pull a newbie into that room
	private boolean pullNew(CommandSender sender, Command cmd, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(notPlayer);
			return true;
		}
		Player player = (Player) sender;
		if (!sender.hasPermission("newbiechat.rooms.pullnew")) {
			sender.sendMessage(noPerms);
			return true;
		}
		int newRoom = getFreeRoom();
		if (args.length == 0) {
			sender.sendMessage(nameErr + "You must specify a username.");
			return true;
		}
		Player newbie = Bukkit.getPlayer(args[0]);
		if (newbie == null) {
			sender.sendMessage(nameErr + "That player is not online/doesn't exist!");
			return true;
		}
		if (player.hasMetadata(meta)) {
			leaveRoom(player);
		}
		setRoom(player, newRoom);
		setRoom(newbie, newRoom);
		
		newbie.sendMessage(name + "You have been pulled into a chat room.");
		sender.sendMessage(name + String.format("You have pulled %s into a room.", newbie.getName()));
		return true;
	}
	
	private boolean pull(CommandSender sender, Command cmd, String[] args) {
		if (!sender.hasPermission("newbiechat.rooms.pull")) {
			sender.sendMessage(noPerms);
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(nameErr + "You must specify a player!");
			return true;
		}
		boolean inRoom;
		if ((sender instanceof Player)) {
			Player player = (Player) sender;
			inRoom = player.hasMetadata(meta);
		} else {
			inRoom = false;
		}
		Player newbie = Bukkit.getPlayer(args[0]);
		if (newbie == null) {
			sender.sendMessage(nameErr + "Player not found!");
			return true;
		}
		if (!inRoom) {
			if (!newbie.hasMetadata(meta)) {
				sender.sendMessage(nameErr + "That player is already in main chat.");
				return true;
			}
			newbie.removeMetadata(meta, plugin);
			newbie.sendMessage(name + "You have been pulled into main chat.");
			sender.sendMessage(String.format("You have pulled %s into main chat.", args[0]));
			return true;
		}
		setRoom(newbie, ((Player)sender).getMetadata(meta).get(0).asInt());
		newbie.sendMessage(name + "You have been pulled into a chat room.");
		sender.sendMessage(name + String.format("You have moved %s to your room.", newbie.getName()));
		return true;
	}
	
	private boolean join(CommandSender sender, Command cmd, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(notPlayer);
			return true;
		}
		if (!sender.hasPermission("newbiechat.rooms.join")) {
			sender.sendMessage(noPerms);
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(nameErr + "You must specify a room.");
			return true;
		}
		int room;
		try {
			room = Integer.parseInt(args[0]);
		} catch(NumberFormatException ex) {
			sender.sendMessage(nameErr + "The room must be a number.");
			return true;
		}
		Player player = (Player) sender;
		if (player.hasMetadata(meta)) {
			leaveRoom(player);
		}
		setRoom(player, room);
		sender.sendMessage(name + String.format("You have joined room %s.", args[0]));
		return true;
	}
}