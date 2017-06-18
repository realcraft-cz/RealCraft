package com.realcraft.test;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.realcraft.RealCraft;
import com.realcraft.nicks.NickManager;

public class NameTest implements Listener {

	public NameTest(){
		RealCraft.getInstance().getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){

		String command = event.getMessage().substring(1).toLowerCase();
		if(command.equalsIgnoreCase("nametest")){
			event.setCancelled(true);
			/*PacketContainer tabList = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO, true);
		    StructureModifier<List<PlayerInfoData>> infoData = tabList.getPlayerInfoDataLists();
		    StructureModifier<PlayerInfoAction> infoAction = tabList.getPlayerInfoAction();
		    List<PlayerInfoData> playerInfo = infoData.read(0);
		    for(Player player : Bukkit.getOnlinePlayers()) {
		        playerInfo.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(player).withName("test"), 0, NativeGameMode.fromBukkit(player.getGameMode()), WrappedChatComponent.fromText("test")));
		    }
		    infoData.write(0, playerInfo);
		    infoAction.write(0, PlayerInfoAction.UPDATE_DISPLAY_NAME);
		    ProtocolLibrary.getProtocolManager().broadcastServerPacket(tabList);*/

			if(NickManager.getPlayerNick(event.getPlayer()).isEnabled()){
				NickManager.clearPlayerNick(event.getPlayer());
			}
			else {
				NickManager.setPlayerPrefix(event.getPlayer(),"§b§kABCDEFGHI§r ");
				NickManager.setPlayerSuffix(event.getPlayer()," §b§kABCDEFGHI");
				event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}

			/*Player victim = event.getPlayer();

			PacketContainer teamPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_TEAM,true);
			teamPacket.getStrings().write(0,victim.getName());
			teamPacket.getStrings().write(2,"prefix");
			teamPacket.getStrings().write(3,"suffix");
			Set<String> entries = new HashSet<String>(Arrays.asList(victim.getName()));
			teamPacket.getSpecificModifier(Collection.class).write(0,entries);
			teamPacket.getIntegers().write(1,0);

			for(Player p : Bukkit.getOnlinePlayers()){
				try {
					ProtocolLibrary.getProtocolManager().sendServerPacket(p,teamPacket);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}*/

			/*ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),ListenerPriority.HIGH,PacketType.Play.Server.PLAYER_INFO){
				@Override
				public void onPacketSending(PacketEvent event){
					if(event.getPacketType() == PacketType.Play.Server.PLAYER_INFO){
						if(event.getPacket().getPlayerInfoAction().read(0) == EnumWrappers.PlayerInfoAction.ADD_PLAYER){
							PlayerInfoData pid = event.getPacket().getPlayerInfoDataLists().read(0).get(0);
							if(pid.getProfile().getName().equalsIgnoreCase(victim.getName())){
								PlayerInfoData newPid = new PlayerInfoData(pid.getProfile().withName("§b§lVIP §rKshFi3"),pid.getPing(),pid.getGameMode(),WrappedChatComponent.fromText(victim.getPlayerListName()));
								event.getPacket().getPlayerInfoDataLists().write(0, Collections.singletonList(newPid));
							}
						}
					}
				}
			});

			for(Player p : Bukkit.getOnlinePlayers()){
			    if(p.equals(victim)) continue;
			    p.showPlayer(victim);
			}*/
		}
	}
}