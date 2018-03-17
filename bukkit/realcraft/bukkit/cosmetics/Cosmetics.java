package realcraft.bukkit.cosmetics;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import realcraft.bukkit.cosmetics.gadgets.Gadget;
import realcraft.bukkit.cosmetics.gadgets.Gadget.GadgetType;
import realcraft.bukkit.cosmetics.gadgets.GadgetChickenator;
import realcraft.bukkit.cosmetics.gadgets.GadgetColorBomb;
import realcraft.bukkit.cosmetics.gadgets.GadgetDiamondShower;
import realcraft.bukkit.cosmetics.gadgets.GadgetExplosiveSheep;
import realcraft.bukkit.cosmetics.gadgets.GadgetFirework;
import realcraft.bukkit.cosmetics.gadgets.GadgetFoodShower;
import realcraft.bukkit.cosmetics.gadgets.GadgetFreezeCannon;
import realcraft.bukkit.cosmetics.gadgets.GadgetGhostParty;
import realcraft.bukkit.cosmetics.gadgets.GadgetGoldShower;
import realcraft.bukkit.cosmetics.gadgets.GadgetMelonThrower;
import realcraft.bukkit.cosmetics.gadgets.GadgetPaintballGun;
import realcraft.bukkit.cosmetics.gadgets.GadgetPartyPopper;
import realcraft.bukkit.cosmetics.gadgets.GadgetTNT;
import realcraft.bukkit.cosmetics.gadgets.GadgetTsunami;
import realcraft.bukkit.cosmetics.hats.Hat;
import realcraft.bukkit.cosmetics.mounts.Mount;
import realcraft.bukkit.cosmetics.mounts.Mount.MountType;
import realcraft.bukkit.cosmetics.mounts.MountGlacialSteed;
import realcraft.bukkit.cosmetics.mounts.MountInfernalHorror;
import realcraft.bukkit.cosmetics.mounts.MountNyanSheep;
import realcraft.bukkit.cosmetics.mounts.MountPig;
import realcraft.bukkit.cosmetics.mounts.MountSlime;
import realcraft.bukkit.cosmetics.mounts.MountSnake;
import realcraft.bukkit.cosmetics.mounts.MountSpider;
import realcraft.bukkit.cosmetics.mounts.MountWalkingDead;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffect;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffect.ParticleEffectType;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectAngelWings;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectBloodHelix;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectCrushedCandyCane;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectEnchanted;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectEnderAura;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectFlameFairy;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectFlameRings;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectFrostLord;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectFrozenWalk;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectGreenSparks;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectInLove;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectInferno;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectMusic;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectRainCloud;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectSantaHat;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectSnowCloud;
import realcraft.bukkit.cosmetics.particleeffects.ParticleEffectSuperHero;
import realcraft.bukkit.cosmetics.pets.Pet;
import realcraft.bukkit.cosmetics.pets.Pet.PetType;
import realcraft.bukkit.cosmetics.pets.PetChick;
import realcraft.bukkit.cosmetics.pets.PetCow;
import realcraft.bukkit.cosmetics.pets.PetDog;
import realcraft.bukkit.cosmetics.pets.PetEasterBunny;
import realcraft.bukkit.cosmetics.pets.PetKitty;
import realcraft.bukkit.cosmetics.pets.PetPiggy;
import realcraft.bukkit.cosmetics.pets.PetSheep;
import realcraft.bukkit.cosmetics.suits.Suit;
import realcraft.bukkit.cosmetics.suits.Suit.SuitType;
import realcraft.bukkit.cosmetics.suits.SuitDiamond;
import realcraft.bukkit.cosmetics.suits.SuitGold;
import realcraft.bukkit.cosmetics.suits.SuitIron;
import realcraft.bukkit.cosmetics.suits.SuitRave;

