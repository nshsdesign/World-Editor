package toolbox;

public class GraphicsOptions {
	
	public static boolean HD_WATER = true;
	public static boolean SHADOWS = true;
	public static boolean ANTI_ALIASING = true;
	public static boolean DOF_EFFECT = true;
	public static boolean BLOOM_EFFECT = true;
	public static int MSAA_SAMPLES = 4;
	
	public static boolean needsPostProcessing(){
		return DOF_EFFECT || BLOOM_EFFECT;
	}

}
