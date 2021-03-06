package com.herocraftonline.heromagic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;



public class HeroMagic extends JavaPlugin {
	private static final Logger logger = Logger.getLogger("Minecraft.HeroMagic");
	public CastManager castManager;
	public Spells spells;
	public HashMap< Entity, ArrayList<Block> > novaBlockList;
	public HashMap< Entity, ArrayList<Integer> > novaBlockId;
    
	@Override
    public void onEnable() {
    	HeroMagicPlayerListener playerListener = new HeroMagicPlayerListener(this);
    	HeroMagicBlockListener blockListener = new HeroMagicBlockListener(this);
    	
		PluginManager pm = getServer().getPluginManager();
	    pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
	    pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Low, this);
	    
	    spells = new Spells(this);
	    castManager = new CastManager(this);
	    
	    novaBlockList = new HashMap<Entity, ArrayList<Block>>();
	    novaBlockId = new HashMap<Entity, ArrayList<Integer>>();
	    
	    setupDatabase();
	    
		logger.info(this.getDescription().getFullName() + " enabled.");
    }
    
	@Override
    public void onDisable() {
    	logger.info(this.getDescription().getFullName() + " disabled.");
    }
	
	@Override
    public boolean onCommand(CommandSender sender, Command  command, String label, String[]  args) {
		String commandName = command.getName().toLowerCase();
		
		if (!anonymousCheck(sender)) {
			Player player = (Player)sender;
			
			if (commandName.equals("cast")) {
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("spellbook"))
					{
						return setSpellBook(player, args);
					}
					else if (args[0].equalsIgnoreCase("cost"))
					{
						return showCost(player, args);
					}
					else if (args[0].equalsIgnoreCase("mark"))
					{
						return castMark(player);
					}
					else if (args[0].equalsIgnoreCase("blink"))
					{
						return castBlink(player);
					}
					else if (args[0].equalsIgnoreCase("recall"))
					{
						return castRecall(player);
					}
					else if (args[0].equalsIgnoreCase("gate"))
					{
						return castGate(player, args);
		 			}
					else if (args[0].equalsIgnoreCase("heal"))
					{
						return castHeal(player);
					}
					else if (args[0].equalsIgnoreCase("food"))
					{
						return castSummonFood(player);
					}
					else if (args[0].equalsIgnoreCase("glassnova"))
					{
						return castGlassNova(player);
					}
				}
				
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "Your magical words have no effect. Perhaps you need to pronounce them better...");
			}
			return true;
		}
		return false;
    }
	


	private void setupDatabase() {
		try {
			getDatabase().find(PlayerMark.class).findRowCount();
			getDatabase().find(PlayerSpell.class).findRowCount();
		} catch (PersistenceException ex) {
			System.out.println("Installing database for " + getDescription().getName() + " due to first time usage");
			installDDL();
		}
	}
	
    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(PlayerMark.class);
        list.add(PlayerSpell.class);
        return list;
    }
	
	/**
	 * Checks if the sender is a player.
	 * @param sender
	 * @return
	 */
	public static boolean anonymousCheck(CommandSender sender) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Cannot execute that command, I don't know who you are!");
			return true;
    	} else {
    		return false;
    	}
	}
	
	/**
	 * Sets a position where a spell can be learned.
	 * @param player
	 * @param args
	 * @return
	 */
    private boolean setSpellBook(Player player, String[] args) {
    	if(!player.isOp()) return true;
    	
    	if (args.length >= 2) {
	    	Block book = player.getTargetBlock(null, 20);
	    	
	    	if(book.getTypeId() == 47) {
	        	Spell spell = spells.getSpellByName(args[1]);
	        	
	        	if (spell != null) {
	        		spell.setLocation(book.getLocation());
	        		spells.save();
	        		player.sendMessage(ChatColor.GREEN + "SpellBook set.");
	        	} else {
	        		player.sendMessage(ChatColor.RED + "The specified spell doesnt exist.");
	        	}
	    	} else {
	    		player.sendMessage(ChatColor.RED + "You dont look at a bookshelf.");
	    	}
    	} else {
    		player.sendMessage(ChatColor.RED + "You have to specify a spell.");
    	}
    	return true;
    }
	
    /**
     * Sends the cost of a spell to the player.
     * @param player
     * @param args
     * @return
     */
	private boolean showCost(Player player, String[] args) {
    	if(args.length >= 2) {
    		Spell spell = spells.getSpellByName(args[1]);
    		
    		if (spell != null) {
	    		player.sendMessage(ChatColor.BLUE + "The spell " + spell.getName() + " costs " + spell.getReagent1_amount() + " of " + spell.getReagent1_name());
	    		if (spell.getReagent2() != 0) {
	    			player.sendMessage(ChatColor.BLUE + "and costs " + spell.getReagent1_amount() + " of " + spell.getReagent1_name());
	    		}
    		} else {
    			player.sendMessage(ChatColor.RED + "The specified spell doesnt exist.");
    		}
    	} else {
    		player.sendMessage(ChatColor.RED + "You have to specify a spell.");
    	}
    	return true;
	}
	
	//TODO Add new spells here!
	/**
	 * #################################################
	 * ############### SPELLS DOWN HERE! ###############
	 * #################################################
	 */
    
    public boolean castRecall(Player player) {
    	if(castManager.canCastSpell(player,"Recall")) {
    		if (!castManager.isOnCooldown(player,"Recall")) {
    			if (castManager.removeRegents(player, "Recall")) {
	    			Location loc = castManager.getPlayerMark(player);
	    			if (loc.getX() == 0.0 && loc.getY() == 0.0 && loc.getZ() == 0.0) {
	    				player.sendMessage(ChatColor.RED + "You must first mark a location before you can Recall!");
	    				return true;
	    			}
	    			player.teleport(loc);
	    			castManager.startCooldown(player,"Recall");
	    			player.sendMessage(ChatColor.BLUE + "You tear a hole in the fabric of space and time...");
	    		} else {
	    			player.sendMessage(ChatColor.RED + "You do not have the reagants to cast Recall");
	    		}
    		} else {
    			player.sendMessage(ChatColor.LIGHT_PURPLE + "This spell Recall is on cooldown for " + castManager.getCoolDownRemaining(player, "Recall") + " more minutes");
    		}
    	} else {
    		player.sendMessage(ChatColor.RED + "You need to learn this spell first.");
    	}
    	return true;
    }
    public boolean castHeal(Player player) {
    	if(castManager.canCastSpell(player,"Heal")) {
    		if (!castManager.isOnCooldown(player,"Heal")) {
    			if (castManager.removeRegents(player, "Heal")) {
	    			
    				player.setHealth(player.getHealth()+2);
    				
	    			castManager.startCooldown(player,"Heal");
	    			player.sendMessage(ChatColor.BLUE + "You heal some of your minor wounds...");
	    		} else {
	    			player.sendMessage(ChatColor.RED + "You do not have the reagants to cast Heal");
	    		}
    		} else {
    			player.sendMessage(ChatColor.LIGHT_PURPLE + "This spell Heal is on cooldown for  " + castManager.getCoolDownRemaining(player, "Heal") + " more minutes");
    		}
    	} else {
    		player.sendMessage(ChatColor.RED + "You need to learn this spell first.");
    	}
    	return true;
    }
    public boolean castSummonFood(Player player)
    {
    	if(castManager.canCastSpell(player,"Food")) {
    		if (!castManager.isOnCooldown(player,"Food")) {
    			if (castManager.removeRegents(player, "Food")) {
	    			
    				PlayerInventory inv = player.getInventory();
    				if(inv.contains(357))
    				{
    					ItemStack[] itm = inv.getContents();
    					
    					ItemStack newitms = new ItemStack(357,itm[inv.first(357)].getAmount()+8);
    					
    					inv.remove(itm[inv.first(357)]);
    					inv.addItem(newitms);
    					
    				}/* else if(inv.contains(281))
    				{
    					ItemStack itm= new ItemStack(281,1);
    					inv.remove(itm);
    					inv.addItem(new ItemStack(282));
    				}*/ else {
    					inv.addItem(new ItemStack(357,8));
    				}
    				
    				
    				castManager.startCooldown(player,"Food");
    				
    				
	    			player.sendMessage(ChatColor.BLUE + "You magically Summon food into existance");
	    		} else {
	    			player.sendMessage(ChatColor.RED + "You do not have the reagants to cast Summon Food");
	    		}
    		} else {
    			player.sendMessage(ChatColor.LIGHT_PURPLE + "This spell Summon Food is on cooldown for " + castManager.getCoolDownRemaining(player, "Food") + " more minutes");
    		}
    	} else {
    		player.sendMessage(ChatColor.RED + "You need to learn this spell first.");
    	}
    	return true;
    }
	public boolean castBlink(Player player) {
    	if(castManager.canCastSpell(player,"Blink")) {
    		Block target = player.getTargetBlock(null, 20);
    		BlockFace face = target.getFace(player.getLocation().getBlock());
    		
    		if (target != null && castManager.getDistance(player, target) <= 20) {
	    		if (!castManager.isOnCooldown(player,"Blink")) {
	    			if (castManager.removeRegents(player, "Blink")) {
			    		if (player.getWorld().getBlockTypeIdAt(target.getX(),target.getY()+1,target.getZ()) == 0 && player.getWorld().getBlockTypeIdAt(target.getX(),target.getY()+2,target.getZ()) == 0) {
			    			player.sendMessage(ChatColor.BLUE + "You Cast Blink!");
			    			player.teleport(new Location(player.getWorld(), target.getX()+.5, (double)target.getY()+1, target.getZ()+.5 ,player.getEyeLocation().getYaw(), player.getEyeLocation().getPitch()  ));
			    			castManager.startCooldown(player,"Blink");
			    			return true;
			    		} else if (target.getTypeId() == 0 && player.getWorld().getBlockTypeIdAt(face.getModX(),face.getModY()+1,face.getModZ()) == 0) {
			    			player.sendMessage(ChatColor.BLUE +"You cast blink");
			    			player.teleport(new Location(player.getWorld(),face.getModX()+.5,face.getModY(),face.getModZ()+.5,player.getEyeLocation().getYaw(), player.getEyeLocation().getPitch()));
			    			castManager.startCooldown(player,"Blink");
			    			return true;
			    		} else {
			    			player.sendMessage(ChatColor.LIGHT_PURPLE + "There is no place to stand at that location!");
			    		}
	    			} else {
	    				player.sendMessage(ChatColor.RED + "You do not have the reagants to cast Blink!");
	    			}	
	    		} else {
	    			player.sendMessage(ChatColor.LIGHT_PURPLE + "This spell Blink is on cooldown for " + castManager.getCoolDownRemaining(player, "Blink") + " more minutes");
	    		}
	    	} else {
	    		player.sendMessage(ChatColor.LIGHT_PURPLE +"Your target is to far!");
	    	}
    	} else {
    		player.sendMessage(ChatColor.RED + "You need to learn this spell first.");
    	}
    	return true;
    }
	
	public boolean castGate(Player player, String[] args) {
		if(castManager.canCastSpell(player, "Gate")) {
			if(!castManager.isOnCooldown(player, "Gate")) {
				if (castManager.removeRegents(player, "Gate")) {
	    			Location loc = player.getWorld().getSpawnLocation();
	    			player.teleport(loc);
	    			castManager.startCooldown(player, "Gate");
	    			player.sendMessage(ChatColor.BLUE + "You focus your magic to return yourself to the Origin...");
	    		} else {
	    			player.sendMessage(ChatColor.RED +"You do not have the reagants to cast gate!");
	    		}
    		} else {
    			player.sendMessage(ChatColor.LIGHT_PURPLE + "This spell Gate is on cooldown for " + castManager.getCoolDownRemaining(player, "Gate") + " more minutes");
    		}
		} else {
			player.sendMessage(ChatColor.RED + "You need to learn this spell first.");
		}
		return true;
	}
	
	public boolean castGlassNova(Player player)
	{
		if(castManager.canCastSpell(player, "GlassNova"))
		{
			if(player.isOp() || castManager.isOnCooldown(player, "GlassNova"))
			{
				if(castManager.removeRegents(player, "GlassNova"))
				{
					List<Entity> entities = player.getNearbyEntities(20.0, 20.0, 20.0);
					for(Entity ent : entities)
					{
						//if(ent instanceof CraftPlayer)
							if(player.getEntityId() != ent.getEntityId())
							{
								if(novaBlockId.containsKey(ent) || novaBlockList.containsKey(ent))
								{
									player.sendMessage(ChatColor.RED +"You already have an active Nova!");
									return true;
								}
								World world =  ent.getWorld();
								int x = (int) ent.getLocation().getX();
								int y = (int) ent.getLocation().getY();
								int z = (int) ent.getLocation().getZ();
								
								Location loc = new Location( world, x+.5, y, z+.5, ent.getLocation().getYaw(),ent.getLocation().getPitch());
								
								ArrayList<Block> glassBlocks = new ArrayList<Block>();
								ArrayList<Integer> BlockIds = new ArrayList<Integer>();
								
								ent.teleport(loc);
								
								Location blockloc = new Location(world,x,y,z-1);
								if(blockloc.getBlock().getTypeId() != -1)
								{
									glassBlocks.add(blockloc.getBlock());
									BlockIds.add(blockloc.getBlock().getTypeId());
									blockloc.getBlock().setTypeId(20);
								}
								
								blockloc = new Location(world,x,y,z+1);
								if(blockloc.getBlock().getTypeId() != -1)
								{
									glassBlocks.add(blockloc.getBlock());
									BlockIds.add(blockloc.getBlock().getTypeId());
									blockloc.getBlock().setTypeId(20);
								}
								
								blockloc = new Location(world,x-1,y,z);
								if(blockloc.getBlock().getTypeId() != -1)
								{
									glassBlocks.add(blockloc.getBlock());
									BlockIds.add(blockloc.getBlock().getTypeId());
									blockloc.getBlock().setTypeId(20);
								}
								
								blockloc = new Location(world,x+1,y,z);
								if(blockloc.getBlock().getTypeId() != -1)
								{
									glassBlocks.add(blockloc.getBlock());
									BlockIds.add(blockloc.getBlock().getTypeId());
									blockloc.getBlock().setTypeId(20);
								}
								
								blockloc = new Location(world,x,y+1,z-1);
								if(blockloc.getBlock().getTypeId() != -1)
								{
									glassBlocks.add(blockloc.getBlock());
									BlockIds.add(blockloc.getBlock().getTypeId());
									blockloc.getBlock().setTypeId(20);
								}
								blockloc = new Location(world,x,y+1,z+1);
								if(blockloc.getBlock().getTypeId() != -1)
								{
									glassBlocks.add(blockloc.getBlock());
									BlockIds.add(blockloc.getBlock().getTypeId());
									blockloc.getBlock().setTypeId(20);
								}
								blockloc = new Location(world,x-1,y+1,z);
								if(blockloc.getBlock().getTypeId() != -1)
								{
									glassBlocks.add(blockloc.getBlock());
									BlockIds.add(blockloc.getBlock().getTypeId());
									blockloc.getBlock().setTypeId(20);
								}
								blockloc = new Location(world,x+1,y+1,z);
								if(blockloc.getBlock().getTypeId() != -1)
								{
									glassBlocks.add(blockloc.getBlock());
									BlockIds.add(blockloc.getBlock().getTypeId());
									blockloc.getBlock().setTypeId(20);
								}
								novaBlockList.put(ent, glassBlocks);
								novaBlockId.put(ent, BlockIds);
								this.getServer().getScheduler().scheduleSyncDelayedTask(this,new PluginRunner(this,ent), 300L);
								
								
							}
					
					}
					player.sendMessage(ChatColor.BLUE + "You focus your magical powers to encase all nearby living things in Glass!");
				}else {
						player.sendMessage(ChatColor.RED +"You do not have the reagants to cast Glass Nova!");
					}
			} else {
				player.sendMessage(ChatColor.LIGHT_PURPLE + "This spell Glass Nova is on cooldown for " + castManager.getCoolDownRemaining(player, "GlassNova") + " more minutes");
			}
		} else {
			player.sendMessage(ChatColor.RED + "You need to learn this spell first.");
		}
		return true;
	}

	public void removeGlassNovaBlocks(Entity ent)
	{
		try{
			ArrayList<Block> rblocks = novaBlockList.remove(ent);
			ArrayList<Integer> iblocks = novaBlockId.remove(ent);
			for(int i =0; i < rblocks.size();i++)
			{
				rblocks.get(i).setTypeId(iblocks.get(i));
			}
		} catch(Exception e)
		{
			
		}
		return;	
	}
	
	private boolean castMark(Player player) {
		if(castManager.canCastSpell(player, "Mark")) {
			if(!castManager.isOnCooldown(player, "Mark")) {
				if (castManager.removeRegents(player, "Mark")) {
					castManager.setPlayerMark(player);
					player.sendMessage(ChatColor.BLUE + "You have marked a location for further use...");
	    		} else {
	    			player.sendMessage(ChatColor.RED + "You do not have the reagants to cast Mark!");
	    		}
    		} else {
    			player.sendMessage(ChatColor.LIGHT_PURPLE + "This spell Mark is on cooldown for " + castManager.getCoolDownRemaining(player, "Mark") + " more minutes");
    		}
		} else {
			player.sendMessage(ChatColor.RED + "You need to learn this spell first.");
		}
		return true;
	}
}

