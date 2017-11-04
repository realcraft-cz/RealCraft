package com.realcraft.utils;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MapUtil {

	private static final int WIDTH = 128;
	private static final int HEIGHT = 128;
	private static HashMap<Integer,ItemStack[]> images = new HashMap<Integer,ItemStack[]>();

	public static void pasteMap(String url,Location location1,Location location2){
		ItemStack[] items = MapUtil.getMapItems(location1.getWorld(),url);
		if(items.length == 0) return;
		int index = 0;
		boolean xDiff = (location1.getBlockX() < location2.getBlockX());
		boolean zDiff = (location1.getBlockZ() < location2.getBlockZ());
		int y = (location1.getBlockY() > location2.getBlockY() ? location1.getBlockY() : location2.getBlockY());
		while(y >= location1.getBlockZ()){
			int x = location1.getBlockX();
			while(xDiff ? x <= location2.getBlockX() : x >= location2.getBlockX()){
				int z = location1.getBlockZ();
				while(zDiff ? z <= location2.getBlockZ() : z >= location2.getBlockZ()){
					Location location = new Location(location1.getWorld(),x,y,z);
					ItemFrame frame = MapUtil.getItemFrameAt(location);
					if(frame != null){
						if(index < items.length){
							frame.setItem(items[index]);
						}
					}
					index ++;
					z += (zDiff ? 1 : -1);
				}
				x += (xDiff ? 1 : -1);
			}
			y --;
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack[] getMapItems(World world,String url){
		int hash = url.hashCode();
		if(!images.containsKey(hash)){
			BufferedImage origImage = MapUtil.loadImage(url);
			if(origImage != null){
				int width = origImage.getWidth();
				int height = origImage.getHeight();

				int columns = (int) Math.ceil(width/WIDTH);
				int rows = (int) Math.ceil(height/HEIGHT);

				int remindX = width % WIDTH;
				int remindY = height % HEIGHT;

				if(remindX > 0) columns ++;
				if(remindY > 0) rows ++;

				BufferedImage[] cutImages = new BufferedImage[columns*rows];
				int imageX;
				int imageY = (remindY == 0 ? 0 : (remindY-HEIGHT)/2);
				for(int i=0;i<rows;i++){
					imageX = (remindX == 0 ? 0 : (remindX-WIDTH)/2);
					for(int a=0;a<columns;a++){
						cutImages[i*columns+a] = MapUtil.cutImage(origImage,imageX,imageY);
						imageX += WIDTH;
					}
					imageY += HEIGHT;
				}

				ItemStack[] items = new ItemStack[columns*rows];
				int index = 0;
				for(BufferedImage image : cutImages){
					MapView map = Bukkit.getServer().createMap(world);
					map.getRenderers().clear();
					map.addRenderer(new CustomMapRenderer(image));
					ItemStack item = new ItemStack(Material.MAP);
					item.setDurability(map.getId());
					items[index++] = item;
				}
				images.put(hash,items);
			}
		}
		return images.get(hash);
	}

	private static ItemFrame getItemFrameAt(Location location){
		Entity entities[] = location.getChunk().getEntities();
		for(Entity entity : entities){
			if(entity instanceof ItemFrame && LocationUtil.isSimilar(location,entity.getLocation())) return (ItemFrame) entity;
		}
		return null;
	}

	private static BufferedImage loadImage(String url){
		try {
			return ImageIO.read(new URL(url));
		} catch (MalformedURLException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		return null;
	}

	private static BufferedImage cutImage(BufferedImage image,int x,int y){
		BufferedImage newImage = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = newImage.getGraphics();
		graphics.drawImage(image,-x,-y,null);
		graphics.dispose();
		return newImage;
	}

	private static class CustomMapRenderer extends MapRenderer {

		private BufferedImage image;

		public CustomMapRenderer(BufferedImage image){
			this.image = image;
		}

		@Override
		public void render(MapView map, MapCanvas canvas, Player player){
			if(image == null) return;
			canvas.drawImage(0,0,image);
			image = null;
		}
	}
}