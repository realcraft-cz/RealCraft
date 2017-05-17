package com.anticheat.utils;

public class FightData {
	public final ActionFrequency   speedBuckets;
    public int                     speedShortTermCount;
    public int                     speedShortTermTick;
    public int                     speedViolations = 0;
    public int                     directionViolations = 0;
    public boolean                 noSwingArmSwung = false;
    public int                     noSwingViolations = 0;

    public FightData(){
        speedBuckets = new ActionFrequency(6, 333);
    }
}