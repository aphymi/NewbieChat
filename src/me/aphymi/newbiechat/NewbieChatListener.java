package me.aphymi.newbiechat;

import static me.aphymi.newbiechat.NewbieChat.name;
import static me.aphymi.newbiechat.NewbieChatExecutor.meta;

import java.util.HashMap;
import java.util.regex.Pattern;

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
	private Plugin plugin;
	
	public NewbieChatListener(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		/*
		 * NON-STAFF CHAT RULES
		 * Non-staff in any room do not receive normal chat
		 * Non-staff receive chat from anyone else in their room as normal chat
		 * Non-staff do not receive chat from those in other rooms
		 * 
		 * STAFF CHAT RULES
		 * Staff receives formatted chat from newbies in their room
		 * Staff receives formatted chat from other staff in their room
		 * Staff talk in newbie chat rooms with normal messaging
		 * Staff can talk in main chat by using ! before their message
		 * Staff cannot see chat in other rooms
		 * 
		 * GLOBAL CHAT RULES
		 * People not in a room cannot see newbie chat
		 * 
		 */
		
		if (!e.isAsynchronous()) return;
		
		boolean hasRoom = e.getPlayer().hasMetadata(meta);
		int room = hasRoom ? e.getPlayer().getMetadata(meta).get(0).asInt() : -1;
		
		//If staff is talking in a room and has ! as the first character of their message
		String mainPrefix = plugin.getConfig().getString("main_prefix");
		if (hasRoom && e.getPlayer().hasPermission("newbiechat.staff") && e.getMessage().startsWith(mainPrefix)) {
			e.setMessage(e.getMessage().replaceFirst(Pattern.quote(mainPrefix), ""));
			hasRoom = false;
		}
		
		for (Player player: Bukkit.getOnlinePlayers()) {
			boolean playerHasRoom = player.hasMetadata(meta);
			int playerRoom = playerHasRoom ? player.getMetadata(meta).get(0).asInt() : -1;
			//Player is in a chat room
			if (playerHasRoom) {
				//Player is not staff
				if (!player.hasPermission("newbiechat.staff")) {
					//Event is from main chat OR event is from someone in a different chat room
					if (!hasRoom || room != playerRoom) {
						e.getRecipients().remove(player);
					}
				}
				//Player is staff
				else {
					//The event player is in a chat room
					if (hasRoom) {
						//The event player is in the same chat room as this staff
						if (room == playerRoom) {
							e.getRecipients().remove(player);
							
							HashMap<String, String> vars = new HashMap<String, String>();
							vars.put("MESSAGE", e.getMessage());
							vars.put("NAME", e.getPlayer().getName());
							vars.put("DNAME", e.getPlayer().getDisplayName());
							
							String format = plugin.getConfig().getString("format");
							for (String var: vars.keySet()) {
								format = format.replace("{" + var + "}", vars.get(var));
							}
							player.sendMessage(format);
						}
						//The event player is in a different chat room than this staff
						else {
							e.getRecipients().remove(player);
						}
					}
				}
			}
			//Player is in global chat and event player is not
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
	/*
	 * Removes player p from the room, flushing everyone from the
	 * room if there aren't any staff remaining in it.
	 */
	protected static void leaveRoom(Player p) {
		boolean roomHasStaff = false;
		int room = p.getMetadata(meta).get(0).asInt();
		Plugin plugin = Bukkit.getPluginManager().getPlugin("NewbieChat");
		p.removeMetadata(meta, plugin);
		for (Player player: Bukkit.getOnlinePlayers()) {
			if (player.hasMetadata(meta) && player.getMetadata(meta).get(0).asInt() == room && player.hasPermission("newbiechat.staff")) {
				roomHasStaff = true;
				break;
			}
		}
		if (!roomHasStaff) {
			for (Player player: Bukkit.getOnlinePlayers()) {
				if (player.hasMetadata(meta) && player.getMetadata(meta).get(0).asInt() == room) {
					player.removeMetadata(meta, plugin);
					player.sendMessage(name + "You are now talking in main chat.");
				}
			}
		}
	}
	
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (plugin.getConfig().getBoolean("block_newbie_damage") && e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if (!player.hasPermission("newbiechat.notnewbie")) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if (plugin.getConfig().getBoolean("block_newbie_hunger") && e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if (!player.hasPermission("newbiechat.notnewbie")) {
				e.setCancelled(true);
			}
		}
	}
}