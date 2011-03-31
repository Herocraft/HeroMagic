package com.bukkit.Dgco.HeroMagic;

import java.io.*;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockEvent;
//import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.*;
import org.bukkit.material.MaterialData;
import org.bukkit.*;





/**
 * Handle events for all Player related events
 * @author Dgco
 */
public class HeroMagicPlayerListener extends PlayerListener 
{
    private final HeroMagic plugin;
    protected HashMap<String,Long> lastcast;

    public HeroMagicPlayerListener(HeroMagic instance) 
    {
        plugin = instance;
        lastcast = new HashMap<String,Long>();
    }
    
    public boolean onPlayerCommand(CommandSender  sender,  
  		  Command  command,  
		  String  label,  
		  String[]  args) 
    {
 	  
 	   
 		if (command.getName().toString().equalsIgnoreCase("cast")) 
 		{
 			
 			if(args.length >0 && args[0].equalsIgnoreCase("blink"))
 			{
 				if(castBlink(sender,command,args))
 				{
 					//spell worked if here
 				}
 			}
 			if(args.length >0 && args[0].equalsIgnoreCase("cost"))
 			{
 				if(castCost(sender,command,args))
 				{
 					//spell worked if here :D
 				}
 			}
 			
 			if(args.length >0 && args[0].equalsIgnoreCase("mark"))
 			{
 				setPlayerMark( ((Player) sender), ((Player) sender).getLocation());
 			}
 			
 			if(args.length >0 && args[0].equalsIgnoreCase("recall"))
 			{
 				return castRecall((Player) sender);
 			}
 			if(args.length >0 && args[0].equalsIgnoreCase("gate"))
 			{
 				if(canCastSpell((Player) sender,"Gate"))
 				{
 					if(isOnCooldown((Player) sender,"Gate",getSpellCooldown("Gate")))
 					{
 						sender.sendMessage("Spell Is On Cooldown");
 					} else if (!removeRegents((Player) sender,getSpellCost("Gate"))) {
 		    			sender.sendMessage("You Do Not Have The Regeants To Cast Gate");
 		    			return false;
 		    		} else {
 		    			Location loc = ((Player) sender).getWorld().getSpawnLocation();
 		    			((Player) sender).teleport(loc);
 		    			startCooldown((Player) sender,"Gate",getSpellCooldown("Gate"));
 		    			
 		    			return true;
 		    		}
 				}
 			}
 			return true;
 		}
 		return false;
    }
 	
    public boolean castRecall(Player player)
    {
    	if(canCastSpell(player,"Recall"))
    	{
    		
    		if (isOnCooldown(player,"Recall",getSpellCooldown("Recall"))) {
    			player.sendMessage("Spell Is On Cooldown");
    			return false;
    		} else if (!removeRegents(player,getSpellCost("Recall"))) {
    			player.sendMessage("You Do Not Have The Regeants To Cast Recall");
    			return false;
    		} else {
    			Location loc = getPlayerMark(player);
    			player.teleport(loc);
    			startCooldown(player,"Recall",getSpellCooldown("Recall"));
    			
    			return true;
    		}
    	}
    	return false;
    }
    
    
    private boolean castCost(CommandSender sender, Command command,
			String[] args) {
    	Player player = (Player) sender;
    //	player.chat("You have checked a spell cost :D");
    	
    	if(/*canCastSpell(player,"Cost") &&*/ args.length >= 2)
    	{
    		int[] rh = getSpellCost(args[1]);
    		if(rh != null)
    		{
    			player.sendMessage("The Spell " + args[1] + " Costs " + rh[1] + " " + rh[0] +
    				" And " + rh[3] + " " + rh[2]);
    		} else {
    			player.sendMessage("You should NOT get this message :O");
    		}
    		
    	}
		
		return true;
	}

