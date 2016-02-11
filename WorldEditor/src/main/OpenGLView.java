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
import guis.GuiRenderer;
import guis.GuiTexture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
import worldParser.WorldFileLoader;

@SuppressWarnings("serial")
public class OpenGLView extends Canvas{

    private static final int WIDTH = 900;
    private static final int HEIGHT = 900 - 100 - 40 - 30;
    
    private Thread glThread;
    
    public OpenGLView(){
        try {
            Display.setParent(this);
        } catch (LWJGLException e) {
            //handle exception
            e.printStackTrace();
        }
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setIgnoreRepaint(true);
        setBounds(25, 40 + 30 + 25, WIDTH, HEIGHT);
    }
    
    public void setupWorld(){
    	DisplayManager.createDisplay();
		Loader loader = new Loader();
		WorldFileLoader.init(loader);
		MasterRenderer renderer = new MasterRenderer(loader);
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		
		List<Entity> entities = new ArrayList<Entity>();
		List<Entity> normalMapEntities = new ArrayList<Entity>();
		List<Light> lights = new ArrayList<Light>();
		List<Terrain> terrains = new ArrayList<Terrain>();
		List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();

		// Terrain Textures:

		TerrainTexture backgroundTexture = new TerrainTexture(
				loader.loadTexture("terrain", "grassy3"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrain", "dirt"));
		TerrainTexture gTexture = new TerrainTexture(
				loader.loadTexture("terrain", "pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrain", "Cobblestone"));

		TerrainTexturePack texturePack = new TerrainTexturePack(
				backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(
				loader.loadTexture("blendMaps", "blendMap3"));		

		Terrain terrain = new Terrain(0, 0, loader, texturePack, blendMap,
				"heightMap4");
		Terrain terrain2 = new Terrain(1, 0, loader, texturePack, blendMap,
				"heightMap");
		terrains.add(terrain);
		terrains.add(terrain2);
		
		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		List<WaterTile> waters = new ArrayList<WaterTile>();		
		for(int r=0; r<5; r++){
			for(int c=0; c<5; c++){
				waters.add(new WaterTile(r*120 + 60, c*120 + 60, 0));
			}
		}

		Light sun = new Light(new Vector3f(0, 50000, 50000), new Vector3f(1f, 1f, 1f));
		lights.add(sun);
		
		Camera camera = new Camera();

		//MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);
		
		while (!Display.isCloseRequested()) {
			camera.move();

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			//render reflection texture
			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -waters.get(0).getHeight()+1));
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			//render refraction texture
			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, waters.get(0).getHeight()));
			
			//render to screen
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			buffers.unbindCurrentFrameBuffer();	
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));	
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
            //handle exception
            e.printStackTrace();
        }
    }
	
}
