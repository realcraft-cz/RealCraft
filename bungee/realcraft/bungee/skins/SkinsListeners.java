package realcraft.bungee.skins;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import realcraft.bungee.playermanazer.PlayerManazer;
import realcraft.bungee.skins.exceptions.SkinsLimitException;
import realcraft.bungee.skins.exceptions.SkinsNotFoundException;

public class SkinsListeners implements Listener {

	@EventHandler(priority=EventPriority.HIGHEST)
	public void PostLoginEvent(PostLoginEvent event){
		ProxiedPlayer player = event.getPlayer();
		if(!PlayerManazer.getPlayerInfo(player).getSkin().isEmpty()){
			ProxyServer.getInstance().getScheduler().runAsync(Skins.plugin,new Runnable(){
				@Override
				public void run(){
					if(player.isConnected()){
						Skin skin = Skins.getSkin(PlayerManazer.getPlayerInfo(player).getSkin());
						if(skin != null){
							Skins.setSkin(player,skin);
						}
					}
				}
			});
		} else {
			ProxyServer.getInstance().getScheduler().runAsync(Skins.plugin,new Runnable(){
				@Override
				public void run(){
					try {
						if(player.isConnected()) Skins.changeSkin(player,player.getName());
					} catch (SkinsLimitException e){
					} catch (SkinsNotFoundException e){
					}
				}
			});
		}
	}
}