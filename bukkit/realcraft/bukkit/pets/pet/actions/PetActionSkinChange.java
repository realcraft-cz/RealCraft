package realcraft.bukkit.pets.pet.actions;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.ItemUtil;

public class PetActionSkinChange extends PetAction {

    public PetActionSkinChange(Pet pet) {
        super(PetActionType.SKIN_CHANGE, pet);
    }

    @Override
    protected void _start() {
        this.getEntity().getEquipment().setHelmet(ItemUtil.getHead(this.getPet().getPetData().getSkin().getValue()));
        this.finish();
    }

    @Override
    protected void _clear() {
    }

    @Override
    public void run() {
        //https://www.spigotmc.org/threads/set-a-players-rotation-without-deleting-his-velocity.535325/
    }
}
