package com.finaltest.Game.level;

import java.awt.image.TileObserver;
import java.util.ArrayList;
import java.util.List;

import com.finaltest.Game.entitues.Entity;
import com.finaltest.Game.entitues.Player;
import com.finaltest.Game.gfx.Screen;
import com.finaltest.Game.level.tiles.Tile;

public class Level {
	private byte[] tiles;
	public int width;
	public int height;
	public List<Entity> entites=new ArrayList<Entity>();
	
	public Level(int width, int height) {
		tiles = new byte[width * height];
		this.height = height;
		this.width = width;
		this.generateLevel();
	}

	public void generateLevel() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x *  y % 10 < 5) {
					tiles[x + y * width] = Tile.GRASS.getId();
				}else {
					tiles[x + y * width] = Tile.STONE.getId();
				}
			}
		}
	}
	public void tick() {
		for(Entity e:entites) {
			e.tick();
		}
	}
	public void renderTile(Screen screen, int xOffset, int yOffset) {
		if (xOffset < 0)
			xOffset = 0;
		if (xOffset > ((width << 3) - screen.width))
			xOffset = ((width << 3) - screen.width);
		if (yOffset < 0)
			yOffset = 0;
		if (yOffset > ((height << 3) - screen.height))
			yOffset = ((height << 3) - screen.height);

		screen.setOffset(xOffset, yOffset);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				getTile(x, y).render(screen, this, x << 3, y << 3);
			}
		}
	}
	public void renderEntities(Screen screen) {
		for(Entity e:entites) {
			e.render(screen);
		}
	}
	

	private Tile getTile(int x, int y) {
		if (x < 0 || x > width || y < 0 || y > height)
			return Tile.VOID;
		return Tile.tiles[tiles[x + y * width]];
	}

	public void addEntity(Player player) {
		this.entites.add(player);
		
	}
}
