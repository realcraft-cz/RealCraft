package com.realcraft.test;

import com.realcraft.RealCraft;

public class Test {
	public Test(){
		//new BoatsTest();
		new SoundsTest();
		new CoinsTest();
		if(RealCraft.isTestServer()) new SignTest();
		//new PremiumTest();
	}
}