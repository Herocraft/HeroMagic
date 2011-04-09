package com.herocraftonline.heromagic;

import org.bukkit.Location;

public class Spell {
	private String name;
	private int coolDown;
	private Location location;
	private int reagent1, reagent2;
	private int reagent1_amount, reagent2_amount;
	private String reagent1_name, reagent2_name;
	
	Spell(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getCoolDown() {
		return coolDown;
	}

	public void setCoolDown(int coolDown) {
		this.coolDown = coolDown;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location loc) {
		this.location = loc;
	}

	public int getReagent1() {
		return reagent1;
	}

	public void setReagent1(int reagent1) {
		this.reagent1 = reagent1;
	}

	public int getReagent2() {
		return reagent2;
	}

	public void setReagent2(int reagent2) {
		this.reagent2 = reagent2;
	}

	public int getReagent1_amount() {
		return reagent1_amount;
	}

	public void setReagent1_amount(int reagent1_amount) {
		this.reagent1_amount = reagent1_amount;
	}

	public int getReagent2_amount() {
		return reagent2_amount;
	}

	public void setReagent2_amount(int reagent2_amount) {
		this.reagent2_amount = reagent2_amount;
	}

	public String getReagent1_name() {
		return reagent1_name;
	}

	public void setReagent1_name(String reagent1_name) {
		this.reagent1_name = reagent1_name;
	}

	public String getReagent2_name() {
		return reagent2_name;
	}

	public void setReagent2_name(String reagent2_name) {
		this.reagent2_name = reagent2_name;
	}
}
