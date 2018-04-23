package realcraft.bukkit.fights.duels;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.utils.StringUtil;

public class FightDuelsRequests implements Listener {

	public FightDuelsRequests(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	public static void sendRequest(FightPlayer sender,FightPlayer recipient){
		FightDuelRequest request = new FightDuelRequest(sender,recipient);
		sender.addRequest(request);
		recipient.addRequest(request);
	}

	public static void acceptRequest(FightDuelRequest request){
		FightDuels.createDuel(request.getSender(),request.getRecipient());
	}

	public static class FightDuelRequest {

		private FightPlayer sender;
		private FightPlayer recipient;
		private Long created;

		public FightDuelRequest(FightPlayer sender,FightPlayer recipient){
			this.sender = sender;
			this.recipient = recipient;
		}

		public FightPlayer getSender(){
			return sender;
		}

		public FightPlayer getRecipient(){
			return recipient;
		}

		public Long getCreated(){
			return created;
		}

		public boolean isExpired(){
			return (this.getCreated()+(FightDuels.REQUEST_TIMEOUT_SECONDS*1000) < System.currentTimeMillis());
		}

		public long getRemains(){
			return ((this.getCreated()+(FightDuels.REQUEST_TIMEOUT_SECONDS*1000)-System.currentTimeMillis())/1000);
		}

		public String getExpireText(){
			int minutes = (int)Math.ceil(this.getRemains()/60f);
			return (this.getRemains() <= 60 ? this.getRemains()+" sekund" : (minutes)+" "+StringUtil.inflect(minutes,new String[]{"minuty","minut","minut"}));
		}
	}
}