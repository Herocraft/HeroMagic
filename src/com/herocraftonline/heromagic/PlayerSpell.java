package com.herocraftonline.heromagic;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name="hc_playerspells")
public class PlayerSpell {
	@Id
	private int id;
    @NotNull
	private String player;
    @NotNull
    private String spell;
    @NotNull
    private boolean learned;
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getSpell() {
		return spell;
	}

	public void setSpell(String spell) {
		this.spell = spell;
	}

	public boolean isLearned() {
		return learned;
	}

	public void setLearned(boolean learned) {
		this.learned = learned;
	}
	
}
