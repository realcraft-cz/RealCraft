package realcraft.bukkit.fights;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.utils.LocationUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FightRankBoard {

	private static final int WIDTH = 128;
	private static final int HEIGHT = 128;
	private Location[] locations;

	public FightRankBoard(){
		this.drawBoard();
	}

	private Location[] getLocations(){
		if(locations == null){
			locations = new Location[2];
			locations[0] = LocationUtil.getConfigLocation(Fights.getConfig(),"rankboard.minLoc");
			locations[1] = LocationUtil.getConfigLocation(Fights.getConfig(),"rankboard.maxLoc");
		}
		return locations;
	}

	private void drawBoard(){
		ItemStack[] images = this.getImages();
		Location location1 = this.getLocations()[0];
		Location location2 = this.getLocations()[1];
		if(images == null) return;
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
					ItemFrame frame = this.getItemFrameAt(location);
					if(frame != null){
						if(index < images.length){
							frame.setItem(images[index]);
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

	private ItemFrame getItemFrameAt(Location location){
		Entity entities[] = location.getChunk().getEntities();
		for(Entity entity : entities){
			if(entity instanceof ItemFrame && LocationUtil.isSimilar(location,entity.getLocation())) return (ItemFrame) entity;
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public ItemStack[] getImages(){
		ItemStack[] images = null;
		if(images == null){
			World world = Bukkit.getWorld("world");
			BufferedImage origImage = this.loadImage(new File(RealCraft.getInstance().getDataFolder()+"/fights/ranks.png"));
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
						cutImages[i*columns+a] = this.cutImage(origImage,imageX,imageY);
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
					ItemStack item = new ItemStack(Material.FILLED_MAP);
					MapMeta meta = (MapMeta)item.getItemMeta();
					meta.setMapId(map.getId());
					item.setItemMeta(meta);
					items[index++] = item;
				}
				images = items;
			}
		}
		return images;
	}

	private BufferedImage loadImage(File file){
		try {
			return ImageIO.read(file);
		} catch (IOException e){
			e.printStackTrace();
		}
		return null;
	}

	private BufferedImage cutImage(BufferedImage image,int x,int y){
		BufferedImage newImage = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = newImage.getGraphics();
		graphics.drawImage(image,-x,-y,null);
		graphics.dispose();
		return newImage;
	}

	private class CustomMapRenderer extends MapRenderer {

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