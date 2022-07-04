package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.jetbrains.annotations.Nullable;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.RandomUtil;
import realcraft.bukkit.wrappers.SafeLocation;

public class PetActionDefend extends PetAction {

    private static final int MIN_DISTANCE_START = 12;
    private static final double MAX_MOB_DISTANCE = 7.0;
    private static final double MOB_REACH_DISTANCE = 2.0;

    public PetActionDefend(Pet pet) {
        super(PetActionType.DEFEND, pet);
    }

    @Override
    public boolean isCancellable() {
        return true;
    }

    @Override
    public boolean shouldStart() {
        if (this.getPet().getPetData().getMode().getType() == PetDataMode.PetDataModeType.HOME) {
            return false;
        }

        if (!this.getPet().getPetEntity().isLiving()) {
            return false;
        }

        SafeLocation targetLoc = new SafeLocation(this.getPet().getPetPlayer().getPlayer().getLocation());
        double distance = targetLoc.distanceSquared(this.getEntity().getLocation());

        if (distance >= MIN_DISTANCE_START * MIN_DISTANCE_START) {
            return false;
        }

        if (this._getClosestTargetMonster() == null) {
            return false;
        }

        return true;
    }

    private @Nullable Monster _getClosestTargetMonster() {
        Monster target = null;
        int minDistance = Integer.MAX_VALUE;
        int dist;

        if (!this.getPet().getPetPlayer().getPlayer().getLocation().getWorld().equals(this.getEntity().getWorld())) {
            return null;
        }

        for (Entity entity : this.getPet().getPetPlayer().getPlayer().getLocation().getNearbyEntities(MAX_MOB_DISTANCE, 3, MAX_MOB_DISTANCE)) {
            if (entity instanceof Monster monster && !entity.equals(this.getEntity())) {
                if (!monster.isDead() && monster.getTarget() != null && (monster.getTarget().equals(this.getPet().getPetPlayer().getPlayer()) || monster.getTarget().equals(this.getEntity()))) {
                    dist = (int) this.getPet().getPetPlayer().getPlayer().getLocation().distanceSquared(monster.getLocation());
                    if (dist < minDistance) {
                        minDistance = dist;
                        target = monster;
                    }
                }
            }
        }

        return target;
    }

    @Override
    protected void _start() {
        this.getEntity().setAI(true);
        this.getEntity().setGravity(true);
        this.getEntity().getPathfinder().stopPathfinding();
        this._startTask(0, 10 + (3 * (this.getPet().getPetData().getHealth().getMaxValue() - this.getPet().getPetData().getHealth().getValue())));

        this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_GHAST_WARN, 0.5f, 1f);
    }

    @Override
    protected void _run() {
        if (!this.getPet().getPetEntity().isTicking()) {
            this.cancel();
            return;
        }

        Monster monster = this._getClosestTargetMonster();
        if (monster == null) {
            this.finish();
            return;
        }

        this.getEntity().setTarget(monster);

        SafeLocation targetLoc = new SafeLocation(monster.getLocation());
        targetLoc.add(new SafeLocation(this.getPet().getPetPlayer().getPlayer().getLocation()).subtract(this.getEntity().getLocation()).toVector().normalize().setY(0));
        targetLoc.setDirection(monster.getLocation().toVector());

        double distance = monster.getLocation().distanceSquared(this.getEntity().getLocation());
        if (distance < MOB_REACH_DISTANCE * MOB_REACH_DISTANCE) {
            this.getEntity().lookAt(monster);

            double damage = 1.0 * (this.getPet().getPetData().getHealth().getValue());
            monster.damage(damage, this.getEntity());

            this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.5f, 1f);
            this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.5f, 1f);

            if (RandomUtil.getRandomInteger(0, 2) == 0) {
                this.getEntity().getWorld().spawnParticle(Particle.SWEEP_ATTACK, this.getEntity().getEyeLocation(), 1, 0, 0, 0);
            }
        }

        EntityUtil.navigate(this.getEntity(), targetLoc, 1.0 * (this.getEntity().isInWater() ? 3 : 1));
    }

    @Override
    protected void _clear() {
        if (this.getEntity() != null) {
            this.getEntity().getPathfinder().stopPathfinding();
        }
    }
}
