package realcraft.bukkit.pets.pet.entity.labels;

import org.bukkit.ChatColor;
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
}
