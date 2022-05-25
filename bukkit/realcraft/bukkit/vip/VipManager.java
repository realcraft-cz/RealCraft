package realcraft.bukkit.vip;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

import java.util.HashMap;

public class VipManager implements Listener {

    public static final String VIPS = "vips";
    private static final HashMap<User, Vip> vips = new HashMap<>();

    public VipManager() {
        Bukkit.getPluginManager().registerEvents(this, RealCraft.getInstance());
        new VipCommand();
        new VipReminder();
    }

    public static Vip getVip(User user) {
        if (!vips.containsKey(user)) {
            vips.put(user, new Vip(user));
        }

        return vips.get(user);
    }

    public static Vip getVip(Player player) {
        return VipManager.getVip(Users.getUser(player));
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event){
        VipManager.getVip(Users.getUser(event.getPlayer())).load();
    }
}
