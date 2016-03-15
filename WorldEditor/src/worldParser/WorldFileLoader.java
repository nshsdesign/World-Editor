package worldParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.BoundingBox;
import entities.Entity;
import entities.World;
import renderEngine.Loader;

public class WorldFileLoader {

	// e "model_file" "posX posY posZ" "rotX rotY rotZ" "scale" "boxposX boxposY
	// boxposZ" " boxsizeX boxsizeY boxsizeZ" "isStatic"

	private static final String RES_LOC = "res/";
	private static final String SUBFOLDER = "worlds/";
	private static Loader loader;
	private static List<Entity> entities = new ArrayList<Entity>();
	private static List<String> objectTypes = new ArrayList<String>();

	public static void loadWorldFile(String worldFileName) {
		FileReader isr = null;
		File objFile = new File(RES_LOC + worldFileName + ".world");
		try {
			isr = new FileReader(objFile);
		} catch (FileNotFoundException e) {
			System.err.println("File not found in res; don't use any extention");
		}
		BufferedReader reader = new BufferedReader(isr);
		String line;
		entities = new ArrayList<Entity>();
		try {
			line = reader.readLine();
			while (line != null) {
				if (line.startsWith("e ")) {
					String[] currentLine = line.split("|");
					String name = currentLine[1];
					Vector3f pos = new Vector3f((float) Float.valueOf(currentLine[2]),
							(float) Float.valueOf(currentLine[3]), (float) Float.valueOf(currentLine[4]));
					Vector3f rot = new Vector3f((float) Float.valueOf(currentLine[5]),
							(float) Float.valueOf(currentLine[6]), (float) Float.valueOf(currentLine[7]));
					float scale = (float) Float.valueOf(currentLine[8]);
					Vector3f boxPos = new Vector3f((float) Float.valueOf(currentLine[9]),
							(float) Float.valueOf(currentLine[10]), (float) Float.valueOf(currentLine[11]));
					Vector3f boxSize = new Vector3f((float) Float.valueOf(currentLine[12]),
							(float) Float.valueOf(currentLine[13]), (float) Float.valueOf(currentLine[14]));

					Entity e = new Entity(loader, name, pos, rot, scale, new BoundingBox(boxPos, boxSize));
					entities.add(e);

				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading the file");
		}
	}

	public static void loadObjectTypes(String objectsFileName) {
		FileReader isr = null;
		File objFile = new File(RES_LOC + SUBFOLDER + objectsFileName + ".type");
		try {
			isr = new FileReader(objFile);
		} catch (FileNotFoundException e) {
			System.err.println("File not found in res; don't use any extention");
		}
		BufferedReader reader = new BufferedReader(isr);
		String line;
		entities = new ArrayList<Entity>();
		try {
			line = reader.readLine();
			while (line != null) {
				if (line.startsWith("obj ")) {
					String[] currentLine = line.split(" ");
					String name = currentLine[1];
					objectTypes.add(name);

				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading the file");
		}
	}

	public static void saveWorldFile(String worldFileName, World world) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(worldFileName, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < world.getEntityInfo().length; i++)
			writer.println(world.getEntityInfo()[i]);

		writer.close();
	}

	public static World getWorld() {
		return new World(entities, objectTypes);
	}

	public static List<String> getObjectTypes() {
		return objectTypes;
	}

	public static List<Entity> getEntities() {
		return entities;
	}

	public static void init(Loader l) {
		loader = l;
	}

	public static String[] getObjectTypesArray() {
		String[] array = new String[objectTypes.size()];
		int count = 0;
		for (String s : objectTypes) {
			array[count] = s;
			count++;
		}
		return array;
	}

}
