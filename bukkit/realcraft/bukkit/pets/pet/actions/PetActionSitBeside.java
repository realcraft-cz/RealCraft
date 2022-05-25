package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.sitting.Sitting;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bukkit.wrappers.SafeLocation;

public class PetActionSitBeside extends PetAction {

    private static final int MAX_DISTANCE_START = 5;
    private static final double SIT_REACH_DISTANCE = 1.7;

    private State state;
    private Location stairsLocation;
    private Location sitLocation;

    public PetActionSitBeside(Pet pet) {
        super(PetActionType.SIT_BESIDE, pet);
    }

    @Override
    public boolean isCancellable() {
        return true;
    }

    @Override
    public boolean shouldStart() {
        if (this.getPet().getPetData().getMode().getType() != PetDataMode.PetDataModeType.FOLLOW) {
            return false;
        }

        if (!this.getPet().getPetEntity().isLiving()) {
            return false;
        }

        if (!Sitting.isPlayerSitting(this.getPet().getPetPlayer().getPlayer())) {
            return false;
        }

        if (new SafeLocation(this.getEntity().getLocation()).distanceSquared(this.getPet().getPetPlayer().getPlayer().getLocation()) > MAX_DISTANCE_START * MAX_DISTANCE_START) {
            return false;
        }

        Block stairs = this.getPet().getPetPlayer().getPlayer().getLocation().getBlock().getRelative(BlockFace.UP);
        if (!(stairs.getBlockData() instanceof Stairs)) {
            return false;
        }

        BlockFace[] faces = new BlockFace[] {
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.NORTH,
            BlockFace.SOUTH,
        };

        for (BlockFace face : faces) {
            Block besideStairs = this._getRelativeStairs(stairs, face);
            if (besideStairs != null) {
                this.stairsLocation = besideStairs.getLocation();
                return true;
            }
        }

        return false;
    }

    private @Nullable Block _getRelativeStairs(Block block, BlockFace face) {
        Block relativeBlock = block.getRelative(face);

        if (!relativeBlock.getBlockData().matches(block.getBlockData())) {
            return null;
        }

        if (!this._isBlockValid(relativeBlock)) {
            return null;
        }

        return relativeBlock;
    }

    private boolean _isBlockValid(Block block) {
        if (!(block.getBlockData() instanceof Stairs)) {
            return false;
        }

        if (((Stairs) block.getBlockData()).getShape() != Stairs.Shape.STRAIGHT || ((Stairs) block.getBlockData()).getHalf() != Bisected.Half.BOTTOM) {
            return false;
        }

        if (!block.getRelative(BlockFace.UP).getType().isAir()) {
            return false;
        }

        for (Entity entity : block.getLocation().add(0.5, 0.5, 0.5).getNearbyEntities(0.5, 0.5, 0.5)) {
            if (entity.getEntityId() != this.getEntity().getEntityId() && (entity.getType() == EntityType.DROWNED || entity.getType() == EntityType.PLAYER)) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected void _start() {
        this.state = State.MOVING;

        this.getEntity().setAI(true);
        this.getEntity().setGravity(true);

        this._startTask(0, 10);
    }

    @Override
    protected void _run() {
        if (this.getPet().getPetData().getMode().getType() != PetDataMode.PetDataModeType.FOLLOW) {
            this.finish();
            return;
        }

        if (!this.getPet().getPetEntity().isLiving()) {
            return;
        }

        if (this.state == State.MOVING) {
            boolean isMoving = Math.abs(this.getEntity().getVelocity().getX()) > 0.01 || Math.abs(this.getEntity().getVelocity().getZ()) > 0.01 || Math.abs(this.getEntity().getVelocity().getY()) > 0.1;
            if (!isMoving) {
                double distance = this.getEntity().getLocation().distance(stairsLocation);

                if (distance < SIT_REACH_DISTANCE) {
                    this.state = State.SITTING;

                    Stairs stairs = (Stairs) stairsLocation.getBlock().getBlockData();

                    sitLocation = stairsLocation.clone();
                    sitLocation.add(0.5, -0.27, 0.5);
                    sitLocation.add(stairs.getFacing().getOppositeFace().getDirection().multiply(0.2));
                    sitLocation.setDirection(stairs.getFacing().getOppositeFace().getDirection());

                    this.getEntity().setAI(false);
                    this.getEntity().setGravity(false);
                    this.getEntity().setVelocity(new Vector(0, 0, 0));
                    this.getEntity().teleport(sitLocation);
                    this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_GHAST_AMBIENT, 0.5f, 2f);

                    this._startTask(20);
                    return;
                }
            }

            if (LocationUtil.isSimilar(EntityUtil.getTargetLocation(this.getEntity()), stairsLocation)) {
                return;
            }

            EntityUtil.navigate(this.getEntity(), stairsLocation, 0.7);
        } else if (this.state == State.SITTING) {
            if (!this._isBlockValid(stairsLocation.getBlock()) || !LocationUtil.isSimilar(sitLocation, this.getEntity().getLocation())) {
                this.finish();
            }
        }
    }

    @Override
    protected void _clear() {
        this.getEntity().setGravity(true);
        this.getEntity().setJumping(true);
    }

    private enum State {
        MOVING, SITTING
    }
}
