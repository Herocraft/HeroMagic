package com.herocraftonline.heromagic;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.*;

public class HeroMagicPlayerListener extends PlayerListener {
	private final HeroMagic plugin;

	public HeroMagicPlayerListener(HeroMagic instance) {
		this.plugin = instance;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		boolean learned;
		
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (event.getClickedBlock().getTypeId() == 47) {
				String spellName = plugin.castManager.getSpellLocations(player).get(event.getClickedBlock().getLocation());
				if (spellName != null) {
					PlayerSpell playerSpell = plugin.getDatabase().find(PlayerSpell.class).where().ieq("player", player.getName()).ieq("spell", spellName).findUnique();
					if (playerSpell == null) learned = false;
					else learned = playerSpell.isLearned();
					if (learned) {
						player.sendMessage(ChatColor.BLUE + "You already learned " + spellName);
					} else {
						event.getPlayer().sendMessage(ChatColor.BLUE + "You have learned the spell " + spellName + "!");
						plugin.castManager.addSpell(player, spellName);
					}
				}
			}
		}
	}
}
