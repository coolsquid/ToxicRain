package coolsquid.toxicrain.util;

import coolsquid.toxicrain.ConfigManager;
import coolsquid.toxicrain.ToxicRain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class PlayerTickHandler {

	public static final PlayerTickHandler INSTANCE = new PlayerTickHandler();

	@SubscribeEvent
	public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(ToxicRain.MODID, "capability_toxicrain"), new IPlayerData.Provider());
		}
	}

	@SubscribeEvent
	public void onPlayerCloned(PlayerEvent.Clone event) {
		if (event.isWasDeath()) {
			event.getEntityPlayer().getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH).setDelay(event.getOriginal().getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH).getDelay());
		}
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if (event.phase == Phase.END && event.side == Side.SERVER && event.player.ticksExisted % ConfigManager.checkTimeDivisor == 0) {
			IPlayerData cap = event.player.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH);
			if (cap.getDelay() == -1) {
				return;
			} else if (cap.getDelay() > 0) {
				cap.decrementDelay(ConfigManager.checkTimeDivisor);
				return;
			}
			if (ConfigManager.enableAntidote && event.player.isPotionActive(ToxicRain.antidote)) {
				return;
			}
			if (isPoisonousDimension(event.player.world.provider.getDimension())) {
				if (event.player.isInWater() && ConfigManager.toxicWater) {
					event.player.addPotionEffect(new PotionEffect(ToxicRain.effect, ConfigManager.duration, ConfigManager.amplifier, false, ConfigManager.particles));
				} else {
					if (!event.player.world.isRaining()) {
						return;
					} else if (!event.player.world.canSeeSky(event.player.getPosition())) {
						return;
					} else if (event.player.world.getPrecipitationHeight(event.player.getPosition()).getY() > event.player
							.getPosition().getY()) {
						return;
					}
					Biome biome = event.player.world.getBiome(event.player.getPosition());
					if (biome.canRain() || biome.getEnableSnow() && ConfigManager.toxicSnow) {
						event.player.addPotionEffect(new PotionEffect(ToxicRain.effect, ConfigManager.duration, ConfigManager.amplifier, false, ConfigManager.particles));
					}
				}
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