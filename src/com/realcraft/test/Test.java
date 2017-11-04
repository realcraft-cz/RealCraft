package com.realcraft.test;

import com.realcraft.RealCraft;

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
			//new SpectatorTest();
		}
	}
}