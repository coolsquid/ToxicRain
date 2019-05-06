package coolsquid.toxicrain.integration.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;

/**
 * Expands the CraftTweaker {@link crafttweaker.api.player.IPlayer} interface
 * with a "toxicRain" property that can be used to access the ToxicRain CT API.
 */
@ZenRegister
@ZenExpansion("crafttweaker.player.IPlayer")
public class CTPlayerExtension {

	/**
	 * Retrieves an instance of {@link CTToxicRain}.
	 */
	@ZenGetter("toxicRain")
	public static CTToxicRain getToxicRain(IPlayer player) {
		return new CTToxicRain.Impl(player);
	}
}