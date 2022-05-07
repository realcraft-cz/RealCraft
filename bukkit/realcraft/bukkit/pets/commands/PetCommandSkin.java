package realcraft.bukkit.pets.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.PetPlayer;
import realcraft.bukkit.pets.PetsManager;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.PetSkin;
import realcraft.bukkit.pets.pet.actions.PetAction;
import realcraft.bukkit.utils.ItemUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PetCommandSkin extends PetCommand implements Listener {

    private static final String INV_NAME = "Pet skins";
    private HashMap<PetPlayer, Integer> petPlayerPage = new HashMap<>();

    public PetCommandSkin() {
        super("skin");
        Bukkit.getPluginManager().registerEvents(this, RealCraft.getInstance());
    }

    @Override
    public void perform(PetPlayer petPlayer, String[] args) {
        Pet pet = petPlayer.getPet();
        if (pet == null) {
            petPlayer.sendMessage("§cNemas zadneho mazlika");
            return;
        }

        this._openCategories(petPlayer);
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        PetPlayer petPlayer = PetsManager.getPetPlayer(player);

        if (event.getView().getTitle().equalsIgnoreCase(INV_NAME)) {
            event.setCancelled(true);

            if (event.getRawSlot() >= 0 && event.getRawSlot() < 4 * 9) {
                ItemStack item = event.getCurrentItem();
                if (item != null && item.getType() != Material.AIR && item.hasItemMeta()) {
                    for (PetSkin.PetSkinCategory category : PetSkin.PetSkinCategory.values()) {
                        if (!ChatColor.stripColor(item.getItemMeta().getDisplayName()).equalsIgnoreCase(category.getName())) {
                            continue;
                        }

                        this._openCategory(petPlayer, category);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                        return;
                    }
                }
            }

            return;
        }

        if (event.getView().getTitle().startsWith(INV_NAME)) {
            for (PetSkin.PetSkinCategory category : PetSkin.PetSkinCategory.values()) {
                if (event.getView().getTitle().equalsIgnoreCase(this._getCategoryInvName(category))) {
                    event.setCancelled(true);

                    if (event.getRawSlot() >= 0 && event.getRawSlot() < 6 * 9) {
                        ItemStack item = event.getCurrentItem();
                        if (item != null && item.getType() != Material.AIR && item.hasItemMeta()) {
                            if (item.getType() == Material.PAPER) {
                                if (event.getRawSlot() == 45) {
                                    this._openCategory(petPlayer, category, petPlayerPage.get(petPlayer) - 1);
                                } else if (event.getRawSlot() == 53) {
                                    this._openCategory(petPlayer, category, petPlayerPage.get(petPlayer) + 1);
                                }

                                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                            } else {
                                if (event.getRawSlot() == 49) {
                                    this._openCategories(petPlayer);
                                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                                } else {
                                    if (petPlayer.getPet() != null) {
                                        for (PetSkin skin : category.getSkins()) {
                                            if (!ChatColor.stripColor(item.getItemMeta().getDisplayName()).equalsIgnoreCase(skin.getName())) {
                                                continue;
                                            }

                                            petPlayer.getPet().getPetData().getSkin().setSkin(skin);
                                            petPlayer.getPet().getPetActions().setActionType(PetAction.PetActionType.SKIN_CHANGE);
                                            petPlayer.getPlayer().playSound(petPlayer.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                                            break;
                                        }
                                    }

                                    petPlayer.getPlayer().closeInventory();
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public List<String> tabCompleter(PetPlayer petPlayer, String[] args) {
        return null;
    }

    private void _openCategories(PetPlayer petPlayer) {
        Inventory inventory = Bukkit.createInventory(petPlayer.getPlayer(), 5 * 9, INV_NAME);

        PetSkin skin = petPlayer.getPet().getPetData().getSkin().getSkin();
        inventory.setItem(9 + 4, ItemUtil.getHead("§r" + skin.getName(), skin.getTexture()));

        int index = (9 * 3) + 4 - 1;
        for (PetSkin.PetSkinCategory category : PetSkin.PetSkinCategory.values()) {
            ItemStack item = new ItemStack(category.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§e§l" + category.getName());
            ArrayList<String> lores = new ArrayList<String>();
            lores.add("§7Klikni pro otevreni");
            meta.setLore(lores);

            item.setItemMeta(meta);

            inventory.setItem(index++, item);
        }

        petPlayer.getPlayer().openInventory(inventory);
    }

    private void _openCategory(PetPlayer petPlayer, PetSkin.PetSkinCategory category) {
        this._openCategory(petPlayer, category, 1);
    }

    private void _openCategory(PetPlayer petPlayer, PetSkin.PetSkinCategory category, int page) {
        petPlayerPage.put(petPlayer, page);

        Inventory inventory = Bukkit.createInventory(null, 6 * 9, this._getCategoryInvName(category));
        ItemStack item;
        ItemMeta meta;

        ArrayList<PetSkin> skins = category.getSkins();
        for (int i = 0; i < 5 * 9; i++) {
            int index = i + ((page - 1) * (5 * 9));
            if (skins.size() > index) {
                PetSkin skin = skins.get(index);
                if (skin != null) {
                    inventory.setItem(i, ItemUtil.getHead("§r" + skin.getName(), skin.getTexture()));
                }
            }
        }

        item = new ItemStack(Material.SKELETON_SKULL);
        meta = item.getItemMeta();
        meta.setDisplayName("§e§lSkins");
        ArrayList<String> lores = new ArrayList<>();
        lores.add("§7Klikni pro navrat");
        meta.setLore(lores);
        item.setItemMeta(meta);
        inventory.setItem(49, item);

        int maxPage = (int) Math.ceil(skins.size() / (5 * 9.0));

        if (page > 1) {
            item = new ItemStack(Material.PAPER);
            meta = item.getItemMeta();
            meta.setDisplayName("§6§lPredchozi");
            item.setItemMeta(meta);
            inventory.setItem(45, item);
        }

        if (page < maxPage) {
            item = new ItemStack(Material.PAPER);
            meta = item.getItemMeta();
            meta.setDisplayName("§6§lDalsi");
            item.setItemMeta(meta);
            inventory.setItem(53, item);
        }

        petPlayer.getPlayer().openInventory(inventory);
    }

    private String _getCategoryInvName(PetSkin.PetSkinCategory category) {
        return INV_NAME + " > " + category.getName();
    }
}
