package realcraft.bukkit.pets.commands;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.ChatColor;
import realcraft.bukkit.pets.PetPlayer;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.pets.pet.data.PetDataFood;
import realcraft.bukkit.pets.pet.data.PetDataHealth;
import realcraft.bukkit.utils.MathUtil;

import java.util.List;

public class PetCommandInfo extends PetCommand {

    private static final String CHAR_WARNING = "\u26a0";

    public PetCommandInfo() {
        super("info");
    }

    @Override
    public void perform(PetPlayer petPlayer, String[] args) {
        Pet pet = petPlayer.getPet();
        if (pet == null) {
            petPlayer.sendMessage("§cNemas zadneho mazlika");
            return;
        }

        Book.Builder book = Book.builder();

        book.addPage(this._buildHomePage(pet));
        book.addPage(this._buildStatsPage(pet));

        petPlayer.getPlayer().openBook(book);
    }

    private Component _buildHomePage(Pet pet) {
        Component health = Component.text(PetDataHealth.COLOR + PetDataHealth.CHAR.repeat(pet.getPetData().getHealth().getValue()) + ChatColor.GRAY + PetDataHealth.CHAR.repeat(pet.getPetData().getHealth().getMaxValue() - pet.getPetData().getHealth().getValue()));
        Component healthHover = Component.text(pet.getPetData().getHealth().getValue() + "/" + pet.getPetData().getHealth().getMaxValue()).font(Key.key("uniform"));

        Component food = Component.text(PetDataFood.COLOR + PetDataFood.CHAR.repeat(pet.getPetData().getFood().getValue()) + ChatColor.GRAY + PetDataFood.CHAR.repeat(pet.getPetData().getFood().getMaxValue() - pet.getPetData().getFood().getValue()));
        Component foodHover = Component.text(pet.getPetData().getFood().getValue() + "/" + pet.getPetData().getFood().getMaxValue()).font(Key.key("uniform"));

        Component skin = Component.text(ChatColor.DARK_BLUE + pet.getPetData().getSkin().getSkin().toString());
        Component skinHover = Component.text("§7Skin nastavis prikazem §6/pet skin").font(Key.key("uniform"));

        Component effect = Component.text(ChatColor.DARK_PURPLE + pet.getPetData().getEffect().getType().toString());
        Component effectHover = Component.text("§7Efekt nastavis prikazem §6/pet effect").font(Key.key("uniform"));

        Component home = Component.text(ChatColor.RED + "nenastaven");
        Component homeHover = Component.text("§7Domov nastavis prikazem §6/pet home").font(Key.key("uniform"));
        ClickEvent homeClick = null;

        if (pet.getPetData().getHome().getLocation() != null) {
            home = Component.text("" + ChatColor.BLUE + pet.getPetData().getHome().getLocation().getBlockX() + " / " + pet.getPetData().getHome().getLocation().getBlockY() + " / " + pet.getPetData().getHome().getLocation().getBlockZ());
            homeHover = Component.text("§7Klikni pro teleport").font(Key.key("uniform"));
            homeClick = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/pet home teleport");
        }

        Component position = Component.text(ChatColor.RED + "zadna");
        Component positionHover = null;
        ClickEvent positionClick = null;

        if (pet.getPetEntity().isSpawned()) {
            position = Component.text("" + ChatColor.BLUE + pet.getPetEntity().getEntity().getLocation().getBlockX() + " / " + pet.getPetEntity().getEntity().getLocation().getBlockY() + " / " + pet.getPetEntity().getEntity().getLocation().getBlockZ());
            positionHover = Component.text("§7Klikni pro teleport").font(Key.key("uniform"));
            positionClick = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/pet tp");
        }

        Component[] lines = new Component[]{
            Component.text("           §d§l§nMazlik"),
            Component.text(""),
            Component.text(" ")
                .append(Component.text("§8Zdravi: " + (pet.getPetData().getHealth().getValue() <= pet.getPetData().getHealth().getCriticalValue() ? ChatColor.RED + CHAR_WARNING : "")).hoverEvent(healthHover))
                .append(Component.text(" ".repeat(pet.getPetData().getHealth().getValue() <= pet.getPetData().getHealth().getCriticalValue() ? 7 : 9)))
                .append(Component.text("§8Jidlo: " + (pet.getPetData().getFood().getValue() <= pet.getPetData().getFood().getCriticalValue() ? ChatColor.RED + CHAR_WARNING : "")).hoverEvent(foodHover)),
            Component.text(" ")
                .append(health.hoverEvent(healthHover))
                .append(Component.text(" ".repeat(6)))
                .append(food.hoverEvent(foodHover)),
            Component.text(""),
            Component.text(" ")
                .append(Component.text("§8Skin:").hoverEvent(skinHover))
                .append(Component.text(" ".repeat(12)))
                .append(Component.text("§8Efekt:").hoverEvent(effectHover)),
            Component.text(" ")
                .append(skin.hoverEvent(skinHover))
                .append(Component.text(" ".repeat(16 - pet.getPetData().getSkin().getSkin().toString().length())))
                .append(effect.hoverEvent(effectHover)),
            Component.text(""),
            Component.text(" ")
                .append(Component.text("§8Aktualni pozice:").hoverEvent(positionHover).clickEvent(positionClick)),
            Component.text(" ")
                .append(position.hoverEvent(positionHover).clickEvent(positionClick)),
            Component.text(""),
            Component.text(" ")
                .append(Component.text("§8Domov:").hoverEvent(homeHover).clickEvent(homeClick)),
            Component.text(" ")
                .append(home.hoverEvent(homeHover).clickEvent(homeClick)),
        };

        TextComponent.Builder builder = Component.text();
        for (Component line : lines) {
            builder.append(line).append(Component.newline());
        }

        return builder.build().font(Key.key("uniform"));
    }

    private Component _buildStatsPage(Pet pet) {
        int distance = pet.getPetData().getStatDistance().getValue();

        Component[] lines = new Component[]{
            Component.text("           §d§l§nMazlik"),
            Component.text(""),
            Component.text(" §8Nachozeno:"),
            Component.text(" ").append(Component.text("" + ChatColor.BLUE + (distance < 1000 ? distance : MathUtil.round(distance / 1000f, 1)) + " " + (distance < 1000 ? "m" : "km"))),
        };

        TextComponent.Builder builder = Component.text();
        for (Component line : lines) {
            builder.append(line).append(Component.newline());
        }

        return builder.build().font(Key.key("uniform"));
    }

    @Override
    public List<String> tabCompleter(PetPlayer petPlayer, String[] args) {
        return null;
    }
}
