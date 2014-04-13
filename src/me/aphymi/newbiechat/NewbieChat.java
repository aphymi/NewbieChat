/*
 * Copyright (c) 2014 Amelia Winters
 * See the file license.txt.
 */
package me.aphymi.newbiechat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class NewbieChat extends JavaPlugin {
	public static final String name = "§b[§eNC§b] §e";
	public static final String nameErr = name + "§c";
	
	public void onEnable() {
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(new NewbieChatListener(this), this);
		NewbieChatExecutor executor = new NewbieChatExecutor(this);
		getCommand("ncreload").setExecutor(executor);
		getCommand("newbies").setExecutor(executor);
		getCommand("staff").setExecutor(executor);
		getCommand("chatters").setExecutor(executor);
		getCommand("pullnew").setExecutor(executor);
		getCommand("pull").setExecutor(executor);
		getCommand("join").setExecutor(executor);
		getCommand("leave").setExecutor(executor);
	}

	public void onDisable() {
		for (Player player: Bukkit.getServer().getOnlinePlayers()) {
			if (player.hasMetadata("NewbieChatRoom")) {
				player.removeMetadata("NewbieChatRoom", this);
			}
		}
	}
}