package com.gmail.aphymi.newbiechat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;


public class NewbieChatListener implements Listener {
	static final String meta = "NewbieChatRoom";
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		/*
		 * NEWBIE CHAT RULES
		 * Newbies/Members in any room do not receive normal chat
		 * Newbies/Members receive chat from anyone else in their room as normal chat
		 * Newbies/Members do not receive chat from those in other rooms
		 * 
		 * STAFF CHAT RULES
		 * Staff receives chat from newbies in their room as [NewbieChat] <Username> message
		 * Staff receives chat from other staff in their room as ^
		 * Staff talks in newbie chat rooms just by normally talking
		 * Staff can talk in main chat by using ! before their message
		 * Staff cannot see chat in other rooms
		 * 
		 * GLOBAL CHAT RULES
		 * Normal chat members cannot see newbie chat
		 * 
		 */
		
		if (!e.isAsynchronous()) {return;}
		
		boolean hasRoom = e.getPlayer().hasMetadata(meta);
		int room = hasRoom ? e.getPlayer().getMetadata(meta).get(0).asInt() : -1;
		
		
		//If staff is talking in a room and has ! as the first character of their message
		if (e.getPlayer().hasPermission("newbiechat.staff") && hasRoom && e.getMessage().charAt(0) == '!') {
			e.setMessage(e.getMessage().replaceFirst("!", ""));
			for (Player player: Bukkit.getServer().getOnlinePlayers()) {
				if (!player.hasPermission("newbiechat.staff") && player.hasMetadata(meta) && player.getMetadata(meta).get(0).asInt() == room) {
					e.getRecipients().remove(player);
				}
			}
			return;
		}
		
		//SET NEWBIE CHAT RULES
		for (Player player: Bukkit.getServer().getOnlinePlayers()) {
			boolean playerHasRoom = player.hasMetadata(meta);
			int playerRoom = playerHasRoom ? player.getMetadata(meta).get(0).asInt() : -1;
			//Player is in a newbie chat room
			if (playerHasRoom) {
				//Player is a newbie or member
				if (!player.hasPermission("newbiechat.staff")) {
					//Chat is from normal chat
					if (!hasRoom) {
						e.getRecipients().remove(player);
					//Chat is from someone else in a different chat room
					} else if (room != playerRoom) {
						e.getRecipients().remove(player);
					}
				}
				//SET STAFF CHAT RULES
				//Player is staff and is in a chat room
				else if (playerHasRoom && player.hasPermission("newbiechat.staff")) {
					//The event source player is in the same chatroom as this staff
					if (hasRoom && (room == playerRoom)) {
						e.getRecipients().remove(player);
						player.sendMessage("§9[§eNewbieChat§9] §f<§8" + e.getPlayer().getName() + "§f> §9" + e.getMessage());
					}
					//The event source is in a different chatroom than this staff
					if (hasRoom && (room != playerRoom)) {
						e.getRecipients().remove(player);
					}
				}
			}
			//SET GLOBAL RULES
			//Player is in global chat and chat source player is not
			else if (hasRoom && !playerHasRoom) {
					e.getRecipients().remove(player);
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (e.getPlayer().hasMetadata(meta)) {
			leaveRoom(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		if (e.getPlayer().hasMetadata(meta)) {
			leaveRoom(e.getPlayer());
		}
	}
	
	protected static void leaveRoom(Player p) {
		boolean isStaff = false;
		int room = p.getMetadata(meta).get(0).asInt();
		Plugin plugin = Bukkit.getPluginManager().getPlugin("NewbieChat");
		p.removeMetadata(meta, plugin);
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
	
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if (!player.hasPermission("newbiechat.notnewbie")) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if (!player.hasPermission("newbiechat.notnewbie")) {
				e.setCancelled(true);
			}
		}
	}
}