package com.bukkit.Dgco.HeroMagic;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * HeroSpells for HEROES
 *
 * @author Dgco
 */
public class HeroMagic extends JavaPlugin {
    private final HeroMagicPlayerListener playerListener = new HeroMagicPlayerListener(this);
	private final HeroMagicBlockListener blockListener = new HeroMagicBlockListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    
    public String pName = "HeroMagic";
    public Logger log;

   /* public HeroMagic(PluginLoader pluginLoader, Server instance,
            PluginDescriptionFile desc, File folder, File plugin,
            ClassLoader cLoader) throws IOException {
      //  super(pluginLoader, instance, desc, folder, plugin, cLoader);
    	//super();
        // TODO: Place any custom initialisation code here
    	
    	

        // NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled

    }*/

   
    public boolean onCommand(CommandSender  sender,  
    		  Command  command,  
    		  String  label,  
    		  String[]  args)
    {
		return playerListener.onPlayerCommand(sender,command,label,args);
    	
    	
    }
    
    
    public void onEnable() {
		// TODO: Place any custom enable code here including the registration of any events

		// Register our events
		PluginManager pm = getServer().getPluginManager();
		//getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_ANIMATION, playerListener, Priority.Normal, this);
	    pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
	    pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
	    
		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
		
		//log = new Logger(pName);
    }
    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        System.out.println("Goodbye world!");
    }
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }


    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}

