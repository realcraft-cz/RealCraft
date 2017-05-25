package com.realcraft.test;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import com.realcraft.RealCraft;
import com.realcraft.utils.DateUtil;

public class KickTest implements Listener {

	public KickTest(){
		RealCraft.getInstance().getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event){
		event.disallow(Result.KICK_OTHER,
			"§r§c\u2716 §fByl jsi zabanovan §c\u2716\n"+
			"§r\n"+
			"§r§7Duvod: §fcheaty\n"+
			"§r§7Ban vyprsi §f"+DateUtil.lastTime(1495580774,true)+"§7\n"+
			"§r\n"+
			"§r§7Zruseni banu muzes ziskat na §6www.realcraft.cz"
		);
	}
}