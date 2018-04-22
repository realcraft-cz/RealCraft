package realcraft.bungee.motd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import realcraft.bungee.RealCraftBungee;

public class ServerMotd implements Listener, Runnable {

	public static RealCraftBungee plugin;
	private String title = "RealCraft.cz";
	private String description = "";

	public ServerMotd(RealCraftBungee realcraft){
		plugin = realcraft;
		plugin.getProxy().getPluginManager().registerListener(plugin,this);
		plugin.getProxy().getScheduler().schedule(plugin,this,1,60,TimeUnit.SECONDS);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
    public void ProxyPingEvent(ProxyPingEvent event){
        event.getResponse().setDescription(title+"\n"+description);
    }

	@Override
	public void run(){
		if(plugin.db.connected){
			plugin.getProxy().getScheduler().runAsync(plugin,new Runnable(){
				@Override
				public void run(){
					try {
						ResultSet rs = plugin.db.query("SELECT server_meta FROM servers_meta WHERE server_name = 'bungee_title'");
						if(rs.next()){
							title = RealCraftBungee.parseColors(rs.getString("server_meta"));
						}
						rs.close();
					} catch (SQLException e){
						e.printStackTrace();
					}
					try {
						ResultSet rs = plugin.db.query("SELECT server_meta FROM servers_meta WHERE server_name = 'bungee_motd'");
						if(rs.next()){
							description = RealCraftBungee.parseColors(rs.getString("server_meta"));
						}
						rs.close();
					} catch (SQLException e){
						e.printStackTrace();
					}
					if(plugin.testServer){
						title = RealCraftBungee.parseColors("&7&lRealCraft.cz&r &7&l|&r &7verze &l1.11");
						description = RealCraftBungee.parseColors("&cTestovaci server");
					}
				}
			});
		}
	}
}