package realcraft.bukkit.test;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EnumMoveType;
import net.minecraft.server.v1_12_R1.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_12_R1.World;
import realcraft.bukkit.RealCraft;

public class VehicleTest implements Listener {

	public VehicleTest(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),PacketType.Play.Client.STEER_VEHICLE){
			@Override
			public void onPacketReceiving(PacketEvent event){
				if(event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE){
					PacketPlayInSteerVehicle packet = (PacketPlayInSteerVehicle) event.getPacket().getHandle();
					float sideMot = (packet.a() > 0 ? 1 : -1);
					float forMot = (packet.b() > 0 ? 1 : -1);
					boolean jump = packet.c();
					if(stand != null){
						System.out.println(stand.getBukkitEntity().getVelocity().toString());
						stand.getBukkitEntity().setVelocity(stand.getBukkitEntity().getVelocity().add(new Vector(sideMot*0.1,0,forMot*0.1)));
					}
				}
			}
		});
	}

	CustomArmorStand stand;

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		if(command.startsWith("vehtest") && player.hasPermission("group.Manazer")){
			event.setCancelled(true);

			stand = new CustomArmorStand(((CraftWorld)player.getWorld()).getHandle());
			stand.setPositionRotation(player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ(),player.getLocation().getYaw(),player.getLocation().getPitch());
	    	((CraftLivingEntity) stand.getBukkitEntity()).setRemoveWhenFarAway(false);
	    	((CraftWorld)player.getWorld()).getHandle().addEntity(stand,SpawnReason.CUSTOM);
	    	stand.getBukkitEntity().setPassenger(player);
		}
	}

	public class CustomArmorStand extends EntityArmorStand {

		public CustomArmorStand(World world){
			super(world);
			this.setSmall(true);
		}

		@Override
		public void a(float sideMot,float forMot,float f2){
			if(this.isNoGravity()){
				move(EnumMoveType.SELF,motX,motY,motZ);
			} else {
				super.a(sideMot,forMot,f2);
			}
		}

		@Override
		public void n(){
			if(this.isNoGravity()){
				double motX = this.motX, motY = this.motY, motZ = this.motZ;
				super.n();
				this.motX = motX;
				this.motY = motY;
				this.motZ = motZ;
			}
			else super.n();
		}
	}
}