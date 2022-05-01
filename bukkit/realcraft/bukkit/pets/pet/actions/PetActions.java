package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.PetsManager;
import realcraft.bukkit.pets.pet.Pet;

import java.lang.reflect.InvocationTargetException;

public class PetActions implements Runnable {

    private final Pet pet;
    private final BukkitTask task;

    private PetAction currentAction;
    private PetAction nextAction;

    public PetActions(Pet pet) {
        this.pet = pet;
        this.currentAction = new PetActionNone(this.getPet());
        this.task = Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(), this, 20, 20);
    }

    public Pet getPet() {
        return pet;
    }

    public @NotNull PetAction getCurrentAction() {
        return currentAction;
    }

    protected void _setCurrentAction(@NotNull PetAction currentAction) {
        this.currentAction = currentAction;

        if (PetsManager.isDebug()) {
            if (this.getPet().getPetEntity().isLiving()) {
                this.getPet().getPetEntity().getEntity().setCustomName(this.getCurrentAction().getType().toString());
            }
        }
    }

    public @Nullable PetAction getNextAction() {
        return nextAction;
    }

    protected void _setNextAction(@Nullable PetAction nextAction) {
        this.nextAction = nextAction;
    }

    public void setAction(@Nullable PetAction nextAction) {
        this._setNextAction(nextAction);

        if (this.getCurrentAction().getState() == PetAction.PetActionState.RUNNING) {
            if (!this.getCurrentAction().isCancellable()) {
                return;
            }

            this.getCurrentAction().cancel();
        }

        if (this.getNextAction() == null) {
            this._setNextAction(new PetActionNone(this.getPet()));
        }

        this._setCurrentAction(this.getNextAction());
        this._setNextAction(null);

        this.getCurrentAction().start();
    }

    @Override
    public void run() {
        if (!this.getPet().getPetEntity().isLiving()) {
            return;
        }

        PetAction nextAction = null;

        for (PetAction.PetActionType type : PetAction.PetActionType.getSortedTypes()) {
            nextAction = this._getNewAction(type);

            if (nextAction == null || !nextAction.shouldStart() || (nextAction.getType() == this.getCurrentAction().getType() && this.getCurrentAction().getState() == PetAction.PetActionState.RUNNING)) {
                nextAction = null;
                continue;
            }

            break;
        }

        if (nextAction != null || this.getCurrentAction().getState() != PetAction.PetActionState.RUNNING) {
            this.setAction(nextAction);
        }
    }

    public void cancel() {
        task.cancel();
    }

    protected PetAction _getNewAction(PetAction.PetActionType type) {
        try {
            return (PetAction) type.getClazz().getConstructor(Pet.class).newInstance(this.getPet());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }
}
