package amidst.mojangapi.mocking;

import java.util.Collections;
import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.mocking.json.BiomeRequestRecordJson;
import amidst.mojangapi.world.WorldType;

@ThreadSafe
public class BenchmarkingMinecraftInterface implements MinecraftInterface {
	private final MinecraftInterface inner;
	private final List<BiomeRequestRecordJson> records;

	public BenchmarkingMinecraftInterface(MinecraftInterface inner, List<BiomeRequestRecordJson> records) {
		this.inner = inner;
		this.records = Collections.synchronizedList(records);
	}

	@Override
	public MinecraftInterface.World createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		return new World(inner.createWorld(seed, worldType, generatorOptions));
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return inner.getRecognisedVersion();
	}

	private class World implements MinecraftInterface.World {
		private final MinecraftInterface.World innerWorld;

		private World(MinecraftInterface.World innerWorld) {
			this.innerWorld = innerWorld;
		}

		@Override
		public int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
				throws MinecraftInterfaceException {
			long start = System.nanoTime();
			int[] biomeData = innerWorld.getBiomeData(x, y, width, height, useQuarterResolution);
			long end = System.nanoTime();

			String thread = Thread.currentThread().getName();
			records.add(new BiomeRequestRecordJson(x, y, width, height, useQuarterResolution, start, end-start, thread));

			return biomeData;
		}
	}
}
