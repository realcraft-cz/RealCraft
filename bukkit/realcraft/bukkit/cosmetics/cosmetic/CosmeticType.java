package realcraft.bukkit.cosmetics.cosmetic;

import org.bukkit.Material;
import realcraft.bukkit.cosmetics.effects.*;
import realcraft.bukkit.cosmetics.gadgets.*;
import realcraft.bukkit.cosmetics.hats.Hat;
import realcraft.bukkit.cosmetics.pets.Pet;

public enum CosmeticType {

	GADGET_CHICKENATOR   	(1,  CosmeticCategory.GADGET, GadgetChickenator.class,      "§6§lKureci bomba",        Material.COOKED_CHICKEN),
	GADGET_MELONTHROWER  	(2,  CosmeticCategory.GADGET, GadgetMelonThrower.class,     "§a§lMelounova bomba",     Material.MELON),
	GADGET_COLORBOMB     	(3,  CosmeticCategory.GADGET, GadgetColorBomb.class,        "§d§lBarevna bomba",       Material.LIGHT_BLUE_WOOL),
	GADGET_EXPLOSIVESHEEP	(4,  CosmeticCategory.GADGET, GadgetExplosiveSheep.class,   "§4§lExplozivni ovce",     Material.SHEARS),
	GADGET_TNT           	(5,  CosmeticCategory.GADGET, GadgetTNT.class,              "§c§lTNT bomba",           Material.TNT),
	GADGET_TSUNAMI       	(6,  CosmeticCategory.GADGET, GadgetTsunami.class,          "§9§lTsunami",             Material.WATER_BUCKET),
	//GADGET_FIREWORK      	(7,  CosmeticCategory.GADGET, GadgetFirework.class,         "§c§lOhnostroj",           Material.FIREWORK_ROCKET),
	GADGET_GHOSTPARTY    	(8,  CosmeticCategory.GADGET, GadgetGhostParty.class,       "§7§lParty duchu",         Material.SKELETON_SKULL),
	GADGET_FREEZECANNON  	(9,  CosmeticCategory.GADGET, GadgetFreezeCannon.class,     "§b§lMrazici delo",        Material.ICE),
	GADGET_PARTYPOPPER   	(10, CosmeticCategory.GADGET, GadgetPartyPopper.class,      "§e§lKonfety",             Material.GOLDEN_CARROT),
	GADGET_PAINTBALLGUN  	(11, CosmeticCategory.GADGET, GadgetPaintballGun.class,     "§e§lPaintball zbran",     Material.DIAMOND_HOE),
	GADGET_DIAMONDSHOWER 	(12, CosmeticCategory.GADGET, GadgetDiamondShower.class,    "§b§lDiamantova sprcha",   Material.DIAMOND),
	GADGET_GOLDSHOWER    	(13, CosmeticCategory.GADGET, GadgetGoldShower.class,       "§e§lZlata sprcha",        Material.GOLD_INGOT),
	GADGET_FOODSHOWER    	(14, CosmeticCategory.GADGET, GadgetFoodShower.class,       "§2§lJidlova sprcha",      Material.APPLE),

	/*MOUNT_GLACIALSTEED      (1,  CosmeticCategory.MOUNT,  MountGlacialSteed.class,      "§b§lGlacial Steed",                   Material.PACKED_ICE),
	MOUNT_INFERNALHORROR    (2,  CosmeticCategory.MOUNT,  MountInfernalHorror.class,    "§4§lInfernal Horror",                 Material.BONE),
	MOUNT_WALKINGDEAD       (3,  CosmeticCategory.MOUNT,  MountWalkingDead.class,       "§2§lWalking Dead",                    Material.ROTTEN_FLESH),
	MOUNT_LLAMA             (4,  CosmeticCategory.MOUNT,  MountLlama.class,             "§6§lLlama",                           Material.ORANGE_CARPET),
	MOUNT_COW               (5,  CosmeticCategory.MOUNT,  MountCow.class,               "§3§lCow",                             Material.MILK_BUCKET),
	MOUNT_NYANSHEEP         (6,  CosmeticCategory.MOUNT,  MountNyanSheep.class,         "§4§lNy§6§la§e§ln §a§lSh§b§lee§d§lp",  Material.CYAN_STAINED_GLASS),
	MOUNT_PIG               (7,  CosmeticCategory.MOUNT,  MountPig.class,               "§d§lPiggy",                           Material.PORKCHOP),
	MOUNT_POLAERBEAR        (8,  CosmeticCategory.MOUNT,  MountPolarBear.class,         "§f§lPolar Bear",                      Material.SNOW_BLOCK),
	MOUNT_TURTLE            (9,  CosmeticCategory.MOUNT,  MountTurtle.class,            "§a§lTurtle",                          Material.TURTLE_HELMET),
	MOUNT_SPIDER            (10, CosmeticCategory.MOUNT,  MountSpider.class,            "§7§lSpider",                          Material.COBWEB),*/

