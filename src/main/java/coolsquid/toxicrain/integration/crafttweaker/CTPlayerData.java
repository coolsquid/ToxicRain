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
 * The actual ToxicRain CT integration.
 * 
 * Can be retrieved through {@link CTPlayerExtension#getToxicRain(IPlayer)}.
 */
@ZenRegister
@ZenClass("mods.toxicrain.PlayerData")
public class CTPlayerData {

	private final IPlayer player;

	protected CTPlayerData(IPlayer player) {
		this.player = player;
	}

	/**
	 * Prevents rain from poisoning the specified player for {@code ticks} ticks.
	 * Can be set to -1 to indefinitely prevent rain from poisoning the specified
	 * player. Can be set to 0 to make the specified player susceptible to toxic
	 * rain again.
	 * 
	 * Must be either -1, 0 or a positive integer.
	 * 
	 * Should be wrapped in an {@code if (!player.world.remote)} block.
	 * 
	 * @see {@link #getDelay()}
	 */
	@ZenSetter("delay")
	public void setDelay(int ticks) {
		if (ticks < -1 || (ticks < 0 && ticks != -1)) {
			CraftTweakerAPI.logError("\"ticks\" must be either -1, 0, or a positive integer. Automatically changing "
					+ ticks + " to -1.");
			ticks = -1;
		}
		EntityPlayer actualPlayer = (EntityPlayer) this.player.getInternal();
		if (!actualPlayer.world.isRemote) {
			actualPlayer.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH).setDelay(ticks);
		} else {
			CraftTweakerAPI.getLogger().logWarning(
					"Tried to access \"toxicRain.delay\", which is not accessible on the client. This is unlikely to cause any major issues, unless you rely on it to execute important client-side logic. You may nonetheless want to wrap the relevant script section in an if (!player.world.remote) block.");
		}
	}

	/**
	 * The delay, in ticks, until the specified player can be poisoned by rain
	 * again.
	 * 
	 * Can be -1, 0 or a positive integer.
	 * 
	 * Should be wrapped in an {@code if (!player.world.remote)} block.
	 * 
	 * @see {@link #setDelay(int)}
	 */
	@ZenGetter("delay")
	public int getDelay() {
		EntityPlayer actualPlayer = (EntityPlayer) this.player.getInternal();
		if (!actualPlayer.world.isRemote) {
			return actualPlayer.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH).getDelay();
		}
		CraftTweakerAPI.getLogger().logWarning(
				"Tried to access \"toxicRain.delay\", which is not accessible on the client. This is unlikely to cause any major issues, unless you rely on it to execute important client-side logic. You may nonetheless want to wrap the relevant script section in an if (!player.world.remote) block. Returning 0.");
		return 0;
	}
}