package realcraft.bukkit.pets.pet;

public enum PetPermission {

    NONE(0), OWNER(1);

    private final int id;

    private PetPermission(int id) {
        this.id = id;
    }

    private int getId() {
        return id;
    }

    public boolean isMinimum(PetPermission perm) {
        return (this.getId() >= perm.getId());
    }

    public boolean isMaximum(PetPermission perm) {
        return (this.getId() <= perm.getId());
    }
}
