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

    public PetEntityLabelText getLabelText() {
        return labelText;
    }

    public void showText(String text, int duration) {
        if (currentLabel != null && currentLabel.getType() != PetEntityLabel.PetEntityLabelType.TEXT) {
            currentLabel.remove();
        }

        labelText.setText(text);
        this._start(labelText, duration);
    }

    public PetEntityLabelProgress getLabelProgress() {
        return labelProgress;
    }

    public void showProgress(PetEntityLabelProgress.ProgressOptions options, int duration) {
        this.showProgress(null, options, duration);
    }

    public void showProgress(String name, PetEntityLabelProgress.ProgressOptions options, int duration) {
        if (currentLabel != null && currentLabel.getType() != PetEntityLabel.PetEntityLabelType.PROGRESS) {
            currentLabel.remove();
        }

        labelProgress.setName(name);
        labelProgress.setOptions(options);
        this._start(labelProgress, duration);
    }

    public PetEntityLabelModes getLabelModes() {
        return labelModes;
    }

    public boolean showModes(PetDataMode.PetDataModeType defaultType, int duration) {
        if (currentLabel != null && currentLabel.getType() != PetEntityLabel.PetEntityLabelType.MODES) {
            currentLabel.remove();
        }

        boolean isVisible = labelModes.isVisible();

        if (!isVisible) {
            labelModes.setCurrentItemType(defaultType);
        }

        this._start(labelModes, duration);

        return isVisible;
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
