package realcraft.bukkit.cosmetics;

import org.bukkit.World;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.cosmetic.Cosmetic;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticCategory;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics.menu.CosmeticMenu;
import realcraft.bukkit.users.Users;
import realcraft.share.ServerType;
import realcraft.share.users.User;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Cosmetics {

	private static HashMap<CosmeticType,Cosmetic> cosmetics = new HashMap<>();
	private static HashMap<User,CosmeticPlayer> players = new HashMap<>();
	private static ArrayList<Cosmetic> cosmeticsList = null;

	static {
		for(CosmeticType type : CosmeticType.values()){
			try {
				cosmetics.put(type,(Cosmetic)type.getClazz().getConstructor(CosmeticType.class).newInstance(type));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e){
				e.printStackTrace();
			}
		}
	}

	public Cosmetics(){
		new CosmeticMenu();
		if(RealCraft.getServerType() == ServerType.LOBBY) new CosmeticCrystals();
	}

	public static ArrayList<Cosmetic> getCosmetics(){
		if(cosmeticsList == null){
			cosmeticsList = new ArrayList<>(cosmetics.values());
			Collections.sort(cosmeticsList,new Comparator<Cosmetic>(){
				@Override
				public int compare(Cosmetic cosmetic1,Cosmetic cosmetic2){
					int compare = Long.compare(cosmetic1.getType().getId(),cosmetic2.getType().getId());
					if(compare > 0) return 1;
					else if(compare < 0) return -1;
					return 0;
				}
			});
		}
		return cosmeticsList;
	}

	public static ArrayList<Cosmetic> getCosmetics(CosmeticCategory category){
		ArrayList<Cosmetic> tmpCosmetics = new ArrayList<>();
		for(Cosmetic cosmetic : Cosmetics.getCosmetics()){
			if(cosmetic.getType().getCategory() == category) tmpCosmetics.add(cosmetic);
		}
		return tmpCosmetics;
	}

	public static Cosmetic getRandomCosmetic(Player player){
		CosmeticPlayer cPlayer = Cosmetics.getCosmeticPlayer(Users.getUser(player));
		ArrayList<Cosmetic> tmpCosmetics = new ArrayList<>(cosmetics.values());
		Collections.shuffle(tmpCosmetics);
		for(Cosmetic cosmetic : tmpCosmetics){
			if(!cPlayer.hasCosmetic(cosmetic.getType()) || cosmetic.getType().getCategory() == CosmeticCategory.GADGET){
				return cosmetic;
			}
		}
		for(Cosmetic cosmetic : tmpCosmetics){
			if(cosmetic.getType().getCategory() == CosmeticCategory.GADGET){
				return cosmetic;
			}
		}
		return tmpCosmetics.get(0);
	}

	public static Cosmetic getCosmetic(CosmeticType type){
		return cosmetics.get(type);
	}

	public static void loadCosmetics(Player player){
		for(Cosmetic cosmetic : getCosmetics()){
			if(cosmetic.isEnabled(player)){
				cosmetic.setEnabled(player,true);
			}
		}
	}

	public static void clearCosmetics(Player player){
		for(Cosmetic cosmetic : getCosmetics()){
			cosmetic.setRunning(player,false);
		}
	}

	public static void disableCosmetics(Player player){
		for(Cosmetic cosmetic : getCosmetics()){
			cosmetic.setEnabled(player,false);
		}
	}

	public static void disableCosmetics(Player player,CosmeticCategory category){
		for(Cosmetic cosmetic : getCosmetics(category)){
			cosmetic.setEnabled(player,false);
		}
	}

	public static CosmeticPlayer getCosmeticPlayer(User user){
		if(!players.containsKey(user)) players.put(user,new CosmeticPlayer(user));
		return players.get(user);
	}

	public static CosmeticPlayer getCosmeticPlayer(Player player){
		return Cosmetics.getCosmeticPlayer(Users.getUser(player));
	}

	public static boolean isAvailable(World world){
		if(RealCraft.getServerType() == ServerType.LOBBY || RealCraft.getServerType() == ServerType.SURVIVAL || RealCraft.getServerType() == ServerType.CREATIVE) return true;
		else if(world.getName().equalsIgnoreCase("world")) return true;
		return false;
	}
}