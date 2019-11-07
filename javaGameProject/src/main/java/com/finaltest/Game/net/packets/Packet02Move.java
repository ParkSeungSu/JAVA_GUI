package com.finaltest.Game.net.packets;

import com.finaltest.Game.net.GameClient;
import com.finaltest.Game.net.GameServer;

public class Packet02Move extends Packet{
	
	private String username;
	private int x,y;
	
	private int numState=0;
	private boolean isMoving;
	private int movingDir=1;
	
	public Packet02Move(byte[] data) {
		super(02);
		String[] dataArray=readData(data).split(",");
		this.username=dataArray[0];
		this.x=Integer.parseInt(dataArray[1]);
		this.y=Integer.parseInt(dataArray[2]);
		this.numState=Integer.parseInt(dataArray[3]);
		this.isMoving=Integer.parseInt(dataArray[4])==1;
		this.movingDir=Integer.parseInt(dataArray[5]);
	}

	public Packet02Move(String username,int x,int y,int numState,boolean isMoving,int movingDir) {
		super(02);
		this.username=username;
		this.x=x;
		this.y=y;
		this.numState=numState;
		this.isMoving=isMoving;
		this.movingDir=movingDir;
	}
	
	
	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
		
	}

	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClient(getData());
		
	}

	@Override
	public byte[] getData() {
		
		return ("02"+this.username+","+this.x+","+this.y+","+this.numState+","+(isMoving?1:0)+","+this.movingDir).getBytes();
	}
	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}
	public String getUsername() {
		
		return username;
	}

	public int getNumState() {
		return numState;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public int getMovingDir() {
		return movingDir;
	}
	

}