public class Cosmetics {
	public static ArrayList<Gadget> gadgets = new ArrayList<Gadget>();
	public static ArrayList<ParticleEffect> particleeffects = new ArrayList<ParticleEffect>();
	public static ArrayList<Pet> pets = new ArrayList<Pet>();
	public static ArrayList<Mount> mounts = new ArrayList<Mount>();
	public static ArrayList<Suit> suits = new ArrayList<Suit>();

	public static void init(){
		gadgets.add(new GadgetChickenator(GadgetType.Chickenator));
		gadgets.add(new GadgetMelonThrower(GadgetType.MelonThrower));
		gadgets.add(new GadgetColorBomb(GadgetType.ColorBomb));
		gadgets.add(new GadgetExplosiveSheep(GadgetType.ExplosiveSheep));
		gadgets.add(new GadgetTNT(GadgetType.TNT));
		gadgets.add(new GadgetTsunami(GadgetType.Tsunami));
		gadgets.add(new GadgetFirework(GadgetType.Firework));
		gadgets.add(new GadgetGhostParty(GadgetType.GhostParty));
		gadgets.add(new GadgetFreezeCannon(GadgetType.FreezeCannon));
		gadgets.add(new GadgetPartyPopper(GadgetType.PartyPopper));
		gadgets.add(new GadgetPaintballGun(GadgetType.PaintballGun));
		gadgets.add(new GadgetDiamondShower(GadgetType.DiamondShower));
		gadgets.add(new GadgetGoldShower(GadgetType.GoldShower));
		gadgets.add(new GadgetFoodShower(GadgetType.FoodShower));

		particleeffects.add(new ParticleEffectRainCloud(ParticleEffectType.RAINCLOUD));
		particleeffects.add(new ParticleEffectSnowCloud(ParticleEffectType.SNOWCLOUD));
		particleeffects.add(new ParticleEffectBloodHelix(ParticleEffectType.BLOODHELIX));
		particleeffects.add(new ParticleEffectFrostLord(ParticleEffectType.FROSTLORD));
		particleeffects.add(new ParticleEffectFlameRings(ParticleEffectType.FLAMERINGS));
		particleeffects.add(new ParticleEffectInLove(ParticleEffectType.INLOVE));
		particleeffects.add(new ParticleEffectGreenSparks(ParticleEffectType.GREENSPARKS));
		particleeffects.add(new ParticleEffectFrozenWalk(ParticleEffectType.FROZENWALK));
		particleeffects.add(new ParticleEffectMusic(ParticleEffectType.MUSIC));
		particleeffects.add(new ParticleEffectEnchanted(ParticleEffectType.ENCHANTED));
		particleeffects.add(new ParticleEffectInferno(ParticleEffectType.INFERNO));
		particleeffects.add(new ParticleEffectAngelWings(ParticleEffectType.ANGELWINGS));
		particleeffects.add(new ParticleEffectSuperHero(ParticleEffectType.SUPERHERO));
		particleeffects.add(new ParticleEffectSantaHat(ParticleEffectType.SANTAHAT));
		particleeffects.add(new ParticleEffectCrushedCandyCane(ParticleEffectType.CRUSHEDCANDYCANE));
		particleeffects.add(new ParticleEffectEnderAura(ParticleEffectType.ENDERAURA));
		particleeffects.add(new ParticleEffectFlameFairy(ParticleEffectType.FLAMEFAIRY));

		pets.add(new PetPiggy(PetType.PIGGY));
		pets.add(new PetSheep(PetType.SHEEP));
		pets.add(new PetEasterBunny(PetType.EASTERBUNNY));
		pets.add(new PetCow(PetType.COW));
		pets.add(new PetKitty(PetType.KITTY));
		pets.add(new PetDog(PetType.DOG));
		pets.add(new PetChick(PetType.CHICK));

		mounts.add(new MountInfernalHorror(MountType.INFERNALHORROR));
		mounts.add(new MountWalkingDead(MountType.WALKINGDEAD));
		mounts.add(new MountGlacialSteed(MountType.GLACIALSTEED));
		mounts.add(new MountSnake(MountType.SNAKE));
		mounts.add(new MountNyanSheep(MountType.NYANSHEEP));
		mounts.add(new MountPig(MountType.PIG));
		mounts.add(new MountSpider(MountType.SPIDER));
		mounts.add(new MountSlime(MountType.SLIME));

		suits.add(new SuitRave(SuitType.RAVE));
		suits.add(new SuitDiamond(SuitType.DIAMOND));
		suits.add(new SuitGold(SuitType.GOLD));
		suits.add(new SuitIron(SuitType.IRON));
	}

