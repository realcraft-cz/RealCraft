package realcraft.bukkit.pets.pet.actions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import realcraft.bukkit.pets.PetsManager;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.actions.PetAction.PetActionType;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class PetActions {

    private final Pet pet;
    private final HashMap<PetActionType, PetAction> petActions = new HashMap<>();

    private PetActionType currentActionType;
    private PetActionType nextActionType;

    public PetActions(Pet pet) {
        this.pet = pet;

        for (PetActionType type : PetActionType.getSortedTypes()) {
            try {
                PetAction action = (PetAction) type.getClazz().getConstructor(Pet.class).newInstance(this.getPet());
                petActions.put(type, action);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        this.currentActionType = PetActionType.NONE;
    }

    public Pet getPet() {
        return pet;
    }

    public PetAction getAction(PetActionType type) {
        return petActions.get(type);
    }

    public @NotNull PetAction getCurrentAction() {
        return this.getAction(this.getCurrentActionType());
    }

    public @NotNull PetActionType getCurrentActionType() {
        return currentActionType;
    }

    protected void _setCurrentActionType(@NotNull PetActionType currentActionType) {
        this.currentActionType = currentActionType;

        if (PetsManager.isDebug()) {
            if (this.getPet().getPetEntity().isLiving()) {
                this.getPet().getPetEntity().getEntity().setCustomName(this.getCurrentActionType().toString());
            }
        }
    }

    public @Nullable PetActionType getNextActionType() {
        return nextActionType;
    }

    protected void _setNextActionType(@Nullable PetActionType nextActionType) {
        this.nextActionType = nextActionType;
    }

    public void setActionType(@NotNull PetActionType nextActionType) {
        this._setNextActionType(nextActionType);

        if (this.getCurrentAction().getState() == PetAction.PetActionState.RUNNING) {
            if (!this.getCurrentAction().isCancellable()) {
                return;
            }

            this.getCurrentAction().cancel();
        }

        if (this.getNextActionType() != null) {
            this._setCurrentActionType(this.getNextActionType());
        }

        this._setNextActionType(null);

        this.getCurrentAction().start();
    }

    public void run() {
        if (!this.getPet().getPetEntity().isLiving()) {
            return;
        }

        for (PetActionType type : PetActionType.getSortedTypes()) {
            PetAction action = this.getAction(type);
            if (!action.shouldStart() || (action.getType() == this.getCurrentActionType() && this.getCurrentAction().getState() == PetAction.PetActionState.RUNNING)) {
                continue;
            }

            this.setActionType(action.getType());
            return;
        }
    }
}
