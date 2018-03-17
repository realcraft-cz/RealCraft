package realcraft.bukkit.cosmetics.mounts;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;

import realcraft.bukkit.cosmetics.utils.UtilParticles;

public class MountNyanSheep extends Mount {

	List<RGBColor> colors = new ArrayList<>();

    public MountNyanSheep(MountType type){
        super(type);
        colors.add(new RGBColor(255, 0, 0));
        colors.add(new RGBColor(255, 165, 0));
        colors.add(new RGBColor(255, 255, 0));
        colors.add(new RGBColor(154, 205, 50));
        colors.add(new RGBColor(30, 144, 255));
        colors.add(new RGBColor(148, 0, 211));
    }

    @Override
    public void onCreate(Player player){
    	Entity entity = player.getWorld().spawnEntity(player.getLocation(),this.getType().toEntityType());
    	entity.setPassenger(player);
        ((LivingEntity) entity).setNoDamageTicks(Integer.MAX_VALUE);
    	this.setEntity(player,entity);
    }

    @Override
    public void onClear(Player player){
    	Entity entity = this.getEntity(player);
    	if(entity != null){
    		entity.remove();
    		this.setEntity(player,null);
    	}
    }

    @Override
    public void onUpdate(Player player,Entity entity){
    	this.move(player);
    	if(this.isRunning(player)){
	    	((Sheep) entity).setColor(DyeColor.values()[new Random().nextInt(15)]);

	    	float y = 1.2f;
	        for (RGBColor rgbColor : colors) {
	            for (int i = 0; i < 10; i++)
	                UtilParticles.display(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue(),
	                        entity.getLocation().add(entity.getLocation().getDirection()
	                                .normalize().multiply(-1).multiply(1.4)).add(0, y, 0));
	            y -= 0.2;
	        }
    	}
    }

    class RGBColor {

        int red;
        int green;
        int blue;

        public RGBColor(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public int getBlue() {
            return blue;
        }

        public int getGreen() {
            return green;
        }

        public int getRed() {
            return red;
        }
    }
}