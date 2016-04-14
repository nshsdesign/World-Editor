package main;

import java.awt.Canvas;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.World;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;
import picking.Picker3D;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import textures.ModelTexture;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
import worldParser.WorldFileLoader;

@SuppressWarnings("serial")
public class OpenGLView extends Canvas {

	private static final int WIDTH = 900;
	private static final int HEIGHT = 900 - 100 - 40 - 30;

	private Thread glThread;
	private World world;
	private static boolean selectionPick = false;
	private Entity currentSelection;
	public static Loader loader;
	public static MasterRenderer renderer;
	private static boolean createNew;

	private Vector3f basePosition;

	public static List<Entity> entities;
	public static List<Entity> normalMapEntities;
	public static List<Light> lights;
	//public static List<Terrain> terrains;
	public static List<GuiTexture> guiTextures;
	public static Camera camera;

	public OpenGLView(World world) {
		try {
			Display.setParent(this);
		} catch (LWJGLException e) {
			// handle exception
			e.printStackTrace();
		}
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setIgnoreRepaint(true);
		setBounds(25, 40 + 30 + 25, WIDTH, HEIGHT);

		this.world = world;
	}

	public void setupWorld() {
		DisplayManager.createDisplay();
		loader = new Loader();
		WorldFileLoader.init(loader);
		renderer = new MasterRenderer(loader);
		GuiRenderer guiRenderer = new GuiRenderer(loader);

		entities = new ArrayList<Entity>();
		normalMapEntities = new ArrayList<Entity>();
		lights = new ArrayList<Light>();
		//terrains = new ArrayList<Terrain>();
		guiTextures = new ArrayList<GuiTexture>();

		// Terrain Textures:

//		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrain", "grassy3"));
//		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrain", "dirt"));
//		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrain", "pinkFlowers"));
//		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrain", "Cobblestone"));
//
//		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
//		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMaps", "blendMap3"));
//
//		Terrain terrain = new Terrain(0, 0, loader, texturePack, blendMap, "heightMap4");
//		Terrain terrain2 = new Terrain(1, 0, loader, texturePack, blendMap, "heightMap");
//		terrains.add(terrain);
//		terrains.add(terrain2);

		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		for (int r = 0; r < 5; r++) {
			for (int c = 0; c < 5; c++) {
				waters.add(new WaterTile(r * 120 + 60, c * 120 + 60, 0));
			}
		}

		Light sun = new Light(new Vector3f(0, 50000, 50000), new Vector3f(1f, 1f, 1f));
		lights.add(sun);

		camera = new Camera();
		Picker3D picker = new Picker3D(camera);

//		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains.get(0));

//		 TEMP
		 Entity temp = new Entity(loader, "virus", new Vector3f(50, 40, 50),
				 new Vector3f(0,0,0), 10);
		 entities.add(temp);
		 Vector3f min = temp.getAABB().getMin();
		 Vector3f max = temp.getAABB().getMax();
		 Vector3f pos = temp.getPosition();
		 entities.add(new Entity(loader,"virus", new Vector3f((min.x*temp.getScale())+pos.x,(min.y*temp.getScale())+pos.y,(min.z*temp.getScale())+pos.z),
				 new Vector3f(0,0,0), 2));
		 entities.add(new Entity(loader,"virus", new Vector3f((max.x*temp.getScale())+pos.x,(max.y*temp.getScale())+pos.y,(max.z*temp.getScale())+pos.z),
				 new Vector3f(0,0,0), 2));

//		 System.out.println(temp.getAABB().getMax());
		 
		 while (!Display.isCloseRequested()) {

			// TODO: make currentSelection the last placed object, and make add
			// object and select object tools

			camera.move();
			for(Entity e: entities){
				e.update();
			}
			picker.update(entities);
			
			if(picker.getPickedEntity() != null) System.out.println("Caught one!!!");
			// temp.setRotY(temp.getRotY()+1);

			if (createNew) {
				if (entities.contains(currentSelection)) {
					entities.add(new Entity(currentSelection));
					entities.remove(currentSelection);
				}
				currentSelection = new Entity(loader, world.getCurrentObjectType(), new Vector3f(0, 0, 0),
						new Vector3f(0, 0, 0), Main.stats[6]);
				entities.add(currentSelection);
				createNew = false;
			}

			if (currentSelection != null) {
				if (currentSelection.getName() != world.getCurrentObjectType()) {
					currentSelection.setModel(loader, world.getCurrentObjectType());
				}

//				if (picker.getCurrentTerrainPoint() != null && selectionPick) {
//					currentSelection.setPosition(picker.getCurrentTerrainPoint());
//					if (Mouse.isButtonDown(0))
//						selectionPick = false;
//					basePosition = currentSelection.getPosition();
//				}
				if (basePosition == null) {
					basePosition = new Vector3f(0, 0, 0);
				}

				// set stats equal to sliders
				currentSelection.setPosition(new Vector3f(basePosition.x + Main.stats[0],
						basePosition.y + Main.stats[1], basePosition.z + Main.stats[2]));
				currentSelection.setRotX(Main.stats[3]);
				currentSelection.setRotY(Main.stats[4]);
				currentSelection.setRotZ(Main.stats[5]);
				currentSelection.setScale(Main.stats[6]);
			}

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

			// render reflection texture
			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, normalMapEntities, lights, camera,
					new Vector4f(0, 1, 0, -waters.get(0).getHeight() + 1));
			camera.getPosition().y += distance;
			camera.invertPitch();

			// render refraction texture
			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, lights, camera,
					new Vector4f(0, -1, 0, waters.get(0).getHeight()));

			// render to screen
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			buffers.unbindCurrentFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, lights, camera, new Vector4f(0, -1, 0, 100000));
			waterRenderer.render(waters, camera, sun);
			guiRenderer.render(guiTextures);

			DisplayManager.updateDisplay();

		}
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

	@Override
	public void addNotify() {
		super.addNotify();
		startGL();
	}

	@Override
	public void removeNotify() {
		stopGL();
		super.removeNotify();
	}

	private void startGL() {
		glThread = new Thread(new Runnable() {
			@Override
			public void run() {
				setupWorld();
			}
		}, "LWJGL Thread");

		glThread.start();
	}

	private void stopGL() {
		try {
			glThread.join();
		} catch (InterruptedException e) {
			// handle exception
			e.printStackTrace();
		}
	}

	public static void addNewObject() {
		createNew = true;
		selectionPick = true;
		// if(entities.contains(currentSelection))
		// entities.remove(currentSelection);
		// currentSelection = new Entity(loader, world.getCurrentObjectType(),
		// new Vector3f(0,0,0), new Vector3f(0,0,0), Main.stats[6], new
		// BoundingBox(new Vector3f(0,0,0),new Vector3f(0,0,0)));
		// entities.add(currentSelection);
	}

}
