package coolsquid.toxicrain.config;

import java.awt.Color;
import java.io.File;
import java.util.regex.Pattern;

import coolsquid.toxicrain.ToxicRain;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

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

	public static float minMoonFullness, maxMoonFullness;
	public static float minRainingStrength, minThunderingStrength, maxThunderingStrength;

	// Considered using a BitSet, but a boolean array is faster.
	// The additional memory use is of little importance, as the array is quite
	// small.
	public static boolean[] timeRanges;

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
		checkTimeDivisor = config.getInt("checkTimeDivisor", "general", 5, 1, Integer.MAX_VALUE,
				"ToxicRain checks whether the player should be poisoned once every xth tick. The performance impact of ToxicRain scales inversely with this value.");

		Property timeRangesProperty = config.get("general", "timeRanges", new String[0]);
		timeRangesProperty.setComment("Rain will only be toxic when the game time within one of these time ranges. Each list entry should be a number (123) or a range (0-123). The numbers must be between 0 and 23999.");
		timeRangesProperty.setValidationPattern(Pattern.compile("^(\\d+-\\d+|\\d+)$"));
		if (timeRangesProperty.getStringList().length == 0) {
			timeRanges = null;
		} else {
			timeRanges = new boolean[24000];
			for (String s : timeRangesProperty.getStringList()) {
				String[] ss = s.split("-");
				if (ss.length == 2) {
					int min = Integer.parseInt(ss[0]), max = Integer.parseInt(ss[1]);
					while (min <= max) {
						timeRanges[min++] = true;
					}
				} else if (ss.length == 1) {
					timeRanges[Integer.parseInt(s)] = true;
				} else {
					throw new RuntimeException("'timeRanges' should contain a number (123) or a range (0-123). \"" + s
							+ "\" is not a valid value.");
				}
			}
		}

		minMoonFullness = config.getFloat("minFullness", "general.moon", 0, 0, 1,
				"Rain will only be toxic when the moon is at least this full.");
		maxMoonFullness = config.getFloat("maxFullness", "general.moon", 1, 0, 1,
				"Rain will only be toxic when the moon is at most this full.");
		minRainingStrength = config.getFloat("minRainingStrength", "general", 0.2F, 0, 1,
				"Rain strength scales from 0 to 1, and changes whenever rain fades in or out. Rain will only be toxic when it is at least this strong. In Vanilla, the world is only considered to experience rain when the rain strength is at least 0.2. Warning: if this is 0, ToxicRain will apply its effects even without rain!");
		minThunderingStrength = config.getFloat("minThunderingStrength", "general.thunder", 0, 0, 1,
				"Thunder strength scales from 0 to 1. Rain will only be toxic when the thunder strength is at least this high. In Vanilla, the world is only considered to experience rain when the rain strength is at least 0.9. Note that thunder strength is multiplied by rain strength.");
		maxThunderingStrength = config.getFloat("maxThunderingStrength", "general.thunder", 0, 0, 1,
				"Thunder strength scales from 0 to 1. Rain will only be toxic when the thunder strength is at most this high. In Vanilla, the world is only considered to experience rain when the rain strength is at least 0.9. Note that thunder strength is multiplied by rain strength.");

		delayOnSpawn = config.getInt("delayOnSpawn", "grace_periods", 600, -1, Integer.MAX_VALUE,
				"The delay / grace period, in ticks, until a recently spawned player can be poisoned by rain. -1 does nothing.");
		delayOnDeath = config.getInt("delayOnDeath", "grace_periods", 200, -1, Integer.MAX_VALUE,
				"The delay / grace period, in ticks, until a recently respawned player can be poisoned by rain. -1 does nothing.");
		delayOnSleep = config.getInt("delayOnSleep", "grace_periods", -1, -1, Integer.MAX_VALUE,
				"The delay / grace period, in ticks, until a player that has recently woken up can be poisoned by rain. -1 does nothing.");
		delayOnDeathMessage = config.getBoolean("delayOnDeathMessage", "grace_periods", false,
				"Whether to inform the player about the grace period that follows a respawn.");
		delayOnSpawnMessage = config.getBoolean("delayOnSpawnMessage", "grace_periods", false,
				"Whether to inform the player about the grace period that follows their first spawn.");
		delayOnSleepMessage = config.getBoolean("delayOnSleepMessage", "grace_periods", false,
				"Whether to inform the player about the grace period that occurs after waking up.");

		effect = config.getString("effect", "effect", "minecraft:poison",
				"The potion effect to apply to players when exposed to rain.");
		duration = config.getInt("duration", "effect", 200, 1, 12000,
				"The duration of the poison effect, in ticks (1/20th second).");
		amplifier = config.getInt("amplifier", "effect", 0, 0, 10, "The amplifier of the effect.");
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

	public static boolean isTimeValid(long time) {
		if (timeRanges == null) {
			return true;
		}
		// The world time occasionally exceeds 23999, for some reason
		return timeRanges[time > 23999 ? 23999 : (int) time];
	}

	public static boolean areRainAndThunderValid(float rainingStrength, float thunderingStrength) {
		return rainingStrength >= minRainingStrength && thunderingStrength >= minThunderingStrength
				&& thunderingStrength <= maxThunderingStrength;
	}

	public static boolean isMoonPhaseValid(float moonPhaseFactor) {
		return moonPhaseFactor >= minMoonFullness && moonPhaseFactor <= maxMoonFullness;
	}
}