	EFFECT_RAINCLOUD       	(1,  CosmeticCategory.EFFECT, EffectRainCloud.class,        "§9§lRain Cloud",          Material.INK_SAC),
	EFFECT_SNOWCLOUD       	(2,  CosmeticCategory.EFFECT, EffectSnowCloud.class,        "§f§lSnow Cloud",          Material.SNOWBALL),
	EFFECT_BLOODHELIX      	(3,  CosmeticCategory.EFFECT, EffectBloodHelix.class,       "§4§lBlood Helix",         Material.REDSTONE),
	EFFECT_FLAMERINGS		(5,  CosmeticCategory.EFFECT, EffectFlameRings.class,       "§c§lFlame Rings",         Material.BLAZE_POWDER),
	EFFECT_INLOVE          	(6,  CosmeticCategory.EFFECT, EffectInLove.class,           "§c§lIn Love",             Material.LEGACY_RED_ROSE),
	EFFECT_GREENSPARKS     	(7,  CosmeticCategory.EFFECT, EffectGreenSparks.class,      "§a§lGreen Sparks",        Material.EMERALD),
	EFFECT_FROZENWALK      	(8,  CosmeticCategory.EFFECT, EffectFrozenWalk.class,       "§b§lFrozen Walk",         Material.SNOWBALL),
	EFFECT_MUSIC           	(9,  CosmeticCategory.EFFECT, EffectMusic.class,            "§9§lMusic",               Material.MUSIC_DISC_MALL),
	EFFECT_ENCHANTED       	(10, CosmeticCategory.EFFECT, EffectEnchanted.class,        "§7§lEnchanted",           Material.BOOK),
	EFFECT_CRUSHEDCANDY  	(11, CosmeticCategory.EFFECT, EffectCrushedCandy.class,     "§4§lCrushed §f§lCandy",   Material.NETHER_WART),
	EFFECT_ANGELWINGS      	(12, CosmeticCategory.EFFECT, EffectAngelWings.class,       "§f§lAngel Wings",         Material.FEATHER),
	EFFECT_SUPERHERO       	(13, CosmeticCategory.EFFECT, EffectSuperHero.class,        "§4§lSuper Hero",          Material.GLOWSTONE_DUST),
	EFFECT_SANTAHAT        	(14, CosmeticCategory.EFFECT, EffectSantaHat.class,         "§4§lSanta §f§lHat",       Material.BEACON),
	EFFECT_ENDERAURA       	(15, CosmeticCategory.EFFECT, EffectEnderAura.class,        "§d§lEnder Aura",          Material.ENDER_EYE),

