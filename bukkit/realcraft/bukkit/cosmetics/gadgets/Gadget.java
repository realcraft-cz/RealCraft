package realcraft.bukkit.cosmetics.gadgets;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.CosmeticPlayer;
import realcraft.bukkit.cosmetics.Cosmetics;
import realcraft.bukkit.cosmetics.cosmetic.Cosmetic;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.ItemUtil;
import realcraft.bukkit.utils.RandomUtil;

import java.util.HashMap;

public abstract class Gadget extends Cosmetic implements Listener {

	private HashMap<CosmeticPlayer,Boolean> runnings = new HashMap<>();

	public Gadget(CosmeticType type){
		super(type);
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@Override
	public void run(Player player){
		player.getInventory().setItem(5,this.getItemStack(player));
	}

	@Override
	public void clear(Player player){
		player.getInventory().remove(this.getItemStack(player));
		player.getInventory().remove(this.getType().getMaterial());
	}

	public boolean isGadgetRunning(Player player){
		return runnings.containsKey(Cosmetics.getCosmeticPlayer(player));
	}

	public void setGadgetRunning(Player player,boolean running){
		if(running) runnings.put(Cosmetics.getCosmeticPlayer(player),true);
		else runnings.remove(Cosmetics.getCosmeticPlayer(player));
	}

	@Override
	public ItemStack getItemStack(Player player){
		ItemStack item = super.getItemStack(player);
		if(item.getType() != Material.GRAY_DYE){
			ItemMeta meta = item.getItemMeta();
			meta.setLore(ItemUtil.getLores("§7Munice: §e"+Cosmetics.getCosmeticPlayer(Users.getUser(player)).getCosmeticData(this.getType()).getAmount()));
			item.setItemMeta(meta);
		}
		return item;
	}

	public abstract void trigger(Player player);

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(event.getAction() != Action.PHYSICAL && event.getHand().equals(EquipmentSlot.HAND) && Cosmetics.isAvailable(player.getWorld()) && player.getInventory().getItemInMainHand().equals(this.getItemStack(player))){
			event.setCancelled(true);
			if(!this.isGadgetRunning(player)){
				this.setGadgetRunning(player,true);
				this.trigger(player);
				Cosmetics.getCosmeticPlayer(player).addCosmetic(this.getType(),-1);
				if(Cosmetics.getCosmeticPlayer(player).hasCosmetic(this.getType())) this.run(player);
				else clear(player);
			}
			else player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			Player player = (Player)event.getWhoClicked();
			if(Cosmetics.isAvailable(player.getWorld())){
				ItemStack item = event.getCurrentItem();
				if(event.getClick() == ClickType.NUMBER_KEY) item = player.getInventory().getItem(event.getHotbarButton());
				if(item != null && item.isSimilar(this.getItemStack(player))){
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void InventoryDragEvent(InventoryDragEvent event){
		if(event.getOldCursor().isSimilar(this.getItemStack())){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		Player player = event.getPlayer();
		if(Cosmetics.isAvailable(player.getWorld()) && event.getItemDrop().getItemStack().isSimilar(this.getItemStack(player))){
			event.setCancelled(true);
			Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					Gadget.this.setEnabled(player,false);
				}
			});
		}
	}

	public int getRandomAmount(){
		switch(this.getType()){
			case GADGET_CHICKENATOR:		return RandomUtil.getRandomInteger(8,20);
			case GADGET_MELONTHROWER:		return RandomUtil.getRandomInteger(8,20);
			case GADGET_COLORBOMB:			return RandomUtil.getRandomInteger(6,10);
			case GADGET_EXPLOSIVESHEEP:		return RandomUtil.getRandomInteger(2,4);
			case GADGET_TNT:				return RandomUtil.getRandomInteger(4,10);
			case GADGET_TSUNAMI:			return RandomUtil.getRandomInteger(4,10);
			//case GADGET_FIREWORK:			return RandomUtil.getRandomInteger(10,20);
			case GADGET_GHOSTPARTY:			return RandomUtil.getRandomInteger(2,4);
			case GADGET_FREEZECANNON:		return RandomUtil.getRandomInteger(10,20);
			case GADGET_PARTYPOPPER:		return RandomUtil.getRandomInteger(30,40);
			case GADGET_PAINTBALLGUN:		return RandomUtil.getRandomInteger(20,30);
			case GADGET_DIAMONDSHOWER:		return RandomUtil.getRandomInteger(3,6);
			case GADGET_GOLDSHOWER:			return RandomUtil.getRandomInteger(3,6);
			case GADGET_FOODSHOWER:			return RandomUtil.getRandomInteger(3,6);
		}
		return 0;
	}
}