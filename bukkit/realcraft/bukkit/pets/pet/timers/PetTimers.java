package realcraft.bukkit.pets.pet.timers;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.timers.PetTimer.PetTimerType;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class PetTimers implements Runnable {

    private final Pet pet;
    private final HashMap<PetTimerType, PetTimer> timers = new HashMap<>();

    private BukkitTask task;

    public PetTimers(Pet pet) {
        this.pet = pet;

        for (PetTimerType type : PetTimerType.values()) {
            try {
                PetTimer timer = (PetTimer) type.getClazz().getConstructor(Pet.class).newInstance(this.getPet());
                timers.put(type, timer);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public Pet getPet() {
        return pet;
    }

    public PetTimer getTimer(PetTimerType type) {
        return timers.get(type);
    }

    @Override
    public void run() {
        for (PetTimer timer : timers.values()) {
            if (timer.shouldRun()) {
                timer.run();
            }
        }
    }

    public void start() {
        this.cancel();
        task = Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(), this, 20, 20);
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
