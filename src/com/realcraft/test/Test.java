package com.realcraft.test;

import com.realcraft.RealCraft;

public class Test {
	public Test(){
		//new BoatsTest();
		new SoundsTest();
		if(RealCraft.isTestServer()){
			new SignTest();
			new CoinsTest();
			//new KickTest();
			new NameTest();
		}
		//new PremiumTest();
	}
}