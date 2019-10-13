package realcraft.bukkit.test;

import realcraft.bukkit.RealCraft;

public class Test {

	public Test(){
		new SoundsTest();
		new ParticlesTest();
		new ChunkTest();
		new ImageTest();
		new PokemonTest();
		new TitleTest();
		if(RealCraft.isTestServer()){
			//new SignTest();
			//new KickTest();
			//new FallingTest();
			new RandomTest();
			new CombatTest();
			new AdvancementTest();
		}
	}
}