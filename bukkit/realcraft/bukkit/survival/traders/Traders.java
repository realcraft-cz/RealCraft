package realcraft.bukkit.survival.traders;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import realcraft.bukkit.RealCraft;
import realcraft.share.utils.RandomUtil;

import java.util.ArrayList;

public class Traders implements Listener {

    public Traders(){
        Bukkit.getPluginManager().registerEvents(this, RealCraft.getInstance());
    }

    @EventHandler
    public void VillagerAcquireTradeEvent(VillagerAcquireTradeEvent event){
        Villager villager = (Villager)event.getEntity();
        if (villager.getProfession() != Villager.Profession.LEATHERWORKER) {
            return;
        }

        for (MerchantRecipe recipe : villager.getRecipes()) {
            if (recipe.getResult().getType() == Material.ELYTRA) {
                return;
            }
        }

        if (RandomUtil.getRandomInteger(1,4) != 1) {
            return;
        }

        Bukkit.getScheduler().runTask(RealCraft.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (MerchantRecipe recipe : villager.getRecipes()) {
                    if (recipe.getResult().getType() == Material.ELYTRA) {
                        return;
                    }
                }

                MerchantRecipe recipe = new MerchantRecipe(new ItemStack(Material.ELYTRA), 1);
                recipe.addIngredient(new ItemStack(Material.DIAMOND, RandomUtil.getRandomInteger(48, 54)));
                recipe.addIngredient(new ItemStack(Material.NETHERITE_INGOT, RandomUtil.getRandomInteger(16, 24)));
                recipe.setPriceMultiplier(0.1f);
                recipe.setIgnoreDiscounts(false);

                ArrayList<MerchantRecipe> recipes = new ArrayList<>(villager.getRecipes());
                recipes.add(recipe);
                villager.setRecipes(recipes);
            }
        });
    }
}
