package realcraft.bukkit.cosmetics;

import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.users.Users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CosmeticTransactions {

	private static final String COSMETICS_TRANSACTIONS = "cosmetics_transactions";
	private static ArrayList<CosmeticTransaction> transactions = new ArrayList<>();

	public static void init(int limit){
		transactions.clear();
		ResultSet rs = DB.query("SELECT * FROM "+COSMETICS_TRANSACTIONS+" ORDER BY transaction_created DESC LIMIT "+limit);
		try {
			while(rs.next()){
				CosmeticPlayer cPlayer = Cosmetics.getCosmeticPlayer(Users.getUser(rs.getInt("user_id")));
				CosmeticType type = CosmeticType.fromId(rs.getInt("cosmetic_id"));
				int amount = rs.getInt("transaction_amount");
				int created = rs.getInt("transaction_created");
				transactions.add(new CosmeticTransaction(cPlayer,type,created,amount));
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public static ArrayList<CosmeticTransaction> getLastTransactions(){
		return transactions;
	}

	private static void sortTransations(){
		Collections.sort(transactions,new Comparator<CosmeticTransaction>(){
			@Override
			public int compare(CosmeticTransaction transaction1,CosmeticTransaction transaction2){
				int compare = Long.compare(transaction1.getCreated(),transaction2.getCreated());
				if(compare > 0) return -1;
				else if(compare < 0) return 1;
				return 0;
			}
		});
	}

	public static void addTransaction(CosmeticPlayer cPlayer,CosmeticType type,int amount){
		CosmeticTransaction transaction = new CosmeticTransaction(cPlayer,type,(int)(System.currentTimeMillis()/1000),amount);
		transactions.add(transaction);
		CosmeticTransactions.sortTransations();
		DB.update("INSERT INTO "+COSMETICS_TRANSACTIONS+" (user_id,cosmetic_id,transaction_amount,transaction_created) VALUES('"+transaction.getCPlayer().getUser().getId()+"','"+transaction.getType().getId()+"','"+transaction.getAmount()+"','"+transaction.getCreated()+"')");
	}

	public static class CosmeticTransaction {

		private CosmeticPlayer cPlayer;
		private CosmeticType type;
		private int created;
		private int amount;

		public CosmeticTransaction(CosmeticPlayer cPlayer,CosmeticType type,int created,int amount){
			this.cPlayer = cPlayer;
			this.type = type;
			this.created = created;
			this.amount = amount;
		}

		public CosmeticPlayer getCPlayer(){
			return cPlayer;
		}

		public CosmeticType getType(){
			return type;
		}

		public int getCreated(){
			return created;
		}

		public int getAmount(){
			return amount;
		}
	}
}