package realcraft.bukkit.test;

import realcraft.bukkit.RealCraft;

public class Test {

	public Test(){
		new SoundsTest();
		new ParticlesTest();
		new ChunkTest();
		new ImageTest();
		new TitleTest();
		new ViewTest();
		if(RealCraft.isTestServer()){
			new ParticlesDemoTest();
			new BlockTest();
			new AsyncTest();
			new HideTest();
			new EntityTest();
			new CombatTest();
			new AdvancementTest();
			new SwimTest();
			new FallingTest();
			new Firecracker();
			new HologramTest();
		}
	}
}