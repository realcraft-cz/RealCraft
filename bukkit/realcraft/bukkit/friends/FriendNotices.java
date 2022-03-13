package realcraft.bukkit.friends;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.friends.FriendPlayerSettings.FriendPlayerSettingsType;
import realcraft.bukkit.friends.FriendRequests.FriendsRequest;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketDataEvent;

public class FriendNotices implements Listener {

	public static final String CHANNEL_FRIEND_REMOVE = "friendsFriendRemove";
	public static final String CHANNEL_BUNGEE_LOGIN = "bungeeLogin";
	public static final String CHANNEL_BUNGEE_LOGOUT = "bungeeLogout";

	private static final String FRIENDS_PREFIX = "§d[#]§r ";
	private static final String FRIENDS_PREFIX_GREEN = "§a[#]§r ";
	private static final String FRIENDS_PREFIX_RED = "§c[#]§r ";

	public FriendNotices(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	public static void showRequestToSender(FriendPlayer fPlayer,FriendsRequest request){
		if(fPlayer.getPlayer() == null) return;
		fPlayer.getPlayer().sendMessage(FRIENDS_PREFIX+"§eZadost o pratelstsvi hraci §f"+request.getRecipient().getUser().getName()+"§e odeslana.");
	}

	public static void showRequestToRecipient(FriendPlayer fPlayer,FriendsRequest request){
		System.out.println("Sdfg");
		if(fPlayer.getPlayer() == null) return;
		fPlayer.getPlayer().sendMessage("");
		fPlayer.getPlayer().sendMessage(FRIENDS_PREFIX+"§eZadost o pratelstvi od hrace §f"+request.getSender().getUser().getName()+"§e.");
		TextComponent message = new TextComponent("§7Muzete ji ");
		TextComponent accept = new TextComponent("§7[§a§lPRIJMOUT§7]");
		TextComponent deny = new TextComponent("§7[§c§lODMITNOUT§7]");
		accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/friend accept "+request.getSender().getUser().getName()));
		deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/friend deny "+request.getSender().getUser().getName()));
		accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Klikni pro prijmuti zadosti").create()));
		deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Klikni pro odmitnuti zadosti").create()));
		message.addExtra(accept);
		message.addExtra("§7 nebo ");
		message.addExtra(deny);
		message.addExtra("§7 do "+request.getExpireText()+".");
		fPlayer.getPlayer().spigot().sendMessage(message);
		fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.BLOCK_NOTE_BLOCK_PLING,1f,1f);
	}

	public static void showRequestAcceptToSender(FriendPlayer fPlayer,FriendsRequest request){
		if(fPlayer.getPlayer() == null) return;
		fPlayer.getPlayer().sendMessage(FRIENDS_PREFIX+"§aHrac §f"+request.getRecipient().getUser().getName()+"§a prijal zadost o pratelstvi.");
		fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
	}

