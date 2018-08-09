package realcraft.bukkit.test;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import net.minecraft.server.v1_13_R1.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityDestroy;
import realcraft.bukkit.RealCraft;

public class VehicleTest implements Listener {

	public VehicleTest(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),PacketType.Play.Client.STEER_VEHICLE){
			@Override
			public void onPacketReceiving(PacketEvent event){
				if(event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE){
					PacketPlayInSteerVehicle packet = (PacketPlayInSteerVehicle) event.getPacket().getHandle();
					float sideMot = (packet.b() > 0 ? 1 : -1);
					float forMot = (packet.c() > 0 ? 1 : -1);
					boolean jump = packet.a();
				}
			}
		});
	}

	private Boat boat;

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerMoveEvent(PlayerMoveEvent event){
		//double speed = this.getPlayerSpeed(event.getPlayer());
		/*double speed = event.getFrom().distance(event.getTo());
		System.out.println("speed: "+speed);*/
	}

	private long lastCheck = 0;
	private Location lastLocation;

	private double getPlayerSpeed(Player player){
		Location location = player.getLocation().clone();
		location.setY(0);
		if(lastLocation == null){
			lastCheck = System.currentTimeMillis();
			lastLocation = location;
		}
		double speed = lastLocation.distance(location)/(System.currentTimeMillis()-lastCheck*1000/20);
		lastCheck = System.currentTimeMillis();
		lastLocation = location;
		return speed;
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		if(command.startsWith("vehtest") && player.hasPermission("group.Manazer")){
			event.setCancelled(true);

			boat = (Boat) player.getWorld().spawnEntity(player.getLocation(),EntityType.BOAT);
			boat.addPassenger(player);

			for(Player player2 : Bukkit.getOnlinePlayers()){
				if(player2.getEntityId() != player.getEntityId()){
					PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(boat.getEntityId());
					((CraftPlayer)player2).getHandle().playerConnection.sendPacket(packet);
				}
			}
		}
		if(command.startsWith("vehtp1") && player.hasPermission("group.Manazer")){
			event.setCancelled(true);
			boat.teleport(boat.getLocation().add(3.0,0,3.0));
		}
		if(command.startsWith("vehtp2") && player.hasPermission("group.Manazer")){
			event.setCancelled(true);
			boat.removePassenger(player);
			boat.teleport(boat.getLocation().add(3.0,0,3.0));
			boat.addPassenger(player);
		}
	}
}