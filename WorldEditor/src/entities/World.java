package entities;

import java.util.ArrayList;
import java.util.List;

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
			BoundingBox b = e.getHitbox();
			info[i] = "e " + e.getName() + " " + e.position.x + " " + e.position.y + " " + e.position.z + " "
					+ e.getRotX() + " " + e.getRotY() + " " + e.getRotZ() + " " + e.getScale() + " " + b.pos.x + " "
					+ b.pos.y + " " + b.pos.z + " " + b.size.x + " " + b.size.y + " " + b.size.z + " "
					+ e.getIsStatic();
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
