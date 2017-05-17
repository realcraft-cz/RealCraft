public class Main {
	public static void main(String[]args){
		int[] bagr = new int[]{1,2};
		test(bagr);
		System.out.println(bagr[0]+" "+bagr[1]);
	}

	public static String getTimeFormat(int time){
		int minutes = (int)(Math.floor(((double)time)/1000/60)%60);
		int seconds = (time/1000)%60;
		int hundredths = (time)%1000;
		return (minutes < 10 ? "0" : "")+minutes+":"+(seconds < 10 ? "0" : "")+seconds+"."+(hundredths < 10 ? "00" : (hundredths < 100 ? "0" : ""))+hundredths;
	}
	/*
	 * 100
	 * 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 88, 92, 95, 97, 98, 99, 100
	 *
	 * */

	private static final int[] percentages = new int[]{0,10,20,30,40,50,60,68,75,81,86,90,93,95,96,97,98,99,100};
	public static void coinsEffect(int coins){
		String text = "";
		for(int percent : percentages){
			text += ((coins/100.0)*percent)+", ";
		}
		System.out.println(text);
	}

	public static void test(int[] coords){
		coords[0] = 5;
		coords[1] = 7;
	}
}