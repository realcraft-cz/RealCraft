package realcraft.bukkit.skins;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import realcraft.bukkit.RealCraft;

public class Skins implements PluginMessageListener {
	RealCraft plugin;
	SkinFactory factory;

	public Skins(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin,"RealCraftSkins",this);
		factory = new SkinFactory();
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message){
		if(!channel.equals("RealCraftSkins")) return;
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if(subchannel.equals("ResetRequest")){
			UUID uuid = UUID.fromString(in.readUTF());
			if(uuid != null){
				player = plugin.getServer().getPlayer(uuid);
				if(player != null){
					factory.updateSkin(player);
				}
			}
		}
	}
}