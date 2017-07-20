package coolsquid.toxicrain;

import java.io.File;

import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@Mod(modid = ToxicRain.MODID, name = ToxicRain.NAME, version = ToxicRain.VERSION, dependencies = ToxicRain.DEPENDENCIES, updateJSON = ToxicRain.UPDATE_JSON)
public class ToxicRain {

	public static final String MODID = "toxicrain";
	public static final String NAME = "ToxicRain";
	public static final String VERSION = "1.0.0";
	public static final String DEPENDENCIES = "required-after:forge@[14.21.1.2387,)";
	public static final String UPDATE_JSON = "https://coolsquid.me/api/version/toxicrain.json";

	private static Potion antidote;
	private static PotionType antidoteType;
	private static PotionType longAntidoteType;

	private static boolean toxicSnow;
	private static boolean toxicWater;

	private static boolean enableAntidote;
	private static int antidoteDuration;
	private static int longAntidoteDuration;

	private static Potion effect;
	private static int duration;
	private static int amplifier;
	private static boolean particles;

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(new File("config/ToxicRain.cfg"));
		enableAntidote = config.getBoolean("enabled", "antidote", true, "Whether to enable the antidote potion.");
		antidoteDuration = config.getInt("duration", "antidote", 3600, 1, 36000,
				"The duration of the antidote effect, in ticks (1/20th seconds).");
		longAntidoteDuration = config.getInt("longDuration", "antidote", 9600, 1, 96000,
				"The duration of the long antidote effect, in ticks (1/20th seconds).");
		toxicSnow = config.getBoolean("toxicSnow", "general", false,
				"If true, being exposed to snow will have the same effect as being exposed to rain.");
		toxicWater = config.getBoolean("toxicWater", "general", false,
				"If true, touching water blocks will have the same effect as being exposed to rain.");
		effect = Potion.getPotionFromResourceLocation(config.getString("effect", "effect", "minecraft:poison",
				"The potion effect to apply to players when exposed to rain."));
		duration = config.getInt("duration", "effect", 200, 1, 12000,
				"The duration of the poison effect, in ticks (1/20th seconds).");
		amplifier = config.getInt("amplifier", "effect", 0, 0, 10,
				"The amplifier of the effect. Has no effect with the standard poison effect (blame Mojang).");
		particles = config.getBoolean("particles", "effect", true,
				"Whether the potion should come with particles or not.");
		if (config.hasChanged()) {
			config.save();
		}
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Mod.EventHandler
	public void onInit(FMLInitializationEvent event) {
		if (enableAntidote) {
			BrewingRecipeRegistry.addRecipe(
					PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1), PotionTypes.POISON),
					new ItemStack(Items.SUGAR),
					PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1), antidoteType));
			BrewingRecipeRegistry.addRecipe(
					PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1), antidoteType),
					new ItemStack(Items.REDSTONE),
					PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1), longAntidoteType));
		}
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if (event.phase == Phase.END && (!enableAntidote || !event.player.isPotionActive(antidote))) {
			if (toxicWater && event.player.isInWater()) {
				event.player.addPotionEffect(new PotionEffect(effect, duration, amplifier, false, particles));
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
				if (biome.canRain() || toxicSnow && biome.getEnableSnow()) {
					event.player.addPotionEffect(new PotionEffect(effect, duration, amplifier, false, particles));
				}
			}
		}
	}

	@SubscribeEvent
	public void registerPotion(RegistryEvent.Register<Potion> event) {
		antidote = new Potion(false, 14500000) {
		};
		antidote.setRegistryName(new ResourceLocation(MODID, "antidote"));
		event.getRegistry().register(antidote);
	}

	@SubscribeEvent
	public void registerPotionType(RegistryEvent.Register<PotionType> event) {
		antidoteType = new PotionType(new PotionEffect(antidote, antidoteDuration));
		antidoteType.setRegistryName(new ResourceLocation(MODID, "antidote_type"));
		event.getRegistry().register(antidoteType);
		longAntidoteType = new PotionType(new PotionEffect(antidote, longAntidoteDuration));
		longAntidoteType.setRegistryName(new ResourceLocation(MODID, "long_antidote_type"));
		event.getRegistry().register(longAntidoteType);
	}
}
