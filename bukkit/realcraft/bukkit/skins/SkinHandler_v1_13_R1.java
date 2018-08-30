package realcraft.bukkit.skins;

import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class SkinHandler_v1_13_R1 {

	public static void updateSkin(Player player){
		EntityPlayer ep = ((CraftPlayer)player).getHandle();
		PacketPlayOutEntityDestroy destroyEntity = new PacketPlayOutEntityDestroy(new int[]{ep.getId()});
		PacketPlayOutPlayerInfo removePlayer = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[]{ep});
		PacketPlayOutPlayerInfo addPlayer = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[]{ep});
		PacketPlayOutNamedEntitySpawn spawnEntity = new PacketPlayOutNamedEntitySpawn(ep);
		PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(ep.getId(), ep.getDataWatcher(), true);
		PacketPlayOutHeldItemSlot helditem = new PacketPlayOutHeldItemSlot(ep.inventory.itemInHandIndex);
		WorldServer worldserver = (WorldServer)ep.getWorld();
		PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(worldserver.dimension, worldserver.getDifficulty(), worldserver.getWorldData().getType(), ep.playerInteractManager.getGameMode());
		PacketPlayOutPosition position = new PacketPlayOutPosition(ep.locX, ep.locY, ep.locZ, ep.yaw, ep.pitch, new HashSet(), 0);
		PacketPlayOutEntityHeadRotation headrotation = new PacketPlayOutEntityHeadRotation(ep, (byte)MathHelper.d(ep.getHeadRotation() * 256.0F / 360.0F));
		DedicatedPlayerList playerList = ((CraftServer)Bukkit.getServer()).getHandle();
		for(int i = 0; i < playerList.players.size(); ++i) {
			EntityPlayer ep1 = playerList.players.get(i);
			if (ep1.getBukkitEntity().canSee(ep.getBukkitEntity())) {
				PlayerConnection con = ep1.playerConnection;
				con.sendPacket(removePlayer);
				con.sendPacket(addPlayer);
				if (ep1.getId() != ep.getId()) {
					con.sendPacket(destroyEntity);
					con.sendPacket(spawnEntity);
					con.sendPacket(headrotation);
				}

				for(int j = 0; j < EnumItemSlot.values().length; ++j) {
					EnumItemSlot slot = EnumItemSlot.values()[j];
					ItemStack itemstack = ep.getEquipment(slot);
					if (!itemstack.isEmpty()) {
						con.sendPacket(new PacketPlayOutEntityEquipment(ep.getId(), slot, itemstack));
					}
				}
			}
		}

		PlayerConnection conx = ep.playerConnection;
		conx.sendPacket(metadata);
		conx.sendPacket(respawn);
		conx.sendPacket(position);
		conx.sendPacket(helditem);
		ep.updateAbilities();
		ep.triggerHealthUpdate();
		ep.updateInventory(ep.defaultContainer);
	}
}