	HAT_CAKE                (1,  CosmeticCategory.HAT, Hat.class, "§bCake",                Material.PLAYER_HEAD),
	HAT_COOKIE              (2,  CosmeticCategory.HAT, Hat.class, "§bCookie",              Material.PLAYER_HEAD),
	HAT_PIE                 (3,  CosmeticCategory.HAT, Hat.class, "§bPie",                 Material.PLAYER_HEAD),
	HAT_HAM                 (4,  CosmeticCategory.HAT, Hat.class, "§bHam",                 Material.PLAYER_HEAD),
	HAT_PUMPKIN             (5,  CosmeticCategory.HAT, Hat.class, "§bPumpkin",             Material.PLAYER_HEAD),
	HAT_FRIES               (6,  CosmeticCategory.HAT, Hat.class, "§bFries",               Material.PLAYER_HEAD),
	HAT_CUPOFMILK           (7,  CosmeticCategory.HAT, Hat.class, "§bCop of Milk",         Material.PLAYER_HEAD),
	HAT_COMPUTER1           (8,  CosmeticCategory.HAT, Hat.class, "§bComputer",            Material.PLAYER_HEAD),
	HAT_COMPUTER2           (9,  CosmeticCategory.HAT, Hat.class, "§bComputer",            Material.PLAYER_HEAD),
	HAT_MONITOR1            (10, CosmeticCategory.HAT, Hat.class, "§bMonitor",             Material.PLAYER_HEAD),
	HAT_SECURITYCAMERA      (11, CosmeticCategory.HAT, Hat.class, "§bSecurity Camera",     Material.PLAYER_HEAD),
	HAT_SPEAKER             (12, CosmeticCategory.HAT, Hat.class, "§bSpeaker",             Material.PLAYER_HEAD),
	HAT_C4                  (13, CosmeticCategory.HAT, Hat.class, "§bC4 Explosive",        Material.PLAYER_HEAD),
	HAT_EYE                 (14, CosmeticCategory.HAT, Hat.class, "§bEye",                 Material.PLAYER_HEAD),
	HAT_LIGHTSPACEHELMET    (15, CosmeticCategory.HAT, Hat.class, "§bLight Space Helmet",  Material.PLAYER_HEAD),
	HAT_DARKSPACEHELMET     (16, CosmeticCategory.HAT, Hat.class, "§bDark Space Helmet",   Material.PLAYER_HEAD),
	HAT_FOOTBALLHELMET      (17, CosmeticCategory.HAT, Hat.class, "§bFootball Helmet",     Material.PLAYER_HEAD),
	HAT_WILSON              (18, CosmeticCategory.HAT, Hat.class, "§bWilson",              Material.PLAYER_HEAD),
	HAT_SKULL               (19, CosmeticCategory.HAT, Hat.class, "§bSkull",               Material.PLAYER_HEAD),
	HAT_MISSINGTEXTURE      (20, CosmeticCategory.HAT, Hat.class, "§bMissing Texture",     Material.PLAYER_HEAD),
	HAT_REDDIT              (21, CosmeticCategory.HAT, Hat.class, "§bReddit",              Material.PLAYER_HEAD),
	HAT_DIAMONDSTEVE        (22, CosmeticCategory.HAT, Hat.class, "§bDiamond Steve",       Material.PLAYER_HEAD),
	HAT_GOLDSTEVE           (23, CosmeticCategory.HAT, Hat.class, "§bGold Steve",          Material.PLAYER_HEAD),
	HAT_EMERALDSTEVE        (24, CosmeticCategory.HAT, Hat.class, "§bEmerald Steve",       Material.PLAYER_HEAD),
	HAT_LUCKYBLOCK          (25, CosmeticCategory.HAT, Hat.class, "§bLuckyBlock",          Material.PLAYER_HEAD),
	HAT_CROWN               (26, CosmeticCategory.HAT, Hat.class, "§bCrown",               Material.PLAYER_HEAD),
	HAT_ENDERPORTAL         (27, CosmeticCategory.HAT, Hat.class, "§bEnder Portal",        Material.PLAYER_HEAD),
	HAT_SPONGE              (28, CosmeticCategory.HAT, Hat.class, "§bSponge",              Material.PLAYER_HEAD),
	HAT_DISPENSER           (29, CosmeticCategory.HAT, Hat.class, "§bDispenser",           Material.PLAYER_HEAD),
	HAT_YOSHI               (30, CosmeticCategory.HAT, Hat.class, "§bYoshi",               Material.PLAYER_HEAD),
	HAT_LUIGI               (31, CosmeticCategory.HAT, Hat.class, "§bLuigi",               Material.PLAYER_HEAD),
	HAT_MARIO               (32, CosmeticCategory.HAT, Hat.class, "§bMario",               Material.PLAYER_HEAD),
	HAT_BATMAN              (33, CosmeticCategory.HAT, Hat.class, "§bBatman",              Material.PLAYER_HEAD),
	HAT_MASTERCHIEF         (34, CosmeticCategory.HAT, Hat.class, "§bMasterChief",         Material.PLAYER_HEAD),
	HAT_PORTALCORE          (35, CosmeticCategory.HAT, Hat.class, "§bPortal Core",         Material.PLAYER_HEAD),
	HAT_ORANGECORE          (36, CosmeticCategory.HAT, Hat.class, "§bOrange Core",         Material.PLAYER_HEAD),
	HAT_GREENCORE           (37, CosmeticCategory.HAT, Hat.class, "§bGreen Core",          Material.PLAYER_HEAD),
	HAT_GOOMBA              (38, CosmeticCategory.HAT, Hat.class, "§bBoomba",              Material.PLAYER_HEAD),
	HAT_DARTHVADER          (39, CosmeticCategory.HAT, Hat.class, "§bDerth Vader",         Material.PLAYER_HEAD),
	HAT_R2D2                (40, CosmeticCategory.HAT, Hat.class, "§bR2D2",                Material.PLAYER_HEAD),
	HAT_CLONETROOPER        (41, CosmeticCategory.HAT, Hat.class, "§bClone Trooper",       Material.PLAYER_HEAD),
	HAT_STORMTROOPER        (42, CosmeticCategory.HAT, Hat.class, "§bStorm Trooper",       Material.PLAYER_HEAD),
	HAT_CHARMANDER          (43, CosmeticCategory.HAT, Hat.class, "§bCharmander",          Material.PLAYER_HEAD),
	HAT_GROOT               (44, CosmeticCategory.HAT, Hat.class, "§bGroot",               Material.PLAYER_HEAD),
	HAT_MANGLE              (45, CosmeticCategory.HAT, Hat.class, "§bMangle",              Material.PLAYER_HEAD),
	HAT_FOXY                (46, CosmeticCategory.HAT, Hat.class, "§bFoxy",                Material.PLAYER_HEAD),
	HAT_ZOIDBERG            (47, CosmeticCategory.HAT, Hat.class, "§bZoidberg",            Material.PLAYER_HEAD),
	HAT_EWOK                (48, CosmeticCategory.HAT, Hat.class, "§bEwok",                Material.PLAYER_HEAD),
	HAT_PATRICK             (49, CosmeticCategory.HAT, Hat.class, "§bPatrick",             Material.PLAYER_HEAD),
	HAT_SAMUS               (50, CosmeticCategory.HAT, Hat.class, "§bSamus",               Material.PLAYER_HEAD),
	HAT_GANONDORF           (51, CosmeticCategory.HAT, Hat.class, "§bGanondorf",           Material.PLAYER_HEAD),
	HAT_BENDER              (52, CosmeticCategory.HAT, Hat.class, "§bBender",              Material.PLAYER_HEAD),
	HAT_CHARMELEON          (53, CosmeticCategory.HAT, Hat.class, "§bCharmeleon",          Material.PLAYER_HEAD),
	HAT_GHOST               (54, CosmeticCategory.HAT, Hat.class, "§bGhost",               Material.PLAYER_HEAD),
	HAT_ENDERDRAGON         (55, CosmeticCategory.HAT, Hat.class, "§bEnder Dragon",        Material.PLAYER_HEAD),
	HAT_YVELTAL             (56, CosmeticCategory.HAT, Hat.class, "§bYveltal",             Material.PLAYER_HEAD),

