package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Particle;
import org.bukkit.Sound;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.utils.ItemUtil;

public class PetActionSkinChange extends PetAction {

    private State state;
    private int yawIncrement;
    private int level;

    public PetActionSkinChange(Pet pet) {
        super(PetActionType.SKIN_CHANGE, pet);
    }

    @Override
    protected void _start() {
        this.state = State.STARTING;
        this.level = 0;
        this.yawIncrement = 15;

        if (this.getPet().getPetData().getMode().getType() == PetDataMode.PetDataModeType.SIT) {
            this.getPet().getPetData().getMode().setType(PetDataMode.PetDataModeType.FOLLOW);
        }

        this.getEntity().setAI(true);
        this.getEntity().setGravity(true);
        this.getEntity().setRotation(this.getEntity().getLocation().getYaw(), 0);

        this._startTask(1);
    }

    @Override
    protected void _run() {
        if (this.state == State.STARTING) {
            this.getEntity().setRotation(this.getEntity().getLocation().getYaw() + this.yawIncrement, this.getEntity().getLocation().getPitch());

            if (this.getTicks() % 10 == 0) {
                this.yawIncrement += 10;
                this.level ++;
            }

            if (this.getTicks() == 10) {
                this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_GHAST_AMBIENT, 0.5f, 2f);
            }

            if (this.getTicks() % 4 == 0) {
                this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_PHANTOM_FLAP, 0.7f, 2f / (6f/this.level));
            }

            if (this.getTicks() % 5 == 0) {
                this.getEntity().getLocation().getWorld().spawnParticle(Particle.EFFECT, this.getEntity().getLocation().add(0, 0.5, 0), this.level, 0.2, 0.2, 0.2, 0);
                this.getEntity().getLocation().getWorld().spawnParticle(Particle.INSTANT_EFFECT, this.getEntity().getLocation().add(0, 0.5, 0), this.level, 0.2, 0.2, 0.2, 0);
            }

            if (this.yawIncrement == 75) {
                this.yawIncrement = 65;
                this.state = State.TRANSFORMING;
                this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1f, 0f);
            }
        } else if (this.state == State.ENDING) {
            this.getEntity().setRotation(this.getEntity().getLocation().getYaw() + this.yawIncrement, this.getEntity().getLocation().getPitch());

            if (this.getTicks() % 10 == 0) {
                this.yawIncrement -= 10;
                this.level --;
            }

            if (this.getTicks() % 5 == 0) {
                this.getEntity().getLocation().getWorld().spawnParticle(Particle.EFFECT, this.getEntity().getLocation().add(0, 0.5, 0), this.level, 0.2, 0.2, 0.2, 0);
                this.getEntity().getLocation().getWorld().spawnParticle(Particle.INSTANT_EFFECT, this.getEntity().getLocation().add(0, 0.5, 0), this.level, 0.2, 0.2, 0.2, 0);
            }

            if (this.yawIncrement == 5) {
                this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_GHAST_AMBIENT, 0.5f, 2f);
                this.finish();
            }
        } else if (this.state == State.TRANSFORMING) {
            this.getEntity().setRotation(this.getEntity().getLocation().getYaw() + this.yawIncrement, this.getEntity().getLocation().getPitch());

            this.getEntity().getLocation().getWorld().spawnParticle(Particle.EFFECT, this.getEntity().getLocation().add(0, 0.5, 0), 8, 0.2, 0.2, 0.2, 0);
            this.getEntity().getLocation().getWorld().spawnParticle(Particle.INSTANT_EFFECT, this.getEntity().getLocation().add(0, 0.5, 0), 8, 0.2, 0.2, 0.2, 0);
            this.getEntity().getLocation().getWorld().spawnParticle(Particle.SMOKE, this.getEntity().getLocation().add(0, 0.7, 0), 6, 0.3, 0.3, 0.3, 0);

            if (this.getTicks() % 4 == 0) {
                this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_PHANTOM_FLAP, 0.7f, 2f);
            }

            if (this.getTicks() % 20 == 0) {
                this.getEntity().getEquipment().setHelmet(ItemUtil.getHead(this.getPet().getPetData().getSkin().getSkin().getTexture()));
                this.getEntity().getWorld().playSound(this.getEntity().getLocation(), Sound.ENTITY_VEX_CHARGE, 1f, 1f);
                this.state = State.ENDING;
            }
        }
    }

    @Override
    protected void _clear() {
    }

    private enum State {
        STARTING, TRANSFORMING, ENDING
    }
}
