package coolsquid.toxicrain.network;

import coolsquid.toxicrain.util.ClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketToxicityUpdate implements IMessage {

	private boolean toxicRain;

	public PacketToxicityUpdate() {

	}

	public PacketToxicityUpdate(boolean toxicRain) {
		this.toxicRain = toxicRain;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		toxicRain = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(toxicRain);
	}

	public static class Handler implements IMessageHandler<PacketToxicityUpdate, IMessage> {

		@Override
		public IMessage onMessage(PacketToxicityUpdate message, MessageContext ctx) {
			if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
				ClientHandler.colorizeTextures(message.toxicRain);
			}
			return null;
		}
	}
}