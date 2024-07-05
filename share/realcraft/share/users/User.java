package realcraft.share.users;

import realcraft.bungee.users.auth.UsersAuthentication.UserLoginAttempts;
import realcraft.share.ServerType;
import realcraft.share.database.DB;
import realcraft.share.skins.Skin;
import realcraft.share.skins.SkinUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class User {

	private int id;
	private UUID uuid;

	private String name;
	private String address;
	private String password;

	private UserRank rank;
	private boolean premium;
	private long premiumAttempt;
	private boolean countryException;

	private String skinName;
	private Skin skin;
	private long lastSkinned;
	private String avatar;

	private boolean logged;
	private boolean registered;
	private long lastLogged;
	private long firstLogged;
	private UserLoginAttempts loginAttempts;
	private ServerType server;

	private int ping;

	private int coins;
	private int coinsboost;

	private int money;

	private long lastPlayTime;

	public User(int id){
		this(id,null);
	}

	public User(int id,UUID uuid){
		this.id = id;
		this.uuid = uuid;
		this.reload();
	}

	public int getId(){
		return id;
	}

	public UUID getUniqueId(){
		return uuid;
	}

	public String getName(){
		return name;
	}

	public UserRank getRank(){
		return rank;
	}

	public boolean isPremium(){
		return premium;
	}

	public void setPremium(boolean premium) {
		this.premium = premium;
		DB.update("UPDATE " + Users.USERS + " SET user_premium = '" + (premium ? 1 : 0) + "' WHERE user_id = '" + this.getId() + "'");
	}

	public boolean hasPremiumAttempt() {
		return (premiumAttempt > (System.currentTimeMillis() / 1000));
	}

	public void setPremiumAttempt(long premiumAttempt) {
		this.premiumAttempt = premiumAttempt;
	}

	public boolean isCountryException(){
		return countryException;
	}

	public Skin getSkin(){
		if(skin == null){
			skin = new Skin("steve","","","");
			if(skinName != null){
				skin = SkinUtil.getSkin(skinName);
				if(skin == null) skin = new Skin("steve","","","");
			}
		}
		return skin;
	}

	public void setSkin(String skinName){
		this.skinName = skinName;
		lastSkinned = System.currentTimeMillis()/1000;
		DB.update("UPDATE "+Users.USERS+" SET user_skin = '"+skinName+"',user_last_skinned = '"+lastSkinned+"' WHERE user_id = '"+this.getId()+"'");
	}

	public String getAvatar(){
		return avatar;
	}

	public long getLastSkinned(){
		return lastSkinned;
	}

	public String getAddress(){
		return address;
	}

	public void setAddress(String address){
		this.address = address;
	}

	public String getPassword(){
		return password;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public boolean isLogged(){
		return logged;
	}

	public void setLogged(boolean logged){
		this.logged = logged;
	}

	public boolean isRegistered(){
		return registered;
	}

	public void setRegistered(boolean registered){
		this.registered = registered;
	}

	public long getLastLogged(){
		return lastLogged;
	}

	public void setLastLogged(long lastLogged){
		this.lastLogged = lastLogged;
	}

	public long getFirstLogged(){
		return firstLogged;
	}

	public void setFirstLogged(long firstLogged){
		this.firstLogged = firstLogged;
	}

	public ServerType getServer(){
		return server;
	}

	public UserLoginAttempts getLoginAttempts(){
		if(loginAttempts == null) loginAttempts = new UserLoginAttempts();
		return loginAttempts;
	}

	public int getPing(){
		return ping;
	}

	public void setPing(int ping){
		this.ping = ping;
	}

	public int getCoins(){
		return coins;
	}

	public boolean hasCoinsBoost(){
		return (coinsboost > System.currentTimeMillis()/1000);
	}

	public int giveCoins(int coins){
		return this.giveCoins(coins,true);
	}

	public int giveCoins(int coins,boolean boost){
		coins = (this.hasCoinsBoost() && boost && coins > 0 ? coins*2 : coins);
		this.coins += coins;
		if(this.coins < 0) this.coins = 0;
		DB.update("UPDATE "+Users.USERS+" SET user_coins = '"+this.getCoins()+"' WHERE user_id = '"+this.getId()+"'");
		return coins;
	}

	public void addMoney(int money){
		this.money += money;
		if(this.money < 0) this.money = 0;
		DB.update("UPDATE "+Users.USERS+" SET user_money = '"+this.getMoney()+"' WHERE user_id = '"+this.getId()+"'");
	}

	public int getMoney(){
		return money;
	}

	public void reload(){
		skin = null;
		ResultSet rs = DB.query("SELECT user_uuid,user_name,user_rank,user_password,user_ip,user_premium,user_skin,user_avatar,user_logged,user_registered,user_firstlogin,user_lastlogin,user_last_skinned,user_server,user_coins,user_coinsboost,user_money,user_countryexception FROM "+Users.USERS+" WHERE user_id = '"+this.getId()+"'");
		try {
			if(rs.next()){
				uuid = UUID.fromString(rs.getString("user_uuid"));
				name = rs.getString("user_name");
				rank = UserRank.fromId(rs.getInt("user_rank"));
				password = rs.getString("user_password");
				address = rs.getString("user_ip");
				premium = rs.getBoolean("user_premium");
				countryException = rs.getBoolean("user_countryexception");
				skinName = rs.getString("user_skin");
				avatar = rs.getString("user_avatar");
				logged = rs.getBoolean("user_logged");
				registered = rs.getBoolean("user_registered");
				firstLogged = rs.getLong("user_firstlogin");
				lastLogged = rs.getLong("user_lastlogin");
				lastSkinned = rs.getLong("user_last_skinned");
				coins = rs.getInt("user_coins");
				coinsboost = rs.getInt("user_coinsboost");
				money = rs.getInt("user_money");
				server = ServerType.getByName(rs.getString("user_server"));
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public void connect(){
		this.setLogged(false);
	}

	public void login(){
		this.setLogged(true);
		this.setLastLogged(System.currentTimeMillis()/1000);
		lastPlayTime = System.currentTimeMillis();
		DB.update("UPDATE "+Users.USERS+" SET user_logged = ?,user_registered = ?,user_lastlogin = ?,user_firstlogin = ?,user_ip = ?,user_password = ?,user_server = ? WHERE user_id = '"+this.getId()+"'",
			this.isLogged(),
			this.isRegistered(),
			this.getLastLogged(),
			this.getFirstLogged(),
			this.getAddress(),
			this.getPassword(),
			ServerType.LOBBY.toString()
		);
	}

	public void logout(){
		this.setLogged(false);
		this.setLastLogged(System.currentTimeMillis()/1000);
		DB.update("UPDATE "+Users.USERS+" SET user_logged = ?,user_lastlogin = ? WHERE user_id = '"+this.getId()+"'",
			this.isLogged(),
			this.getLastLogged()
		);
	}

	public void setServer(ServerType server){
		this.server = server;
		DB.update("UPDATE "+Users.USERS+" SET user_server = '"+server.toString()+"' WHERE user_id = '"+this.getId()+"'");
	}

	public void updatePlayTime(){
		if(lastPlayTime != 0){
			int playtime = (int)((System.currentTimeMillis()-lastPlayTime)/1000);
			lastPlayTime = System.currentTimeMillis();
			DB.update("UPDATE "+Users.USERS+" SET user_playtime = user_playtime + "+playtime+" WHERE user_id = '"+this.getId()+"'");
		}
	}

	@Override
	public int hashCode(){
		return id;
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof User){
			User toCompare = (User) object;
			return (toCompare.getId() == this.getId());
		}
		return false;
	}
}