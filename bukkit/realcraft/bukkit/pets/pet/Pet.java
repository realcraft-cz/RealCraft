package realcraft.bukkit.pets.pet;

import org.bukkit.Bukkit;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.pets.PetPlayer;
import realcraft.bukkit.pets.events.pet.PetLoadEvent;
import realcraft.bukkit.pets.pet.actions.PetActions;
import realcraft.bukkit.pets.pet.data.PetData;
import realcraft.bukkit.pets.pet.entity.PetEntity;
import realcraft.bukkit.pets.pet.timers.PetTimers;
import realcraft.bukkit.utils.json.JsonData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Pet {

    public static final String PETS = "pets";

    private final PetPlayer petPlayer;
    private final PetData petData;
    private final PetEntity petEntity;
    private final PetActions petActions;
    private final PetTimers petTimers;

    public Pet(PetPlayer petPlayer) {
        this.petPlayer = petPlayer;
        this.petData = new PetData(this);
        this.petEntity = new PetEntity(this);
        this.petActions = new PetActions(this);
        this.petTimers = new PetTimers(this);
    }

    public PetPlayer getPetPlayer() {
        return petPlayer;
    }

    public PetData getPetData() {
        return petData;
    }

    public PetEntity getPetEntity() {
        return petEntity;
    }

    public PetActions getPetActions() {
        return petActions;
    }

    public PetTimers getPetTimers() {
        return petTimers;
    }

    public void load() {
        Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(), new Runnable() {
            @Override
            public void run() {
                ResultSet rs = DB.query("SELECT * FROM " + PETS + " WHERE user_id = ?", Pet.this.getPetPlayer().getUser().getId());
                if (rs == null) {
                    return;
                }

                try {
                    if (rs.next()) {
                        final String data = rs.getString("pet_data");
                        Bukkit.getScheduler().runTask(RealCraft.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                Pet.this.getPetData().loadData(new JsonData(data));
                                Bukkit.getPluginManager().callEvent(new PetLoadEvent(Pet.this));
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
        Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(), new Runnable() {
            @Override
            public void run() {
                String jsonData = Pet.this.getPetData().getJsonData().toString();

                DB.update("INSERT INTO " + PETS + " (user_id,pet_data,pet_created,pet_updated) VALUES(?,?,?,?)" +
                        "ON DUPLICATE KEY UPDATE pet_data = ?,pet_updated = ?",
                    Pet.this.getPetPlayer().getUser().getId(),
                    jsonData,
                    (int) (System.currentTimeMillis() / 1000),
                    (int) (System.currentTimeMillis() / 1000),
                    jsonData,
                    (int) (System.currentTimeMillis() / 1000)
                );
            }
        });
    }

    public void delete() {
        this.getPetEntity().remove();

        Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(), new Runnable() {
            @Override
            public void run() {
                DB.update("DELETE FROM " + PETS + " WHERE user_id = ?", Pet.this.getPetPlayer().getUser().getId());
            }
        });
    }
}
