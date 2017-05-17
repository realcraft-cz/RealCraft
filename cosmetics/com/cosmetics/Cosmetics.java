package com.cosmetics;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.gadgets.Gadget;
import com.gadgets.Gadget.GadgetType;
import com.gadgets.GadgetChickenator;
import com.gadgets.GadgetColorBomb;
import com.gadgets.GadgetDiamondShower;
import com.gadgets.GadgetExplosiveSheep;
import com.gadgets.GadgetFirework;
import com.gadgets.GadgetFoodShower;
import com.gadgets.GadgetFreezeCannon;
import com.gadgets.GadgetGhostParty;
import com.gadgets.GadgetGoldShower;
import com.gadgets.GadgetMelonThrower;
import com.gadgets.GadgetPaintballGun;
import com.gadgets.GadgetPartyPopper;
import com.gadgets.GadgetTNT;
import com.gadgets.GadgetTsunami;
import com.hats.Hat;
import com.mounts.Mount;
import com.mounts.Mount.MountType;
import com.mounts.MountGlacialSteed;
import com.mounts.MountInfernalHorror;
import com.mounts.MountNyanSheep;
import com.mounts.MountPig;
import com.mounts.MountSlime;
import com.mounts.MountSnake;
import com.mounts.MountSpider;
import com.mounts.MountWalkingDead;
import com.particleeffects.ParticleEffect;
import com.particleeffects.ParticleEffect.ParticleEffectType;
import com.particleeffects.ParticleEffectAngelWings;
import com.particleeffects.ParticleEffectBloodHelix;
import com.particleeffects.ParticleEffectCrushedCandyCane;
import com.particleeffects.ParticleEffectEnchanted;
import com.particleeffects.ParticleEffectEnderAura;
import com.particleeffects.ParticleEffectFlameFairy;
import com.particleeffects.ParticleEffectFlameRings;
import com.particleeffects.ParticleEffectFrostLord;
import com.particleeffects.ParticleEffectFrozenWalk;
import com.particleeffects.ParticleEffectGreenSparks;
import com.particleeffects.ParticleEffectInLove;
import com.particleeffects.ParticleEffectInferno;
import com.particleeffects.ParticleEffectMusic;
import com.particleeffects.ParticleEffectRainCloud;
import com.particleeffects.ParticleEffectSantaHat;
import com.particleeffects.ParticleEffectSnowCloud;
import com.particleeffects.ParticleEffectSuperHero;
import com.pets.Pet;
import com.pets.Pet.PetType;
import com.pets.PetChick;
import com.pets.PetCow;
import com.pets.PetDog;
import com.pets.PetEasterBunny;
import com.pets.PetKitty;
import com.pets.PetPiggy;
import com.pets.PetSheep;
import com.suits.Suit;
import com.suits.Suit.SuitType;
import com.suits.SuitDiamond;
import com.suits.SuitGold;
import com.suits.SuitIron;
import com.suits.SuitRave;

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