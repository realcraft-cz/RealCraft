package realcraft.bukkit.pets.pet.timers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Zombie;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.entity.PetEntity;

public abstract class PetTimer {

    private final PetTimerType type;
    private final Pet pet;

    private int lastRunTick;

    public PetTimer(PetTimerType type, Pet pet) {
        this.type = type;
        this.pet = pet;
        this.lastRunTick = Bukkit.getCurrentTick();
    }

    public PetTimerType getType() {
        return type;
    }

    public Pet getPet() {
        return pet;
    }

    public PetEntity getPetEntity() {
        return this.getPet().getPetEntity();
    }

    public Zombie getEntity() {
        return this.getPet().getPetEntity().getEntity();
    }

    public final boolean shouldRun() {
        return Bukkit.getCurrentTick() >= lastRunTick + this.getType().getPeriod();
    }

    public final void run() {
        lastRunTick = Bukkit.getCurrentTick();
        this._run();
    }

    protected abstract void _run();

    public enum PetTimerType {

        LIVE            (20, PetTimerLive.class),
        ACTIONS         (20, PetTimerActions.class),
        SAVE            (60 * 20, PetTimerSave.class),
        HEALTH          (12 * 20, PetTimerHealth.class),
        FOOD            (10 * 20, PetTimerFood.class),
        HEAT            (5 * 20, PetTimerHeat.class),
        FOLLOW_LEVEL    (10 * 20, PetTimerFollowLevel.class),
        STAT_DISTANCE   (2 * 20, PetTimerStatDistance.class),
        ;

        private final int period;
        private final Class<?> clazz;

        private PetTimerType(int period, Class<?> clazz) {
            this.period = period;
            this.clazz = clazz;
        }

        public int getPeriod() {
            return period;
        }

        public Class<?> getClazz() {
            return clazz;
        }
    }
}
