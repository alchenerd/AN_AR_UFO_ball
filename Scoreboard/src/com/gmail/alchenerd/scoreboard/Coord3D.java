package com.gmail.alchenerd.scoreboard;

public class Coord3D {
	private float x, y, z;

	public Coord3D(float inX, float inY, float inZ) {
		// TODO Auto-generated constructor stub
		x = inX;
		y = inY;
		z = inZ;
	}
	public float getData(String str) {
		// TODO Auto-generated constructor stub
		if(str == "X"){return x;}
		else if(str == "Y"){return y;}
		else if(str == "Z"){return z;}
		else return (float) 0;
	}
	
	public void setData(String str, float data) {
		// TODO Auto-generated constructor stub
		if(str == "X"){x = data;}
		else if(str == "Y"){y = data;}
		else if(str == "Z"){z = data;}
	}
}