	public static Gadget getGadget(GadgetType type){
		for(Gadget gadget : gadgets){
			if(gadget.getType() == type) return gadget;
		}
		return null;
	}

	public static ParticleEffect getParticleEffect(ParticleEffectType type){
		for(ParticleEffect particleeffect : particleeffects){
			if(particleeffect.getType() == type) return particleeffect;
		}
		return null;
	}

	public static Pet getPet(PetType type){
		for(Pet pet : pets){
			if(pet.getType() == type) return pet;
		}
		return null;
	}

	public static Mount getMount(MountType type){
		for(Mount mount : mounts){
			if(mount.getType() == type) return mount;
		}
		return null;
	}

	public static Suit getSuit(SuitType type){
		for(Suit suit : suits){
			if(suit.getType() == type) return suit;
		}
		return null;
	}

	public static Gadget getRandomGadget(){
		return gadgets.get((int)(Math.random()*gadgets.size()));
	}

	public static ParticleEffect getRandomParticleEffect(Player player,int progress){
		ParticleEffect effect;
		effect = particleeffects.get((int)(Math.random()*particleeffects.size()));
		if(effect.isEnabled(player) && progress < particleeffects.size()) effect = getRandomParticleEffect(player,progress+1);
		else if(progress == particleeffects.size()) effect = null;
		return effect;
	}

	public static Hat.HatType getRandomHat(Player player,int progress){
		Hat.HatType hat;
		hat = Hat.HatType.getRandom();
		if(hat.isEnabled(player) && progress < Hat.HatType.values().length) hat = getRandomHat(player,progress+1);
		else if(progress == Hat.HatType.values().length) hat = null;
		return hat;
	}

	public static Pet getRandomPet(Player player,int progress){
		Pet pet;
		pet = pets.get((int)(Math.random()*pets.size()));
		if(pet.isEnabled(player) && progress < pets.size()) pet = getRandomPet(player,progress+1);
		else if(progress == pets.size()) pet = null;
		return pet;
	}

	public static Mount getRandomMount(Player player,int progress){
		Mount mount;
		mount = mounts.get((int)(Math.random()*mounts.size()));
		if(mount.isEnabled(player) && progress < mounts.size()) mount = getRandomMount(player,progress+1);
		else if(progress == mounts.size()) mount = null;
		return mount;
	}

	public static Suit getRandomSuit(Player player,int progress){
		Suit suit;
		suit = suits.get((int)(Math.random()*suits.size()));
		if(suit.isEnabled(player) && progress < suits.size()) suit = getRandomSuit(player,progress+1);
		else if(progress == suits.size()) suit = null;
		return suit;
	}

	public static void clearCosmetics(Player player){
		clearGadgets(player);
		clearParticleEffects(player);
		clearHats(player);
		clearPets(player);
		clearMounts(player);
		clearSuits(player);
	}

	public static void clearGadgets(Player player){
		for(Gadget gadget : gadgets){
			gadget.clearCosmetic(player);
		}
	}

	public static void clearHats(Player player){
		player.getInventory().setHelmet(null);
	}

	public static void clearSuits(Player player){
		for(Suit suit : suits){
			suit.clearCosmetic(player);
		}
	}

	public static void clearParticleEffects(Player player){
		for(ParticleEffect particleeffect : particleeffects){
			particleeffect.clearCosmetic(player);
		}
	}

	public static void clearPets(Player player){
		for(Pet pet : pets){
			pet.clearCosmetic(player);
		}
	}

	public static void clearMounts(Player player){
		for(Mount mount : mounts){
			mount.clearCosmetic(player);
		}
	}
}