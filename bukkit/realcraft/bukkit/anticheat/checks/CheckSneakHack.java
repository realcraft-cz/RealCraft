package realcraft.bukkit.anticheat.checks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import realcraft.bukkit.anticheat.AntiCheat;

public class CheckSneakHack extends Check {

	private static final int SNEAK_LIMIT = 60;
	private static final int CHECKS_LIMIT = 3;

	public CheckSneakHack(){
		super(CheckType.SNEAKHACK);
	}

	@Override
	public void run(){
	}

	@EventHandler
	public void PlayerToggleSneakEvent(PlayerToggleSneakEvent event){
		this.check(event.getPlayer(),event.isSneaking());
	}

	public void check(Player player,boolean sneak){
		AntiCheat.getPlayer(player).sneakFrequency.add();
		int frequency = AntiCheat.getPlayer(player).sneakFrequency.getFrequency();
		if(frequency > SNEAK_LIMIT){
			AntiCheat.getPlayer(player).sneakFrequency.clear();
			AntiCheat.getPlayer(player).sneakChecks ++;
			if(AntiCheat.getPlayer(player).sneakChecks >= CHECKS_LIMIT){
				AntiCheat.getPlayer(player).sneakChecks = 0;
				this.detect(player);
			}
		}
	}

	public static class SneakFrequency {

		private long[] buckets;

		public SneakFrequency(int range){
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