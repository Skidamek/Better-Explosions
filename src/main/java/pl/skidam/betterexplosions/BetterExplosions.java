package pl.skidam.betterexplosions;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.skidam.betterexplosions.config.Config;
import pl.skidam.betterexplosions.data.RebuildExplosion;

import java.util.HashMap;
import java.util.Map;

public class BetterExplosions implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("better-explosions");
	public static Map<Integer, RebuildExplosion> explosions = new HashMap<>();
	@Override
	public void onInitialize() {
		new Config();
		LOGGER.info("BetterExplosions initialized");
	}
}