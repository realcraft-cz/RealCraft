package realcraft.bungee.skins;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import realcraft.bungee.users.Users;
import realcraft.share.skins.Skin;

public class SkinsListeners implements Listener {

	@EventHandler(priority=EventPriority.HIGHEST)
	public void PostLoginEvent(PostLoginEvent event){
		ProxiedPlayer player = event.getPlayer();
		ProxyServer.getInstance().getScheduler().runAsync(Skins.plugin,new Runnable(){
			@Override
			public void run(){
				if(player.isConnected()){
					Skin skin = Users.getUser(player).getSkin();
					Skins.setSkin(player,skin);
				}
			}
		});
	}
}