package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.utils.LocationUtil;

public class PetActionSit extends PetAction {

    private static final int MAX_LOOK_DISTANCE = 3;
    private Location sitLocation;

    public PetActionSit(Pet pet) {
        super(PetActionType.SIT, pet);
    }

    @Override
    public boolean shouldStart() {
        if (this.getPet().getPetData().getMode().getType() != PetDataMode.PetDataModeType.SIT) {
            return false;
        }

        return true;
    }

    @Override
    protected void _start() {
        this.getEntity().setAI(false);
        this.getEntity().setGravity(false);
        this.getEntity().setVelocity(new Vector(0, 0, 0));

        Bukkit.getScheduler().runTask(RealCraft.getInstance(), new Runnable() {
            public void run() {
                sitLocation = PetActionSit.this.getEntity().getLocation();
                sitLocation.setY(sitLocation.getBlockY() - 0.8);
                PetActionSit.this.getEntity().teleport(sitLocation);
            }
        });
    }

    @Override
    protected void _clear() {
        this.getEntity().setGravity(true);
        this.getEntity().teleport(this.getEntity().getLocation().clone().add(0, 1, 0));
        this.getEntity().setVelocity(new Vector(0, 0, 0));
    }

    @Override
    public void run() {
        if (this.getPet().getPetData().getMode().getType() != PetDataMode.PetDataModeType.SIT) {
            this.finish();
            return;
        }

        if (!LocationUtil.isSimilar(sitLocation, this.getEntity().getLocation())) {
            this.getPet().getPetData().getMode().setType(PetDataMode.PetDataModeType.FOLLOW);
            this.finish();
            return;
        }

        Player player = this.getPet().getPetPlayer().getPlayer();
        Location lookLocation = player.getLocation();

        if (lookLocation.distanceSquared(this.getEntity().getLocation()) <= MAX_LOOK_DISTANCE * MAX_LOOK_DISTANCE) {
            lookLocation = this.getEntity().getLocation().setDirection(player.getLocation().subtract(this.getEntity().getLocation()).toVector());

            if (lookLocation.getPitch() < -60) {
                lookLocation.setPitch(-60);
            } else if (lookLocation.getPitch() > 60) {
                lookLocation.setPitch(60);
            }

            this.getEntity().setRotation(lookLocation.getYaw(), lookLocation.getPitch());
        } else {
            this.getEntity().setRotation(this.getEntity().getLocation().getYaw(), 0);
        }
    }
}
