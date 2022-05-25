package realcraft.bukkit.vip;

import realcraft.bukkit.utils.DateUtil;
import realcraft.share.database.DB;
import realcraft.share.users.User;

import java.util.concurrent.CompletableFuture;

public class Vip {

    private static final int REMIND_DAYS_BEFORE_EXPIRE = 7;

    private final User user;

    private boolean isActive = false;
    private int activeTo;

    public Vip(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getActiveTo() {
        return activeTo;
    }

    public boolean isAboutToExpire() {
        return this.getActiveTo() - (REMIND_DAYS_BEFORE_EXPIRE * 86400) < DateUtil.getTimestamp();
    }

    public CompletableFuture<Vip> load() {
        CompletableFuture<Vip> future = new CompletableFuture<>();

        isActive = false;
        activeTo = 0;

        CompletableFuture.supplyAsync(() -> {
            return DB.query("SELECT active,active_to FROM " + VipManager.VIPS + " WHERE user_id = ?", getUser().getId());
        }).whenComplete((rs, throwable) -> {
            try {
                if (rs.next()) {
                    isActive = rs.getBoolean("active");
                    activeTo = rs.getInt("active_to");
                }
                rs.close();
                future.complete(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return future;
    }
}
