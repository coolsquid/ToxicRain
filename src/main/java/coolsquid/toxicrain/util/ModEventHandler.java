package coolsquid.toxicrain.util;

import coolsquid.toxicrain.ToxicRain;
import coolsquid.toxicrain.config.ConfigGuiFactory;
import coolsquid.toxicrain.config.ConfigManager;
import coolsquid.toxicrain.network.PacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
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
			IPlayerData newCap = event.getEntityPlayer().getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH);
			IPlayerData oldCap = event.getOriginal().getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH);
			newCap.setDelay(oldCap.getDelay());
			newCap.setFirstSpawn(oldCap.isFirstSpawn());
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (!event.player.world.isRemote && ConfigManager.delayOnDeath != -1) {
			IPlayerData cap = event.player.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH);
			if (cap.getDelay() != -1) {
				cap.setDelay(ConfigManager.delayOnDeath);
				if (ConfigManager.delayOnDeathMessage) {
					event.player.sendStatusMessage(
							new TextComponentTranslation("toxicrain.alert.delay", ConfigManager.delayOnDeath / 20)
									.setStyle(new Style().setColor(TextFormatting.DARK_GREEN)),
							true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerSpawn(PlayerLoggedInEvent event) {
		if (!event.player.world.isRemote && ConfigManager.delayOnSpawn != -1) {
			IPlayerData cap = event.player.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH);
			if (cap.isFirstSpawn()) {
				if (cap.getDelay() != -1) {
					cap.setDelay(ConfigManager.delayOnSpawn);
					if (ConfigManager.delayOnSpawnMessage) {
						event.player.sendStatusMessage(
								new TextComponentTranslation("toxicrain.alert.delay", ConfigManager.delayOnSpawn / 20)
										.setStyle(new Style().setColor(TextFormatting.DARK_GREEN)),
								true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerWakeUp(PlayerWakeUpEvent event) {
		if (!event.getEntityPlayer().world.isRemote && event.shouldSetSpawn() && ConfigManager.delayOnSleep != -1) {
			IPlayerData cap = event.getEntityPlayer().getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH);
			if (cap.getDelay() != -1) {
				cap.setDelay(ConfigManager.delayOnSleep);
				if (ConfigManager.delayOnSleepMessage) {
					event.getEntityPlayer().sendStatusMessage(
							new TextComponentTranslation("toxicrain.alert.delay", ConfigManager.delayOnSpawn / 20)
									.setStyle(new Style().setColor(TextFormatting.DARK_GREEN)),
							true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if (event.phase == Phase.END && !event.player.world.isRemote
				&& event.player.ticksExisted % ConfigManager.checkTimeDivisor == 0) {
			IPlayerData cap = event.player.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH);
			if (cap.getDelay() == -1) {
				if (cap.areClientTweaksActive()) {
					PacketManager.sendToxicityUpdate(false, (EntityPlayerMP) event.player);
					cap.setClientTweaksActive(false);
				}
				return;
			} else if (cap.getDelay() > 0) {
				if (cap.areClientTweaksActive()) {
					PacketManager.sendToxicityUpdate(false, (EntityPlayerMP) event.player);
					cap.setClientTweaksActive(false);
				}
				cap.setDelay(cap.getDelay() - ConfigManager.checkTimeDivisor);
				return;
			}
			if (!(ConfigManager.enableAntidote && event.player.isPotionActive(ToxicRain.antidote))
					&& ConfigManager.isPoisonousDimension(event.player.world.provider.getDimension())) {
				float moonPhaseFactor = event.player.world.getCurrentMoonPhaseFactor();
				long time = event.player.world.getWorldTime();
				if (moonPhaseFactor >= ConfigManager.minMoonFullness && moonPhaseFactor <= ConfigManager.maxMoonFullness
						&& time >= ConfigManager.minTime && time <= ConfigManager.maxTime) {
					boolean rainAndThunderConditions = event.player.world.rainingStrength >= ConfigManager.minRainingStrength
							&& event.player.world.thunderingStrength >= ConfigManager.minThunderingStrength;
					if (event.player.isInWater() && ConfigManager.toxicWater) {
						event.player.addPotionEffect(new PotionEffect(ToxicRain.effect, ConfigManager.duration,
								ConfigManager.amplifier, false, ConfigManager.particles));
					} else {
						if (event.player.world.isRaining() && rainAndThunderConditions
								&& event.player.world.canSeeSky(event.player.getPosition())
								&& event.player.world.getPrecipitationHeight(event.player.getPosition())
										.getY() <= event.player.posY) {
							Biome biome = event.player.world.getBiome(event.player.getPosition());
							if (biome.canRain() || biome.getEnableSnow() && ConfigManager.toxicSnow) {
								event.player.addPotionEffect(new PotionEffect(ToxicRain.effect, ConfigManager.duration,
										ConfigManager.amplifier, false, ConfigManager.particles));
							}
						}
					}
					if (rainAndThunderConditions) {
						if (!cap.areClientTweaksActive()) {
							PacketManager.sendToxicityUpdate(true, (EntityPlayerMP) event.player);
							cap.setClientTweaksActive(true);
						}
					} else {
						if (cap.areClientTweaksActive()) {
							PacketManager.sendToxicityUpdate(false, (EntityPlayerMP) event.player);
							cap.setClientTweaksActive(false);
						}
					}
				} else {
					if (cap.areClientTweaksActive()) {
						PacketManager.sendToxicityUpdate(false, (EntityPlayerMP) event.player);
						cap.setClientTweaksActive(false);
					}
				}
			} else {
				if (cap.areClientTweaksActive()) {
					PacketManager.sendToxicityUpdate(false, (EntityPlayerMP) event.player);
					cap.setClientTweaksActive(false);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if (event.getModID().equals(ToxicRain.MODID)) {
			if (Minecraft.getMinecraft().currentScreen instanceof ConfigGuiFactory.Gui) {
				((ConfigGuiFactory.Gui) Minecraft.getMinecraft().currentScreen).config.save();
			}
			ConfigManager.load();
			if (Minecraft.getMinecraft().world != null) {
				ClientHandler.newRainTexture = null;
				ClientHandler.newSnowTexture = null;
				ClientHandler.newParticlesTexture = null;
				ClientHandler.colorizeTextures(ClientHandler.enable);
			}
		}
	}
}