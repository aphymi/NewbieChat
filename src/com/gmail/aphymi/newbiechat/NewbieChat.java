package com.gmail.aphymi.newbiechat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class NewbieChat extends JavaPlugin {
        public void onEnable() {
                getServer().getPluginManager().registerEvents(new NewbieChatListener(this), this);
                NewbieChatExecutor executor = new NewbieChatExecutor(this);
                getCommand("newbies").setExecutor(executor);
                getCommand("leave").setExecutor(executor);
                getCommand("pullnew").setExecutor(executor);
                getCommand("pull").setExecutor(executor);
                getCommand("chatters").setExecutor(executor);
                getCommand("join").setExecutor(executor);
        }
        
        public void onDisable() {
                for (Player player: Bukkit.getServer().getOnlinePlayers()) {
                        if (player.hasMetadata("NewbieChatRoom")) {
                                player.removeMetadata("NewbieChatRoom", this);
                        }
                }
        }
}