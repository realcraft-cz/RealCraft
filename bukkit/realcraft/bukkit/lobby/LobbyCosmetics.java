package realcraft.bukkit.lobby;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.auth.AuthLoginEvent;
import realcraft.bukkit.cosmetics.Cosmetics;
import realcraft.bukkit.cosmetics.hats.Hat;
import realcraft.bukkit.cosmetics.mounts.Mount;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffect;
import realcraft.bukkit.cosmetics.pets.Pet;
import realcraft.bukkit.cosmetics.suits.Suit;

public class LobbyCosmetics implements Listener {
	RealCraft plugin;

	public LobbyCosmetics(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		Cosmetics.init();
		LobbyCosmeticsMain.init(this);
	}

	public void onReload(){
	}

	public void onDisable(){
		for(Player player : plugin.getServer().getOnlinePlayers()){
			this.clearCosmetics(player);
		}
	}

	@EventHandler
	public void AuthLoginEvent(AuthLoginEvent event){
		Player player = event.getPlayer();
		ItemStack chest = new ItemStack(Material.CHEST,1);
		ItemMeta meta = chest.getItemMeta();
		meta.setDisplayName("§e§lDoplnky");
		chest.setItemMeta(meta);
		player.getInventory().setItem(4,chest);
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(plugin.playermanazer.getPlayerInfo(player).isLogged() && player.getWorld().getName().equalsIgnoreCase("world")){
			ItemStack chest = new ItemStack(Material.CHEST,1);
			ItemMeta meta = chest.getItemMeta();
			meta.setDisplayName("§e§lDoplnky");
			chest.setItemMeta(meta);
			player.getInventory().setItem(4,chest);
		}
	}

	@EventHandler
	public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event){
		ItemStack chest = new ItemStack(Material.CHEST,1);
		ItemMeta meta = chest.getItemMeta();
		meta.setDisplayName("§e§lDoplnky");
		chest.setItemMeta(meta);
		if(event.getFrom().getName().equalsIgnoreCase("world")){
			event.getPlayer().getInventory().remove(chest);
			this.clearCosmetics(event.getPlayer());
		}
		else if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
			event.getPlayer().getInventory().setItem(4,chest);
		}
	}

	public String giveRandomReward(Player player){
		int random = this.getRandomNumber(1,12);
		switch(random){
			case 1:
			case 2:
				ParticleEffect effect = Cosmetics.getRandomParticleEffect(player,0);
				if(effect == null) return Cosmetics.getRandomGadget().giveRandomReward(player);
				else return effect.giveReward(player);
			case 3:
			case 4:
				Hat.HatType hat = Cosmetics.getRandomHat(player,0);
				if(hat == null) return Cosmetics.getRandomGadget().giveRandomReward(player);
				else return hat.giveReward(player);
			case 5:
			case 6:
				Pet pet = Cosmetics.getRandomPet(player,0);
				if(pet == null) return Cosmetics.getRandomGadget().giveRandomReward(player);
				else return pet.giveReward(player);
			case 7:
			case 8:
				Mount mount = Cosmetics.getRandomMount(player,0);
				if(mount == null) return Cosmetics.getRandomGadget().giveRandomReward(player);
				else return mount.giveReward(player);
			case 9:
			case 10:
				Suit suit = Cosmetics.getRandomSuit(player,0);
				if(suit == null) return Cosmetics.getRandomGadget().giveRandomReward(player);
				else return suit.giveReward(player);
			default: return Cosmetics.getRandomGadget().giveRandomReward(player);
		}
		/*switch(random){
			case 1:
			case 2:
				ParticleEffect effect = Cosmetics.getRandomParticleEffect(player,0);
				if(effect == null) return Cosmetics.getRandomGadget().giveRandomReward(player);
				else return effect.giveReward(player);
			case 3:
			case 4:
				Hat.HatType hat = Cosmetics.getRandomHat(player,0);
				if(hat == null) return Cosmetics.getRandomGadget().giveRandomReward(player);
				else return hat.giveReward(player);
			case 5:
			case 6:
				Suit suit = Cosmetics.getRandomSuit(player,0);
				if(suit == null) return Cosmetics.getRandomGadget().giveRandomReward(player);
				else return suit.giveReward(player);
			default: return Cosmetics.getRandomGadget().giveRandomReward(player);
		}*/
	}

	public int getRandomNumber(int min, int max){
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.getWorld().getName().equalsIgnoreCase("world") && player.getInventory().getItemInMainHand().getType() == Material.CHEST && (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
			event.setCancelled(true);
			if(plugin.playermanazer.getPlayerInfo(player).isLogged()){
				LobbyCosmeticsMain.openMenu(player);
			}
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player && ((Player)event.getWhoClicked()).getWorld().getName().equalsIgnoreCase("world")){
			if(event.getSlotType() == SlotType.QUICKBAR && event.getCurrentItem().getType() == Material.CHEST){
				event.setCancelled(true);
				if(plugin.playermanazer.getPlayerInfo((Player)event.getWhoClicked()).isLogged()){
					LobbyCosmeticsMain.openMenu((Player)event.getWhoClicked());
				}
			}
			else if(event.getInventory().getTitle().equalsIgnoreCase(LobbyCosmeticsMain.getInventoryName())){
				event.setCancelled(true);
				LobbyCosmeticsMain.InventoryClickEvent(event);
			}
			else if(event.getInventory().getTitle().equalsIgnoreCase(LobbyCosmeticsHats.getInventoryName())){
				event.setCancelled(true);
				LobbyCosmeticsHats.InventoryClickEvent(event);
			}
			else if(event.getInventory().getTitle().equalsIgnoreCase(LobbyCosmeticsSuits.getInventoryName())){
				event.setCancelled(true);
				LobbyCosmeticsSuits.InventoryClickEvent(event);
			}
			else if(event.getInventory().getTitle().equalsIgnoreCase(LobbyCosmeticsGadgets.getInventoryName())){
				event.setCancelled(true);
				LobbyCosmeticsGadgets.InventoryClickEvent(event);
			}
			else if(event.getInventory().getTitle().equalsIgnoreCase(LobbyCosmeticsEffects.getInventoryName())){
				event.setCancelled(true);
				LobbyCosmeticsEffects.InventoryClickEvent(event);
			}
			else if(event.getInventory().getTitle().equalsIgnoreCase(LobbyCosmeticsPets.getInventoryName())){
				event.setCancelled(true);
				LobbyCosmeticsPets.InventoryClickEvent(event);
			}
			else if(event.getInventory().getTitle().equalsIgnoreCase(LobbyCosmeticsMounts.getInventoryName())){
				event.setCancelled(true);
				LobbyCosmeticsMounts.InventoryClickEvent(event);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world") && event.getItemDrop().getItemStack().getType() == Material.CHEST){
			event.setCancelled(true);
		}
	}

	public void clearCosmetics(Player player){
		Cosmetics.clearCosmetics(player);
	}
}