	public void onPlayerInteract(PlayerInteractEvent event)
    {
    	event.getAction();
		if (event.getAction().equals(Action.LEFT_CLICK_AIR))
    	{
    		//event.getPlayer().sendMessage("Left Clicked in Air :D");
    	}
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			//event.getPlayer().sendMessage("Right Clicked Block :D");
			if(event.getClickedBlock().getTypeId() == 47)
			{
				//event.getPlayer().sendMessage("Clicked ona book shelf");
				String spellname = getSpellLocations().get(event.getClickedBlock().getLocation());
				if(spellname != null)
				{
					event.getPlayer().sendMessage("This is the " + spellname + " Location");
					addSpell(event.getPlayer(),spellname);
				}
			}
		}
    	return;
    }
    public void onPlayerAnimation(PlayerAnimationEvent event)
    {
    	event.getAnimationType();
		if(event.getAnimationType().equals(PlayerAnimationType.ARM_SWING))
    	{
			//event.getPlayer().sendMessage("arm swung");
    	}
		
		
    }
    
    
    
    
    @SuppressWarnings("unused")
	public boolean castBlink(CommandSender sender,Command command,String[] args)
    {
    	Player player = (Player) sender;
    	//player.chat("You have blinked :D");
    	
    	if(canCastSpell(player,"Blink"))
    	{
    		//old magic spells code
    		Block target = player.getTargetBlock(null, 20); //second int is max range
    		BlockFace face = target.getFace(player.getLocation().getBlock());
    		
    		if (target == null) {
    			player.sendMessage("Too Far To Blink");
    			return false;
    		} else if (getDistance(player,target) > 20) {
    			player.sendMessage("Too Far To Blink");
    			return false;
    		} else if (60 > 0 && isOnCooldown(player,"Blink",getSpellCooldown("Blink"))) {
    			player.sendMessage("Spell Is On Cooldown");
    			return false;
    		}  else if (player.getWorld().getBlockTypeIdAt(target.getX(),target.getY()+1,target.getZ()) == 0 && player.getWorld().getBlockTypeIdAt(target.getX(),target.getY()+2,target.getZ()) == 0) {
    			// teleport to top of target block if possible
    			if (!removeRegents(player,getSpellCost("Blink"))) {
        			player.sendMessage("You Do Not Have The Regeants To Cast Blink!");
        			return false;
        		}
    			player.sendMessage("You Cast Blink!");
    		//	sendMessageToPlayersInRange(player,STR_CAST_OTHERS.replace("[caster]",player.getName()));
    			player.teleport(new Location(player.getWorld(), target.getX()+.5, (double)target.getY()+1, target.getZ()+.5 ,player.getEyeLocation().getYaw(), player.getEyeLocation().getPitch()  ));
    			//if (COOLDOWN > 0) {
    				startCooldown(player,"Blink",getSpellCooldown("Blink"));
    			//}
    			return true;
    		} else if (target.getTypeId() == 0 && player.getWorld().getBlockTypeIdAt(face.getModX(),face.getModY()+1,face.getModZ()) == 0) {
    			// otherwise teleport to face of target block
    			if (!removeRegents(player,getSpellCost("Blink"))) {
        			player.sendMessage("You Do Not Have The Regeants To Cast Blink!");
        			return false;
        		}
    			player.sendMessage("You cast blink");
    			//sendMessageToPlayersInRange(player,STR_CAST_OTHERS.replace("[caster]",player.getName()));
    			player.teleport(new Location(player.getWorld(),face.getModX()+.5,(double)face.getModY(),face.getModZ()+.5,player.getEyeLocation().getYaw(), player.getEyeLocation().getPitch()));
    			//if (COOLDOWN > 0) {
    				startCooldown(player,"Blink",getSpellCooldown("Blink"));
    			//}
    			return true;
    		} else {
    			// no place to stand
    			player.sendMessage("There Is No Place To Stand At That Location!");
    			return false;
    		}
	
    		
    		
    	} 
    	return false;
    }
    
    
    private void startCooldown(Player player, String spellname, int cooldowntime)
    {
    	String str = player.getName()+spellname;
    	if(!lastcast.containsKey(str))
    	{
    		lastcast.put(str, System.currentTimeMillis());
    	}
    }
    private boolean isOnCooldown(Player player, String spellname, int cooldown) {
		if (lastcast == null) {
			return false;
		}/* else if (player.isInGroup(FREE_SPELL_RANK)) {
			return false;
		}*/ else if (!lastcast.containsKey(player.getName()+spellname)) {
			return false;
		} else if (System.currentTimeMillis() - lastcast.get(player.getName()+spellname) > cooldown*1000) {
			lastcast.remove(player.getName()+spellname);
			return false;
		} else {
			return true;
		}
	}
    
    private boolean removeRegents(Player player, int[] regents)
    {
    	
    	Inventory inv = player.getInventory();
    	ItemStack[] items = inv.getContents();
    	int counter=0;
    	int holdi =-1;
    	int holdj =-1;
    	
    	
    	for(int i =0; i < items.length;i++)
    	{
    		if(items[i].getTypeId() == regents[0] && items[i].getAmount() >= regents[1])
    		{
    			holdi = i;
    			counter++;
    			break;
    		}
    	}
    	if(counter == 0 && regents[1] == 0)
    	{
    		counter++;
    	}
    	for(int j =0; j< items.length;j++)
    	{
    		if(items[j].getTypeId() == regents[2] && items[j].getAmount() >= regents[3])
    		{
    			holdj = j;
    			counter++;
    			break;
    		}
    	}
    	if(counter == 1 && regents[3] == 0 && holdj == -1)
    	{
    		counter++;
    	}
    	if(counter >=2)
    	{
    		if(holdi != -1) items[holdi].setAmount(items[holdi].getAmount() - regents[1]);
    		if(holdj != -1) items[holdj].setAmount(items[holdj].getAmount() - regents[3]);
    		return true;
    	}
    	
    	
    	
		return false;
    }

 
    public boolean canCastSpell(Player player, String spellname)
    {
    	Property playerfile = new Property(player.getName(), plugin);
    	return playerfile.getBoolean(spellname);
    	
    }    
    public int[] getSpellCost(String spellname)
    {
    	int[] spellcost = {0,0,0,0};
    	Property sfile = new Property(spellname,plugin);
    	if((spellcost[0] = sfile.getInt("Regeant-1")) == 0)
    	{
    		sfile.setInt("Regeant-1", 331);
    	}
    	if((spellcost[1] = sfile.getInt("Regeant-1-amt")) == 0)
    	{
    		sfile.setInt("Regeant-1-amt", 5);
    	}
    	if((spellcost[2] = sfile.getInt("Regeant-2")) == 0)
    	{
    		sfile.setInt("Regeant-2", 0);
    	}
    	if((spellcost[3] = sfile.getInt("Regeant-2-amt")) == 0)
    	{
    		sfile.setInt("Regeant-2-amt", 0);
    	}
    	sfile.save();
    	return spellcost;
    }  
    public void addSpell(Player player, String spellname)
    {
    	Property playerfile = new Property(player.getName(),plugin);
    	playerfile.setBoolean(spellname, true);
    	playerfile.save();
    }
    public void removeSpell(Player player, String spellname)
    {
    	Property playerfile = new Property(player.getName(),plugin);
    	playerfile.setBoolean(spellname, false);
    	playerfile.save();
    }
    public int getSpellCooldown(String spellname)
    {
    	Property sprop = new Property(spellname, plugin);
    	int cooldown = sprop.getInt("CoolDown");
		if( cooldown  == 0)
    	{
    		sprop.setInt("CoolDown", 60);
    		sprop.save();
    	}
    	return cooldown;
    }
    public HashMap<Location,String> getSpellLocations()
    {
    	HashMap<Location,String> map = new HashMap<Location,String>();
    	/*
    	Location temploc = new Location(plugin.getServer().getWorld("world"), 100, 68, 90);
    	HashMap<Location,String> map = new HashMap<Location,String>();
    	map.put(temploc, "Blink");
    	return map;*/
    	Property blink = new Property("Blink", plugin);
    	Property mark = new Property("Mark", plugin);
    	Property recall = new Property("Recall", plugin);
    	Property gate = new Property("Gate", plugin);
    	
    	Location blinkloc = new Location(plugin.getServer().getWorld("world"), 0, 0, 0);
    	double x,y,z;
    	x = blink.getDouble("Blink-X-Loc");
    	if(x == 0.0D)
    	{
    		blink.setDouble("Blink-X-Loc", 0.0);
    	}
    	blinkloc.setX(x);
    	
    	y = blink.getDouble("Blink-Y-Loc");
    	if(y == 0.0D)
    	{
    		blink.setDouble("Blink-Y-Loc", 0.0);
    	}
    	blinkloc.setY(y);
    	
    	z = blink.getDouble("Blink-Z-Loc");
    	if(z == 0.0D)
    	{
    		blink.setDouble("Blink-Z-Loc", 0.0);
    	}
    	blinkloc.setZ(z);
    	
    	blink.save();
    	
    	Location markloc = new Location(plugin.getServer().getWorld("world"), 0, 0, 0);
       	x = mark.getDouble("mark-X-Loc");
       	if(x == 0.0D)
       	{
       		mark.setDouble("mark-X-Loc", 0.0);
       	}
       	markloc.setX(x);
       	
       	y = mark.getDouble("mark-Y-Loc");
       	if(y == 0.0D)
       	{
       		mark.setDouble("mark-Y-Loc", 0.0);
       	}
       	markloc.setY(y);
       	
       	z = mark.getDouble("mark-Z-Loc");
       	if(z == 0.0D)
       	{
       		mark.setDouble("mark-Z-Loc", 0.0);
       	}
       	markloc.setZ(z);
       	
       	mark.save();
    	
    	
       	Location recallloc = new Location(plugin.getServer().getWorld("world"), 0, 0, 0);
       	x = recall.getDouble("recall-X-Loc");
       	if(x == 0.0D)
       	{
       		recall.setDouble("recall-X-Loc", 0.0);
       	}
       	recallloc.setX(x);
       	
       	y = recall.getDouble("recall-Y-Loc");
       	if(y == 0.0D)
       	{
       		recall.setDouble("recall-Y-Loc", 0.0);
       	}
       	recallloc.setY(y);
       	
       	z = recall.getDouble("recall-Z-Loc");
       	if(z == 0.0D)
       	{
       		recall.setDouble("recall-Z-Loc", 0.0);
       	}
       	recallloc.setZ(z);
       	
       	recall.save();
    	
       	
       	
        Location gateloc = new Location(plugin.getServer().getWorld("world"), 0, 0, 0);
       	x = gate.getDouble("gate-X-Loc");
       	if(x == 0.0D)
       	{
       		gate.setDouble("gate-X-Loc", 0.0);
       	}
       	gateloc.setX(x);
       	
       	y = gate.getDouble("gate-Y-Loc");
       	if(y == 0.0D)
       	{
       		gate.setDouble("gate-Y-Loc", 0.0);
       	}
       	gateloc.setY(y);
       	
       	z = gate.getDouble("gate-Z-Loc");
       	if(z == 0.0D)
       	{
       		gate.setDouble("gate-Z-Loc", 0.0);
       	}
       	gateloc.setZ(z);
       	
       	gate.save();
    	
    	
    	
    	map.put(blinkloc, "Blink");
    	map.put(markloc, "Mark");
    	map.put(recallloc, "Recall");
    	map.put(gateloc, "Gate");
    	return map;
    	
    	
    }
    public void setPlayerMark(Player player,Location loc)
    {
    	Property pprop = new Property(player.getName(), plugin);
    	double x,y,z;
    	x = loc.getX();
    	y = loc.getY();
    	z = loc.getZ();
    	pprop.setDouble("Mark-X", x);
    	pprop.setDouble("Mark-Y", y);
    	pprop.setDouble("Mark-Z", z);
    	pprop.save();
    }
    public Location getPlayerMark(Player player)
    {
    	Property pprop = new Property(player.getName(), plugin);
    	
    	double x,y,z;
    	x = pprop.getDouble("Mark-X");
    	y = pprop.getDouble("Mark-Y");
    	z = pprop.getDouble("Mark-Z");
    	
    	Location loc = new Location(plugin.getServer().getWorld("world"),x,y,z);
    	return loc;
    }
    
	public double getDistance(Player pl, Block tg) {
		Location player = pl.getLocation();
		Location target = tg.getLocation();
		return Math.sqrt(Math.pow(player.getX()-target.getX(),2) + Math.pow(player.getY()-target.getY(),2) + Math.pow(player.getZ()-target.getZ(),2));
	}

}

