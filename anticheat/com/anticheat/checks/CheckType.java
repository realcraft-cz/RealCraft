package com.anticheat.checks;

public enum CheckType {
	FLYHACK, NOFALL, WATERWALK, KILLAURA_SPEED, KILLAURA_DIR, KILLAURA_NOSWING, ENCHANT;

	public String toString(){
		switch(this){
			case FLYHACK: return "FlyHack";
			case NOFALL: return "NoFall";
			case WATERWALK: return "WaterWalk";
			case KILLAURA_SPEED: return "KillAura (speed)";
			case KILLAURA_DIR: return "KillAura (direction)";
			case KILLAURA_NOSWING: return "KillAura (noswing)";
			case ENCHANT: return "SuperEnchant";
		}
		return null;
	}

	public int getId(){
		switch(this){
			case FLYHACK: return 1;
			case NOFALL: return 2;
			case WATERWALK: return 3;
			case KILLAURA_SPEED: return 4;
			case KILLAURA_DIR: return 5;
			case KILLAURA_NOSWING: return 6;
			case ENCHANT: return 7;
		}
		return 0;
	}
}