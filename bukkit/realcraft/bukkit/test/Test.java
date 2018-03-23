package realcraft.bukkit.test;

import realcraft.bukkit.RealCraft;

public class Test {

	public Test(){
		new SoundsTest();
		new ParticlesTest();
		new ChunkTest();
		new ImageTest();
		new PokemonTest();
		if(RealCraft.isTestServer()){
			//new SignTest();
			//new KickTest();
			new SpectatorTest();
			new RandomTest();
			new CombatTest();
		}
	}
}