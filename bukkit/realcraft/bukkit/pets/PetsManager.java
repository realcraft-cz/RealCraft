package realcraft.bukkit.pets;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.pets.commands.PetCommands;
import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

import java.util.HashMap;

public class PetsManager {

    private static final String PREFIX = "§d[Pets]§r ";

    private static final HashMap<User, PetPlayer> players = new HashMap<>();
    private static final HashMap<Entity, Pet> pets = new HashMap<>();

    public PetsManager() {
        new PetsListeners();
        new PetCommands();
    }

    public static PetPlayer getPetPlayer(User user) {
        if (!players.containsKey(user)) {
            players.put(user, new PetPlayer(user));
        }

        return players.get(user);
    }

    public static PetPlayer getPetPlayer(Player player) {
        return getPetPlayer(Users.getUser(player));
    }

    public static @Nullable Pet getPet(Entity entity) {
        return pets.get(entity);
    }

    public static void registerPet(Pet pet) {
        pets.put(pet.getPetEntity().getEntity(), pet);
    }

    public static void unregisterPet(Pet pet) {
        pets.remove(pet.getPetEntity().getEntity());
    }

    public static void sendMessage(String message) {
        Bukkit.broadcastMessage(PREFIX + message);
    }

    public static void sendMessage(Player player, String message) {
        if (player != null) {
            player.sendMessage(PREFIX + message);
        }
    }

    public static void sendMessage(PetPlayer fPlayer, String message) {
        sendMessage(fPlayer.getPlayer(), message);
    }

    public static boolean isDebug() {
        return RealCraft.isTestServer();
    }

    public static void debug(String message) {
        if (!isDebug()) {
            return;
        }

        RealCraft.getInstance().getLogger().info(message);
    }
}
