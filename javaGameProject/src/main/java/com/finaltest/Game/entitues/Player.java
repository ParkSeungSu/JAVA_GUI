package com.finaltest.Game.entitues;

import java.awt.Rectangle;

import com.finaltest.Game.Game;
import com.finaltest.Game.InputHandler;
import com.finaltest.Game.gfx.Colours;
import com.finaltest.Game.gfx.Font;
import com.finaltest.Game.gfx.Screen;
import com.finaltest.Game.level.Level;
import com.finaltest.Game.net.packets.Packet02Move;
import com.finaltest.Game.net.packets.Packet03Collision;

public class Player extends Mob {

	private InputHandler input;
	private int colour = Colours.get(-1, 111, 145, 543);
	private int scale = 1;
	protected boolean isSwimming = false;
	private int tickCount = 0;
	private String username;
	private boolean alive;
	

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public Player(Level level, int x, int y, InputHandler input, String username) {
		super(level, "Player", x, y, 1);
		this.input = input;
		this.username = username;
		this.alive =true;

	}

	public void tick() {
		int xa = 0;
		int ya = 0;

		if (input != null) {
			if (input.up.isPressed()) {
				ya--;
			}
			if (input.down.isPressed()) {
				ya++;
			}
			if (input.left.isPressed()) {
				xa--;
			}
			if (input.right.isPressed()) {
				xa++;
			}

			if (xa != 0 || ya != 0) {
				move(xa, ya);
				isMoving = true;
				Packet02Move packet = new Packet02Move(this.getUsername(), this.x, this.y, this.numState, this.isMoving,
						this.movingDir);
				packet.writeData(Game.game.socketClient);
			} else {
				isMoving = false;
			}
			if (level.getTile(this.x >> 3, this.y >> 3).getId() == 3) {
				isSwimming = true;
			}
			if (isSwimming && level.getTile(this.x >> 3, this.y >> 3).getId() != 3) {
				isSwimming = false;
			}
			tickCount++;
		}
	}

	public void render(Screen screen) {
			
			int xTile = 0;
			int yTile = 28;
			if(!this.alive) {
				xTile = 0;
				yTile=30;
			}
			int walkingSpeed = 4;
			int flipTop = (numState >> walkingSpeed) & 1;
			int flipBottom = (numState >> walkingSpeed) & 1;
			if (movingDir == 1) {
				xTile += 2;
			} else if (movingDir > 1) {
				xTile += 4 + ((numState >> walkingSpeed) & 1) * 2;
				flipTop = (movingDir - 1) % 2;
			}
			int modifier = 8 * scale;
			int xOffset = x - modifier / 2;
			int yOffset = y - modifier / 2 - 4;
			if (isSwimming) {
				int waterColour = 0;
				yOffset += 4;
				if (tickCount % 60 < 15) {

					waterColour = Colours.get(-1, -1, 225, -1);
				} else if (15 <= tickCount % 60 && tickCount % 60 < 30) {
					yOffset -= 1;
					waterColour = Colours.get(-1, 235, 115, -1);
				} else if (30 <= tickCount % 60 && tickCount % 60 < 45) {
					waterColour = Colours.get(-1, 115, -1, 225);
				} else {
					yOffset -= 1;
					waterColour = Colours.get(-1, 225, 115, -1);
				}
				screen.render(xOffset, yOffset + 3, 0 + 27 * 32, waterColour, 0x00, 1);
				screen.render(xOffset + 8, yOffset + 3, 0 + 27 * 32, waterColour, 0x01, 1);
			}
			screen.render(xOffset + (modifier * flipTop), yOffset, xTile + yTile * 32, colour, flipTop, scale);
			screen.render(xOffset + modifier - (modifier * flipTop), yOffset, (xTile + 1) + yTile * 32, colour, flipTop,
					scale);

			if (!isSwimming) {
				screen.render(xOffset + (modifier * flipBottom), yOffset + modifier, xTile + (yTile + 1) * 32, colour,
						flipBottom, scale);
				screen.render(xOffset + modifier - (modifier * flipBottom), yOffset + modifier,
						(xTile + 1) + (yTile + 1) * 32, colour, flipBottom, scale);
			}
			if (username != null) {
				Font.render(username, screen, xOffset - ((username.length() - 1) / 2 * 8), yOffset - 10,
						Colours.get(-1, -1, -1, 555), 1);
			}
		
	}

	public boolean hasCollided(int xa, int ya) {
		int xMin = 0;
		int xMax = 7;
		int yMin = 3;
		int yMax = 7;
		for (int x = xMin; x < xMax; x++) {
			if (isSolidTile(xa, ya, x, yMin)) {
				return true;
			}
		}
		for (int x = xMin; x < xMax; x++) {
			if (isSolidTile(xa, ya, x, yMax)) {
				return true;
			}
		}
		for (int y = yMin; y < yMax; y++) {
			if (isSolidTile(xa, ya, xMin, y)) {
				return true;
			}
		}
		for (int y = yMin; y < yMax; y++) {
			if (isSolidTile(xa, ya, xMax, y)) {
				return true;
			}
		}
		Rectangle me = new Rectangle(this.x, this.y, 7, 4);
		for (Entity e : level.getEntities()) {
			if (!this.equals(e)) {
				Rectangle other = new Rectangle(e.x, e.y, 7, 4);
				if (me.intersects(other)&&((Player)e).alive) {
					((Player) e).alive = false;
					Packet03Collision packet=new Packet03Collision(((Player)e).username, e.x, e.y, ((Player)e).alive);
					packet.writeData(Game.game.socketClient);
				} 
			}
		}
		return false;
	}

	public String getUsername() {
		return this.username;

	}

}
