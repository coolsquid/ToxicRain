package coolsquid.toxicrain.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import coolsquid.toxicrain.ToxicRain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ConfigGuiFactory implements IModGuiFactory {

	public static Configuration config;

	@Override
	public void initialize(Minecraft minecraftInstance) {
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public boolean hasConfigGui() {
		return ConfigManager.enableConfigGui;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new Gui(parentScreen, new Configuration(new File("config/ToxicRain.cfg")));
	}

	public static class Gui extends GuiConfig {

		public Gui(GuiScreen parent, Configuration config) {
			super(parent, getConfigElements(config), ToxicRain.MODID, ToxicRain.MODID, false, false,
					ToxicRain.NAME + " configuration", config.getConfigFile().getAbsolutePath());
		}

		private static List<IConfigElement> getConfigElements(Configuration config) {
			if (ConfigGuiFactory.config != null) {
				throw new RuntimeException("What?");
			}
			ConfigGuiFactory.config = config;
			config.load();
			List<IConfigElement> list = new ArrayList<>();
			config.getCategoryNames().stream().sorted().forEach((e) -> list.add(new ConfigElement(config.getCategory(e))));
			return list;
		}
	}
}