package coolsquid.toxicrain.integration.crafttweaker;

import coolsquid.toxicrain.util.IPlayerData;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;

/**
 * The actual ToxicRain CT API.
 * 
 * Can be retrieved through {@link CTPlayerExtension#getToxicRain(IPlayer)}.
 */
@ZenRegister
@ZenClass
public interface CTToxicRain {

	/**
	 * Prevents rain from poisoning the specified player for {@code ticks} ticks.
	 * Can be set to -1 to indefinitely prevent rain from poisoning the specified
	 * player. Can be set to 0 to make the specified player susceptible to toxic
	 * rain again.
	 * 
	 * Must be either -1, 0 or a positive integer.
	 * 
	 * @see {@link #getDelay()}
	 */
	@ZenSetter("delay")
	public void setDelay(int ticks);

	/**
	 * The delay, in ticks, until the specified player can be poisoned by rain
	 * again.
	 * 
	 * Can be -1, 0 or a positive integer.
	 * 
	 * @see {@link #setDelay(int)}
	 */
	@ZenGetter("delay")
	public int getDelay();

	static class Impl implements CTToxicRain {

		private final IPlayer player;

		public Impl(IPlayer player) {
			this.player = player;
		}

		@Override
		public void setDelay(int ticks) {
			if (ticks < -1 || (ticks < 0 && ticks != -1)) {
				CraftTweakerAPI
						.logError("\"ticks\" must be either -1, 0, or a positive integer. Automatically changing "
								+ ticks + " to -1.");
				ticks = -1;
			}
			EntityPlayer actualPlayer = (EntityPlayer) this.player.getInternal();
			if (!actualPlayer.world.isRemote) {
				actualPlayer.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH).setDelay(ticks);
			}
		}

		@Override
		public int getDelay() {
			EntityPlayer actualPlayer = (EntityPlayer) this.player.getInternal();
			return actualPlayer.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH).getDelay();
		}
	}
}