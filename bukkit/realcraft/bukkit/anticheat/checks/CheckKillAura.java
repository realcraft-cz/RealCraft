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
import realcraft.bukkit.anticheat.utils.ActionFrequency;

public class CheckKillAura extends Check {

	private static final int HIT_LIMIT = 15;
	private static final int CHECKS_LIMIT = 3;

	private int tick;

	public CheckKillAura(){
		super(CheckType.KILLAURA);
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),ListenerPriority.HIGH,PacketType.Play.Client.USE_ENTITY){
			@Override
			public void onPacketReceiving(PacketEvent event){
				Player player = event.getPlayer();
				if(event.getPacketType() == PacketType.Play.Client.USE_ENTITY){
					if(event.getPacket().getEntityUseActions().read(0) == EntityUseAction.ATTACK){
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
		tick ++;
	}

	public void check(Player player){
		AntiCheat.getPlayer(player).killBuckets.add(System.currentTimeMillis(), 1f);

		final long fullTime = 333 * 6;
		final float fullLag = 1f;
		final float total = AntiCheat.getPlayer(player).killBuckets.score(1f) * 1000f / (fullLag * fullTime);

		if (tick < AntiCheat.getPlayer(player).killShortTermTick){
			AntiCheat.getPlayer(player).killShortTermTick = tick;
			AntiCheat.getPlayer(player).killShortTermCount = 1;
		}
		else if (tick - AntiCheat.getPlayer(player).killShortTermTick < 7){
			AntiCheat.getPlayer(player).killShortTermCount ++;
		} else {
			AntiCheat.getPlayer(player).killShortTermTick = tick;
			AntiCheat.getPlayer(player).killShortTermCount = 1;
		}

		final float shortTerm = AntiCheat.getPlayer(player).killShortTermCount * 1000f / (50f * 7);
		final float max = Math.max(shortTerm,total);

		if (max > HIT_LIMIT){
			AntiCheat.getPlayer(player).killChecks ++;
			if(AntiCheat.getPlayer(player).killChecks > CHECKS_LIMIT){
				AntiCheat.getPlayer(player).killChecks = 0;
				AntiCheat.getPlayer(player).killBuckets = new ActionFrequency(6,333);
				this.detect(player);
			}
			AntiCheat.getPlayer(player).killShortTermTick = tick;
			AntiCheat.getPlayer(player).killShortTermCount = 1;
		}
	}
}