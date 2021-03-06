package com.finaltest.Game.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.finaltest.Game.Game;

import com.finaltest.Game.entitues.PlayerMP;
import com.finaltest.Game.net.packets.Packet;
import com.finaltest.Game.net.packets.Packet00Login;
import com.finaltest.Game.net.packets.Packet01Disconnect;
import com.finaltest.Game.net.packets.Packet02Move;
import com.finaltest.Game.net.packets.Packet03Collision;
import com.finaltest.Game.net.packets.Packet.PacketTypes;

public class GameServer extends Thread {
	private DatagramSocket socket;
	private Game game;
	private List<PlayerMP> connectedPlayers = new ArrayList<PlayerMP>();

	public GameServer(Game game) {
		this.game = game;
		try {
			this.socket = new DatagramSocket(1331);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}

	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "]"
					+ ((Packet00Login) packet).getUsername() + " has connected...");
			PlayerMP player = new PlayerMP(game.level, 100, 100, ((Packet00Login) packet).getUsername(), address, port);
			player.setAlive(((Packet00Login)packet).isAlive());
			this.addConnection(player,(Packet00Login)packet);
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "]"
					+ ((Packet01Disconnect) packet).getUsername() + " has left...");
			this.removeConnection((Packet01Disconnect) packet);
			break;
		case MOVE:
			packet = new Packet02Move(data);
			this.handleMove(((Packet02Move) packet));
			break;
		case COLLISION:
			packet=new Packet03Collision(data);
			this.handleCollision((Packet03Collision)packet);
		}
	}

	private void handleCollision(Packet03Collision packet) {
		if (getPlayerMP(packet.getUsername()) != null) {
			int index = getPlayerMPindex(packet.getUsername());
			PlayerMP player = this.connectedPlayers.get(index);
			player.setAlive(packet.isAlive());
		}
		
	}

	private synchronized void handleMove(Packet02Move packet) {
		if (getPlayerMP(packet.getUsername()) != null) {
			int index = getPlayerMPindex(packet.getUsername());
			PlayerMP player = this.connectedPlayers.get(index);
			player.x = packet.getX();
			player.y = packet.getY();
			player.setMoving(packet.isMoving());
			player.setMovingDir(packet.getMovingDir());
			player.setNumState(packet.getNumState());
			
			packet.writeData(this);
		}
	}

	public synchronized void removeConnection(Packet01Disconnect packet) {
		this.connectedPlayers.remove(getPlayerMPindex(packet.getUsername()));
		packet.writeData(this);
	}

	public int getPlayerMPindex(String username) {
		int index = 0;
		for (PlayerMP player : this.connectedPlayers) {
			if (player.getUsername().equals(username)) {
				break;
			}
			index++;
		}
		return index;
	}

	public PlayerMP getPlayerMP(String username) {
		for (PlayerMP player : this.connectedPlayers) {
			if (player.getUsername().equals(username)) {
				return player;
			}
		}
		return null;
	}

	public synchronized void addConnection(PlayerMP player, Packet00Login packet) {
		boolean alreadyConnected = false;
		for (PlayerMP p : this.connectedPlayers) {
			if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
				if (p.ipAddress == null) {
					p.ipAddress = player.ipAddress;
				}
				if (p.port == -1) {
					p.port = player.port;
				}
				alreadyConnected = true;
			} else {
				// 현재 존재하고 있는 사람들에게 새로운 사람이 들어왔다고 알려준다.
				sendData(packet.getData(), p.ipAddress, p.port);
				packet = new Packet00Login(p.getUsername(), p.x, p.y,p.isAlive());
				sendData(packet.getData(), player.ipAddress, player.port);
			}
		}
		if (!alreadyConnected) {
			this.connectedPlayers.add(player);
		}
	}

	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		if (!game.isAppelet) {
			
			DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
			try {
				this.socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendDataToAllClient(byte[] data) {
		for (PlayerMP p : connectedPlayers) {
			sendData(data, p.ipAddress, p.port);
		}

	}
}
