package realcraft.bukkit.anticheat.checks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_13_R1.*;
import net.minecraft.server.v1_13_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.anticheat.AntiCheat;

import java.util.UUID;

public class CheckKillAura extends Check {

	private static final int GHOST_TIMEOUT = 30*1000;
	private static final int GHOST_DURATION = 5*1000;

	private static final int HIT_LIMIT = 3;
	private static final int CHECKS_LIMIT = 2;


	public CheckKillAura(){
		super(CheckType.KILLAURA);
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),ListenerPriority.HIGH,PacketType.Play.Client.USE_ENTITY){
			@Override
			public void onPacketReceiving(PacketEvent event){
				Player player = event.getPlayer();
				if(event.getPacketType() == PacketType.Play.Client.USE_ENTITY){
					if(event.getPacket().getEntityUseActions().read(0) == EntityUseAction.ATTACK && AntiCheat.getPlayer(player).ghostPlayer != null && event.getPacket().getIntegers().read(0) == AntiCheat.getPlayer(player).ghostPlayer.getId()){
						if(!AntiCheat.isPlayerExempted(player)){
							CheckKillAura.this.check(player);
						}
					}
				}
			}
		});
	}

	@Override
	public void run(){
	}

	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(AntiCheat.getPlayer(player).ghostPlayer != null){
			AntiCheat.getPlayer(player).ghostPlayer.ticksLived ++;
			if(AntiCheat.getPlayer(player).lastGhost+GHOST_DURATION >= System.currentTimeMillis()) this.moveFakePlayer(player);
			else this.removeFakePlayer(player);
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
			Player player = (Player)event.getDamager();
			if(!AntiCheat.isPlayerExempted(player)){
				if(event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.ENTITY_SWEEP_ATTACK){
					this.addFakePlayer(player);
				}
			}
		}
	}

	public void check(Player player){
		AntiCheat.getPlayer(player).ghostFrequency.add();
		int frequency = AntiCheat.getPlayer(player).ghostFrequency.getFrequency(5000);
		if(frequency > HIT_LIMIT){
			AntiCheat.getPlayer(player).ghostFrequency.clear();
			AntiCheat.getPlayer(player).ghostChecks ++;
			if(AntiCheat.getPlayer(player).ghostChecks >= CHECKS_LIMIT){
				AntiCheat.getPlayer(player).ghostChecks = 0;
				AntiCheat.getPlayer(player).lastGhost = System.currentTimeMillis();
				this.detect(player);
			}
		}
	}

	private void addFakePlayer(Player player){
		if(AntiCheat.getPlayer(player).lastGhost+GHOST_TIMEOUT < System.currentTimeMillis()){
			this.removeFakePlayer(player);
			MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
			WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
			AntiCheat.getPlayer(player).ghostPlayer = new EntityPlayer(server,world,new GameProfile(UUID.randomUUID(),"_"),new PlayerInteractManager(world));
			Location location = player.getLocation().clone();
			location = this.getBehindLocation(location);
			AntiCheat.getPlayer(player).ghostPlayer.setLocation(location.getX(),location.getY(),location.getZ(),location.getYaw(),location.getPitch());
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER,AntiCheat.getPlayer(player).ghostPlayer));
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(AntiCheat.getPlayer(player).ghostPlayer));
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER,AntiCheat.getPlayer(player).ghostPlayer));
			AntiCheat.getPlayer(player).lastGhost = System.currentTimeMillis();
		}
	}

	private void moveFakePlayer(Player player){
		if(AntiCheat.getPlayer(player).ghostPlayer != null){
			Location location = player.getLocation().clone();
			if((AntiCheat.getPlayer(player).ghostPlayer.ticksLived%10) == 0
			|| (AntiCheat.getPlayer(player).ghostPlayer.ticksLived+1)%10 == 0
			|| (AntiCheat.getPlayer(player).ghostPlayer.ticksLived+2)%10 == 0 || location.getPitch() < -45) location = this.getBehindLocation(location);
			else location = this.getAboveLocation(location);
			AntiCheat.getPlayer(player).ghostPlayer.setLocation(location.getX(),location.getY(),location.getZ(),location.getYaw(),location.getPitch());
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(AntiCheat.getPlayer(player).ghostPlayer));
		}
	}

	private void removeFakePlayer(Player player){
		if(AntiCheat.getPlayer(player).ghostPlayer != null){
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(AntiCheat.getPlayer(player).ghostPlayer.getId()));
			AntiCheat.getPlayer(player).ghostPlayer = null;
		}
	}

	private Location getAboveLocation(Location location){
		location.setPitch(0);
		location.add(location.getDirection().normalize().multiply(-1));
		location.add(0,2.5,0);
		return location;
	}

	private Location getBehindLocation(Location location){
		location.setPitch(0);
		location.add(location.getDirection().normalize().multiply(-3));
		return location;
	}

	public static class GhostFrequency {

		private long[] buckets;

		public GhostFrequency(int range){
			this.buckets = new long[range];
		}

		public void add(){
			this.update();
			buckets[0] = System.currentTimeMillis();
		}

		public int getFrequency(int delay){
			long now = System.currentTimeMillis();
			int buckets = 0;
			for(long bucket : this.buckets){
				if(bucket > now-delay){
					buckets ++;
				}
			}
			return buckets;
		}

		public void clear(){
			this.buckets = new long[this.buckets.length];
		}

		private void update(){
			for(int i=this.buckets.length-1;i>0;i--){
				this.buckets[i] = this.buckets[i-1];
			}
		}
	}
}