package coolsquid.toxicrain.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import coolsquid.toxicrain.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientHandler {

	public static final ResourceLocation RAIN_ORIGINAL_TEXTURE = new ResourceLocation("minecraft", "textures/environment/rain.png");
	public static final ResourceLocation SNOW_ORIGINAL_TEXTURE = new ResourceLocation("minecraft", "textures/environment/snow.png");
	public static final ResourceLocation PARTICLES_ORIGINAL_TEXTURE = new ResourceLocation("minecraft", "textures/particle/particles.png");

	public static IParticleFactory originalRainDropsFactory, originalWaterSplashFactory;

	public static void colorizeTextures(boolean toxicDimension) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			if (ConfigManager.rainColor != null && toxicDimension) {
				load(ConfigManager.rainColor, RAIN_ORIGINAL_TEXTURE);
			} else {
				Minecraft.getMinecraft().getTextureManager().loadTexture(RAIN_ORIGINAL_TEXTURE, new SimpleTexture(RAIN_ORIGINAL_TEXTURE));
			}
			if (ConfigManager.snowColor != null && toxicDimension) {
				load(ConfigManager.snowColor, SNOW_ORIGINAL_TEXTURE);
			} else {
				Minecraft.getMinecraft().getTextureManager().loadTexture(SNOW_ORIGINAL_TEXTURE, new SimpleTexture(SNOW_ORIGINAL_TEXTURE));
			}
			if (ConfigManager.rainDropsColor != null && toxicDimension) {
				boolean b = true;
				int red = 0, green = 0, blue = 0, alpha = 0;
				try (InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(PARTICLES_ORIGINAL_TEXTURE).getInputStream()) {
					BufferedImage im = TextureUtil.readBufferedImage(in);
					for (int x = 26; x <= 50; x++) {
						for (int y = 14; y <= 15; y++) {
							int rgb = im.getRGB(x, y);
							Color c = new Color(rgb);
							if (c.getRGB() != -16777216) {
								if (b) {
									b = false;
									red = 255 - c.getRed();
									green = 255 - c.getGreen();
									blue = 255 - c.getBlue();
									alpha = 255 - c.getAlpha();
								}
								im.setRGB(x, y, new Color(bounds(c.getRed() + red), bounds(c.getGreen() + green), bounds(c.getBlue() + blue), bounds(c.getAlpha() + alpha)).getRGB());
							}
						}
					}
					Minecraft.getMinecraft().getTextureManager().loadTexture(PARTICLES_ORIGINAL_TEXTURE, new DynamicTexture(im));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else {
				Minecraft.getMinecraft().getTextureManager().loadTexture(PARTICLES_ORIGINAL_TEXTURE, new SimpleTexture(PARTICLES_ORIGINAL_TEXTURE));
			}
			if (ConfigManager.rainDropsColor != null && toxicDimension && originalRainDropsFactory == null && originalWaterSplashFactory == null) {
				Map<Integer, IParticleFactory> particleTypes = ReflectionHelper.getPrivateValue(ParticleManager.class, Minecraft.getMinecraft().effectRenderer, 6);
				originalRainDropsFactory = particleTypes.get(EnumParticleTypes.WATER_DROP.getParticleID());
				final float r = ConfigManager.rainDropsColor.getRed() / 255F;
				final float g = ConfigManager.rainDropsColor.getGreen() / 255F;
				final float b = ConfigManager.rainDropsColor.getBlue() / 255F;
				final float a = ConfigManager.rainDropsColor.getAlpha() / 255F;
				Minecraft.getMinecraft().effectRenderer.registerParticle(EnumParticleTypes.WATER_DROP.getParticleID(), (particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, p_178902_15_) -> {
					Particle particle = originalRainDropsFactory.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, p_178902_15_);
					particle.setRBGColorF(r,g,b);
					particle.setAlphaF(a);
					return particle;
				});
				originalWaterSplashFactory = particleTypes.get(EnumParticleTypes.WATER_SPLASH.getParticleID());
				Minecraft.getMinecraft().effectRenderer.registerParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), (particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, p_178902_15_) -> {
					Particle particle = originalWaterSplashFactory.createParticle(particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, p_178902_15_);
					particle.setRBGColorF(0, 52 / 255F, 204 / 255F);
					particle.setAlphaF(a);
					return particle;
				});
			} else {
				Minecraft.getMinecraft().effectRenderer.registerParticle(EnumParticleTypes.WATER_DROP.getParticleID(), originalRainDropsFactory);
				Minecraft.getMinecraft().effectRenderer.registerParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), originalWaterSplashFactory);
			}
		});
	}

	private static void load(Color color, ResourceLocation texture) {
		boolean b = true;
		int red = 0, green = 0, blue = 0, alpha = 0;
		try (InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(texture).getInputStream()) {
			BufferedImage im = TextureUtil.readBufferedImage(in);
			for (int x = 0; x < im.getWidth(); x++) {
				for (int y = 0; y < im.getHeight(); y++) {
					int rgb = im.getRGB(x, y);
					Color c = new Color(rgb);
					if (c.getRGB() != -16777216) {
						if (b) {
							b = false;
							red = color.getRed() - c.getRed();
							green = color.getGreen() - c.getGreen();
							blue = color.getBlue() - c.getBlue();
							alpha = color.getAlpha() - c.getAlpha();
						}
						im.setRGB(x, y, new Color(bounds(c.getRed() + red), bounds(c.getGreen() + green), bounds(c.getBlue() + blue), bounds(c.getAlpha() + alpha)).getRGB());
					}
				}
			}
			Minecraft.getMinecraft().getTextureManager().loadTexture(texture, new DynamicTexture(im));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static int bounds(int i) {
		return Math.min(255, Math.max(0, i));
	}
}