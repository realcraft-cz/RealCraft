package realcraft.bukkit.test;

import com.google.common.base.Charsets;
import realcraft.bukkit.utils.RandomUtil;

import java.util.LinkedList;
import java.util.UUID;

public class _Test {

	private final static LinkedList<Double> history = new LinkedList<>();

    public static void main(String[] args) {
		System.out.println(UUID.nameUUIDFromBytes(("OfflinePlayer:"+"FluBoo").getBytes(Charsets.UTF_8)));

		String message = "hadfg aaaaaaaaaaaaaaaaaaaaaaa lopata";
		var maxSameChars = 4;
		message = message.replaceAll("(.)\\1{"+maxSameChars+",}","/F");
		System.out.println(message);

		for (int i=0;i<20;i++) {
			run();
		}
    }

	public static void run() {
		history.add(RandomUtil.getRandomDouble(10, 15));

		if (history.size() < 20) {
			return;
		}

		history.remove();

		double limitTps = 16;

		System.out.println("avarage " + _getAverageTps());
	}

	protected static double _getAverageTps() {
		double avg = 0;
		for (final Double f : history) {
			if (f != null) {
				avg += f;
			}
		}
		return avg / history.size();
	}
}
