package realcraft.bukkit.quiz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.coins.Coins;
import realcraft.bukkit.lobby.LobbyMenu;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketDataEvent;
import realcraft.bukkit.sockets.SocketManager;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.RandomUtil;
import realcraft.bukkit.utils.StringUtil;
import realcraft.share.ServerType;
import realcraft.share.database.DB;

public class Quiz implements Listener, Runnable {

	RealCraft plugin;
	private boolean master = false;
	private static final String CHANNEL_QUESTION = "quizQuestion";
	private static final String CHANNEL_ANSWER = "quizAnswer";
	private static final String CHANNEL_WINNER = "quizWinner";
	private static final String QUIZ_QUESTIONS = "quiz_questions";
	private static final String QUIZ_ANSWERS = "quiz_answers";
	private static final int ANSWER_LIMIT = 10;
	private static final int REWARD = 100;
	private static final String CHAR = "\u2588";
	private static final int[] CHAR_PATTERN = new int[]{
		0,0,0,0,0,0,0,
		0,0,1,1,1,0,0,
		0,1,0,0,0,1,0,
		0,0,0,0,0,1,0,
		0,0,0,0,1,0,0,
		0,0,0,1,0,0,0,
		0,0,0,0,0,0,0,
		0,0,0,1,0,0,0,
		0,0,0,0,0,0,0,
	};
	private ArrayList<QuizQuestion> questions = new ArrayList<QuizQuestion>();
	private HashMap<Player,Long> lastPlayerAnswers = new HashMap<Player,Long>();

