package realcraft.bukkit.friends;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketDataEvent;
import realcraft.share.ServerType;

public class FriendList implements Listener {

	private static final String INVENTORY_NAME = "Friends";

	public static final String CHANNEL_TELEPORT = "friendsTeleport";

	public FriendList(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	public static void openList(FriendPlayer fPlayer){
		int rows = (fPlayer.getFriends().size()/7)+1;
		if(rows >= 5) rows = 4;
		Inventory inventory = Bukkit.createInventory(null,(rows+2)*9,INVENTORY_NAME);
		int index = 10;
		for(FriendPlayer friend : fPlayer.getOrderedFriends()){
			inventory.setItem(index++,friend.getItemStack(fPlayer));
			if(index == 17) index = 19;
			else if(index == 26) index = 28;
			else if(index == 35) index = 37;
		}
		fPlayer.getPlayer().openInventory(inventory);
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_TELEPORT)){
			FriendPlayer fPlayer = Friends.getFriendPlayer(data.getInt("player"));
			FriendPlayer friend = Friends.getFriendPlayer(data.getInt("friend"));
			fPlayer.setFriendTeleport(friend);
		}
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		if(RealCraft.getServerType() == ServerType.LOBBY || RealCraft.getServerType() == ServerType.SURVIVAL || RealCraft.getServerType() == ServerType.CREATIVE){
			FriendPlayer fPlayer = Friends.getFriendPlayer(event.getPlayer());
			if(fPlayer.getFriendTeleport() != null){
				Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
					@Override
					public void run(){
						fPlayer.getPlayer().teleport(fPlayer.getFriendTeleport().friend.getPlayer().getLocation());
						FriendNotices.showFriendTeleport(fPlayer.getFriendTeleport().friend,fPlayer);
					}
				});
			}
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			FriendPlayer fPlayer = Friends.getFriendPlayer((Player)event.getWhoClicked());
			if(event.getInventory().getName().equalsIgnoreCase(INVENTORY_NAME)){
				event.setCancelled(true);
				if(event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.SKULL_ITEM){
					for(FriendPlayer friend : fPlayer.getFriends()){
						if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(friend.getItemStack(fPlayer).getItemMeta().getDisplayName())){
							this.clickFriend(fPlayer,friend);
							break;
						}
					}
				}
			}
		}
	}

	private void clickFriend(FriendPlayer fPlayer,FriendPlayer friend){
		if(friend.getUser().isLogged()){
			fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
			fPlayer.getPlayer().closeInventory();
			fPlayer.teleportToFriend(friend);
		}
	}
}