package entities;

import java.util.List;

import models.TexturedModel;

public class World {
	
	private List<Entity> entities;
	private List<TexturedModel> objectTypes;
	
	public World(List<Entity> entities, List<TexturedModel> objectTypes) {
		this.entities = entities;
		this.objectTypes = objectTypes;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

	public List<TexturedModel> getObjectTypes() {
		return objectTypes;
	}

	public void setObjectTypes(List<TexturedModel> objectTypes) {
		this.objectTypes = objectTypes;
	}
	
}
