package realcraft.bukkit.test;

import org.bukkit.event.Listener;

import realcraft.bukkit.RealCraft;

public class AntiCheatTest implements Listener {

	public AntiCheatTest(){
		RealCraft.getInstance().getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	/*@EventHandler
	public void PlayerToggleSprintEvent(PlayerToggleSprintEvent event){
		if(event.isSprinting()){
			event.setCancelled(true);
			System.out.println("PlayerToggleSprintEvent");
			event.getPlayer().setSprinting(false);
		}
	}*/

	/*private boolean drawing = false;

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		System.out.println("PlayerInteractEvent");
		if(event.getItem() != null && event.getItem().getType() == Material.BOW){
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
				drawing = true;
			}
		}
	}

	@EventHandler
	public void EntityShootBowEvent(EntityShootBowEvent event){
		drawing = false;
	}

	@EventHandler
	public void PlayerItemHeldEvent(PlayerItemHeldEvent event){
		System.out.println("PlayerItemHeldEvent");
		drawing = false;
	}

	@Override
	public void run(){
		if(drawing){
			for(Player player : Bukkit.getOnlinePlayers()){
				player.getWorld().playSound(player.getLocation(),Sound.ENTITY_ARROW_SHOOT,1f,1f);
				Arrow arrow = player.launchProjectile(Arrow.class);
				Vector vector = arrow.getVelocity();
				vector.setX(vector.getX() + RandomUtil.getRandomDouble(-0.2,0.2));
				vector.setY(vector.getY() + RandomUtil.getRandomDouble(-0.2,0.2));
				vector.setZ(vector.getZ() + RandomUtil.getRandomDouble(-0.2,0.2));
				arrow.setVelocity(vector);
            }
		}
	}*/
}