	PET_CHICKEN             (1,  CosmeticCategory.PET, Pet.class, "§bChicken",             Material.PLAYER_HEAD),
	PET_PIG                 (2,  CosmeticCategory.PET, Pet.class, "§bPig",                 Material.PLAYER_HEAD),
	PET_SHEEP               (3,  CosmeticCategory.PET, Pet.class, "§bSheep",               Material.PLAYER_HEAD),
	PET_COW                 (4,  CosmeticCategory.PET, Pet.class, "§bCow",                 Material.PLAYER_HEAD),
	PET_OCELOT              (5,  CosmeticCategory.PET, Pet.class, "§bOcelot",              Material.PLAYER_HEAD),
	PET_CAT                 (6,  CosmeticCategory.PET, Pet.class, "§bCat",                 Material.PLAYER_HEAD),
	PET_WOLF                (7,  CosmeticCategory.PET, Pet.class, "§bWolf",                Material.PLAYER_HEAD),
	PET_RABBIT              (8,  CosmeticCategory.PET, Pet.class, "§bRabbit",              Material.PLAYER_HEAD),
	PET_KOALA               (9,  CosmeticCategory.PET, Pet.class, "§bKoala",               Material.PLAYER_HEAD),
	PET_MONKEY              (10, CosmeticCategory.PET, Pet.class, "§bMonkey",              Material.PLAYER_HEAD),
	PET_POLARBEAR           (11, CosmeticCategory.PET, Pet.class, "§bPolarbear",           Material.PLAYER_HEAD),
	PET_PENGUIN             (12, CosmeticCategory.PET, Pet.class, "§bPenguin",             Material.PLAYER_HEAD),
	PET_WALRUS              (13, CosmeticCategory.PET, Pet.class, "§bWalrus",              Material.PLAYER_HEAD),
	PET_SQUID               (14, CosmeticCategory.PET, Pet.class, "§bSquid",               Material.PLAYER_HEAD),
	PET_TIGER               (15, CosmeticCategory.PET, Pet.class, "§bTiger",               Material.PLAYER_HEAD),
	PET_PANDA               (16, CosmeticCategory.PET, Pet.class, "§bPanda",               Material.PLAYER_HEAD),
	PET_CLOWNFISH           (17, CosmeticCategory.PET, Pet.class, "§bClownfish",           Material.PLAYER_HEAD),
	PET_BIRD                (18, CosmeticCategory.PET, Pet.class, "§bBird",                Material.PLAYER_HEAD),
	PET_BEE                 (19, CosmeticCategory.PET, Pet.class, "§bBee",                 Material.PLAYER_HEAD),
	PET_FISH                (20, CosmeticCategory.PET, Pet.class, "§bFish",                Material.PLAYER_HEAD),
	PET_SALMONFISH          (21, CosmeticCategory.PET, Pet.class, "§bSalmonfish",          Material.PLAYER_HEAD),
	PET_TORTOISE            (22, CosmeticCategory.PET, Pet.class, "§bTortoise",            Material.PLAYER_HEAD),
	PET_SEAGULL             (23, CosmeticCategory.PET, Pet.class, "§bSeagull",             Material.PLAYER_HEAD),
	PET_FERRET              (24, CosmeticCategory.PET, Pet.class, "§bFerret",              Material.PLAYER_HEAD),
	PET_ELEPHANT            (25, CosmeticCategory.PET, Pet.class, "§bElephant",            Material.PLAYER_HEAD),
	PET_FURBY               (26, CosmeticCategory.PET, Pet.class, "§bFurby",               Material.PLAYER_HEAD),
	PET_BLAZE               (27, CosmeticCategory.PET, Pet.class, "§bBlaze",               Material.PLAYER_HEAD),
	PET_GHAST               (28, CosmeticCategory.PET, Pet.class, "§bGhast",               Material.PLAYER_HEAD),
	PET_ENDERMAN            (29, CosmeticCategory.PET, Pet.class, "§bEnderman",            Material.PLAYER_HEAD),
	PET_LAVASLIME           (30, CosmeticCategory.PET, Pet.class, "§bLavaslime",           Material.PLAYER_HEAD),
	PET_SLIME               (31, CosmeticCategory.PET, Pet.class, "§bSlime",               Material.PLAYER_HEAD),
	PET_GUARDIAN            (32, CosmeticCategory.PET, Pet.class, "§bGuardian",            Material.PLAYER_HEAD),
	PET_WITHERBOSS          (33, CosmeticCategory.PET, Pet.class, "§bWitherboss",          Material.PLAYER_HEAD),
	PET_ENDERDRAGON         (34, CosmeticCategory.PET, Pet.class, "§bEnderdragon",         Material.PLAYER_HEAD),
	;

	private int id;
	private CosmeticCategory category;
	private Class<?> clazz;

	private String name;
	private Material material;

	private CosmeticType(int id,CosmeticCategory category,Class<?> clazz,String name,Material material){
		this.id = id+category.getId();
		this.category = category;
		this.clazz = clazz;
		this.name = name+"§r";
		this.material = material;
	}

	public int getId(){
		return id;
	}

	public CosmeticCategory getCategory(){
		return category;
	}

	public Class<?> getClazz(){
		return clazz;
	}

	public String getName(){
		return name;
	}

	public Material getMaterial(){
		return material;
	}

	public static CosmeticType fromId(int id){
		for(CosmeticType type : CosmeticType.values()){
			if(type.getId() == id) return type;
		}
		return null;
	}
}