package coolsquid.toxicrain;

import com.google.common.base.Preconditions;

import coolsquid.toxicrain.config.ConfigManager;
import coolsquid.toxicrain.network.PacketManager;
import coolsquid.toxicrain.util.CommandToxicRain;
import coolsquid.toxicrain.util.IPlayerData;
import coolsquid.toxicrain.util.ModEventHandler;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ToxicRain.MODID, name = ToxicRain.NAME, version = ToxicRain.VERSION, dependencies = ToxicRain.DEPENDENCIES,
		updateJSON = ToxicRain.UPDATE_JSON, guiFactory = "coolsquid.toxicrain.config.ConfigGuiFactory", acceptableRemoteVersions = "*")
public class ToxicRain {

	public static final String MODID = "toxicrain";
	public static final String NAME = "ToxicRain";
	public static final String VERSION = "2.0.0";
	public static final String DEPENDENCIES = "required-after:forge@[14.21.1.2387,)";
	public static final String UPDATE_JSON =
			"https://gist.githubusercontent.com/coolsquid/6b48d527776a66c644e1ce1b3c2776ae/raw/toxicrain.json";

	public static Potion effect;

	public static Potion antidote;
	public static PotionType antidoteType;
	public static PotionType longAntidoteType;

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {
		ConfigManager.load();
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
		CapabilityManager.INSTANCE.register(IPlayerData.class, new IPlayerData.CapabilityStorage(),
				IPlayerData.Impl::new);
	}

	@Mod.EventHandler
	public void onInit(FMLInitializationEvent event) throws Exception {
		if (ConfigManager.enableAntidote) {
			BrewingRecipeRegistry.addRecipe(
					PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1), PotionTypes.POISON),
					new ItemStack(Items.SUGAR),
					PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1), antidoteType));
			BrewingRecipeRegistry.addRecipe(
					PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1), antidoteType),
					new ItemStack(Items.REDSTONE),
					PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1), longAntidoteType));
		}
		PacketManager.load();
	}

	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent event) {
		effect = Preconditions.checkNotNull(Potion.getPotionFromResourceLocation(ConfigManager.effect), "The effect cannot be null");
	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		if (ConfigManager.enableCommand) {
			event.registerServerCommand(new CommandToxicRain());
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
		antidoteType = new PotionType(new PotionEffect(antidote, ConfigManager.antidoteDuration));
		antidoteType.setRegistryName(new ResourceLocation(MODID, "antidote_type"));
		event.getRegistry().register(antidoteType);
		longAntidoteType = new PotionType(new PotionEffect(antidote, ConfigManager.longAntidoteDuration));
		longAntidoteType.setRegistryName(new ResourceLocation(MODID, "long_antidote_type"));
		event.getRegistry().register(longAntidoteType);
	}
}
