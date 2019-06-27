package com.github.qianniancc.anticmd;

import java.io.File;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiCmd extends JavaPlugin implements Listener {
	private HashSet<String> unaffectedPlayers = new HashSet<String>();

	public void onEnable() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		File file = new File(getDataFolder(), "config.yml");
		if (!file.exists()) {
			saveDefaultConfig();
		}
		getLogger().info("启动成功！");
		reloadConfig();
		getServer().getPluginManager().registerEvents(this, this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("acreload")) {
			if ((!sender.isOp()) && (!sender.hasPermission("ac.reload")) && (!sender.hasPermission("ac.*"))) {
				String TextNotReloadPerm = getConfig().getString("NotReloadPerm");
				String NotReloadPerm = ChatColor.translateAlternateColorCodes('&', TextNotReloadPerm);
				sender.sendMessage(NotReloadPerm);
				return false;
			}
			reloadConfig();
			String TextReloadOK = getConfig().getString("ReloadOK");
			String ReloadOK = ChatColor.translateAlternateColorCodes('&', TextReloadOK);
			sender.sendMessage(ReloadOK);
			return true;
		}
		return false;
	}

	@EventHandler
	public void onCmd(PlayerCommandPreprocessEvent e) {
		String cmd = e.getMessage().trim();
		if (cmd.startsWith("/")) {
			cmd = cmd.substring(1).trim();
		}
		int firstSpace = cmd.indexOf(' ');
		if (firstSpace < 0) {
			firstSpace = cmd.length();
		}
		cmd = cmd.substring(0, firstSpace);
		cmd = cmd.toLowerCase();
		if (!getConfig().getStringList("onworld").contains(e.getPlayer().getWorld().getName())) {
			return;
		}
		String world = e.getPlayer().getWorld().getName();
		if (getConfig().getConfigurationSection("world").getStringList(world).contains(cmd)) {
			return;
		}
		if (e.getPlayer().isOp()) {
			return;
		}
		if ((e.getPlayer().hasPermission("ac.*")) || (e.getPlayer().hasPermission("ac.noac"))) {
			return;
		}
		String playerName = e.getPlayer().getName();
		if (this.unaffectedPlayers.contains(playerName)) {
			return;
		}
		e.setCancelled(true);
		String TextAntiMsg = getConfig().getString("AntiMsg");
		String AntiMsg = ChatColor.translateAlternateColorCodes('&', TextAntiMsg);
		e.getPlayer().sendMessage(AntiMsg);
	}
}
