package coolsquid.toxicrain.config;

import java.awt.Color;
import java.io.File;

import coolsquid.toxicrain.ToxicRain;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraftforge.common.config.Configuration;

public class ConfigManager {

	public static boolean toxicSnow;
	public static boolean toxicWater;
	public static int delayOnSpawn;
	public static int delayOnDeath;
	public static int delayOnSleep;
	public static boolean delayOnSpawnMessage;
	public static boolean delayOnDeathMessage;
	public static boolean delayOnSleepMessage;
	public static int checkTimeDivisor;

	public static boolean enableAntidote;
	public static int antidoteDuration;
	public static int longAntidoteDuration;

	public static String effect;
	public static int duration;
	public static int amplifier;
	public static boolean particles;

	public static boolean isWhitelist;
	public static IntSet blacklist;

	public static boolean enableCommand;

	public static Color rainColor, rainDropsColor;
	public static Color snowColor;

	public static float minMoonFullness;
	public static float maxMoonFullness;

	public static boolean enableConfigGui;

	public static void load() {
		Configuration config = new Configuration(new File("config/ToxicRain.cfg"), ToxicRain.VERSION);
		enableAntidote = config.getBoolean("enabled", "antidote", true, "Whether to enable the antidote potion.");
		antidoteDuration = config.getInt("duration", "antidote", 3600, 1, 36000,
				"The duration of the antidote effect, in ticks (1/20th seconds).");
		longAntidoteDuration = config.getInt("longDuration", "antidote", 9600, 1, 96000,
				"The duration of the long antidote effect, in ticks (1/20th seconds).");

		toxicSnow = config.getBoolean("toxicSnow", "general", false,
				"If true, being exposed to snow will have the same effect as being exposed to rain.");
		toxicWater = config.getBoolean("toxicWater", "general", false,
				"If true, touching water blocks will have the same effect as being exposed to rain.");
		minMoonFullness = config.getFloat("minFullness", "general.moon", 0, 0, 1, "Rain will only be toxic when the moon is at least this full.");
		maxMoonFullness = config.getFloat("maxFullness", "general.moon", 1, 0, 1, "Rain will only be toxic when the moon is at most this full.");
		delayOnSpawn = config.getInt("delayOnSpawn", "grace_periods", 600, -1, Integer.MAX_VALUE,
				"The delay / grace period, in ticks, until a recently spawned player can be poisoned by rain. -1 does nothing.");
		delayOnDeath = config.getInt("delayOnDeath", "grace_periods", 200, -1, Integer.MAX_VALUE,
				"The delay / grace period, in ticks, until a recently respawned player can be poisoned by rain. -1 does nothing.");
		delayOnSleep = config.getInt("delayOnSleep", "grace_periods", -1, -1, Integer.MAX_VALUE,
				"The delay / grace period, in ticks, until a player that has recently woken up can be poisoned by rain. -1 does nothing.");
		delayOnDeathMessage = config.getBoolean("delayOnDeathMessage", "grace_periods", false, "Whether to inform the player about the grace period that follows a respawn.");
		delayOnSpawnMessage = config.getBoolean("delayOnSpawnMessage", "grace_periods", false, "Whether to inform the player about the grace period that follows their first spawn.");
		delayOnSleepMessage = config.getBoolean("delayOnSleepMessage", "grace_periods", false, "Whether to inform the player about the grace period that occurs after waking up.");
		checkTimeDivisor = config.getInt("checkTimeDivisor", "general", 5, 1, Integer.MAX_VALUE,
				"ToxicRain checks whether the player should be poisoned once every xth tick. The performance impact of ToxicRain scales inversely with this value.");

		effect = config.getString("effect", "effect", "minecraft:poison",
				"The potion effect to apply to players when exposed to rain.");
		duration = config.getInt("duration", "effect", 200, 1, 12000,
				"The duration of the poison effect, in ticks (1/20th second).");
		amplifier = config.getInt("amplifier", "effect", 0, 0, 10,
				"The amplifier of the effect. Has no effect with the standard poison effect (blame Mojang).");
		particles =
				config.getBoolean("particles", "effect", true, "Whether the potion should come with particles or not.");

		isWhitelist = config.getBoolean("dimensionWhitelist", "blacklist", false,
				"If true, 'dimensionList' operates as a whitelist instead of a blacklist.");
		int[] blacklistArray = config.get("blacklist", "dimensionList", new int[0],
				"A list of dimensions that should not have poisonous rain. Can be used as a whitelist if 'dimensionWhitelist' is true.")
				.getIntList();
		blacklist = new IntOpenHashSet(blacklistArray.length);
		for (int i : blacklistArray) {
			blacklist.add(i);
		}

		enableCommand =
				config.getBoolean("enableCommand", "command", true, "Whether to enable the /toxicrain command or not.");

		rainColor = getColor(config.getString("rainColor", "client", "#586100",
				"The color of rain. Vanilla is #4667c3. #586100 is a suitable green-brownish color. Leave empty to disable."));
		rainDropsColor = getColor(config.getString("rainDropsColor", "client", "#586100",
				"The color of rain that hits the ground. Vanilla is #4667c3. #586100 is a suitable green-brownish color. Leave empty to disable."));
		snowColor = getColor(config.getString("snowColor", "client", "#586100",
				"The color of rain that hits the ground. Vanilla is #ffffff. #586100 is a suitable green-brownish color. Leave empty to disable."));

		enableConfigGui = config.getBoolean("enableConfigGui", "client", true,
				"Whether to enable the in-game configuration screen or not.");
		if (config.hasChanged()) {
			config.save();
		}
	}

	private static Color getColor(String s) {
		if (s.isEmpty()) {
			return null;
		} else {
			return Color.decode(s);
		}
	}

	public static boolean isPoisonousDimension(int dim) {
		if (ConfigManager.isWhitelist) {
			return ConfigManager.blacklist.contains(dim);
		} else {
			return !ConfigManager.blacklist.contains(dim);
		}
	}
}