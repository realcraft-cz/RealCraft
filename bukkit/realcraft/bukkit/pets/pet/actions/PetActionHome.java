package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.LocationUtil;

public class PetActionHome extends PetAction {

    private static final double HOME_REACH_DISTANCE = 1.5;

    private State state;
    private Location targetLocation;

    public PetActionHome(Pet pet) {
        super(PetActionType.HOME, pet);
    }

    @Override
    public boolean isCancellable() {
        return true;
    }

    @Override
    public boolean shouldStart() {
        return (this.getPet().getPetData().getMode().getType() == PetDataMode.PetDataModeType.HOME && this.getPet().getPetData().getHome().getLocation() != null);
    }

    @Override
    protected void _start() {
        this.state = State.MOVING;

        this.getEntity().setAI(true);
        this.getEntity().setGravity(true);

        this.targetLocation = this.getPet().getPetData().getHome().getLocation().clone();
        this.targetLocation.add(0, -0.8, 0);
        this.targetLocation.setPitch(0);

        this._startTask(0, 10);
    }

    @Override
    protected void _run() {
        if (this.getPet().getPetData().getMode().getType() != PetDataMode.PetDataModeType.HOME) {
            this.finish();
            return;
        }

        if (this.state != State.SITTING && !this.getPet().getPetEntity().isTicking()) {
            this.getEntity().setAI(false);
            this.getEntity().setGravity(false);
            this.getEntity().teleport(targetLocation);
            this.state = State.SITTING;
            this._startTask(40);
            return;
        }

        if (this.state == State.MOVING) {
            boolean isMoving = Math.abs(this.getEntity().getVelocity().getX()) > 0.01 || Math.abs(this.getEntity().getVelocity().getZ()) > 0.01 || Math.abs(this.getEntity().getVelocity().getY()) > 0.1;
            if (!isMoving) {
                double distance = this.getEntity().getLocation().distance(this.targetLocation);

                if (distance < HOME_REACH_DISTANCE) {
                    this.state = State.DROPPING;

                    Location location = this.targetLocation.clone().add(0, 0.8, 0);
                    location.setYaw(this.getEntity().getLocation().getYaw());

                    this.getEntity().setAI(false);
                    this.getEntity().teleport(location);
                    this.getEntity().setRotation(location.getYaw(), 50);
                    this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_GHAST_AMBIENT, 0.5f, 2f);

                    this._startTask(1, 10);
                    return;
                }
            }

            if (LocationUtil.isSimilar(EntityUtil.getTargetLocation(this.getEntity()), this.targetLocation)) {
                return;
            }

            EntityUtil.navigate(this.getEntity(), this.targetLocation, 0.7);
        } else if (this.state == State.DROPPING) {
            if (!getEntity().hasGravity()) {
                return;
            }

            getEntity().setAI(false);
            getEntity().setGravity(false);

            for (int i = 0; i < 10; i++) {
                Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        if (getState() != PetActionState.RUNNING) {
                            return;
                        }

                        getEntity().teleport(getEntity().getLocation().add(0, -0.08, 0));
                        getEntity().setRotation(getEntity().getLocation().getYaw() + ((targetLocation.getYaw() - getEntity().getLocation().getYaw()) / 10), getEntity().getLocation().getPitch() - 5);
                    }
                }, i * 2);
            }

            Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (getState() != PetActionState.RUNNING) {
                        return;
                    }

                    getEntity().teleport(targetLocation);
                    state = State.SITTING;
                    _startTask(40);
                }
            }, (9 * 2) + 2);
        } else if (this.state == State.SITTING) {
            if (!LocationUtil.isSimilar(this.targetLocation, this.getEntity().getLocation())) {
                this.getPet().getPetData().getMode().setType(PetDataMode.PetDataModeType.FOLLOW);
                this.finish();
            }
        }
    }

    @Override
    protected void _clear() {
        if (this.state == State.SITTING || this.state == State.DROPPING) {
            this.getEntity().teleport(this.getEntity().getLocation().clone().add(0, 1, 0));
            this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_GHAST_AMBIENT, 0.5f, 2f);
        }
    }

    private enum State {
        MOVING, DROPPING, SITTING
    }
}
