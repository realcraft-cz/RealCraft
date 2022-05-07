package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Particle;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.ItemUtil;

public class PetActionSkinChange extends PetAction {

    private State state;
    private int ticks;
    private int yawIncrement;
    private int level;

    public PetActionSkinChange(Pet pet) {
        super(PetActionType.SKIN_CHANGE, pet);
    }

    @Override
    protected void _start() {
        this.state = State.STARTING;
        this.ticks = 0;
        this.level = 0;
        this.yawIncrement = 15;
    }

    @Override
    protected void _clear() {
    }

    @Override
    public void run() {
        this.ticks ++;

        if (this.state == State.STARTING) {
            this.getEntity().setRotation(this.getEntity().getLocation().getYaw() + this.yawIncrement, this.getEntity().getLocation().getPitch());

            if (this.ticks % 10 == 0) {
                this.yawIncrement += 10;
                this.level ++;
            }

            if (this.ticks % 5 == 0) {
                this.getEntity().getLocation().getWorld().spawnParticle(Particle.SPELL, this.getEntity().getLocation().add(0, 0.5, 0), this.level, 0.2, 0.2, 0.2, 0);
                this.getEntity().getLocation().getWorld().spawnParticle(Particle.SPELL_MOB_AMBIENT, this.getEntity().getLocation().add(0, 0.5, 0), this.level, 0.2, 0.2, 0.2, 0);
            }

            if (this.yawIncrement == 75) {
                this.yawIncrement = 65;
                this.state = State.TRANSFORM;
            }
        } else if (this.state == State.ENDING) {
            this.getEntity().setRotation(this.getEntity().getLocation().getYaw() + this.yawIncrement, this.getEntity().getLocation().getPitch());

            if (this.ticks % 10 == 0) {
                this.yawIncrement -= 10;
                this.level --;
            }

            if (this.ticks % 5 == 0) {
                this.getEntity().getLocation().getWorld().spawnParticle(Particle.SPELL, this.getEntity().getLocation().add(0, 0.5, 0), this.level, 0.2, 0.2, 0.2, 0);
                this.getEntity().getLocation().getWorld().spawnParticle(Particle.SPELL_MOB_AMBIENT, this.getEntity().getLocation().add(0, 0.5, 0), this.level, 0.2, 0.2, 0.2, 0);
            }

            if (this.yawIncrement == 5) {
                this.finish();
            }
        } else if (this.state == State.TRANSFORM) {
            this.getEntity().setRotation(this.getEntity().getLocation().getYaw() + this.yawIncrement, this.getEntity().getLocation().getPitch());

            this.getEntity().getLocation().getWorld().spawnParticle(Particle.SPELL, this.getEntity().getLocation().add(0, 0.5, 0), 8, 0.2, 0.2, 0.2, 0);
            this.getEntity().getLocation().getWorld().spawnParticle(Particle.SPELL_MOB_AMBIENT, this.getEntity().getLocation().add(0, 0.5, 0), 8, 0.2, 0.2, 0.2, 0);
            this.getEntity().getLocation().getWorld().spawnParticle(Particle.SMOKE_NORMAL, this.getEntity().getLocation().add(0, 0.7, 0), 6, 0.3, 0.3, 0.3, 0);

            if (this.ticks % 20 == 0) {
                this.getEntity().getEquipment().setHelmet(ItemUtil.getHead(this.getPet().getPetData().getSkin().getSkin().getTexture()));
                this.state = State.ENDING;
                //sound 726,1090
            }
        }
    }

    private enum State {
        STARTING, TRANSFORM, ENDING
    }
}
