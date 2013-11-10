package com.gmail.aphymi.newbiechat;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class NewbieChatExecutor implements CommandExecutor {
	private NewbieChat plugin;
	String name = "§b[§eNC§b] ";
	String meta = "NewbieChatRoom";
	public NewbieChatExecutor(NewbieChat plugin) {
		this.plugin = plugin;
		
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("newbies")) {
			return newbies(sender, cmd, args);
		}
		if (cmd.getName().equalsIgnoreCase("leave")) {
			return leave(sender, cmd, args);
		}
		if (cmd.getName().equalsIgnoreCase("pullnew")) {
			return pullNew(sender, cmd, args);
		}
		if (cmd.getName().equalsIgnoreCase("pull")) {
			return pull(sender, cmd, args);
		}
		if (cmd.getName().equalsIgnoreCase("chatters")) {
			return chatters(sender, cmd, args);
		}
		if (cmd.getName().equalsIgnoreCase("join")) {
			return join(sender, cmd, args);
		}
		return true;
	}
	
	private boolean newbies(CommandSender sender, Command cmd, String[] args) {
		if (!sender.hasPermission("newbiechat.newbies.do")) {
			sender.sendMessage(name + "§cYou don't have permission to do that");
			return true;
		}
		ArrayList<Player> newbieList = new ArrayList<Player>();
		for (Player player: Bukkit.getServer().getOnlinePlayers()) {
			if (!(player.hasPermission("newbiechat.notnewbie"))) {
				newbieList.add(player);
			}
		}
		if (newbieList.size() == 0) {
			sender.sendMessage(name + "§cThere are no newbies online");
			return true;
		}
		StringBuilder sb = new StringBuilder();
		for (Player player: newbieList) {
			sb.append(player.getName() + ", ");
		}
		sender.sendMessage(name + "§eOnline newbies: " + sb.toString().substring(0, sb.toString().length() - 2));
		return true;
	}
	
	private int getFreeRoom() {
		boolean a = true;
		int freeRoom = 0;
		while (a) {
			a = false;
			for (Player player: Bukkit.getServer().getOnlinePlayers()) {
				if (player.hasMetadata(meta) && player.getMetadata(meta).get(0).asInt() == freeRoom) {
					a = true;
					freeRoom++;
				}
			}
		}
		return freeRoom;
	}
	
	private boolean leave(CommandSender sender, Command cmd, String[] args) {
		if (!sender.hasPermission("newbiechat.rooms.leave")) {
			sender.sendMessage(name + "§cYou don't have permission to do that");
			return true;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(name + "§cYou must be a player to do that");
			return true;
		}
		Player player = (Player) sender;
		if (!player.hasMetadata(meta)) {
			sender.sendMessage(name + "§cYou are not in a room");
			return true;
		}
		int room = player.getMetadata(meta).get(0).asInt();
		player.removeMetadata(meta, plugin);
		boolean isStaff = false;
		for (Player onlinePlayer: Bukkit.getServer().getOnlinePlayers()) {
			if (onlinePlayer.hasMetadata(meta) && onlinePlayer.hasPermission("newbiechat.staff") && (onlinePlayer.getMetadata(meta).get(0).asInt() == room)) {
				isStaff = true;
			}
		}
		if (!isStaff) {
			for (Player onlinePlayer: Bukkit.getServer().getOnlinePlayers()) {
				if (onlinePlayer.hasMetadata(meta) && onlinePlayer.getMetadata(meta).get(0).asInt() == room) {
					onlinePlayer.removeMetadata(meta, plugin);
				}
			}
		}
		player.sendMessage(name + "§eYou are now talking in main chat");
		return true;
	}
	
	private void setRoom(Player player, int value) {
		player.setMetadata(meta, new FixedMetadataValue(plugin, value));
	}
	
	//Join a new room and pull a newbie into that room
	private boolean pullNew(CommandSender sender, Command cmd, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(name + "§cYou must be a player to do that");
			return true;
		}
		if (!sender.hasPermission("newbiechat.rooms.pullnew")) {
			sender.sendMessage(name + "§cYou don't have permission to do that");
			return true;
		}
		int newRoom = getFreeRoom();
		if (args.length == 0) {
			sender.sendMessage(name + "§cYou must specify a username");
			return true;
		}
		Player newbie = Bukkit.getServer().getPlayer(args[0]);
		if (newbie == null) {
			sender.sendMessage(name + "§cThat player is not online/doesn't exist!");
			return true;
		}
		setRoom((Player)sender, newRoom);
		setRoom(newbie, newRoom);
		
		newbie.sendMessage(name + "§eYou have been pulled into a chat room");
		sender.sendMessage(String.format(name + "§eYou have pulled %s into a room.", newbie.getName()));
		return true;
	}
	
	private boolean pull(CommandSender sender, Command cmd, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length == 0) {
				sender.sendMessage(name + "§cYou must specify a player!");
				return true;
			}
			Player newbie = Bukkit.getServer().getPlayer(args[0]);
			if (newbie == null) {
				sender.sendMessage(name + "§cPlayer not found!");
				return true;
			}
			if (!newbie.hasMetadata(meta)) {
				sender.sendMessage(name + "§cThat player is already in main chat");
				return true;
			}
			newbie.removeMetadata(meta, plugin);
			return true;
		}
		if (!sender.hasPermission("newbiechat.rooms.pull")) {
			sender.sendMessage(name + "§cYou don't have permission for that");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(name + "§cYou must specify a username");
			return true;
		}
		Player newbie = Bukkit.getServer().getPlayer(args[0]);
		if (newbie == null) {
			sender.sendMessage(name + "§cPlayer not found!");
			return true;
		}
		if (!((Player)sender).hasMetadata(meta)) {
			if (!newbie.hasMetadata(meta)) {
				sender.sendMessage(name + "§cThat person is already in main chat");
				return true;
			}
			sender.sendMessage(String.format(name + "§eYou have moved %s to main chat", newbie.getName()));
			newbie.removeMetadata(meta, plugin);
			return true;
		}
		setRoom(newbie, ((Player)sender).getMetadata(meta).get(0).asInt());
		newbie.sendMessage(name + "§eYou have been pulled into a chat room");
		sender.sendMessage(String.format(name + "§eYou have moved %s to your room", newbie.getName()));
		return true;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes" })
	private boolean chatters(CommandSender sender, Command cmd, String[] args) {
		if (!sender.hasPermission("newbiechat.chatters.do")) {
			sender.sendMessage(name + "§cYou don't have permission to do that");
			return true;
		}
		Player[] players = Bukkit.getServer().getOnlinePlayers();
		int maxchat = -1;
		for (Player player: players) {
			if (player.hasMetadata(meta) && (player.getMetadata(meta).get(0).asInt() > maxchat)) {
				maxchat = player.getMetadata(meta).get(0).asInt();
			}
		}
		if (maxchat == -1) {
			sender.sendMessage(name + "§eNo one is in a room");
			return true;
		}
		ArrayList[] chatterlist = new ArrayList[maxchat + 1];
		for (int i = 0; i < chatterlist.length; i++) {
			chatterlist[i] = new ArrayList();
			for (Player player: players) {
				if (player.hasMetadata(meta) && player.getMetadata(meta).get(0).asInt() == i) {
					chatterlist[i].add(player.getName());
				}
			}
		}
		for (int i = 0; i < chatterlist.length; i++) {
			String send = "";
			if (chatterlist[i].size() == 0) {
				continue;
			}
			send += "§eRoom " + i + ": §f";
			for (int j = 0; j < chatterlist[i].size(); j++) {
				send += chatterlist[i].get(j) + ", ";
			}
			send = send.substring(0, send.length() - 2);
			sender.sendMessage(send);
		}
		return true;
	}
	
	private boolean join(CommandSender sender, Command cmd, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(name + "§cYou must be a player to do that!");
			return true;
		}
		if (!sender.hasPermission("newbiechat.rooms.join")) {
			sender.sendMessage(name + "§cYou don't have permission to do that");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(name + "§cYou must specify a room");
			return true;
		}
		try {
			Integer.parseInt(args[0]);
		} catch(NumberFormatException ex) {
			sender.sendMessage(name + "§cThe room must be a number");
			return true;
		}
		if (((Player)sender).hasMetadata(meta)) {
			boolean isStaff = false;
			int room = ((Player)sender).getMetadata(meta).get(0).asInt();
			for (Player play: Bukkit.getServer().getOnlinePlayers()) {
				if (play.hasMetadata(meta) && play.getMetadata(meta).get(0).asInt() == room && play.hasPermission("newbiechat.staff")) {
					isStaff = true;
					break;
				}
			}
			if (!isStaff) {
				for (Player play: Bukkit.getServer().getOnlinePlayers()) {
					if (play.hasMetadata(meta) && play.getMetadata(meta).get(0).asInt() == room) {
						play.removeMetadata(meta, plugin);
					}
				}
			}
		}
		setRoom((Player) sender, Integer.parseInt(args[0]));
		sender.sendMessage(name + "You have joined room " + args[0]);
		return true;
	}
}
