package realcraft.bukkit.nicks;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import realcraft.bukkit.RealCraft;

public class NickManager implements Listener {

	private static HashMap<Player,PlayerNick> nicks = new HashMap<Player,PlayerNick>();

	public NickManager(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),ListenerPriority.HIGH,PacketType.Play.Server.PLAYER_INFO){
			@Override
			public void onPacketSending(PacketEvent event){
				if(event.getPacketType() == PacketType.Play.Server.PLAYER_INFO){
					Player player = Bukkit.getServer().getPlayer(event.getPacket().getPlayerInfoDataLists().read(0).get(0).getProfile().getName());
					if(player != null && player.isOnline() && player.getEntityId() != event.getPlayer().getEntityId()){
						if(NickManager.getPlayerNick(player).isEnabled()){
							NickManager.getPlayerNick(player).updateForPlayer(event.getPlayer());
						}
					}
				}
			}
		});
	}

	public static PlayerNick getPlayerNick(Player player){
		if(!nicks.containsKey(player)) nicks.put(player,new PlayerNick(player));
		return nicks.get(player);
	}

	public static void setPlayerPrefix(Player player,String prefix){
		NickManager.getPlayerNick(player).setPrefix(prefix);
	}

	public static void setPlayerSuffix(Player player,String suffix){
		NickManager.getPlayerNick(player).setSuffix(suffix);
	}

	public static void clearPlayerNick(Player player){
		NickManager.getPlayerNick(player).remove();
		nicks.remove(player);
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		for(PlayerNick nick : nicks.values()){
			if(nick.isEnabled()){
				nick.updateForPlayer(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event){
		NickManager.getPlayerNick(event.getPlayer()).remove();
		nicks.remove(event.getPlayer());
	}

	public static class PlayerNick {

		private boolean enabled = false;
		private PacketContainer createPacket;
		private PacketContainer removePacket;

		public PlayerNick(Player player){
			createPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_TEAM,true);
			createPacket.getStrings().write(0,player.getName());
			Set<String> entries = new HashSet<String>(Arrays.asList(player.getName()));
			createPacket.getSpecificModifier(Collection.class).write(0,entries);
			createPacket.getIntegers().write(1,0);

			removePacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_TEAM,true);
			removePacket.getStrings().write(0,player.getName());
			removePacket.getIntegers().write(1,1);
		}

		public void setPrefix(String prefix){
			createPacket.getStrings().write(2,prefix);
			this.update();
		}

		public void setSuffix(String suffix){
			createPacket.getStrings().write(3,suffix);
			this.update();
		}

		public boolean isEnabled(){
			return enabled;
		}

		public void update(){
			for(Player target : Bukkit.getOnlinePlayers()){
				try {
					ProtocolLibrary.getProtocolManager().sendServerPacket(target,removePacket);
					ProtocolLibrary.getProtocolManager().sendServerPacket(target,createPacket);
				} catch (InvocationTargetException e){
					e.printStackTrace();
				}
			}
			this.enabled = true;
		}

		public void updateForPlayer(Player target){
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(target,removePacket);
				ProtocolLibrary.getProtocolManager().sendServerPacket(target,createPacket);
			} catch (InvocationTargetException e){
				e.printStackTrace();
			}
		}

		public void remove(){
			if(enabled){
				for(Player player2 : Bukkit.getOnlinePlayers()){
					try {
						ProtocolLibrary.getProtocolManager().sendServerPacket(player2,removePacket);
					} catch (InvocationTargetException e){
						e.printStackTrace();
					}
				}
				enabled = false;
			}
		}
	}
}