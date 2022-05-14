package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.sitting.Sitting;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.LocationUtil;

public class PetActionSitBeside extends PetAction {

    private static final int MAX_DISTANCE_START = 5;
    private static final double SIT_REACH_DISTANCE = 1.5;

    private State state;
    private Location stairsLocation;

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

        if (!Sitting.isPlayerSitting(this.getPet().getPetPlayer().getPlayer())) {
            return false;
        }

        if (this.getEntity().getLocation().distanceSquared(this.getPet().getPetPlayer().getPlayer().getLocation()) > MAX_DISTANCE_START * MAX_DISTANCE_START) {
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
            Block besideStairs = this.getRelativeStairs(stairs, face);
            if (besideStairs != null) {
                this.stairsLocation = besideStairs.getLocation();
                return true;
            }
        }

        return false;
    }

    private @Nullable Block getRelativeStairs(Block block, BlockFace face) {
        Block relativeBlock = block.getRelative(face);

        if (!relativeBlock.getBlockData().matches(block.getBlockData())) {
            return null;
        }

        if (((Stairs) relativeBlock.getBlockData()).getShape() != Stairs.Shape.STRAIGHT || ((Stairs) relativeBlock.getBlockData()).getHalf() != Bisected.Half.BOTTOM) {
            return null;
        }

        if (!relativeBlock.getRelative(BlockFace.UP).getType().isAir()) {
            return null;
        }

        return relativeBlock;
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

        if (this.state == State.MOVING) {
            boolean isMoving = Math.abs(this.getEntity().getVelocity().getX()) > 0.01 || Math.abs(this.getEntity().getVelocity().getZ()) > 0.01 || Math.abs(this.getEntity().getVelocity().getY()) > 0.1;
            if (!isMoving) {
                double distance = this.getEntity().getLocation().distance(stairsLocation);

                if (distance < SIT_REACH_DISTANCE) {
                    this.state = State.SITTING;

                    Stairs stairs = (Stairs) stairsLocation.getBlock().getBlockData();

                    stairsLocation.add(0.5, -0.27, 0.5);
                    stairsLocation.add(stairs.getFacing().getOppositeFace().getDirection().multiply(0.2));
                    stairsLocation.setDirection(stairs.getFacing().getOppositeFace().getDirection());

                    this.getEntity().setAI(false);
                    this.getEntity().setGravity(false);
                    this.getEntity().setVelocity(new Vector(0, 0, 0));
                    this.getEntity().teleport(stairsLocation);
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
            //TODO: ambient look

            if (!LocationUtil.isSimilar(stairsLocation, this.getEntity().getLocation())) {
                this.finish();
            }
        }
    }

    @Override
    protected void _clear() {
        this.getEntity().setGravity(true);
        this.getEntity().setVelocity(new Vector(0, 0, 0));
    }

    private enum State {
        MOVING, SITTING
    }
}