	public Quiz(RealCraft realcraft){
		if(RealCraft.getInstance().serverName.equalsIgnoreCase("lobby")) master = true;
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		if(master) plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,1800*20,1800*20);
		this.loadQuestions();
	}

	public void onReload(){
	}

	@Override
	public void run(){
		if(master) this.runRandomQuestion();
	}

	public void loadQuestions(){
		ResultSet rs = DB.query("SELECT quiz_id,quiz_question,quiz_answers FROM "+QUIZ_QUESTIONS);
		try {
			questions = new ArrayList<QuizQuestion>();
			while(rs.next()){
				int id = rs.getInt("quiz_id");
				String question = rs.getString("quiz_question");
				String answers = rs.getString("quiz_answers");
				questions.add(new QuizQuestion(id,question,answers));
			}
			rs.close();
		} catch (SQLException e){
		}
	}

	public void runRandomQuestion(){
		if(LobbyMenu.getAllPlayersCount() >= 10 || RealCraft.isTestServer()){
			QuizQuestion question = questions.get(RandomUtil.getRandomInteger(0,questions.size()-1));
			question.run();
			SocketData data = new SocketData(CHANNEL_QUESTION);
			data.setInt("id",question.getId());
			SocketManager.sendToAll(data);
		}
	}

	public boolean hasPlayerAnswered(Player player){
		return (lastPlayerAnswers.get(player) != null && lastPlayerAnswers.get(player)+ANSWER_LIMIT*1000 > System.currentTimeMillis());
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		String command = event.getMessage().substring(1);
		if(command.startsWith("quiz")){
			String[] args = event.getMessage().split(" ");
			if(args.length == 3){
				try {
					int id = Integer.valueOf(args[1]);
					String answer = args[2];
					for(QuizQuestion question : questions){
						if(question.getId() == id){
							question.answer(event.getPlayer(),answer);
							break;
						}
					}
					event.setCancelled(true);
				} catch (Exception e){
				}
			}
		}
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(CHANNEL_QUESTION)){
			for(QuizQuestion question : questions){
				if(question.getId() == data.getInt("id")){
					question.run();
					break;
				}
			}
		}
		else if(data.getChannel().equalsIgnoreCase(CHANNEL_ANSWER)){
			for(QuizQuestion question : questions){
				if(question.getId() == data.getInt("id")){
					question.setAnswered(data.getString("name"),data.getBoolean("boost"));
					break;
				}
			}
		}
		else if(data.getChannel().equalsIgnoreCase(CHANNEL_WINNER)){
			for(QuizQuestion question : questions){
				if(question.getId() == data.getInt("id")){
					question.showWinner(data.getString("name"),data.getInt("reward"));
					break;
				}
			}
		}
	}

	private class QuizQuestion {

		private int id;
		private String question;
		private QuizAnswer[] answers;
		private boolean answered = false;

		public QuizQuestion(int id,String question,String answers){
			this.id = id;
			this.question = question;
			JsonElement element = new JsonParser().parse(answers);
			if(element.isJsonArray()){
				JsonArray array = element.getAsJsonArray();
				this.answers = new QuizAnswer[array.size()];
				for(int i=0;i<array.size();i++){
					this.answers[i] = new QuizAnswer(array.get(i).getAsString(),(i == 0));
				}
			}
		}

		public int getId(){
			return id;
		}

		public String getQuestion(){
			return question;
		}

		public QuizAnswer[] getAnswers(){
			return answers;
		}

		public boolean isAnswered(){
			return answered;
		}

		public void run(){
			answered = false;
			QuizAnswer[] answers = new QuizAnswer[this.getAnswers().length];
			List<QuizAnswer> list = Arrays.asList(this.getAnswers());
			Collections.shuffle(list);
			list.toArray(answers);
			TextComponent[] lines = new TextComponent[8];
			int index = 0;
			lines[index++] = new TextComponent("");
			if(answers.length == 2) index += 1;
			if(answers.length == 3) index += 1;
			lines[index ++] = new TextComponent(this.getQuestion());
			lines[index ++] = new TextComponent("");
			int number = 1;
			for(QuizAnswer answer : answers){
				TextComponent component = new TextComponent("§7"+(number)+".§r "+answer.getText());
				component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/quiz "+this.getId()+" "+answer.getId()));
				component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Zvolit "+number+". moznost").create()));
				lines[index ++] = new TextComponent(component);
				number ++;
			}
			for(Player player : Bukkit.getOnlinePlayers()){
				this.sendToPlayer(player,lines);
			}
		}

		public void answer(Player player,String id){
			if(this.isAnswered() || Quiz.this.hasPlayerAnswered(player)) return;
			lastPlayerAnswers.put(player,System.currentTimeMillis());
			for(QuizAnswer answer : this.getAnswers()){
				if(answer.getId().equalsIgnoreCase(id)){
					if(answer.isCorrect()){
						QuizQuestion.this.sendAnswered(player.getName(),Users.getUser(player).hasCoinsBoost());
					} else {
						player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
					}
				}
			}
		}

		public void setAnswered(String name,boolean boost){
			if(this.isAnswered()) return;
			this.answered = true;
			this.sendWinner(name,(boost ? REWARD*2 : REWARD));
		}

		public void showWinner(String name,int reward){
			this.answered = true;
			Bukkit.broadcastMessage("§3[Quiz] §6"+name+"§f odpovedel nejrychleji a ziskava §a+"+reward+" coins");
			Player player = Bukkit.getPlayer(name);
			if(player != null){
				Users.getUser(player).giveCoins(reward,false);
				player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
				Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
					public void run(){
						Coins.runCoinsEffect(player,"§3Quiz",reward,true);
					}
				},20);
				Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(),new Runnable(){
					@Override
					public void run(){
						DB.update("INSERT INTO "+QUIZ_ANSWERS+" (quiz_id,user_id,answer_created) VALUES('"+QuizQuestion.this.getId()+"','"+Users.getUser(player).getId()+"','"+(System.currentTimeMillis()/1000)+"')");
					}
				});
			}
		}

		private void sendAnswered(String name,boolean boost){
			SocketData data = new SocketData(CHANNEL_ANSWER);
			data.setInt("id",this.getId());
			data.setString("name",name);
			data.setBoolean("boost",boost);
			SocketManager.send(ServerType.LOBBY,data);
		}

		private void sendWinner(String name,int reward){
			SocketData data = new SocketData(CHANNEL_WINNER);
			data.setInt("id",this.getId());
			data.setString("name",name);
			data.setInt("reward",reward);
			SocketManager.sendToAll(data,true);
		}

		private void sendToPlayer(Player player,TextComponent[] lines){
			player.sendMessage("");
			int currentLine = 0;
			String line = "";
			for(int i=0;i<CHAR_PATTERN.length;i++){
				line += ((CHAR_PATTERN[i] == 1 ? ChatColor.DARK_AQUA : ChatColor.WHITE)+""+ChatColor.BOLD+CHAR);
				if((i+1)%7 == 0){
					TextComponent component = new TextComponent(line+ChatColor.RESET+" "+ChatColor.RESET+" ");
					if(currentLine < lines.length){
						if(lines[currentLine] != null) component.addExtra(lines[currentLine]);
						currentLine ++;
					}
					player.spigot().sendMessage(component);
					line = "";
				}
			}
		}

		private class QuizAnswer {

			private String id;
			private String text;
			private boolean correct;

			public QuizAnswer(String text,boolean correct){
				this.id = StringUtil.getRandomChars(8).toLowerCase();
				this.text = text;
				this.correct = correct;
			}

			public String getId(){
				return id;
			}

			public String getText(){
				return text;
			}

			public boolean isCorrect(){
				return correct;
			}
		}
	}
}