package com.realcraft.residences;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.realcraft.RealCraft;

public class CheckResidences {
	RealCraft plugin;
	
	boolean enabled = false;
	long expireDays;
	
	public CheckResidences(RealCraft realcraft){
		plugin = realcraft;
		if(plugin.config.getBoolean("residences.enabled")){
			enabled = true;
			expireDays = plugin.config.getInt("residences.expireDays");
			plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,new Runnable(){
				@Override
				public void run(){
					checkResidences();
				}
			},60*20,3600*20);
		}
	}
	
	public void onReload(){
		enabled = false;
		if(plugin.config.getBoolean("residences.enabled")){
			enabled = true;
			expireDays = plugin.config.getInt("residences.expireDays");
		}
	}
	
	public void checkResidences(){
		if(!enabled) return;
		String [] residences = Residence.getResidenceManager().getResidenceList();
		for(String res : residences){
			ClaimedResidence residence = Residence.getResidenceManager().getByName(res);
			if(residence != null){
				String owner = residence.getOwner();
				ResultSet rs = plugin.db.query("SELECT user_lastlogin FROM authme WHERE user_name = '"+owner+"'");
				try {
					if(rs.next()){
						long lastlogin = rs.getLong("user_lastlogin");
						if(lastlogin+(expireDays*86400*1000) < System.currentTimeMillis()){
							System.out.println("[RealCraft] Residence "+res+" ("+owner+") smazana!");
							residence.remove();
						}
					}
					rs.close();
				} catch (SQLException e){
				}
			}
		}
	}
}