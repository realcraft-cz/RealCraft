package realcraft.bukkit.anticheat.checks;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.anticheat.AntiCheat;

public class CheckClickAura extends Check {

	private static final int HIT_LIMIT = 15;
	private static final int CHECKS_LIMIT = 3;

	public CheckClickAura(){
		super(CheckType.CLICKAURA);
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),ListenerPriority.HIGH,PacketType.Play.Client.USE_ENTITY){
			@Override
			public void onPacketReceiving(PacketEvent event){
				Player player = event.getPlayer();
				if(event.getPacketType() == PacketType.Play.Client.USE_ENTITY){
					if(event.getPacket().getEntityUseActions().read(0) == EntityUseAction.ATTACK){
						if(!AntiCheat.isPlayerExempted(player)){
							CheckClickAura.this.check(player);
						}
					}
				}
			}
		});
	}

	@Override
	public void run(){
	}

	public void check(Player player){
		AntiCheat.getPlayer(player).hitFrequency.add();
		int frequency = AntiCheat.getPlayer(player).hitFrequency.getFrequency();
		if(frequency > HIT_LIMIT){
			AntiCheat.getPlayer(player).hitFrequency.clear();
			AntiCheat.getPlayer(player).hitChecks ++;
			if(AntiCheat.getPlayer(player).hitChecks >= CHECKS_LIMIT){
				AntiCheat.getPlayer(player).hitChecks = 0;
				this.detect(player);
			}
		}
	}

	public static class HitFrequency {

		private long[] buckets;

		public HitFrequency(int range){
			this.buckets = new long[range];
		}

		public void add(){
			this.update();
			buckets[0] = System.currentTimeMillis();
		}

		public int getFrequency(){
			long now = System.currentTimeMillis();
			int buckets = 0;
			for(long bucket : this.buckets){
				if(bucket > now-this.buckets.length){
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