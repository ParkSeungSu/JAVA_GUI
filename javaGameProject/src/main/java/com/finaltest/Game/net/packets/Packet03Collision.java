package com.finaltest.Game.net.packets;

import com.finaltest.Game.net.GameClient;
import com.finaltest.Game.net.GameServer;

public class Packet03Collision extends Packet{
	
	private String username;
	private int x,y;
	
	
	private boolean isAlive;
	
	
	
	public Packet03Collision(byte[] data) {
		super(03);
		String[] dataArray=readData(data).split(",");
		this.username=dataArray[0];
		this.x=Integer.parseInt(dataArray[1]);
		this.y=Integer.parseInt(dataArray[2]);
		this.isAlive=Integer.parseInt(dataArray[3])==1;
		
	
	}

	public Packet03Collision(String username,int x,int y,boolean isAlive) {
		super(03);
		this.username=username;
		this.x=x;
		this.y=y;
		this.isAlive=isAlive;
		
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
		
		return ("03"+this.username+","+this.x+","+this.y+","+(isAlive?1:0)).getBytes();
	}
	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}
	public String getUsername() {
		
		return this.username;
	}


	public boolean isAlive() {
		return this.isAlive;
	}
	

}
