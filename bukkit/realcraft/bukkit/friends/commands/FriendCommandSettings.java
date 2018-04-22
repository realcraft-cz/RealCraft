package realcraft.bukkit.friends.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import realcraft.bukkit.friends.FriendPlayer;
import realcraft.bukkit.friends.FriendPlayerSettings;
import realcraft.bukkit.friends.FriendPlayerSettings.FriendPlayerSettingsType;
import realcraft.bukkit.friends.Friends;

public class FriendCommandSettings extends FriendCommand {

	private static final String CHECKBOX_CHAR = "\u2589";

	public FriendCommandSettings(){
		super("settings","set");
	}

	@Override
	public void perform(Player player,String[] args){
		if(args.length == 0){
			this.showSettingsPage(player);
			return;
		}
		FriendPlayer fPlayer = Friends.getFriendPlayer(player);
		FriendPlayerSettingsType type = FriendPlayerSettingsType.fromName(args[0]);
		if(type != null){
			fPlayer.getSettings().setValue(type,!fPlayer.getSettings().getValue(type));
		}
		this.showSettingsPage(player);
	}

	private void showSettingsPage(Player player){
		FriendPlayer fPlayer = Friends.getFriendPlayer(player);
		player.sendMessage("§7§m"+StringUtils.repeat(" ",10)+"§r §a§lFriends > Nastaveni §7§m"+StringUtils.repeat(" ",47-"Friends > Nastaveni".length()));
		for(FriendPlayerSettingsType type : FriendPlayerSettingsType.values()){
			TextComponent component = new TextComponent(" "+this.getCheckboxSettings(fPlayer.getSettings(),type)+"§7 "+type.getName());
			component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/friend settings "+type.toString().toLowerCase()));
			if(fPlayer.getSettings().getValue(type)) component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§cKlikni pro vypnuti").create()));
			else component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§aKlikni pro zapnuti").create()));
			player.spigot().sendMessage(component);
		}
		player.sendMessage("§7§m"+StringUtils.repeat(" ",62));
	}

	private String getCheckboxSettings(FriendPlayerSettings settings,FriendPlayerSettingsType type){
		return (settings.getValue(type) ? "§a" : "§c")+CHECKBOX_CHAR;
	}
}