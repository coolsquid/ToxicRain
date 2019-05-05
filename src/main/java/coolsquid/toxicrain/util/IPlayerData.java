package coolsquid.toxicrain.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IPlayerData {

	@CapabilityInject(IPlayerData.class)
	static Capability<IPlayerData> CAPABILITY = null;

	public void setDelay(int ticks);

	public int getDelay();

	public void decrementDelay(int ticks);

	public static class CapabilityStorage implements IStorage<IPlayerData> {

		@Override
		public NBTBase writeNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side) {
			return new NBTTagInt(instance.getDelay());
		}

		@Override
		public void readNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side, NBTBase nbt) {
			instance.setDelay(((NBTTagInt) nbt).getInt());
		}
	}

	public static class Impl implements IPlayerData {

		private int ticks = 0;

		@Override
		public void setDelay(int b) {
			this.ticks = b;
		}

		@Override
		public int getDelay() {
			return this.ticks;
		}

		@Override
		public void decrementDelay(int ticks) {
			this.ticks -= ticks;
		}
	}

	public static class Provider implements ICapabilitySerializable<NBTTagInt> {

		private final IPlayerData instance = new IPlayerData.Impl();

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == CAPABILITY;
		}
		
		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			return capability == CAPABILITY ? (T) instance  : null;
		}

		@Override
		public NBTTagInt serializeNBT() {
			return new NBTTagInt(instance.getDelay());
		}

		@Override
		public void deserializeNBT(NBTTagInt nbt) {
			instance.setDelay(nbt.getInt());
		}
	}
}