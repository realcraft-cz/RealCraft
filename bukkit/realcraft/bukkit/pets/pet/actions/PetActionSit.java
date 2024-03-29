package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bukkit.wrappers.SafeLocation;

public class PetActionSit extends PetAction {

    private static final int MAX_LOOK_DISTANCE = 3;
    private Location sitLocation;

    public PetActionSit(Pet pet) {
        super(PetActionType.SIT, pet);
    }

    @Override
    public boolean isCancellable() {
        return true;
    }

    @Override
    public boolean shouldStart() {
        if (this.getPet().getPetData().getMode().getType() != PetDataMode.PetDataModeType.SIT) {
            return false;
        }

        if (!this.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).isSolid()) {
            return false;
        }

        return true;
    }

    public Location getSitLocation() {
        return sitLocation;
    }

    @Override
    protected void _start() {
        this.getEntity().setAI(false);
        this.getEntity().setGravity(false);
        this.getEntity().setVelocity(new Vector(0, 0, 0));

        sitLocation = this.getEntity().getLocation();
        sitLocation.setY(sitLocation.getBlockY() - 0.8 + sitLocation.getBlock().getBoundingBox().getHeight());

        Bukkit.getScheduler().runTask(RealCraft.getInstance(), new Runnable() {
            public void run() {
                getEntity().teleport(sitLocation);
            }
        });

        this._startTask(10);
    }

    @Override
    protected void _run() {
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
        Location lookLocation = new SafeLocation(player.getLocation());

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

    @Override
    protected void _clear() {
        this.getEntity().setGravity(true);
        this.getEntity().setJumping(true);
    }
}
