package main;

import java.awt.Canvas;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrain.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;

@SuppressWarnings("serial")
public class OpenGLView extends Canvas{

    private static final int WIDTH = 500;
    private static final int HEIGHT = 300;
    
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
        setBounds(25, Main.HEIGHT-(WIDTH*2/3)-25, WIDTH, HEIGHT);
    }
    
    public void setupWorld(){
    	DisplayManager.createDisplay();
		Loader loader = new Loader();

		// Terrain Textures:

		TerrainTexture backgroundTexture = new TerrainTexture(
				loader.loadTexture("grassy3"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(
				loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("Cobblestone"));

		TerrainTexturePack texturePack = new TerrainTexturePack(
				backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(
				loader.loadTexture("blendMap3"));		
		
		ModelData data = OBJFileLoader.loadOBJ("fern");
		RawModel firstModel = loader.loadToVAO(data.getVertices(),
				data.getTextureCoords(), data.getNormals(), data.getIndices());
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);
		TexturedModel firstTexModel = new TexturedModel(firstModel,	fernTextureAtlas);
		firstTexModel.getTexture().setHasTransparency(true);
		firstTexModel.getTexture().setUseFakeLighting(true);

		ModelData data2 = OBJFileLoader.loadOBJ("pine");
		RawModel secondModel = loader.loadToVAO(data2.getVertices(),
				data2.getTextureCoords(), data2.getNormals(),
				data2.getIndices());
		TexturedModel secondTexModel = new TexturedModel(secondModel,
				new ModelTexture(loader.loadTexture("pine")));
		secondTexModel.getTexture().setHasTransparency(true);
		secondTexModel.getTexture().setUseFakeLighting(true);
		// rockTexture.setShineDamper(10);
		// rockTexture.setReflectivity(1);

		ModelData data3 = OBJFileLoader.loadOBJ("stanfordBunny");
		RawModel rawBunnyModel = loader.loadToVAO(data3.getVertices(),
				data3.getTextureCoords(), data3.getNormals(),
				data3.getIndices());
		TexturedModel bunny = new TexturedModel(rawBunnyModel,
				new ModelTexture(loader.loadTexture("white")));

		Terrain terrain = new Terrain(0, 0, loader, texturePack, blendMap,
				"heightMap4");
		Terrain terrain2 = new Terrain(1, 0, loader, texturePack, blendMap,
				"heightMap");
		Terrain[][] terrains = new Terrain[2][2];
		terrains[0][0] = terrain;
		terrains[1][0] = terrain2;

		List<Entity> allEntities = new ArrayList<Entity>();

		Light sun = new Light(new Vector3f(0, 500, 500), new Vector3f(1f, 1f, 1f));
		List<Light> lights = new ArrayList<Light>();
		lights.add(sun);

		MasterRenderer renderer = new MasterRenderer(loader);
		
		Player player = new Player(bunny, new Vector3f(200,0,200), 0, 0, 0, 1, terrains);
		Camera camera = new Camera(player);

		//MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);
		
		allEntities.add(player);
		List<Entity> entities = new ArrayList<Entity>(allEntities);
		while (!Display.isCloseRequested()) {
			entities = new ArrayList<Entity>(allEntities);
			renderer.renderScene(entities,terrains,lights, camera, new Vector4f(0,1,0,100000));	
			
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
