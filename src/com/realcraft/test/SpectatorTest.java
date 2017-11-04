package com.realcraft.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.realcraft.RealCraft;
import com.realcraft.utils.ReflectionUtils;

import net.minecraft.server.v1_12_R1.EnumGamemode;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;

public class SpectatorTest implements Listener {

	public SpectatorTest(){
		RealCraft.getInstance().getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(), PacketType.Play.Server.PLAYER_INFO, PacketType.Play.Server.GAME_STATE_CHANGE, PacketType.Play.Server.CAMERA) {
            @Override
            public void onPacketSending(PacketEvent event) {
            	try {
            		if(event.getPacketType() == PacketType.Play.Server.PLAYER_INFO){
		                PacketPlayOutPlayerInfo packet = (PacketPlayOutPlayerInfo) event.getPacket().getHandle();
		                PacketPlayOutPlayerInfo.EnumPlayerInfoAction action = (PacketPlayOutPlayerInfo.EnumPlayerInfoAction) ReflectionUtils.getField(packet.getClass(), true, "a").get(packet);
		                if (action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE) {
		                    @SuppressWarnings("unchecked")
							List<PacketPlayOutPlayerInfo.PlayerInfoData> infoList = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) ReflectionUtils.getField(packet.getClass(), true, "b").get(packet);
		                    for (PacketPlayOutPlayerInfo.PlayerInfoData infoData : infoList) {
		                        if (infoData.c() == EnumGamemode.SPECTATOR){
		                            ReflectionUtils.setValue(infoData,true,"c",EnumGamemode.CREATIVE);
		                        }
		                    }
		                }
            		}
            		else if(event.getPacketType() == PacketType.Play.Server.GAME_STATE_CHANGE){
            			event.setCancelled(true);
            		}
            		else if(event.getPacketType() == PacketType.Play.Server.CAMERA){
            			event.setCancelled(true);
            		}
            	} catch (Exception e){
            		e.printStackTrace();
            	}
            }
        });
    }

    public static Object getDeclaredField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void modifyFinalField(Field field, Object target, Object newValue) {
        try {
            field.setAccessible(true);
            Field modifierField = Field.class.getDeclaredField("modifiers");
            modifierField.setAccessible(true);
            modifierField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(target, newValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		if(command.equalsIgnoreCase("spectest")){
			event.setCancelled(true);
			player.setGameMode(GameMode.SPECTATOR);
			//((CraftPlayer)player).getHandle().abilities.mayBuild
		}
	}
}