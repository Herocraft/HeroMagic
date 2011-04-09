package com.herocraftonline.heromagic;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name="hc_playermarks")
public class PlayerMark {
	@Id
	private int id;
    @NotNull
	private String player;
    @NotNull
	private String world;
    @NotNull
	private double x, y, z;
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getPlayer() {
		return this.player;
	}
	
	public void setPlayer(String player) {
		this.player = player;
	}
	
	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
}
