package com.finaltest.Game.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.finaltest.Game.Game;
import com.finaltest.Game.entitues.PlayerMP;
import com.finaltest.Game.net.packets.Packet;
import com.finaltest.Game.net.packets.Packet00Login;
import com.finaltest.Game.net.packets.Packet01Disconnect;
import com.finaltest.Game.net.packets.Packet02Move;
import com.finaltest.Game.net.packets.Packet03Collision;
import com.finaltest.Game.net.packets.Packet.PacketTypes;

public class GameClient extends Thread {
	private InetAddress ipAddress;
	private DatagramSocket socket;
	private Game game;

	public GameClient(Game game, String ipAddress) {
		this.game = game;
		try {
			this.socket = new DatagramSocket();
			this.ipAddress = InetAddress.getByName(ipAddress);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
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
			handleLogin((Packet00Login) packet, address, port);
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "]"
					+ ((Packet01Disconnect) packet).getUsername() + " has left the world...");
			game.level.removePlayerMP(((Packet01Disconnect) packet).getUsername());
			break;
		case MOVE:
			packet = new Packet02Move(data);
			handleMove((Packet02Move) packet);
			break;
		case COLLISION:
			packet = new Packet03Collision(data);
			handleCollision((Packet03Collision)packet);
		}
	}

	private void handleCollision(Packet03Collision packet) {
		this.game.level.setAlivePlayer(packet.getUsername(),packet.isAlive());
		
	}

	public void sendData(byte[] data) {
		if (!game.isAppelet) {
			DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 1331);
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleLogin(Packet00Login packet, InetAddress address, int port) {
		System.out.println(
				"[" + address.getHostAddress() + ":" + port + "]" + packet.getUsername() + " has joined the game...");
		PlayerMP player = new PlayerMP(game.level, packet.getX(), packet.getY(), packet.getUsername(), address, port);
		player.setAlive(packet.isAlive());
		game.level.addEntity(player);
	}

	private void handleMove(Packet02Move packet) {
		this.game.level.movePlayer(packet.getUsername(), packet.getX(), packet.getY(), packet.getNumState(),
				packet.isMoving(), packet.getMovingDir());
	}
}
