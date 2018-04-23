package realcraft.bukkit.fights.duels;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.FightPlayer.FightPlayerState;
import realcraft.bukkit.fights.Fights;
import realcraft.bukkit.utils.ReflectionUtils;

public class FightDuelsSpectator implements Listener {

	public FightDuelsSpectator(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),PacketType.Play.Client.USE_ENTITY,PacketType.Play.Server.PLAYER_INFO){

			@Override
			public void onPacketReceiving(PacketEvent event){
				if(event.getPacketType() == PacketType.Play.Client.USE_ENTITY){
					FightPlayer fPlayer = Fights.getFightPlayer(event.getPlayer());
					if(fPlayer.getState() == FightPlayerState.SPECTATOR){
						event.setCancelled(true);
					}
				}
			}

			@Override
			public void onPacketSending(PacketEvent event){
				if(event.getPacketType() == PacketType.Play.Server.PLAYER_INFO){
					try {
						UUID uuid = event.getPacket().getPlayerInfoDataLists().read(0).get(0).getProfile().getUUID();
						Player player = Bukkit.getPlayer(uuid);
						if(player != null && player.isOnline() && event.getPlayer().getUniqueId() != uuid){
							FightPlayer fPlayer = Fights.getFightPlayer(player);
							if(fPlayer != null && fPlayer.getState() == FightPlayerState.SPECTATOR && !fPlayer.isLeaving()){
								PacketPlayOutPlayerInfo packet = (PacketPlayOutPlayerInfo) event.getPacket().getHandle();
								PacketPlayOutPlayerInfo.EnumPlayerInfoAction action = (PacketPlayOutPlayerInfo.EnumPlayerInfoAction) ReflectionUtils.getField(packet.getClass(),true,"a").get(packet);
								if(action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER){
									event.setCancelled(true);
								}
							}
						}
					} catch (Exception e){
						e.printStackTrace();
					}
				}
			}
		});
	}

	@EventHandler(ignoreCancelled=false)
	public void PlayerInteractEvent(PlayerInteractEvent event){
		FightPlayer fPlayer = Fights.getFightPlayer(event.getPlayer());
		if(fPlayer.getState() == FightPlayerState.SPECTATOR){
			if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.SLIME_BALL && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
				//TODO: leave
				event.setCancelled(true);
			}
		}
	}
}