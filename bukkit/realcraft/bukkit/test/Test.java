package realcraft.bukkit.test;

import realcraft.bukkit.RealCraft;

public class Test {

	public Test(){
		new SoundsTest();
		new ParticlesTest();
		new ChunkTest();
		new ImageTest();
		new TitleTest();
		if(RealCraft.isTestServer()){
			new HolographTest();
			new EntityTest();
			new CombatTest();
			new AdvancementTest();
		}
	}
}