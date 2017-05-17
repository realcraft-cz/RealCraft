package com.hats;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cosmetics.Cosmetic.CosmeticCategory;
import com.utils.ItemFactory;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Hat {
	public enum HatType {
		ASTRONAUT("M2U4YWFkNjczMTU3YzkyMzE3YTg4YjFmODZmNTI3MWYxY2Q3Mzk3ZDdmYzhlYzMyODFmNzMzZjc1MTYzNCJ9fX0", "Astronaut", "&7&oHouston, we have got a problem."),
	    SCARED("NjM2ZTI2YzQ0NjU5ZTgxNDhlZDU4YWE3OWU0ZDYwZGI1OTVmNDI2NDQyMTE2ZjgxYjU0MTVjMjQ0NmVkOCJ9fX0", "Scared", "&7&oOh gawd, that scared me!"),
	    ANGEL("M2UxZGViYzczMjMxZjhlZDRiNjlkNWMzYWMxYjFmMThmMzY1NmE4OTg4ZTIzZjJlMWJkYmM0ZTg1ZjZkNDZhIn19fQ=", "Angel", "&7&oDid it hurt when you fell from heaven?"),
	    EMBARASSED("ZjcyMGRmOTExYzA1MjM3NzA2NTQwOGRiNzhhMjVjNjc4Zjc5MWViOTQ0YzA2MzkzNWFlODZkYmU1MWM3MWIifX19", "Embarassed", "&7&oI am kinda embarassed by that."),
	    KISSY("NTQ1YmQxOGEyYWFmNDY5ZmFkNzJlNTJjZGU2Y2ZiMDJiZmJhYTViZmVkMmE4MTUxMjc3Zjc3OWViY2RjZWMxIn19fQ=", "Kissy", "&7&oWanna kiss?"),
	    SAD("MTQ5NjhhYzVhZjMxNDY4MjZmYTJiMGQ0ZGQxMTRmZGExOTdmOGIyOGY0NzUwNTUzZjNmODg4MzZhMjFmYWM5In19fQ=", "Sad", "&7&oI am so sad."),
	    COOL("ODY4ZjRjZWY5NDlmMzJlMzNlYzVhZTg0NWY5YzU2OTgzY2JlMTMzNzVhNGRlYzQ2ZTViYmZiN2RjYjYifX19", "Cool", "&7&oI am such a cool guy."),
	    SURPRISED("YmMyYjliOWFlNjIyYmQ2OGFkZmY3MTgwZjgyMDZlYzQ0OTRhYmJmYTEzMGU5NGE1ODRlYzY5MmU4OTg0YWIyIn19fQ=", "Surprised", "&7&oWow, did not expect that!"),
	    DEAD("YjM3MWU0ZTFjZjZhMWEzNmZkYWUyNzEzN2ZkOWI4NzQ4ZTYxNjkyOTk5MjVmOWFmMmJlMzAxZTU0Mjk4YzczIn19fQ=", "Dead", "&7&ogot rekt"),
	    CRYING("MWYxYjg3NWRlNDljNTg3ZTNiNDAyM2NlMjRkNDcyZmYyNzU4M2ExZjA1NGYzN2U3M2ExMTU0YjViNTQ5OCJ9fX0", "Crying", "&7&oi cri evrytiem"),
	    BIGSMILE("NTA1OWQ1OWViNGU1OWMzMWVlY2Y5ZWNlMmY5Y2YzOTM0ZTQ1YzBlYzQ3NmZjODZiZmFlZjhlYTkxM2VhNzEwIn19fQ=", "BigSmile", "&7&oUh, because I am really happy!"),
	    WINK("ZjRlYTJkNmY5MzlmZWZlZmY1ZDEyMmU2M2RkMjZmYThhNDI3ZGY5MGIyOTI4YmMxZmE4OWE4MjUyYTdlIn19fQ=", "Wink", "&7&oYou know what I mean ;)"),
	    DERP("M2JhYWJlNzI0ZWFlNTljNWQxM2Y0NDJjN2RjNWQyYjFjNmI3MGMyZjgzMzY0YTQ4OGNlNTk3M2FlODBiNGMzIn19fQ=", "Derp", "&7&oDerp Derp Derping all around"),
	    SMILE("NTJlOTgxNjVkZWVmNGVkNjIxOTUzOTIxYzFlZjgxN2RjNjM4YWY3MWMxOTM0YTQyODdiNjlkN2EzMWY2YjgifX19", "Smile", "&7&oUh, because I am happy"),
	    IRON("YmJhODQ1OTE0NWQ4M2ZmYzQ0YWQ1OGMzMjYwZTc0Y2E1YTBmNjM0YzdlZWI1OWExYWQzMjM0ODQ5YzkzM2MifX19", "Iron", "&7&oAs hard as iron!"),
	    GOLD("YjZkMWNlNjk3ZTlkYmFhNGNjZjY0MjUxNmFhYTU5ODEzMzJkYWMxZDMzMWFmZWUyZWUzZGNjODllZmRlZGIifX19", "Gold", "&7&oMy precious!"),
	    DIAMOND("YzAxNDYxOTczNjM0NTI1MTk2ZWNjNzU3NjkzYjE3MWFkYTRlZjI0YWE5MjgzNmY0MmVhMTFiZDc5YzNhNTAyZCJ9fX0", "Diamond", "&7&oThis is really strong!"),
	    PISTON("YWE4NjhjZTkxN2MwOWFmOGU0YzM1MGE1ODA3MDQxZjY1MDliZjJiODlhY2E0NWU1OTFmYmJkN2Q0YjExN2QifX19", "Piston", "&7&oHave you got the redstone?"),
	    COMMANDBLOCK("ODUxNGQyMjViMjYyZDg0N2M3ZTU1N2I0NzQzMjdkY2VmNzU4YzJjNTg4MmU0MWVlNmQ4YzVlOWNkM2JjOTE0In19fQ=", "CommandBlock", "&7&oControl the world with it!"),
	    MUSIC("NGNlZWI3N2Q0ZDI1NzI0YTljYWYyYzdjZGYyZDg4Mzk5YjE0MTdjNmI5ZmY1MjEzNjU5YjY1M2JlNDM3NmUzIn19fQ=", "Music", "&7&oYou are so musical."),
	    SQUID("MDE0MzNiZTI0MjM2NmFmMTI2ZGE0MzRiODczNWRmMWViNWIzY2IyY2VkZTM5MTQ1OTc0ZTljNDgzNjA3YmFjIn19fQ=", "Squid", "&7&oBloop Bloop!"),
	    CHICKEN("MTYzODQ2OWE1OTljZWVmNzIwNzUzNzYwMzI0OGE5YWIxMWZmNTkxZmQzNzhiZWE0NzM1YjM0NmE3ZmFlODkzIn19fQ=", "Chicken", "&7&oBwwaaaaaaaaaaaakkkkk!"),
	    PIG("NjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0", "Pig", "&7&oOink Oink!"),
	    BLAZE("Yjc4ZWYyZTRjZjJjNDFhMmQxNGJmZGU5Y2FmZjEwMjE5ZjViMWJmNWIzNWE0OWViNTFjNjQ2Nzg4MmNiNWYwIn19fQ=", "Blaze", "&7&oWatch out for the fire!"),
	    SHEEP("ZjMxZjljY2M2YjNlMzJlY2YxM2I4YTExYWMyOWNkMzNkMThjOTVmYzczZGI4YTY2YzVkNjU3Y2NiOGJlNzAifX19", "Sheep", "&7&oBaaaa, baa"),
	    GOLEM("ODkwOTFkNzllYTBmNTllZjdlZjk0ZDdiYmE2ZTVmMTdmMmY3ZDQ1NzJjNDRmOTBmNzZjNDgxOWE3MTQifX19", "Golem", "&7&oI am your guard."),
	    ENDERMAN("N2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0", "Enderman", "&7&oNow I am here, now I am there."),
	    MARIO("ZGJhOGQ4ZTUzZDhhNWE3NTc3MGI2MmNjZTczZGI2YmFiNzAxY2MzZGU0YTliNjU0ZDIxM2Q1NGFmOTYxNSJ9fX0", "Mario", "&7&oIt is me! Mario!"),
	    LUIGI("ZmYxNTMzODcxZTQ5ZGRhYjhmMWNhODJlZGIxMTUzYTVlMmVkMzc2NGZkMWNlMDI5YmY4MjlmNGIzY2FhYzMifX19", "Luigi", "&7&oLuigi time!"),
	    BATMAN("ZjI1NmY3MTczNWVmNDU4NTgxYzlkYWNmMzk0MTg1ZWVkOWIzM2NiNmVjNWNkNTk0YTU3MTUzYThiNTY2NTYwIn19fQ=", "Batman", "&7&oI am batman!"),
	    CHEST("NmY2OGQ1MDliNWQxNjY5Yjk3MWRkMWQ0ZGYyZTQ3ZTE5YmNiMWIzM2JmMWE3ZmYxZGRhMjliZmM2ZjllYmYifX19", "Chest", "&7&oOpen, and close"),
	    SKULL("MTFmNTRmZjliYjQyODUxOTEyYWE4N2ExYmRhNWI3Y2Q5ODE0Y2NjY2ZiZTIyNWZkZGE4ODdhZDYxODBkOSJ9fX0", "Skull", "&7&oWho iss headless now?"),
	    GHOST("NjhkMjE4MzY0MDIxOGFiMzMwYWM1NmQyYWFiN2UyOWE5NzkwYTU0NWY2OTE2MTllMzg1NzhlYTRhNjlhZTBiNiJ9fX0", "Ghost", "&7&o2spooky4u"),
	    JACKOLANTERN("MDI4OWQ0YjRjOTYyOTU5MTVmMDY4Yjk5YzI3ZDM5NDI3M2Y5ZjI2NGZjOTY4YzVkNWM0N2RmMmY1YmUyIn19fQ=", "JackOLantern", "&7&oA little pumkin"),
	    SCARYCLOW("ODZkYmMxZGViYzU3NDM4YTVkZTRiYTkxNTE1MTM4MmFiYzNkOGYxMzE4ZTJhMzVlNzhkZmIzMGYwNGJjNDY3In19fQ=", "ScaryClown", "&7&oHope you are not scared of clowns."),
	    SANTA("MmQ2MWNjYmZkY2RmODk0MWFkYWY3NmM2YzBlMDE4MmQyYzhiYmI1ZGMxOGYzNzQ4OTU2NTJiYzY2MWI2ZWQifX19", "Santa", "&7&oOh oh oh! Merry Christmas!"),
	    SNOWMAN("OThlMzM0ZTRiZWUwNDI2NDc1OWE3NjZiYzE5NTVjZmFmM2Y1NjIwMTQyOGZhZmVjOGQ0YmYxYmIzNmFlNiJ9fX0", "Snowman", "&7&oI don't have a skull.. or bones"),
	    PRESENT("ZjBhZmE0ZmZmZDEwODYzZTc2YzY5OGRhMmM5YzllNzk5YmNmOWFiOWFhMzdkODMxMjg4MTczNDIyNWQzY2EifX19", "Present", "&7&oFrom Santa, to you!"),
	    ELF("ODJhYjZjNzljNjNiODMzNGIyYzAzYjZmNzM2YWNmNjFhY2VkNWMyNGYyYmE3MmI3NzdkNzdmMjhlOGMifX19", "Elf", "&7&oI work for Santa!");

		ItemStack itemStack;
		String name;
		String permission;

		private HatType(String url, String name, String defaultDesc){
			this.permission = "cosmetics.hats."+name.toLowerCase();
			this.name = "�e�l"+name;
	        this.itemStack = ItemFactory.createSkull(url,this.name);
	    }

		public void equip(Player player){
			if(!this.isEnabled(player)){
				player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
				return;
			}
			player.closeInventory();
			player.getInventory().setHelmet(itemStack);
		}

		public String toString(){
			return this.name;
		}

		public ItemStack toItemStack(){
			return this.itemStack;
		}

		public String toPermission(){
			return this.permission;
		}

		public boolean isEnabled(Player player){
			PermissionUser user = PermissionsEx.getUser(player);
			String option = user.getOption(this.toPermission());
			if(player.hasPermission("group.iVIP") || player.hasPermission("group.gVIP") || player.hasPermission("group.dVIP")) return true;
			return (option != null ? Boolean.valueOf(option) : false);
		}

		public void setEnabled(Player player,boolean enabled){
			PermissionUser user = PermissionsEx.getUser(player);
			user.setOption(this.toPermission(),""+enabled);
		}

		public String giveReward(Player player){
			this.setEnabled(player,true);
			return CosmeticCategory.HAT.toString()+" > "+this.name();
		}

		public static HatType getRandom(){
			return values()[(int) (Math.random() * values().length)];
		}
	}
}