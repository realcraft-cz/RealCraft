package com.parkour.utils;

public class LocationUtil {

	public static int[] getIndexCoords(int index){
		int dir = 1;
		int step = 1;
		int round = 1;
		int x = 0,y = 1;
		for(int i=1;i<=index;i++){
			int sideSize = ((round*8)/4)+1;
			if(step == 1){
				x -= 1;
				y -= 1*2;
			} else {
				if(dir == 1) x += 1;
				else if(dir == 2) y += 1;
				else if(dir == 3) x -= 1;
				else if(dir == 4) y -= 1;
			}
			if(step == sideSize) dir = 2;
			else if(step == (sideSize+sideSize)-1) dir = 3;
			else if(step == (sideSize+sideSize+sideSize)-2) dir = 4;
			else if(step == (sideSize+sideSize+sideSize+sideSize)-4){
				dir = 1;
				round ++;
				step = 0;
			}
			step ++;
		}
		return new int[]{x,y};
	}
}