package realcraft.bukkit.pets.pet.timers;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.pet.Pet;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class PetTimers implements Runnable {

    private final Pet pet;
    private final BukkitTask task;
    private final ArrayList<PetTimer> timers = new ArrayList<>();

    public PetTimers(Pet pet) {
        this.pet = pet;
        this.task = Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(), this, 20, 20);

        for (PetTimer.PetTimerType type : PetTimer.PetTimerType.values()) {
            timers.add(this._getNewTimer(type));
        }
    }

    public Pet getPet() {
        return pet;
    }

    @Override
    public void run() {
        for (PetTimer timer : timers) {
            if (timer.shouldRun()) {
                timer.run();
            }
        }
    }

    public void cancel() {
        task.cancel();
    }

    protected PetTimer _getNewTimer(PetTimer.PetTimerType type) {
        try {
            return (PetTimer) type.getClazz().getConstructor(Pet.class).newInstance(this.getPet());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }
}
