package realcraft.bukkit.fights.duels;

import org.bukkit.Sound;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import realcraft.bukkit.fights.FightPlayer;
import realcraft.bukkit.fights.Fights;

public class FightDuelsRequests {

	private static final long REQUEST_TIMEOUT_SECONDS = 60;

	public static void sendRequest(FightPlayer sender,FightPlayer recipient){
		FightDuelRequest request = new FightDuelRequest(sender,recipient);
		sender.addRequest(request);
		recipient.addRequest(request);
		FightDuels.sendMessage(sender,"§eVyzval jsi hrace §f"+recipient.getUser().getName()+"§e na souboj.");
		TextComponent message = new TextComponent(FightDuels.PREFIX+"§f"+request.getSender().getUser().getName()+"§e te vyzval na souboj ");
		TextComponent accept = new TextComponent("§7[§a§lPRIJMOUT§7]");
		accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/fight accept "+request.getSender().getUser().getName()));
		accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Klikni pro prijmuti vyzvy").create()));
		message.addExtra(accept);
		recipient.getPlayer().spigot().sendMessage(message);
		recipient.getPlayer().playSound(recipient.getPlayer().getLocation(),Sound.BLOCK_NOTE_PLING,1f,1f);
	}

	public static void acceptRequest(FightDuelRequest request){
		request.getSender().getRequests().remove(request);
		request.getRecipient().getRequests().remove(request);
		if(!Fights.getDuels().createDuel(request.getSender(),request.getRecipient(),false)){
			FightDuels.sendMessage(request.getSender(),"§cVsechny areny jsou obsazene, zkus to za chvili.");
			FightDuels.sendMessage(request.getRecipient(),"§cVsechny areny jsou obsazene, zkus to za chvili.");
		}
	}

	public static class FightDuelRequest {

		private FightPlayer sender;
		private FightPlayer recipient;
		private Long created;

		public FightDuelRequest(FightPlayer sender,FightPlayer recipient){
			this.sender = sender;
			this.recipient = recipient;
			this.created = System.currentTimeMillis();
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
			return (this.getCreated()+(REQUEST_TIMEOUT_SECONDS*1000) < System.currentTimeMillis());
		}

		@Override
		public boolean equals(Object object){
			if(object instanceof FightDuelRequest){
				FightDuelRequest toCompare = (FightDuelRequest) object;
				return (toCompare.getSender().equals(this.getSender()) && toCompare.getRecipient().equals(this.getRecipient()) && toCompare.getCreated() == this.getCreated());
			}
			return false;
		}
	}
}