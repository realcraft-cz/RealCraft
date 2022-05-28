package realcraft.bukkit.pets.pet.data;

import realcraft.bukkit.pets.pet.Pet;
import realcraft.bukkit.utils.json.JsonData;

public class PetData {

    private final Pet pet;

    private final PetDataSkin skin;
    private final PetDataEffect effect;
    private final PetDataHealth health;
    private final PetDataHeat heat;
    private final PetDataFood food;
    private final PetDataMode mode;
    private final PetDataHome home;
    private final PetDataStatDistance statDistance;
    private final PetDataStatKills statKills;

    public PetData(Pet pet) {
        this.pet = pet;

        this.skin = new PetDataSkin(this.getPet());
        this.effect = new PetDataEffect(this.getPet());
        this.health = new PetDataHealth(this.getPet());
        this.heat = new PetDataHeat(this.getPet());
        this.food = new PetDataFood(this.getPet());
        this.mode = new PetDataMode(this.getPet());
        this.home = new PetDataHome(this.getPet());
        this.statDistance = new PetDataStatDistance(this.getPet());
        this.statKills = new PetDataStatKills(this.getPet());
    }

    public Pet getPet() {
        return pet;
    }

    public PetDataSkin getSkin() {
        return skin;
    }

    public PetDataEffect getEffect() {
        return effect;
    }

    public PetDataHealth getHealth() {
        return health;
    }

    public PetDataHeat getHeat() {
        return heat;
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

    public PetDataStatDistance getStatDistance() {
        return statDistance;
    }

    public PetDataStatKills getStatKills() {
        return statKills;
    }

    public JsonData getJsonData() {
        JsonData data = new JsonData();

        data.addProperty(skin);
        data.addProperty(effect);
        data.addProperty(health);
        data.addProperty(heat);
        data.addProperty(food);
        data.addProperty(mode);
        data.addProperty(home);
        data.addProperty(statDistance);
        data.addProperty(statKills);

        return data;
    }

    public void loadData(JsonData data) {
        skin.loadData(data);
        effect.loadData(data);
        health.loadData(data);
        heat.loadData(data);
        food.loadData(data);
        mode.loadData(data);
        home.loadData(data);
        statDistance.loadData(data);
        statKills.loadData(data);
    }
}
