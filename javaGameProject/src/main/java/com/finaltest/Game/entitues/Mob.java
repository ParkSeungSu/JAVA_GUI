package com.finaltest.Game.entitues;

import com.finaltest.Game.level.Level;

public abstract class Mob extends Entity{
	
	protected String name;
	protected int speed;
	protected int numState=0;
	protected boolean isMoving;
	protected int movingDir=1;
	protected int scale=1;

	public Mob(Level level, String name, int x,int y,int speed) {
		super(level);
		this.name=name;
		this.x=x;
		this.y=y;
		this.speed=speed;
	}
	
	public void move(int xa,int ya) {
		if(xa!=0 && ya!=0) {
			move(xa,0);
			move(ya,0);
			numState--;
			return;
		}
		numState++;
		if(!hasCollided(xa,ya)) {
			if(ya<0) movingDir=0;
			if(ya>0) movingDir=1;
			if(xa<0) movingDir=2;
			if(xa>0) movingDir=3;
			x+=xa*speed;
			y+=ya*speed;
		}
	}
	public abstract boolean hasCollided(int xa,int xy);
	
	public String getName() {
		return name;
	}

}
