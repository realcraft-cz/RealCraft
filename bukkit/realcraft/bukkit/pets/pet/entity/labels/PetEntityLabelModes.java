package realcraft.bukkit.pets.pet.entity.labels;

import org.bukkit.ChatColor;
import realcraft.bukkit.pets.pet.actions.PetAction;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.pets.pet.entity.PetEntity;

public class PetEntityLabelModes extends PetEntityLabelRotable {

    public PetEntityLabelModes(PetEntity petEntity) {
        super(PetEntityLabelType.MODES, petEntity, new RotableItem[]{
            new RotableItem(
                PetDataMode.PetDataModeType.HOME,
                PetDataMode.PetDataModeType.HOME.getColor() + PetDataMode.PetDataModeType.HOME.getName(),
                ChatColor.GRAY + PetDataMode.PetDataModeType.HOME.getName()
            ),
            new RotableItem(
                PetDataMode.PetDataModeType.FOLLOW,
                PetDataMode.PetDataModeType.FOLLOW.getColor() + PetDataMode.PetDataModeType.FOLLOW.getName(),
                ChatColor.GRAY + PetDataMode.PetDataModeType.FOLLOW.getName()
            ),
            new RotableItem(
                PetDataMode.PetDataModeType.SIT,
                PetDataMode.PetDataModeType.SIT.getColor() + PetDataMode.PetDataModeType.SIT.getName(),
                ChatColor.GRAY + PetDataMode.PetDataModeType.SIT.getName()
            ),
        });
    }

    @Override
    public void show() {
        for (RotableItem item : this.getItems()) {
            PetDataMode.PetDataModeType type = (PetDataMode.PetDataModeType) item.getType();

            if (type == PetDataMode.PetDataModeType.HOME) {
                if (this.getPetEntity().getPet().getPetData().getHome().getLocation() != null
                    && this.getPetEntity().getPet().getPetData().getMode().getType() != PetDataMode.PetDataModeType.HOME) {
                    item.setSelectedText(type.getColor() + type.getName());
                    item.setDisabledText(ChatColor.GRAY + type.getName());
                } else {
                    item.setSelectedText("" + type.getColor() + ChatColor.STRIKETHROUGH + type.getName());
                    item.setDisabledText("" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + type.getName());
                }
            } else if (type == PetDataMode.PetDataModeType.SIT) {
                if (this.getPetEntity().getPet().getPetData().getMode().getType() != PetDataMode.PetDataModeType.SIT
                    && this.getPetEntity().getPet().getPetData().getMode().getType() != PetDataMode.PetDataModeType.HOME
                    && this.getPetEntity().getPet().getPetActions().getCurrentAction().getType() != PetAction.PetActionType.SIT_BESIDE) {
                    item.setSelectedText(type.getColor() + type.getName());
                    item.setDisabledText(ChatColor.GRAY + type.getName());
                } else {
                    item.setSelectedText("" + type.getColor() + ChatColor.STRIKETHROUGH + type.getName());
                    item.setDisabledText("" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + type.getName());
                }
            }
        }

        super.show();
    }
}
