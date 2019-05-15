package coolsquid.toxicrain.util;

import coolsquid.toxicrain.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientEventHandler {

	public static ClientEventHandler instance;

	private static float moonPhaseFactor = 0;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onWorldTick(TickEvent.ClientTickEvent event) {
		if (event.phase == Phase.END && Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().world.getTotalWorldTime() % ConfigManager.checkTimeDivisor == 0) {
			float moonPhaseFactor = Minecraft.getMinecraft().world.getCurrentMoonPhaseFactor();
			if (ClientEventHandler.moonPhaseFactor != moonPhaseFactor) {
				ClientEventHandler.moonPhaseFactor = moonPhaseFactor;
				ClientHandler.colorizeTextures(ConfigManager.isPoisonousDimension(Minecraft.getMinecraft().world.provider.getDimension()), moonPhaseFactor);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		// This should only fire in client worlds
		if (event.getWorld().isRemote) {
			moonPhaseFactor = event.getWorld().getCurrentMoonPhaseFactor();
			ClientHandler.colorizeTextures(ConfigManager.isPoisonousDimension(event.getWorld().provider.getDimension()), moonPhaseFactor);
		}
	}
}