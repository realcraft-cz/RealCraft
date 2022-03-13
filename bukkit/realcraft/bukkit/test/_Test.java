package realcraft.bukkit.test;

public class _Test {

    public static void main(String[] args) {
		String message = "bagr aaaaaaaaaaaaaaaaaaaaaaa lopata";
		var maxSameChars = 4;
		message = message.replaceAll("(.)\\1{"+maxSameChars+",}","/");
		System.out.println(message);
    }
}
