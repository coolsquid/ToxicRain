package coolsquid.toxicrain.integration.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenRegister
@ZenExpansion("crafttweaker.player.IPlayer")
public class CTPlayerExtension {

	@ZenGetter("toxicRain")
	public static CTToxicRain getToxicRain(IPlayer player) {
		return new CTToxicRain.Impl(player);
	}
}