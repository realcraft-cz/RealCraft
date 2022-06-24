package realcraft.bukkit.pets.pet.actions;

import org.bukkit.*;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bukkit.wrappers.SafeLocation;

public class PetActionHome extends PetAction {

    private static final double HOME_REACH_DISTANCE = 1.5;
    private static final int NOT_MOVING_THRESHOLD = 4;

    private State state;
    private SafeLocation targetLocation;
    private int notMovingCounter;

    public PetActionHome(Pet pet) {
        super(PetActionType.HOME, pet);
    }

    @Override
    public boolean isCancellable() {
        return true;
    }

    @Override
    public boolean shouldStart() {
        if (this.getPet().getPetData().getMode().getType() != PetDataMode.PetDataModeType.HOME) {
            return false;
        }

        if (this.getPet().getPetData().getHome().getLocation() == null) {
            return false;
        }

        return true;
    }

    @Override
    protected void _start() {
        this.state = State.MOVING;
        this.notMovingCounter = 0;

        this.getEntity().setAI(true);
        this.getEntity().setGravity(true);

        this.targetLocation = new SafeLocation(this.getPet().getPetData().getHome().getLocation());
        this.targetLocation.setY(this.targetLocation.getBlockY() - 0.8 + this.targetLocation.getBlock().getBoundingBox().getHeight());
        this.targetLocation.setPitch(0);

        this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_GHAST_AMBIENT, 0.5f, 2f);

        this._startTask(0, 10);
    }

    @Override
    protected void _run() {
        if (this.getPet().getPetData().getMode().getType() != PetDataMode.PetDataModeType.HOME) {
            this.finish();
            return;
        }

        if (this.state != State.SITTING && (!this.getPet().getPetEntity().isTicking() || !this.getEntity().getWorld().equals(targetLocation.getWorld()))) {
            this.getEntity().setAI(false);
            this.getEntity().setGravity(false);
            this.getPetEntity().teleport(targetLocation);
            this.state = State.SITTING;
            this._startTask(40);
            return;
        }

        if (this.state == State.MOVING) {
            boolean isMoving = Math.abs(this.getEntity().getVelocity().getX()) > 0.01 || Math.abs(this.getEntity().getVelocity().getZ()) > 0.01 || Math.abs(this.getEntity().getVelocity().getY()) > 0.1;
            if (!isMoving) {
                notMovingCounter ++;

                if (notMovingCounter >= NOT_MOVING_THRESHOLD) {
                    this.getPet().getPetData().getMode().setType(PetDataMode.PetDataModeType.FOLLOW);
                    this.cancel();
                    return;
                }

                double distance = this.targetLocation.distance(this.getEntity().getLocation());

                if (distance < HOME_REACH_DISTANCE) {
                    this.state = State.DROPPING;

                    Location location = this.targetLocation.clone().add(0, 0.8, 0);
                    location.setYaw(this.getEntity().getLocation().getYaw());

                    this.getEntity().setAI(false);
                    this.getPetEntity().teleport(location);
                    this.getEntity().setRotation(location.getYaw(), 50);
                    this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_GHAST_AMBIENT, 0.5f, 2f);

                    this._startTask(1, 10);
                    return;
                }
            } else {
                notMovingCounter = 0;
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

                        getPetEntity().teleport(getEntity().getLocation().add(0, -0.08, 0));
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

                    getPetEntity().teleport(targetLocation);
                    state = State.SITTING;
                    _startTask(15);
                }
            }, (9 * 2) + 2);
        } else if (this.state == State.SITTING) {
            this.getPetEntity().getEntity().getWorld().spawnParticle(Particle.WAX_OFF, this.getPetEntity().getEntity().getLocation().add(0, 1.2, 0), 1, 0.3, 0.2, 0.3, 0);
        }
    }

    @Override
    protected void _clear() {
        if (this.state == State.SITTING || this.state == State.DROPPING) {
            this.getEntity().setGravity(true);
            this.getEntity().setJumping(true);
            this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_GHAST_AMBIENT, 0.5f, 2f);
        }
    }

    private enum State {
        MOVING, DROPPING, SITTING
    }
}
