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
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
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
	public void onPlayerSpawn(PlayerLoggedInEvent event) {
		if (!event.player.world.isRemote) {
			IPlayerData cap = event.player.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH);
			if (cap.isFirstSpawn()) {
				if (cap.getDelay() != -1) {
					cap.setDelay(ConfigManager.delayOnSpawn);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerSpawn(EntityJoinWorldEvent event) {
		if (!event.getEntity().world.isRemote && event.getEntity() instanceof EntityPlayer) {
			IPlayerData cap = event.getEntity().getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH);
			System.out.println(cap.isFirstSpawn());
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
			if (ConfigManager.isPoisonousDimension(event.player.world.provider.getDimension())) {
				float moonPhaseFactor = event.player.world.getCurrentMoonPhaseFactor();
				if (moonPhaseFactor >= ConfigManager.minMoonFullness
						&& moonPhaseFactor <= ConfigManager.maxMoonFullness) {
					if (event.player.isInWater() && ConfigManager.toxicWater) {
						event.player.addPotionEffect(new PotionEffect(ToxicRain.effect, ConfigManager.duration,
								ConfigManager.amplifier, false, ConfigManager.particles));
					} else if (event.player.world.isRaining() && event.player.world.canSeeSky(event.player.getPosition()) && event.player.world.getPrecipitationHeight(event.player.getPosition())
								.getY() <= event.player.getPosition().getY()) {
						Biome biome = event.player.world.getBiome(event.player.getPosition());
						if (biome.canRain() || biome.getEnableSnow() && ConfigManager.toxicSnow) {
							event.player.addPotionEffect(new PotionEffect(ToxicRain.effect, ConfigManager.duration,
									ConfigManager.amplifier, false, ConfigManager.particles));
						}
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if (event.getModID().equals(ToxicRain.MODID)) {
			ConfigGuiFactory.config.save();
			ConfigManager.load();
			if (Minecraft.getMinecraft().world != null) {
				ClientHandler.newRainTexture = null;
				ClientHandler.newSnowTexture = null;
				ClientHandler.newParticlesTexture = null;
				ClientHandler
						.colorizeTextures(ConfigManager.isPoisonousDimension(Minecraft.getMinecraft().world.provider.getDimension()), Minecraft.getMinecraft().world.getCurrentMoonPhaseFactor());
			}
		}
	}
}