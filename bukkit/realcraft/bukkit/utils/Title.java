package realcraft.bukkit.utils;


import net.minecraft.network.chat.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import realcraft.bukkit.RealCraft;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Title {

	public static void showTitle(Player player, String title, double fadeIn, double stay, double fadeOut) {
        player.sendTitle(title, null, (int) Math.round(fadeIn * 20), (int) Math.round(stay * 20), (int) Math.round(fadeOut * 20));

        /*PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.TITLE);

        packet.getTitleActions().writeSafely(0, EnumWrappers.TitleAction.TITLE);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(title));

		PacketPlayOutTitle timesPacket = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, (int) Math.round(fadeIn*20), (int) Math.round(stay*20), (int) Math.round(fadeOut*20));

		((CraftPlayer) player).getHandle().b.a(timesPacket);
		((CraftPlayer) player).getHandle().b.a(titlePacket);*/
	}

	public static void showSubTitle(Player player, String subTitle, double fadeIn, double stay, double fadeOut) {
        player.sendTitle(null, subTitle, (int) Math.round(fadeIn * 20), (int) Math.round(stay * 20), (int) Math.round(fadeOut * 20));

		/*IChatBaseComponent subTitleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subTitle + "\"}");

		PacketPlayOutTitle subTitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subTitleComponent);
		PacketPlayOutTitle timesPacket = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, (int) Math.round(fadeIn*20.0), (int) Math.round(stay*20.0), (int) Math.round(fadeOut*20.0));

		((CraftPlayer) player).getHandle().b.a(timesPacket);
		((CraftPlayer) player).getHandle().b.a(subTitlePacket);*/
	}

	public static void showActionTitle(Player player, String message){
		Title.sendActionBar(player,message);
	}

	public static void showActionTitle(Player player, String message, int duration){
		Title.sendActionBar(player,message,duration);
	}

	public static void sendActionBar(Player player, String message){

		String nmsver = Bukkit.getServer().getClass().getPackage().getName();
        nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);

        try {
            Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
            Object p = c1.cast(player);
            Object ppoc;
            Class<?> c4 = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
            Class<?> c5 = Class.forName("net.minecraft.server." + nmsver + ".Packet");
            Class<?> c2 = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
            Class<?> c3 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
            Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
            ppoc = c4.getConstructor(new Class<?>[]{c3, ChatMessageType.class}).newInstance(o, ChatMessageType.c);
            Method m1 = c1.getDeclaredMethod("getHandle");
            Object h = m1.invoke(p);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
            m5.invoke(pc, ppoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendActionBar(final Player player, final String message, int duration) {
        sendActionBar(player, message);

        if (duration >= 0 && duration < 60){
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(player, "");
                }
            }.runTaskLater(RealCraft.getInstance(),duration+1);
        }

        while (duration > 40) {
            duration -= 40;
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(player, message);
                }
            }.runTaskLater(RealCraft.getInstance(), duration);
        }
    }
}