	public static void showRequestAcceptToRecipient(FriendPlayer fPlayer,FriendsRequest request){
		if(fPlayer.getPlayer() == null) return;
		fPlayer.getPlayer().sendMessage(FRIENDS_PREFIX+"§aHrac §f"+request.getSender().getUser().getName()+"§a pridan mezi tve pratele.");
		fPlayer.getPlayer().playSound(fPlayer.getPlayer().getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
	}

	public static void showRequestDenyToSender(FriendPlayer fPlayer,FriendsRequest request){
		if(fPlayer.getPlayer() == null) return;
		fPlayer.getPlayer().sendMessage(FRIENDS_PREFIX+"§cHrac §f"+request.getRecipient().getUser().getName()+"§c odmitnul zadost o pratelstvi.");
	}

	public static void showRequestDenyToRecipient(FriendPlayer fPlayer,FriendsRequest request){
		if(fPlayer.getPlayer() == null) return;
		fPlayer.getPlayer().sendMessage(FRIENDS_PREFIX+"§cZadost o pratelstsvi hrace §f"+request.getSender().getUser().getName()+"§c odmitnuta.");
	}

	public static void showRemoveToSender(FriendPlayer fPlayer,FriendPlayer friend){
		if(fPlayer.getPlayer() == null) return;
		fPlayer.getPlayer().sendMessage(FRIENDS_PREFIX+"§7Hrac §f"+friend.getUser().getName()+" §7odebran z pratel.");
	}

	public static void showRemoveToRecipient(FriendPlayer fPlayer,FriendPlayer friend){
		if(fPlayer.getPlayer() == null) return;
		fPlayer.getPlayer().sendMessage(FRIENDS_PREFIX+"§7Hrac §f"+friend.getUser().getName()+" §7si te odebral z pratel.");
	}

	public static void showFriendTeleport(FriendPlayer fPlayer,FriendPlayer friend){
		if(fPlayer.getPlayer() == null || !fPlayer.getSettings().getValue(FriendPlayerSettingsType.TELEPORTS) || friend.getPlayer() == null)
			return;
		fPlayer.getPlayer().sendMessage(FRIENDS_PREFIX+"§f"+friend.getUser().getName()+" §7se k tobe teleportoval.");
	}

	public static void showFriendChat(FriendPlayer fPlayer,FriendPlayer friend,String message){
		if(fPlayer.getPlayer() == null || !fPlayer.getSettings().getValue(FriendPlayerSettingsType.CHATS))
			return;
		fPlayer.getPlayer().sendMessage(FRIENDS_PREFIX+"§f"+friend.getUser().getName()+": §7"+message);
	}

	public static void showToggleFriendChat(FriendPlayer fPlayer){
		if(fPlayer.getPlayer() == null) return;
		fPlayer.getPlayer().sendMessage(FRIENDS_PREFIX+"§7Soukromy chat mezi prateli "+(fPlayer.hasFriendChat() ? "§azapnut" : "§cvypnut")+"§7.");
	}

	private static void showFriendJoin(FriendPlayer fPlayer,FriendPlayer friend){
		if(fPlayer.getPlayer() == null || !fPlayer.getSettings().getValue(FriendPlayerSettingsType.JOINS) || friend.getPlayer() != null)
			return;
		fPlayer.getPlayer().sendMessage(FRIENDS_PREFIX_GREEN+"§f"+friend.getUser().getName()+" §7se pripojil.");
	}

	private static void showFriendQuit(FriendPlayer fPlayer,FriendPlayer friend){
		if(fPlayer.getPlayer() == null || !fPlayer.getSettings().getValue(FriendPlayerSettingsType.QUITS) || friend.getPlayer() != null)
			return;
		fPlayer.getPlayer().sendMessage(FRIENDS_PREFIX_RED+"§f"+friend.getUser().getName()+" §7se odpojil.");
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_FRIEND_REMOVE)){
			FriendPlayer sender = Friends.getFriendPlayer(data.getInt("sender"));
			FriendPlayer recipient = Friends.getFriendPlayer(data.getInt("recipient"));
			sender.reload();
			recipient.reload();
			FriendNotices.showRemoveToSender(sender,recipient);
			FriendNotices.showRemoveToRecipient(recipient,sender);
		} else if(data.getChannel().equalsIgnoreCase(CHANNEL_BUNGEE_LOGIN)){
			FriendPlayer fPlayer = Friends.getFriendPlayer(data.getInt("id"));
			for(FriendPlayer friend : fPlayer.getFriends()){
				FriendNotices.showFriendJoin(friend,fPlayer);
			}
		} else if(data.getChannel().equalsIgnoreCase(CHANNEL_BUNGEE_LOGOUT)){
			FriendPlayer fPlayer = Friends.getFriendPlayer(data.getInt("id"));
			for(FriendPlayer friend : fPlayer.getFriends()){
				FriendNotices.showFriendQuit(friend,fPlayer);
			}
		}
	}
}