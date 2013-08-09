package amidst.map.layers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import MoF.Biome;
import MoF.ChunkManager;
import MoF.ReflectionInfo;
import amidst.Log;
import amidst.Options;
import amidst.foreign.VersionInfo;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.MapObjectNether;
import amidst.map.MapObjectStronghold;
import amidst.map.MapObjectTemple;
import amidst.map.MapObjectVillage;
import amidst.map.MapObjectWitchHut;

public class TempleLayer extends IconLayer {
	public static List<Biome> validBiomes;
	
	public TempleLayer() {
		super("temples");
		setVisibilityPref(Options.instance.showIcons);
		
		validBiomes = getValidBiomes();
	}
	public void generateMapObjects(Fragment frag) {
		int size = Fragment.SIZE >> 4;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				int chunkX = x + frag.getChunkX();
				int chunkY = y + frag.getChunkY();
				if (checkChunk(chunkX, chunkY, chunkManager)) {
					String biomeName = BiomeLayer.getBiomeNameForFragment(frag, x << 4, y << 4);
					if (biomeName.equals("Swampland"))
						frag.addObject(new MapObjectWitchHut(x << 4, y << 4).setParent(this));
					else
						frag.addObject(new MapObjectTemple(x << 4, y << 4).setParent(this));
				}
			}
		}
	}
	
	public List<Biome> getValidBiomes() {
		Biome[] ret;
		
		if (ReflectionInfo.instance.version.isAtLeast(VersionInfo.V1_4_2))
			ret = new Biome[] { Biome.d, Biome.s, Biome.w, Biome.x, Biome.h };
		else if (ReflectionInfo.instance.version.isAtLeast(VersionInfo.V12w22a))
			ret = new Biome[] { Biome.d, Biome.s, Biome.w };
		else
			ret = new Biome[] { Biome.d, Biome.s };
		
		return Arrays.asList(ret);
	}

	public boolean checkChunk(int chunkX, int chunkY, ChunkManager chunkManager) {
		int i = 32;
		int j = 8;
		
		int k = chunkX;
		int m = chunkY;
		if (chunkX < 0) chunkX -= i - 1;
		if (chunkY < 0) chunkY -= i - 1;
		
		int n = chunkX / i;
		int i1 = chunkY / i;
		Random localRandom = new Random();
	    long l1 = n * 341873128712L + i1 * 132897987541L + chunkManager.seed + 14357617;
	    localRandom.setSeed(l1);
		n *= i;
		i1 *= i;
		n += localRandom.nextInt(i - j);
		i1 += localRandom.nextInt(i - j);
		
		return (k == n) && (m == i1) && chunkManager.a(k * 16 + 8, m * 16 + 8, 0, validBiomes);
	}
}