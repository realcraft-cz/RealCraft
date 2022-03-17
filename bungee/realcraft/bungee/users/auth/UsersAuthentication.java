package realcraft.bungee.users.auth;

import com.google.common.base.Charsets;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import realcraft.bungee.RealCraftBungee;
import realcraft.bungee.sockets.SocketData;
import realcraft.bungee.sockets.SocketManager;
import realcraft.bungee.users.Users;
import realcraft.share.users.User;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsersAuthentication implements Listener {

	private static final int MIN_NAME_LENGTH = 3;
	private static final Pattern NICKNAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]*");

	public UsersAuthentication(){
		BungeeCord.getInstance().getPluginManager().registerListener(RealCraftBungee.getInstance(),this);
	}

	@EventHandler
	public void PreLoginEvent(PreLoginEvent event){
		if(event.getConnection().getName().length() < MIN_NAME_LENGTH){
			event.setCancelReason(TextComponent.fromLegacyText("§cDelka nicku musi byt alespon 3 znaky!"));
			event.setCancelled(true);
			return;
		}
		Matcher match = NICKNAME_PATTERN.matcher(event.getConnection().getName());
		if(!match.matches()){
			event.setCancelReason(TextComponent.fromLegacyText("§cNespravny nick, povolene znaky jsou [a-zA-Z0-9_]"));
			event.setCancelled(true);
			return;
		}
		event.getConnection().setUniqueId(UUID.nameUUIDFromBytes(("OfflinePlayer:"+event.getConnection().getName()).getBytes(Charsets.UTF_8)));
		User user = Users.getUser(event.getConnection().getName());
		if(user == null){
			/*if(GeoLiteAPI.isCountryBlocked(event.getConnection().getAddress())){
				event.setCancelReason(TextComponent.fromLegacyText("§cZeme, ze ktere se pripojujes, je zablokovana!"));
				event.setCancelled(true);
				return;
			}*/
			Users.createUser(
				event.getConnection().getName(),
				event.getConnection().getUniqueId(),
				event.getConnection().getAddress().getAddress().getHostAddress().replace("/","")
			);
		} else {
			if(!user.getUniqueId().equals(event.getConnection().getUniqueId())){
				event.setCancelReason(TextComponent.fromLegacyText("§cJiz je registrovany hrac s nickem "+user.getName()+""));
				event.setCancelled(true);
				return;
			}
			user.reload();
			user.setAddress(event.getConnection().getAddress().getAddress().getHostAddress().replace("/",""));
			if(user.isPremium()) event.getConnection().setOnlineMode(true);
			else {
				/*if(!user.isCountryException() && GeoLiteAPI.isCountryBlocked(event.getConnection().getAddress())){
					event.setCancelReason(TextComponent.fromLegacyText("§cZeme, ze ktere se pripojujes, je zablokovana!"));
					event.setCancelled(true);
				}*/
			}
		}
	}

	@EventHandler
	public void PostLoginEvent(PostLoginEvent event){
		ProxiedPlayer player = event.getPlayer();
		User user = Users.getUser(player);
		if(user.isRegistered()){
			if(!user.isPremium()) this.showPlayerLoginMessage(player);
			else {
				this.loginUser(user);
				player.sendMessage(TextComponent.fromLegacyText("§aPrihlaseni bylo uspesne."));
			}
		}
		else this.showPlayerRegisterMessage(player);
	}

	@EventHandler
	public void ChatEvent(ChatEvent event){
		ProxiedPlayer player = (ProxiedPlayer) event.getSender();
		User user = Users.getUser(player);

		String command = event.getMessage().substring(1);
		if(command.toLowerCase().startsWith("login")){
			event.setCancelled(true);
			String[] tmpArgs = command.split(" ");
			String[] args = new String[tmpArgs.length-1];
			System.arraycopy(tmpArgs,1,args,0,tmpArgs.length-1);

			if(user.isLogged()){
				player.sendMessage(TextComponent.fromLegacyText("§cJiz jsi prihlaseny!"));
				return;
			}
			if(!user.isRegistered()){
				this.showPlayerRegisterMessage(player);
				return;
			}
			if(user.getLoginAttempts().hasTooManyAttempts()){
				player.sendMessage(TextComponent.fromLegacyText("§cPrilis mnoho pokusu! Opakovat muzes za "+user.getLoginAttempts().getRemainingSeconds()+" s."));
				return;
			}
			if(args.length == 0){
				this.showPlayerLoginMessage(player);
				return;
			}
			String password = args[0];
			if(!this.passwordVerify(user,password)){
				user.getLoginAttempts().addAttempt();
				player.sendMessage(TextComponent.fromLegacyText("§cSpatne zadane heslo!"));
				return;
			}
			this.loginUser(user,password);
			player.sendMessage(TextComponent.fromLegacyText("§aPrihlaseni bylo uspesne."));
		}
		else if(command.toLowerCase().startsWith("register")){
			event.setCancelled(true);
			String[] tmpArgs = command.split(" ");
			String[] args = new String[tmpArgs.length-1];
			System.arraycopy(tmpArgs,1,args,0,tmpArgs.length-1);

			if(user.isLogged()){
				player.sendMessage(TextComponent.fromLegacyText("§cJiz jsi prihlaseny!"));
				return;
			}
			if(user.isRegistered()){
				this.showPlayerLoginMessage(player);
				return;
			}
			if(args.length < 2){
				this.showPlayerRegisterMessage(player);
				return;
			}
			String password1 = args[0];
			String password2 = args[1];
			if(!password1.equals(password2)){
				player.sendMessage(TextComponent.fromLegacyText("§cZadana hesla se neshoduji, zkus to znovu!"));
				return;
			}
			this.registerUser(user,password1);
			player.sendMessage(TextComponent.fromLegacyText("§aPrihlaseni bylo uspesne."));
		} else {
			if(!user.isLogged()){
				event.setCancelled(true);
				if(user.isRegistered()) this.showPlayerLoginMessage(player);
				else this.showPlayerRegisterMessage(player);
			}
		}
	}

	private void showPlayerLoginMessage(ProxiedPlayer player){
		player.sendMessage(TextComponent.fromLegacyText("§2Prihlas se pomoci §e§l/login <heslo>"));
	}

	private void showPlayerRegisterMessage(ProxiedPlayer player){
		player.sendMessage(TextComponent.fromLegacyText("§2Registruj se pomoci §e§l/register <heslo> <heslo znovu>"));
	}

	private boolean passwordVerify(User user,String password){
		if(user.getPassword().length() < 1 || password.length() < 1) return false;
		return ((user.getPassword().length() == 60 && BCrypt.checkPassword(password,user.getPassword())) || SHA256.comparePassword(user.getPassword(),password));
	}

	private void loginUser(User user){
		this.loginUser(user,null);
	}

	private void loginUser(User user,String password){
		if(password != null && user.getPassword().length() > 60) user.setPassword(BCrypt.hashPassword(password));
		user.login();
		SocketData data = new SocketData(Users.CHANNEL_BUNGEE_LOGIN);
        data.setInt("id",user.getId());
        SocketManager.sendToAll(data);
	}

	private void registerUser(User user,String password){
		user.setRegistered(true);
		user.setFirstLogged(System.currentTimeMillis()/1000);
		user.setPassword(BCrypt.hashPassword(password));
		this.loginUser(user,password);
	}

	public static class UserLoginAttempts {

		private int attempts;
		private long expireTime;

		public boolean hasTooManyAttempts(){
			return (expireTime > System.currentTimeMillis());
		}

		public int getRemainingSeconds(){
			return (int)(expireTime-System.currentTimeMillis())/1000;
		}

		public void addAttempt(){
			attempts ++;
			if(attempts >= 3){
				attempts = 0;
				expireTime = System.currentTimeMillis()+(20*1000);
			}
		}
	}
}