package com.finaltest.Game.net.packets;

import com.finaltest.Game.net.GameClient;
import com.finaltest.Game.net.GameServer;

public class Packet00Login extends Packet{
	
	private String username;
	private int x,y;
	private boolean isAlive;
	
	
	public Packet00Login(byte[] data) {
		super(00);
		String[] dataArray=readData(data).split(",");
		this.username=dataArray[0];
		this.x=Integer.parseInt(dataArray[1]);
		this.y=Integer.parseInt(dataArray[2]);
		this.isAlive=Integer.parseInt(dataArray[3])==1;
		
	}

	public Packet00Login(String username,int x,int y,boolean alive) {
		super(00);
		this.username=username;
		this.x=x;
		this.y=y;
		this.isAlive=alive;
		
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
		
		return ("00"+this.username+","+getX()+","+getY()+","+(this.isAlive?1:0)).getBytes();
	}

	public String getUsername() {
		
		return username;
	}
	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}
	public boolean isAlive() {
		return this.isAlive;
	}
	
}
