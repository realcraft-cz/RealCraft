package realcraft.bukkit.pets.pet.entity.labels;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.pets.pet.entity.PetEntity;

public class PetEntityLabels implements Runnable {

    private final PetEntity petEntity;
    private final PetEntityLabelText labelText;
    private final PetEntityLabelProgress labelProgress;
    private final PetEntityLabelModes labelModes;

    private PetEntityLabel currentLabel;
    private BukkitTask task;
    private int endTick;

    public PetEntityLabels(PetEntity petEntity) {
        this.petEntity = petEntity;
        this.labelText = new PetEntityLabelText(this.getPetEntity());
        this.labelProgress = new PetEntityLabelProgress(this.getPetEntity());
        this.labelModes = new PetEntityLabelModes(this.getPetEntity());
    }

    public PetEntity getPetEntity() {
        return petEntity;
    }

    public void showText(String text, int duration) {
        if (currentLabel != null && currentLabel.getType() != PetEntityLabel.PetEntityLabelType.TEXT) {
            currentLabel.remove();
        }

        labelText.setText(text);
        this._start(labelText, duration);
    }

    public void showProgress(PetEntityLabelProgress.ProgressOptions options, int duration) {
        if (currentLabel != null && currentLabel.getType() != PetEntityLabel.PetEntityLabelType.PROGRESS) {
            currentLabel.remove();
        }

        labelProgress.setOptions(options);
        this._start(labelProgress, duration);
    }

    public boolean showModes(int duration) {
        if (currentLabel != null && currentLabel.getType() != PetEntityLabel.PetEntityLabelType.MODES) {
            currentLabel.remove();
        }

        boolean isVisible = labelModes.isVisible();

        this._start(labelModes, duration);

        return isVisible;
    }

    public PetDataMode.PetDataModeType getSelectedMode() {
        return (PetDataMode.PetDataModeType) labelModes.getSelectedItem().getType();
    }

    private void _start(PetEntityLabel label, int duration) {
        this._cancel();

        currentLabel = label;
        endTick = Bukkit.getCurrentTick() + duration;
        task = Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(), this, 1, 1);

        currentLabel.show();
        this.run();
    }

    private void _cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void run() {
        if (Bukkit.getCurrentTick() > endTick) {
            this.clear();
            return;
        }

        if (currentLabel != null) {
            currentLabel.run();
        }
    }

    public void clear() {
        this._cancel();

        if (currentLabel != null) {
            currentLabel.remove();
        }
    }
}
