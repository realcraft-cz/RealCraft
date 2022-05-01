package realcraft.bukkit.pets.pet.data;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.json.JsonData;

public class PetData {

    private final Pet pet;
    private final JsonData data;

    private final PetDataName name;
    private final PetDataSkin skin;
    private final PetDataFood food;
    private final PetDataMode mode;

    public PetData(Pet pet) {
        this.pet = pet;
        this.data = new JsonData();

        this.name = new PetDataName(this.getPet());
        this.skin = new PetDataSkin(this.getPet());
        this.food = new PetDataFood(this.getPet());
        this.mode = new PetDataMode(this.getPet());

        data.addProperty(name);
        data.addProperty(skin);
        data.addProperty(food);
        data.addProperty(mode);
    }

    public Pet getPet() {
        return pet;
    }

    public PetDataName getName() {
        return name;
    }

    public PetDataSkin getSkin() {
        return skin;
    }

    public PetDataFood getFood() {
        return food;
    }

    public PetDataMode getMode() {
        return mode;
    }

    public JsonData getJsonData() {
        return data;
    }

    public void loadData(JsonData data) {
        name.loadData(data);
        skin.loadData(data);
    }
}
