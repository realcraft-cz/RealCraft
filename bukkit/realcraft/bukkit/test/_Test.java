package realcraft.bukkit.test;

import java.util.Arrays;

public class _Test {

	public static void main(String[]args){
		String[] args2 = "bagr lopata".split(" ");
		args2 = Arrays.copyOfRange(args2,1,args2.length);
		System.out.println(args2.length);
	}
}