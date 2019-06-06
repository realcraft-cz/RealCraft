package realcraft.bukkit.test;

import eu.endercentral.crazy_advancements.Advancement;
import eu.endercentral.crazy_advancements.AdvancementDisplay;
import eu.endercentral.crazy_advancements.AdvancementVisibility;
import eu.endercentral.crazy_advancements.NameKey;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import realcraft.bukkit.others.AbstractCommand;

public class AdvancementTest extends AbstractCommand {

	//https://www.spigotmc.org/threads/advancementapi-create-1-12-advancements.240462/

	public AdvancementTest(){
		super("adv");
	}

	@Override
	public void perform(Player player,String[] args){
		AdvancementDisplay display = new AdvancementDisplay(Material.DIAMOND_SWORD,"§a§lTitle","§fLorem ipsum §ldolor§f sit amet",AdvancementDisplay.AdvancementFrame.TASK,true,false,AdvancementVisibility.HIDDEN);
		Advancement advancement = new Advancement(null,new NameKey("custom","test"),display);
		advancement.displayToast(player);
		player.sendMessage("Sending advancement");
	}
}