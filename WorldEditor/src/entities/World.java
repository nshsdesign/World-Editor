package entities;

import java.util.ArrayList;
import java.util.List;

import picking.BoundingBox;

public class World {

	private List<Entity> entities;
	private List<String> objectTypes;
	private String currentObjectType;

	public World(List<Entity> entities, List<String> objectTypes) {
		this.entities = entities;
		this.objectTypes = objectTypes;
	}

	public World() {
		this.entities = new ArrayList<Entity>();
		this.objectTypes = new ArrayList<String>();
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

	public List<String> getObjectTypes() {
		return objectTypes;
	}

	public void setObjectTypes(List<String> objectTypes) {
		this.objectTypes = objectTypes;
	}

	public String[] getEntityInfo() {
		String[] info = new String[entities.size()];

		for (int i = 0; i < info.length; i++) {
			Entity e = entities.get(i);
			BoundingBox b = e.getBoundingBox();
			info[i] = "e " + e.getName() + " " + e.position.x + " " + e.position.y + " " + e.position.z + " "
					+ e.getRotX() + " " + e.getRotY() + " " + e.getRotZ() + " " + e.getScale() + " " + e.getIsStatic();
		}

		return info;
	}

	public String getCurrentObjectType() {
		return currentObjectType;
	}

	public void setCurrentObjectType(String currentObjectType) {
		this.currentObjectType = currentObjectType;
	}

}
