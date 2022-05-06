package realcraft.bukkit.pets.pet.actions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.Nullable;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.pets.pet.entity.labels.PetEntityLabelProgress;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bukkit.utils.Particles;

import java.util.HashMap;

public class PetActionEat extends PetAction {

    private static final HashMap<Material, Food> FOODS = new HashMap<>();
    private static final double MAX_FOOD_DISTANCE = 5.0;
    private static final double FOOD_REACH_DISTANCE = 1.2;

    private Item foodItem;
    private State state;

    public PetActionEat(Pet pet) {
        super(PetActionType.EAT, pet);
    }

    @Override
    public boolean shouldStart() {
        if (this.getPet().getPetData().getFood().getValue() >= this.getPet().getPetData().getFood().getMaxValue()) {
            return false;
        }

        for (Entity entity : this.getEntity().getLocation().getNearbyEntities(MAX_FOOD_DISTANCE, 2.2, MAX_FOOD_DISTANCE)) {
            if (entity.getType() == EntityType.DROPPED_ITEM && entity.isOnGround()) {
                if (this.getFood(((Item) entity).getItemStack().getType()) != null) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void _start() {
        if (this.getPet().getPetData().getMode().getType() == PetDataMode.PetDataModeType.SIT) {
            this.getPet().getPetData().getMode().setType(PetDataMode.PetDataModeType.FOLLOW);
        }

        this.state = State.MOVING;
        this.getEntity().setAI(true);
        this.getEntity().setGravity(true);

        for (Entity entity : this.getEntity().getLocation().getNearbyEntities(MAX_FOOD_DISTANCE, 2.2, MAX_FOOD_DISTANCE)) {
            if (entity.getType() == EntityType.DROPPED_ITEM) {
                if (this.getFood(((Item) entity).getItemStack().getType()) != null) {
                    this.foodItem = (Item) entity;
                    return;
                }
            }
        }

        this.cancel();
    }

    @Override
    protected void _clear() {
    }

    @Override
    public void run() {
        if (!this.foodItem.isValid()) {
            this.cancel();
            return;
        }

        if (this.state == State.MOVING) {
            Location targetLoc = this.foodItem.getLocation();
            double distance = this.getEntity().getLocation().distance(targetLoc);

            if (distance > MAX_FOOD_DISTANCE + 2) {
                this.cancel();
                return;
            }

            if (distance < FOOD_REACH_DISTANCE) {
                this._startEating();
                return;
            }

            if (LocationUtil.isSimilar(EntityUtil.getTargetLocation(this.getEntity()), targetLoc)) {
                return;
            }

            EntityUtil.navigate(this.getEntity(), targetLoc, 0.7);
        } else if (this.state == State.EATING) {

        }
    }

    private void _startEating() {
        this.state = State.EATING;

        Food food = this.getFood(foodItem.getItemStack().getType());
        if (food != null) {
            int oldFoodValue = this.getPet().getPetData().getFood().getValue();
            this.getPet().getPetData().getFood().setValue(this.getPet().getPetData().getFood().getValue() + food.nutrition);

            PetActionEat.this.getPet().getPetEntity().getEntityLabels().showProgress(new PetEntityLabelProgress.ProgressOptions(
                oldFoodValue,
                PetActionEat.this.getPet().getPetData().getFood().getMaxValue(),
                PetActionEat.this.getPet().getPetData().getFood().getValue() - oldFoodValue,
                ChatColor.GOLD,
                ChatColor.GRAY,
                ChatColor.GREEN
            ), 20);

            Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(), new Runnable() {
                @Override
                public void run() {
                    PetActionEat.this.getPet().getPetEntity().getEntityLabels().showProgress(new PetEntityLabelProgress.ProgressOptions(
                        PetActionEat.this.getPet().getPetData().getFood().getValue(),
                        PetActionEat.this.getPet().getPetData().getFood().getMaxValue(),
                        ChatColor.GOLD,
                        ChatColor.GRAY
                    ), 20);
                }
            }, 10);
        }

        Particles.HEART.display(0f, 0f, 0f, 0f, 1, this.getEntity().getEyeLocation().add(0f, 0.5f, 0f), 64);
        this.foodItem.remove();

        this.finish();
    }

    public @Nullable Food getFood(Material type) {
        return FOODS.get(type);
    }

    private record Food(Material type, int nutrition, boolean poisoned) {

        Food(Material type, int nutrition) {
            this(type, nutrition, false);
        }
    }

    static {
        Food[] foods = new Food[]{
            new Food(Material.APPLE, 2),
            new Food(Material.BAKED_POTATO, 3),
            new Food(Material.BEETROOT, 1),
            new Food(Material.BREAD, 3),
            new Food(Material.CARROT, 1),
            new Food(Material.CHORUS_FRUIT, 2),
            new Food(Material.COOKED_CHICKEN, 3),
            new Food(Material.COOKED_COD, 3),
            new Food(Material.COOKED_MUTTON, 3),
            new Food(Material.COOKED_PORKCHOP, 4),
            new Food(Material.COOKED_RABBIT, 3),
            new Food(Material.COOKED_SALMON, 3),
            new Food(Material.COOKIE, 1),
            new Food(Material.DRIED_KELP, 1),
            new Food(Material.ENCHANTED_GOLDEN_APPLE, 2),
            new Food(Material.GOLDEN_APPLE, 2),
            new Food(Material.GLOW_BERRIES, 1),
            new Food(Material.GOLDEN_CARROT, 3),
            new Food(Material.MELON_SLICE, 1),
            new Food(Material.POISONOUS_POTATO, 1, true),
            new Food(Material.POTATO, 1),
            new Food(Material.BEEF, 1),
            new Food(Material.CHICKEN, 1),
            new Food(Material.COD, 1),
            new Food(Material.MUTTON, 1),
            new Food(Material.PORKCHOP, 1),
            new Food(Material.RABBIT, 1),
            new Food(Material.SALMON, 1),
            new Food(Material.ROTTEN_FLESH, 2, true),
            new Food(Material.SPIDER_EYE, 1, true),
            new Food(Material.SWEET_BERRIES, 1),
            new Food(Material.TROPICAL_FISH, 1),
        };

        for (Food food : foods) {
            FOODS.put(food.type(), food);
        }
    }

    private enum State {
        MOVING, EATING
    }
}
