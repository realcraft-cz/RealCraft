package realcraft.bukkit.pets;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.pets.exceptions.pet.PetAlreadyExistsException;
import realcraft.bukkit.pets.exceptions.player.PetPlayerNoPetException;
import realcraft.bukkit.pets.exceptions.player.PetPlayerVipException;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;
import realcraft.share.users.UserRank;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PetPlayer {

    private final User user;
    private Player player;
    private Pet pet;

    public PetPlayer(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Player getPlayer() {
        if (player == null || !player.isOnline() || !player.isValid()) {
            player = Users.getPlayer(this.getUser());
        }

        return player;
    }

    public @Nullable Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public boolean hasPermissions() {
        return this.getUser().getRank().isMinimum(UserRank.VIP);
    }

    public void load() {
        if (!this.hasPermissions()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(), new Runnable() {
            @Override
            public void run() {
                ResultSet rs = DB.query("SELECT 1 FROM " + Pet.PETS + " WHERE user_id = ?", PetPlayer.this.getUser().getId());
                if (rs == null) {
                    return;
                }

                try {
                    if (rs.next()) {
                        Bukkit.getScheduler().runTask(RealCraft.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                if (PetPlayer.this.getPet() == null) {
                                    PetPlayer.this.setPet(new Pet(PetPlayer.this));
                                }

                                PetPlayer.this.getPet().load();
                            }
                        });
                    }
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void save() {
        if (this.getPet() != null) {
            this.getPet().save();
        }
    }

    public void createPet() throws PetAlreadyExistsException, PetPlayerVipException {
        if (this.getPet() != null) {
            throw new PetAlreadyExistsException(this.getPet());
        }

        if (!this.hasPermissions()) {
            throw new PetPlayerVipException(this);
        }

        this.setPet(new Pet(this));
        this.save();
    }

    public void deletePet() throws PetPlayerNoPetException {
        if (this.getPet() == null) {
            throw new PetPlayerNoPetException(this);
        }

        this.getPet().delete();
        this.setPet(null);
    }

    public void sendMessage(String message) {
        this.sendMessage(message, false);
    }

    public void sendMessage(String message, boolean prefix) {
        if (prefix) {
            PetsManager.sendMessage(this.getPlayer(), message);
            return;
        }

        this.getPlayer().sendMessage(message);
    }

    @Override
    public int hashCode() {
        return this.getUser().getId();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof PetPlayer toCompare) {
            return (toCompare.getUser().equals(this.getUser()));
        }

        return false;
    }
}
