package realcraft.bukkit.falling.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.falling.FallManager;
import realcraft.bukkit.falling.FallPlayer;
import realcraft.bukkit.falling.arena.FallArena;
import realcraft.bukkit.falling.exceptions.FallArenaLockedException;
import realcraft.bukkit.utils.DateUtil;
import realcraft.bukkit.utils.Glow;

import java.util.ArrayList;
import java.util.List;

public class FallCommandList extends FallCommand implements Listener {

	private static final String INV_NAME = "Moje ostrovy";
	private static ItemStack hotbarItem;

	public FallCommandList(){
		super("list");
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@Override
	public void perform(FallPlayer fPlayer,String[] args){
		fPlayer.getPlayer().openInventory(this.getMainMenu(fPlayer));
	}

	@Override
	public List<String> tabCompleter(FallPlayer fPlayer,String[] args){
		return null;
	}

	public static ItemStack getHotbarItem(){
		if(hotbarItem == null){
			hotbarItem = new ItemStack(Material.CLOCK);
			ItemMeta meta = hotbarItem.getItemMeta();
			meta.setDisplayName("§6§lMoje ostrovy");
			hotbarItem.setItemMeta(meta);
		}
		return hotbarItem;
	}

	public Inventory getMainMenu(FallPlayer fPlayer){
		Inventory menu = Bukkit.createInventory(null,6*9,INV_NAME);
		int idx = 0;
		for(FallArena arena : fPlayer.getSortedArenas()){
			int amount = arena.getTrusted().size()+1;
			ItemStack item = new ItemStack(Material.CHORUS_FLOWER,amount);
			ItemMeta meta = item.getItemMeta();
			String[] trusted = new String[arena.getTrusted().size()];
			int idx2 = 0;
			for(FallPlayer fPlayer2 : arena.getTrusted()){
				trusted[idx2++] = fPlayer2.getUser().getRank().getChatColor()+"§l"+fPlayer2.getUser().getName();
			}
			meta.setDisplayName("§6§lOstrov "+arena.getOwner().getRank().getChatColor()+arena.getOwner().getName());
			ArrayList<String> lores = new ArrayList<>();
			if(trusted.length > 0){
				lores.add("§7Trusted: "+StringUtils.join(trusted,", "));
				lores.add("§r");
			}
			lores.add("§7Vytvoreno: §f"+DateUtil.lastTime(arena.getCreated()));
			lores.add("§7Odehrany cas: §f"+(Math.round((arena.getTicks()/20f/60/60)*10)/10.0)+" hodin");
			lores.add("§r");
			lores.add("§7Klikni pro pripojeni");
			meta.setLore(lores);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			if(arena.getOwner().equals(fPlayer.getUser())){
				meta.addEnchant(Glow.getGlow(),1,true);
			}
			item.setItemMeta(meta);
			menu.setItem(10+idx+((idx/7)*6),item);
			idx ++;
		}
		return menu;
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(player.getInventory().getItem(0) == null || player.getInventory().getItem(0).getType() == Material.AIR){
			player.getInventory().setItem(0,FallCommandList.getHotbarItem());
		}
		player.getInventory().setHeldItemSlot(0);
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			Player player = (Player)event.getWhoClicked();
			FallPlayer fPlayer = FallManager.getFallPlayer(player);
			if(event.getView().getTitle().equalsIgnoreCase(INV_NAME)){
				event.setCancelled(true);
				if(event.getRawSlot() >= 10 && event.getRawSlot() <= 43 && event.getRawSlot()%9 >= 1 && event.getRawSlot()%9 <= 7){
					int idx = ((event.getRawSlot()/9)-1)*6+(event.getRawSlot()%9)-1;
					ArrayList<FallArena> arenas = fPlayer.getSortedArenas();
					if(arenas.size() <= idx) return;
					FallArena arena = arenas.get(idx);
					if(arena != null){
						try {
							fPlayer.joinArena(arena);
						} catch (FallArenaLockedException e){
							fPlayer.sendMessage("§cOstrov je zamknuty.");
						}
					}
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerInteractEvent(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.getInventory().getItemInMainHand().isSimilar(FallCommandList.getHotbarItem()) && event.getAction() != Action.PHYSICAL){
			event.setCancelled(true);
			player.openInventory(this.getMainMenu(FallManager.getFallPlayer(player)));
		}
	}
}