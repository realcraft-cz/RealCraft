package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.PetsManager;
import realcraft.bukkit.pets.events.pet.PetActionCancelEvent;
import realcraft.bukkit.pets.events.pet.PetActionFinishEvent;
import realcraft.bukkit.pets.events.pet.PetActionStartEvent;
import realcraft.bukkit.pets.pet.Pet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public abstract class PetAction implements Listener, Runnable {

    private final PetActionType type;
    private final Pet pet;

    private PetActionState state = PetActionState.NONE;
    private BukkitTask task;

    public PetAction(PetActionType type, Pet pet) {
        this.type = type;
        this.pet = pet;
    }

    public PetActionType getType() {
        return type;
    }

    public Pet getPet() {
        return pet;
    }

    public Zombie getEntity() {
        return this.getPet().getPetEntity().getEntity();
    }

    public PetActionState getState() {
        return state;
    }

    public boolean isCancellable() {
        return false;
    }

    public boolean shouldStart() {
        return false;
    }

    public void start() {
        PetActionStartEvent event = new PetActionStartEvent(this.getPet(), this);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            PetsManager.debug("["+this.getType()+"] start");
            this.state = PetActionState.RUNNING;
            this._start();

            if (this.state == PetActionState.RUNNING) {
                this._startTask();
            }
        }
    }

    public void cancel() {
        PetsManager.debug("["+this.getType()+"] cancel");
        this.state = PetActionState.CANCELLED;
        this._stopTask();
        this._clear();
        Bukkit.getPluginManager().callEvent(new PetActionCancelEvent(this.getPet(), this));
    }

    public void finish() {
        PetsManager.debug("["+this.getType()+"] finish");
        this.state = PetActionState.FINISHED;
        this._stopTask();
        this._clear();
        Bukkit.getPluginManager().callEvent(new PetActionFinishEvent(this.getPet(), this));
    }

    protected void _startTask() {
        if (this.getType().getPeriod() > 0) {
            this._stopTask();

            task = Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(), this, 0, this.getType().getPeriod());
        }
    }

    protected void _stopTask() {
        if (task != null) {
            task.cancel();
        }
    }

    protected abstract void _start();
    protected abstract void _clear();

    public enum PetActionType {

        SPAWN       (10, 0, PetActionSpawn.class),
        DESPAWN     (10, 0, PetActionDespawn.class),
        SKIN_CHANGE (9, 1, PetActionSkinChange.class),
        EAT         (5, 10, PetActionEat.class),
        FOLLOW      (3, 10, PetActionFollow.class),
        SIT         (2, 10, PetActionSit.class),
        NONE        (0, 0, PetActionNone.class),
        ;

        private final int priority;
        private final int period;
        private final Class<?> clazz;
        private static final PetActionType[] sortedTypes;

        static {
            ArrayList<PetActionType> types = new ArrayList<>(Arrays.asList(PetActionType.values()));
            types.sort(new Comparator<>(){
                @Override
                public int compare(PetActionType type1,PetActionType type2){
                    return -1 * Integer.compare(type1.getPriority(),type2.getPriority());
                }
            });

            sortedTypes = types.toArray(new PetActionType[0]);
        }

        private PetActionType(int priority, int period, Class<?> clazz) {
            this.priority = priority;
            this.period = period;
            this.clazz = clazz;
        }

        public int getPriority() {
            return priority;
        }

        public int getPeriod() {
            return period;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public static PetActionType[] getSortedTypes() {
            return sortedTypes;
        }
    }

    public enum PetActionState {
        NONE,
        RUNNING,
        CANCELLED,
        FINISHED,
    }
}
