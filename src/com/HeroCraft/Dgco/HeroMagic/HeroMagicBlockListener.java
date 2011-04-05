package com.bukkit.Dgco.HeroMagic;

import org.bukkit.block.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.*;
import org.bukkit.inventory.Inventory;

/**
 * BukkitSpells block listener
 * @author Dgco
 */
public class HeroMagicBlockListener extends BlockListener {
    private final HeroMagic plugin;
    public String stuff ="";
    

    public HeroMagicBlockListener(final HeroMagic plugin) {
        this.plugin = plugin;
    }
    
    public void onBlockBreak(BlockBreakEvent event)
    {
		
    	
    }
    
   
    
}
