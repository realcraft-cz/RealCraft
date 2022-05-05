package realcraft.bukkit.pets.pet.data;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.json.JsonData;

public class PetData {

    private final Pet pet;

    private final PetDataName name;
    private final PetDataSkin skin;
    private final PetDataEffect effect;
    private final PetDataFood food;
    private final PetDataMode mode;
    private final PetDataHome home;

    public PetData(Pet pet) {
        this.pet = pet;

        this.name = new PetDataName(this.getPet());
        this.skin = new PetDataSkin(this.getPet());
        this.effect = new PetDataEffect(this.getPet());
        this.food = new PetDataFood(this.getPet());
        this.mode = new PetDataMode(this.getPet());
        this.home = new PetDataHome(this.getPet());
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

    public PetDataEffect getEffect() {
        return effect;
    }

    public PetDataFood getFood() {
        return food;
    }

    public PetDataMode getMode() {
        return mode;
    }

    public PetDataHome getHome() {
        return home;
    }

    public JsonData getJsonData() {
        JsonData data = new JsonData();

        data.addProperty(name);
        data.addProperty(skin);
        data.addProperty(effect);
        data.addProperty(food);
        data.addProperty(mode);
        data.addProperty(home);

        return data;
    }

    public void loadData(JsonData data) {
        name.loadData(data);
        skin.loadData(data);
        effect.loadData(data);
        food.loadData(data);
        mode.loadData(data);
        home.loadData(data);
    }
}
