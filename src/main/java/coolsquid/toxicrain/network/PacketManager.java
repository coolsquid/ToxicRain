package coolsquid.toxicrain.network;

import coolsquid.toxicrain.ToxicRain;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketManager {

	private static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ToxicRain.MODID);

	public static void load() {
		INSTANCE.registerMessage(PacketToxicityUpdate.Handler.class, PacketToxicityUpdate.class, 0, Side.CLIENT);
	}

	public static void sendToxicityUpdate(boolean b, EntityPlayerMP player) {
		INSTANCE.sendTo(new PacketToxicityUpdate(b), player);
	}
}