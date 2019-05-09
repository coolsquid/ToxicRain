package coolsquid.toxicrain.util;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/**
 * Lets other mods, the CraftTweaker integration and the /toxicrain command
 * protect specific players from toxic rain, either temporarily or indefinitely.
 * 
 * The capability is not available in {@link WorldClient}s, as the underlying
 * logic is not executed on the client, in order to limit CPU usage.
 */
public interface IPlayerData {

	@CapabilityInject(IPlayerData.class)
	static Capability<IPlayerData> CAPABILITY = null;

	/**
	 * Prevents rain from poisoning the specified player for {@code ticks} ticks.
	 * Can be set to -1 to indefinitely prevent rain from poisoning the specified
	 * player. Can be set to 0 to make the specified player susceptible to toxic
	 * rain again.
	 * 
	 * Must be either -1, 0 or a positive integer.
	 * 
	 * @see {@link #getDelay()}
	 * 
	 * @throws IllegalArgumentException If
	 *                                  {@code ticks < -1 || (ticks < 0 && ticks != -1)}.
	 */
	public void setDelay(int ticks);

	/**
	 * The delay, in ticks, until the specified player can be poisoned by rain
	 * again.
	 * 
	 * Can be -1, 0 or a positive integer.
	 * 
	 * @see {@link #setDelay(int)}
	 */
	public int getDelay();

	public static class CapabilityStorage implements IStorage<IPlayerData> {

		@Override
		public NBTBase writeNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("delay", instance.getDelay());
			return tag;
		}

		@Override
		public void readNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side, NBTBase nbt) {
			instance.setDelay(((NBTTagCompound) nbt).getInteger("delay"));
		}
	}

	public static class Impl implements IPlayerData {

		private int ticks = 0;

		@Override
		public void setDelay(int ticks) {
			if (ticks < -1 || (ticks < 0 && ticks != -1)) {
				throw new IllegalArgumentException(
						ticks + " is not a valid argument. \"ticks\" must be -1, 0 or a positive integer.");
			}
			this.ticks = ticks;
		}

		@Override
		public int getDelay() {
			return this.ticks;
		}
	}

	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

		private final IPlayerData instance = new IPlayerData.Impl();

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == CAPABILITY;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			return capability == CAPABILITY ? (T) instance : null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("delay", instance.getDelay());
			return tag;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			instance.setDelay(nbt.getInteger("delay"));
		}
	}
}