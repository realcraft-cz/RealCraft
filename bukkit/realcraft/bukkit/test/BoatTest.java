package realcraft.bukkit.test;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.others.AbstractCommand;

public class BoatTest extends AbstractCommand implements Listener {

    private Scoreboard scoreboard;
    private Objective objective;
    private Team spectatorTeam;

    public BoatTest() {
        super("boat");

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        spectatorTeam = scoreboard.registerNewTeam("xSpectator");
        spectatorTeam.setAllowFriendlyFire(false);
        spectatorTeam.setColor(ChatColor.GRAY);
        spectatorTeam.setPrefix(ChatColor.GRAY.toString());
        spectatorTeam.setCanSeeFriendlyInvisibles(true);
        spectatorTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);

        Bukkit.getPluginManager().registerEvents(this, RealCraft.getInstance());
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission("group.Manazer")) {
            return;
        }

        Boat boat = (Boat) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.BOAT);
        boat.setWoodType(TreeSpecies.REDWOOD);
        boat.addPassenger(player);

        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(4, 0, 0), EntityType.ARMOR_STAND);
        stand.setGravity(false);
        stand.setPersistent(false);
        stand.setSmall(true);
        stand.setInvisible(true);
        stand.setCollidable(false);

        player.setCollidable(false);

        player.setScoreboard(scoreboard);
        spectatorTeam.addEntry(player.getName());

        /*Boat boat2 = (Boat) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.BOAT);
        boat2.setWoodType(TreeSpecies.REDWOOD);

        stand.addPassenger(boat2);*/

        for (Player player2: Bukkit.getOnlinePlayers()) {
            if (player.equals(player2)) {
                continue;
            }

            player2.setCollidable(false);
            player2.setScoreboard(scoreboard);
            spectatorTeam.addEntry(player2.getName());

            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(boat.getEntityId());
            ((CraftPlayer)player2).getHandle().b.a(packet);

            PacketPlayOutEntityDestroy packet2 = new PacketPlayOutEntityDestroy(player.getEntityId());
            ((CraftPlayer)player2).getHandle().b.a(packet2);
        }

        Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(),new Runnable() {
            @Override
            public void run(){
                if (boat.isDead()) {
                    return;
                }
                if (boat != null) {
                }
                if (stand != null) {
                    stand.setInvisible(false);
                    ((CraftArmorStand)stand).getHandle().c(player.getLocation().getX() + 3.0, player.getLocation().getY() - 0, player.getLocation().getZ() + 0.0);
                }
            }
        }, 1, 1);
    }

    @EventHandler
    public void VehicleEntityCollisionEvent(VehicleEntityCollisionEvent event){
        if(event.getVehicle().getType() == EntityType.BOAT || event.getEntity().getType() == EntityType.BOAT){
            event.setCancelled(true);
            event.setCollisionCancelled(true);
        }
    }

    @EventHandler
    public void VehicleEntityCollisionEvent(PlayerJumpEvent event){
        //event.getPlayer().playEffect(EntityEffect.TOTEM_RESURRECT);
    }
}
