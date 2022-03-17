package realcraft.bukkit.lobby;

/*import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_18_R2.BlockPosition;
import net.minecraft.server.v1_18_R2.TileEntitySkull;*/

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.entity.TileEntitySkull;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.wrappers.LightApi;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.UUID;

public class LobbyLanterns implements Listener {
	RealCraft plugin;

	private String lanternTexture1 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFmZjkzZWJlY2MxZjhmYmQxM2JhNzgzOWVjN2JkY2RlY2FiN2MwN2ZkOGJhNzhlZTc4YWQwYmQzYWNjYmUifX19";
	private String lanternTexture2 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2NjMjE3YTliOWUzY2UzY2QwNDg0YzdlOGNlNDlkMWNmNzQxMjgxYmRkYTVhNGQ2Y2I4MjFmMzc4NzUyNzE4In19fQ==";

	public LobbyLanterns(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
			@Override
			public void run(){
				loadLights(Bukkit.getServer().getWorld("world"));
			}
		},20);
	}

	public void onReload(){
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		if(event.isCancelled()) return;
		Player player = event.getPlayer();
		String [] args = event.getMessage().substring(1).split(" ");
		if(player.getGameMode() == GameMode.CREATIVE && (args[0].equalsIgnoreCase("lucerna") || args[0].equalsIgnoreCase("lantern"))){
			event.setCancelled(true);
			int type = 1;
			if(args.length < 2){
				player.sendMessage("/"+args[0]+" [1-2]");
				return;
			}
			try {
				type = Integer.parseInt(args[1]);
			} catch(NumberFormatException e){
				player.sendMessage("/"+args[0]+" [1-2]");
				return;
			}
			ItemStack head = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta headMeta = (SkullMeta) head.getItemMeta();
			GameProfile profile = new GameProfile(UUID.randomUUID(),null);
			if(type == 2) profile.getProperties().put("textures",new Property("textures",lanternTexture2));
			else profile.getProperties().put("textures",new Property("textures",lanternTexture1));
			Field profileField = null;
			try {
				profileField = headMeta.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);
				profileField.set(headMeta,profile);
			} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1){
				e1.printStackTrace();
			}
			head.setItemMeta(headMeta);
			player.getInventory().addItem(head);
		}
	}

	public void loadLights(World world){
		Location center = world.getSpawnLocation();
		int radius = 256;
		int minX = center.getBlockX()-(radius/2);
		int minY = center.getBlockY()-(radius/2);
		int minZ = center.getBlockZ()-(radius/2);
		for(int x=minX;x<=center.getBlockX()+(radius/2);x++){
			for(int y=minY;y<=center.getBlockY()+(radius/2);y++){
				for(int z=minZ;z<=center.getBlockZ()+(radius/2);z++){
					Block block = world.getBlockAt(x,y,z);
					if(this.isBlockLantern(block)){
						LightApi.createLight(block.getLocation(),15,false);
					}
				}
			}
		}
	}

	public boolean isBlockLantern(Block block){
		if(block.getType() == Material.PLAYER_HEAD){
			Location location = block.getLocation();
			TileEntitySkull skullTile = (TileEntitySkull)((CraftWorld)block.getWorld()).getHandle().getBlockEntity(new BlockPosition(location.getBlockX(),location.getBlockY(),location.getBlockZ()), false);
			if(skullTile != null){
				GameProfile profile = skullTile.e;
				if(profile != null){
					Collection<Property> properties = profile.getProperties().get("textures");
					for(Property property : properties){
						if(property.getValue().equals(lanternTexture1) || property.getValue().equals(lanternTexture2)){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent event){
		Block block = event.getBlock();
		if(this.isBlockLantern(block)){
			LightApi.createLight(block.getLocation(),15,false);
			for(ChunkInfo info : LightApi.collectChunks(block.getLocation())){
				LightApi.updateChunk(info);
			}
		}
	}

	@EventHandler
	public void BlockBreakEvent(BlockBreakEvent event){
		Block block = event.getBlock();
		if(this.isBlockLantern(block)){
			LightApi.deleteLight(block.getLocation(),false);
			for(ChunkInfo info : LightApi.collectChunks(block.getLocation())){
				LightApi.updateChunk(info);
			}
		}
	}
}
///give @p skull 1 3 {display:{Name:"Redstone Lamp"},SkullOwner:{Id:"b6864e28-93f7-4c61-b497-8f226140747b",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFmZjkzZWJlY2MxZjhmYmQxM2JhNzgzOWVjN2JkY2RlY2FiN2MwN2ZkOGJhNzhlZTc4YWQwYmQzYWNjYmUifX19"}]}}}
///give @p skull 1 3 {display:{Name:"Lantern"},SkullOwner:{Id:"8c0662db-972a-4039-a360-3dc1a938390d",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2NjMjE3YTliOWUzY2UzY2QwNDg0YzdlOGNlNDlkMWNmNzQxMjgxYmRkYTVhNGQ2Y2I4MjFmMzc4NzUyNzE4In19fQ=="}]}}}