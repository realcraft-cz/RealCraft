package com.parkour;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.realcraft.RealCraft;

public class ParkourOfflinePlayer {

	private int id = 0;
	private String name;
	private String skin;

	public ParkourOfflinePlayer(int id){
		this.id = id;
		if(RealCraft.getInstance().db.connected){
			ResultSet rs = RealCraft.getInstance().db.query("SELECT user_id,user_name,user_skin FROM authme WHERE user_id = '"+this.getId()+"'");
			try {
				if(rs.next()){
					this.id = rs.getInt("user_id");
					this.name = rs.getString("user_name");
					this.skin = rs.getString("user_skin");
				}
				rs.close();
			} catch (SQLException e){
				e.printStackTrace();
			}
		}
	}

	public int getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public String getSkin(){
		return skin;
	}

	public ItemStack getItemStack(){
		return this.getItemStack(false);
	}

	public ItemStack getItemStack(boolean author){
		ItemStack item = new ItemStack(Material.SKULL_ITEM,1,(byte)3);
		SkullMeta headMeta = (SkullMeta) item.getItemMeta();
		headMeta.setOwner((this.getSkin().isEmpty() ? "steve" : this.getSkin()));
		headMeta.setDisplayName("§r"+this.getName());
		ArrayList<String> lore = new ArrayList<String>();
		if(author) lore.add("§bAutor parkouru");
		else lore.add("§7Klikni pro odebrani hrace.");
		headMeta.setLore(lore);
		item.setItemMeta(headMeta);
		return item;
	}
}