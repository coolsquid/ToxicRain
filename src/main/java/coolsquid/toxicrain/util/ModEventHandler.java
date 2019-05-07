package coolsquid.toxicrain.util;

import coolsquid.toxicrain.ToxicRain;
import coolsquid.toxicrain.config.ConfigGuiFactory;
import coolsquid.toxicrain.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModEventHandler {

	@SubscribeEvent
	public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer && !event.getObject().world.isRemote) {
			event.addCapability(new ResourceLocation(ToxicRain.MODID, "capability_toxicrain"),
					new IPlayerData.Provider());
		}
	}

	@SubscribeEvent
	public void onPlayerCloned(PlayerEvent.Clone event) {
		if (event.isWasDeath() && !event.getEntityPlayer().world.isRemote) {
			event.getEntityPlayer().getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH)
					.setDelay(event.getOriginal().getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH).getDelay());
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (!event.player.world.isRemote && ConfigManager.delayOnDeath != -1) {
			IPlayerData cap = event.player.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH);
			if (cap.getDelay() != -1) {
				cap.setDelay(ConfigManager.delayOnDeath);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if (event.phase == Phase.END && !event.player.world.isRemote
				&& event.player.ticksExisted % ConfigManager.checkTimeDivisor == 0) {
			IPlayerData cap = event.player.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH);
			if (cap.getDelay() == -1) {
				return;
			} else if (cap.getDelay() > 0) {
				cap.setDelay(cap.getDelay() - ConfigManager.checkTimeDivisor);
				return;
			}
			if (ConfigManager.enableAntidote && event.player.isPotionActive(ToxicRain.antidote)) {
				return;
			}
			if (isPoisonousDimension(event.player.world.provider.getDimension())) {
				if (event.player.isInWater() && ConfigManager.toxicWater) {
					event.player.addPotionEffect(new PotionEffect(ToxicRain.effect, ConfigManager.duration,
							ConfigManager.amplifier, false, ConfigManager.particles));
				} else {
					if (!event.player.world.isRaining()) {
						return;
					} else if (!event.player.world.canSeeSky(event.player.getPosition())) {
						return;
					} else if (event.player.world.getPrecipitationHeight(event.player.getPosition())
							.getY() > event.player.getPosition().getY()) {
						return;
					}
					Biome biome = event.player.world.getBiome(event.player.getPosition());
					if (biome.canRain() || biome.getEnableSnow() && ConfigManager.toxicSnow) {
						event.player.addPotionEffect(new PotionEffect(ToxicRain.effect, ConfigManager.duration,
								ConfigManager.amplifier, false, ConfigManager.particles));
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		ClientHandler.colorizeTextures(isPoisonousDimension(event.getWorld().provider.getDimension()));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if (event.getModID().equals(ToxicRain.MODID)) {
			ConfigGuiFactory.config.save();
			ConfigGuiFactory.config = null;
			ConfigManager.load();
			if (Minecraft.getMinecraft().world != null) {
				ClientHandler
						.colorizeTextures(isPoisonousDimension(Minecraft.getMinecraft().world.provider.getDimension()));
			}
		}
	}

	private static boolean isPoisonousDimension(int dim) {
		if (ConfigManager.isWhitelist) {
			return ConfigManager.blacklist.contains(dim);
		} else {
			return !ConfigManager.blacklist.contains(dim);
		}
	}
}