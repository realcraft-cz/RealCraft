package realcraft.bungee.skins;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.protocol.Property;
import realcraft.bungee.RealCraftBungee;
import realcraft.bungee.skins.exceptions.SkinsLimitException;
import realcraft.bungee.skins.exceptions.SkinsNotFoundException;
import realcraft.bungee.skins.utils.ReflectionUtil;
import realcraft.bungee.sockets.SocketData;
import realcraft.bungee.sockets.SocketManager;
import realcraft.bungee.users.Users;
import realcraft.share.skins.Skin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Skins {

	public static RealCraftBungee plugin;
	public static final String CHANNEL_SKIN_RESET = "bungeeSkinReset";
	private static final int SKIN_LIMIT = 30;
	private static final String SKIN_API = "https://www.realcraft.cz/api/skin/";

	public Skins(RealCraftBungee realcraft){
		plugin = realcraft;
		plugin.getProxy().getPluginManager().registerListener(plugin,new SkinsListeners());
		plugin.getProxy().getPluginManager().registerCommand(plugin,new SkinsCommand());
	}

	public static void changeSkin(ProxiedPlayer player,String name) throws SkinsLimitException, SkinsNotFoundException {
		if(Users.getUser(player).getLastSkinned()+SKIN_LIMIT > System.currentTimeMillis()/1000) throw new SkinsLimitException((int)(Users.getUser(player).getLastSkinned()+SKIN_LIMIT-(System.currentTimeMillis()/1000)));
		Skin skin = Skins.getSkin(name);
		if(skin == null) throw new SkinsNotFoundException();
		Users.getUser(player).setSkin(name);
		Skins.setSkin(player,skin);
		Skins.sendResetRequest(player);
	}

	public static void setSkin(ProxiedPlayer player,Skin skin){
		try {
			Property textures = new Property("textures",skin.getValue(),skin.getSignature());
			InitialHandler handler = (InitialHandler) player.getPendingConnection();
			LoginResult profile = new LoginResult(player.getUniqueId().toString(),player.getName(),new Property[]{textures});
			Property[] present = profile.getProperties();
			Property[] newprops = new Property[present.length+1];
			System.arraycopy(present,0,newprops,0,present.length);
			newprops[present.length] = textures;
			profile.getProperties()[0].setName(newprops[0].getName());
			profile.getProperties()[0].setValue(newprops[0].getValue());
			profile.getProperties()[0].setSignature(newprops[0].getSignature());
			ReflectionUtil.setObject(InitialHandler.class,handler,"loginProfile",profile);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void sendResetRequest(ProxiedPlayer player){
		plugin.getProxy().getScheduler().runAsync(plugin,new Runnable(){
			@Override
			public void run(){
				SocketData data = new SocketData(CHANNEL_SKIN_RESET);
				data.setString("uuid",player.getUniqueId().toString());
				SocketManager.sendToAll(data);
			}
		});
	}

	public static Skin getSkin(String name){
		String content = Skins.getAPIContent(name);
		if(content != null && !content.isEmpty()){
			JsonElement element = new JsonParser().parse(content);
			if(!element.isJsonNull() && element.isJsonObject()){
				JsonObject object = element.getAsJsonObject();
				return new Skin(object.get("name").getAsString(),object.get("uuid").getAsString(),object.get("value").getAsString(),object.get("signature").getAsString());
			}
		}
		return null;
	}

	public static String getAPIContent(String name){
		try {
			HttpURLConnection request = (HttpURLConnection) new URL(SKIN_API+name).openConnection();
			request.setConnectTimeout(5000);
			request.setReadTimeout(5000);
			request.setDoOutput(true);
			String line;
			StringBuilder output = new StringBuilder();
			BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
			while((line = in.readLine()) != null) output.append(line);
			in.close();
			return output.toString();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
}