package coolsquid.toxicrain.integration.crafttweaker;

import coolsquid.toxicrain.util.IPlayerData;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenRegister
@ZenClass
public interface CTToxicRain {

	@ZenSetter("delay")
	public void setDelay(int ticks);

	@ZenGetter("delay")
	public int getDelay();

	static class Impl implements CTToxicRain {

		private final IPlayer player;

		public Impl(IPlayer player) {
			this.player = player;
		}

		@Override
		public void setDelay(int ticks) {
			EntityPlayer actualPlayer = (EntityPlayer) this.player.getInternal();
			actualPlayer.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH).setDelay(ticks);
		}

		@Override
		public int getDelay() {
			EntityPlayer actualPlayer = (EntityPlayer) this.player.getInternal();
			return actualPlayer.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH).getDelay();
		}
	}
}