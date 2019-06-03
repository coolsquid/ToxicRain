package coolsquid.toxicrain.integration.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
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
	 * Retrieves an instance of {@link CTPlayerData}.
	 */
	@ZenGetter("toxicRain")
	public static CTPlayerData getToxicRain(IPlayer player) {
		return new CTPlayerData(CraftTweakerMC.getPlayer(player));
	}
}