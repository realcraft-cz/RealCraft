package realcraft.bukkit.pets.pet.timers;

import org.bukkit.Bukkit;
import realcraft.bukkit.pets.pet.Pet;

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

    public boolean shouldRun() {
        return Bukkit.getCurrentTick() >= lastRunTick + this.getType().getPeriod();
    }

    public void run() {
        lastRunTick = Bukkit.getCurrentTick();
        this._run();
    }

    protected abstract void _run();

    public enum PetTimerType {

        LIVE            (20, PetTimerLive.class),
        ACTIONS         (20, PetTimerActions.class),
        SAVE            (60 * 20, PetTimerSave.class),
        FOOD            (10 * 20, PetTimerFood.class),
        FOLLOW_LEVEL    (10 * 20, PetTimerFollowLevel.class),
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
