package realcraft.bukkit.pets.pet.actions;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataMode;
import realcraft.bukkit.pets.pet.entity.labels.PetEntityLabelProgress;
import realcraft.bukkit.utils.EntityUtil;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bukkit.utils.RandomUtil;

import java.util.HashMap;

public class PetActionEat extends PetAction {

    private static final HashMap<Material, Food> FOODS = new HashMap<>();
    private static final double MAX_FOOD_DISTANCE = 5.0;
    private static final double FOOD_REACH_DISTANCE = 1.5;
    private static final int SAME_LOCATION_THRESHOLD = 4;

    private State state;
    private Item foodItem;
    private ItemStack foodItemStack;

    private Location entityLastLocation;
    private int sameLocationCounter;
    private int maxFoodSteps;

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
        this.entityLastLocation = this.getEntity().getLocation();
        this.sameLocationCounter = 0;
        this.maxFoodSteps = 1;

        this.getEntity().setAI(true);
        this.getEntity().setGravity(true);

        for (Entity entity : this.getEntity().getLocation().getNearbyEntities(MAX_FOOD_DISTANCE, 2.2, MAX_FOOD_DISTANCE)) {
            if (entity.getType() == EntityType.DROPPED_ITEM) {
                if (this.getFood(((Item) entity).getItemStack().getType()) != null) {
                    this.foodItem = (Item) entity;
                    this._startTask(0, 5);
                    return;
                }
            }
        }

        this.cancel();
    }

    @Override
    protected void _run() {
        if (this.state == State.MOVING) {
            if (!this.foodItem.isValid()) {
                this.cancel();
                return;
            }

            Location targetLoc = this.foodItem.getLocation();
            double distance = this.getEntity().getLocation().distance(targetLoc);

            if (distance > MAX_FOOD_DISTANCE + 2 || sameLocationCounter >= SAME_LOCATION_THRESHOLD) {
                this.cancel();
                return;
            }

            boolean isMoving = Math.abs(this.getEntity().getVelocity().getX()) > 0.01 || Math.abs(this.getEntity().getVelocity().getZ()) > 0.01 || Math.abs(this.getEntity().getVelocity().getY()) > 0.1;

            if (distance < FOOD_REACH_DISTANCE && !isMoving) {
                this.setTicks(0);

                this.getEntity().playPickupItemAnimation(this.foodItem);
                this.foodItemStack = this.foodItem.getItemStack();
                this.foodItem.remove();

                Food food = this.getFood(this.foodItemStack.getType());
                if (food != null) {
                    maxFoodSteps = Math.max(1, (int)Math.ceil(((this.getPet().getPetData().getFood().getMaxValue() - this.getPet().getPetData().getFood().getValue()) * 1f) / food.nutrition));
                    maxFoodSteps = Math.min(maxFoodSteps, this.foodItemStack.getAmount());
                }

                this.state = State.EATING;
                this._startTask(4);
                return;
            }

            if (this.getEntity().getLocation().equals(entityLastLocation)) {
                sameLocationCounter ++;
            }

            if (LocationUtil.isSimilar(EntityUtil.getTargetLocation(this.getEntity()), targetLoc)) {
                return;
            }

            EntityUtil.navigate(this.getEntity(), targetLoc, 0.7);
        } else if (this.state == State.EATING) {
            Location particleLocation = this.getEntity().getEyeLocation().add(this.getEntity().getEyeLocation().getDirection().setY(0).normalize().multiply(0.3));
            this.getEntity().getWorld().spawnParticle(Particle.ITEM_CRACK, particleLocation, 3, 0.1, 0.1, 0.1, 0.05, this.foodItemStack);
            this.getEntity().getWorld().playSound(getEntity().getLocation(), Sound.ENTITY_GENERIC_EAT, 0.7f, 1f);

            if (this.getTicks() % 12 == 0) {
                this._eatOnePiece();
                this.getEntity().setRotation(this.getEntity().getLocation().getYaw() + RandomUtil.getRandomInteger(-40, 40), this.getEntity().getLocation().getPitch());
            }

            if (this.getTicks() + 3 >= 12 * Math.min(maxFoodSteps, this.foodItemStack.getAmount())) {
                this._finish();
            }
        }

        this.entityLastLocation = this.getEntity().getLocation();
    }

    @Override
    protected void _clear() {
        EntityUtil.clearPathfinders(this.getEntity());
    }

    private void _eatOnePiece() {
        Food food = this.getFood(this.foodItemStack.getType());
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
        }
    }

    private void _finish() {
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
        }, 6);

        if (this.getPet().getPetData().getFood().getValue() == this.getPet().getPetData().getFood().getMaxValue()) {
            this.getEntity().getWorld().playSound(getEntity().getLocation(), Sound.ENTITY_PLAYER_BURP, 0.7f, 1f);
        }

        int leftoverAmount = Math.max(0, this.foodItemStack.getAmount() - maxFoodSteps);
        if (leftoverAmount > 0) {
            this.foodItemStack.setAmount(leftoverAmount);
            this.getEntity().getWorld().dropItem(this.getEntity().getLocation(), this.foodItemStack);
        }

        this.getEntity().getWorld().spawnParticle(Particle.HEART, this.getEntity().getEyeLocation().add(0, 0.5, 0), 1, 0, 0, 0, 0);

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
