package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitTask;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.PetsManager;
import realcraft.bukkit.pets.events.pet.PetActionCancelEvent;
import realcraft.bukkit.pets.events.pet.PetActionFinishEvent;
import realcraft.bukkit.pets.events.pet.PetActionStartEvent;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.entity.PetEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public abstract class PetAction implements Runnable {

    private final PetActionType type;
    private final Pet pet;

    private PetActionState state = PetActionState.NONE;
    private BukkitTask task;
    private int period;
    private int ticks;

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

    public PetEntity getPetEntity() {
        return this.getPet().getPetEntity();
    }

    public Zombie getEntity() {
        return this.getPet().getPetEntity().getEntity();
    }

    public PetActionState getState() {
        return state;
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public boolean isCancellable() {
        return false;
    }

    public boolean shouldStart() {
        return false;
    }

    public final void start() {
        PetActionStartEvent event = new PetActionStartEvent(this.getPet(), this);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            PetsManager.debug("["+this.getType()+"] start");
            this.state = PetActionState.RUNNING;
            this.ticks = 0;
            this._start();
        }
    }

    public final void cancel() {
        PetsManager.debug("["+this.getType()+"] cancel");
        this.state = PetActionState.CANCELLED;
        this._stopTask();
        this._clear();
        Bukkit.getPluginManager().callEvent(new PetActionCancelEvent(this.getPet(), this));
    }

    public final void finish() {
        PetsManager.debug("["+this.getType()+"] finish");
        this.state = PetActionState.FINISHED;
        this._stopTask();
        this._clear();
        Bukkit.getPluginManager().callEvent(new PetActionFinishEvent(this.getPet(), this));
    }

    @Override
    public final void run() {
        this.ticks += this.period;

        if (!this.getPet().getPetEntity().isSpawned()) {
            this.state = PetActionState.CANCELLED;
            this._stopTask();
            return;
        }

        this._run();
    }

    protected void _startTask(int period) {
        this._startTask(period, period);
    }

    protected void _startTask(int delay, int period) {
        this._stopTask();
        this.ticks = 0;
        this.period = period;
        task = Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(), this, delay, period);
    }

    protected void _stopTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    protected abstract void _start();
    protected abstract void _run();
    protected abstract void _clear();

    public enum PetActionType {

        SPAWN       (100, PetActionSpawn.class),
        SKIN_CHANGE (90, PetActionSkinChange.class),
        DEFEND      (50, PetActionDefend.class),
        EAT         (40, PetActionEat.class),
        HOME        (30, PetActionHome.class),
        FOLLOW      (20, PetActionFollow.class),
        SIT_BESIDE  (15, PetActionSitBeside.class),
        SIT         (10, PetActionSit.class),
        NONE        (0, PetActionNone.class),
        ;

        private final int priority;
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

        private PetActionType(int priority, Class<?> clazz) {
            this.priority = priority;
            this.clazz = clazz;
        }

        public int getPriority() {
            return priority;
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
