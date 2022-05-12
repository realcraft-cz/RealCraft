package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.BlockUtil;
import realcraft.bukkit.utils.RandomUtil;

public class PetActionSpawn extends PetAction {

    private static final int MAX_RANDOM_LOCATION_STEPS = 10;

    private Location spawnLocation;
    private Location blockLocation;
    private BlockData blockData;

    public PetActionSpawn(Pet pet) {
        super(PetActionType.SPAWN, pet);
    }

    private @Nullable Location _getSpawnLocation() {
        for (int i = 0; i < 4; i++) {
            Location location = this._getRandomLocation(this.getPet().getPetPlayer().getPlayer().getLocation().add(0, -1 * i, 0), 0);
            if (location != null) {
                return location;
            }
        }

        return null;
    }

    private @Nullable Location _getRandomLocation(Location location, int step) {
        Location tmpLocation = location.clone();

        tmpLocation.setPitch(0f);
        tmpLocation.add(location.getDirection().setY(0).normalize().rotateAroundY(RandomUtil.getRandomDouble(-0.7, 0.7)).multiply(RandomUtil.getRandomDouble(2, 5)));

        if (this._isSpawnBlockValid(tmpLocation.getBlock())) {
            return tmpLocation;
        }

        if (step < MAX_RANDOM_LOCATION_STEPS) {
            return this._getRandomLocation(location, step + 1);
        }

        return null;
    }

    private boolean _isSpawnBlockValid(Block block) {
        if (!block.getType().isAir()) {
            return false;
        }

        Block belowBlock = block.getRelative(BlockFace.DOWN);

        if (!belowBlock.isSolid()) {
            return false;
        }

        if (!belowBlock.isBuildable()) {
            return false;
        }

        if (belowBlock.getType().isAir()) {
            return false;
        }

        if (belowBlock.getBlockData() instanceof Stairs) {
            return false;
        }

        if (belowBlock.getBlockData() instanceof Slab) {
            return false;
        }

        return true;
    }

    @Override
    protected void _start() {
        this.spawnLocation = this._getSpawnLocation();

        if (this.spawnLocation == null) {
            this.spawnLocation = this.getPet().getPetPlayer().getPlayer().getLocation();
            this.spawnLocation.setPitch(0f);
            this.spawnLocation.add(this.spawnLocation.getDirection().setY(0).normalize().multiply(1.5));

            this.getPet().getPetEntity().spawn(this.spawnLocation);
            this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_VEX_CHARGE, 1f, 1f);
            this.finish();
            return;
        }

        this.spawnLocation = this.spawnLocation.toBlockLocation().add(0.5, 0, 0.5);
        this.spawnLocation.setYaw(this.spawnLocation.clone().setDirection(this.getPet().getPetPlayer().getPlayer().getLocation().subtract(this.spawnLocation).toVector()).getYaw());
        this.blockLocation = this.spawnLocation.clone().add(0, -1, 0);
        this.blockData = this.blockLocation.getBlock().getBlockData();

        this.getPet().getPetEntity().spawn(this.spawnLocation.clone().add(0, -1.5, 0));

        this.getEntity().setAI(false);
        this.getEntity().setGravity(false);
        this.getEntity().setVelocity(new Vector(0, 0, 0));
        this.getEntity().setRotation(this.getEntity().getLocation().getYaw(), -20);

        this._startTask(1);
    }

    @Override
    protected void _run() {
        if (!this.getPet().getPetEntity().isTicking()) {
            this.getEntity().teleport(this.spawnLocation.clone().add(0, 0.4, 0));
            this.finish();
            return;
        }

        if (this.getTicks() <= 8) {
            if (this.getTicks() % 2 == 0) {
                this.getEntity().getWorld().spawnParticle(Particle.BLOCK_CRACK, spawnLocation, 4, 0.1, 0.0, 0.1, 0, blockData);
            }

            if (this.getTicks() % 4 == 0) {
                this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.BLOCK_GRAVEL_HIT, 0.5f, 0.5f);
                for (Player player : this.getEntity().getTrackedPlayers()) {
                    BlockUtil.sendBlockDamage(player, blockLocation, this.getTicks() / 3);
                }
            }
        }

        if (this.getTicks() > 8 + 16 && this.getTicks() <= 8 + 16 + 12) {
            if (this.getTicks() % 2 == 0) {
                this.getEntity().getWorld().spawnParticle(Particle.BLOCK_CRACK, spawnLocation, 4, 0.2, 0.1, 0.2, 0, blockData);
            }

            if (this.getTicks() % 4 == 0) {
                this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.BLOCK_GRAVEL_HIT, 0.5f, 0.5f);
                for (Player player : this.getEntity().getTrackedPlayers()) {
                    BlockUtil.sendBlockDamage(player, blockLocation, this.getTicks() / 6);
                }
            }
        }

        if (this.getTicks() > 8 + 16 + 12 + 16 && this.getTicks() <= 8 + 16 + 12 + 16 + 32) {
            if (this.getTicks() % 2 == 0) {
                this.getEntity().getWorld().spawnParticle(Particle.BLOCK_CRACK, spawnLocation, 4, 0.2, 0.1, 0.2, 0, blockData);
            }

            if (this.getTicks() % 3 == 0) {
                if (this.getEntity().getLocation().getY() < this.spawnLocation.getY() - 0.8) {
                    this.getEntity().teleport(this.getEntity().getLocation().add(0, 0.08, 0));
                    this.getEntity().setRotation(this.spawnLocation.getYaw() + RandomUtil.getRandomInteger(-40, 40), -20);

                    if (RandomUtil.getRandomInteger(0, 2) > 0) {
                        this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.BLOCK_GRAVEL_HIT, 0.5f, 0.5f);
                    }
                }
            }

            if (this.getTicks() == 8 + 16 + 12 + 16 + 32) {
                this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.BLOCK_GRAVEL_HIT, 0.5f, 0.5f);
            }
        }

        if (this.getTicks() >= 8 + 16 + 12 + 16 + 32 + 8) {
            this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_VEX_CHARGE, 1f, 1f);
            this.getEntity().teleport(this.spawnLocation.clone().add(0, 0.4, 0));
            for (Player player : this.getEntity().getTrackedPlayers()) {
                BlockUtil.sendBlockDamage(player, blockLocation, 10);
            }
            this.finish();
        }
    }

    @Override
    protected void _clear() {
    }
}
