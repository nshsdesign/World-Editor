package picking;

import org.lwjgl.input.Mouse;

import entities.Entity;
import main.OpenGLView;

public class EntitySelectionManager {

//	private static final Color MO_COLOUR = new Color(0.8f, 0.8f, 0.8f);
//	private static final Color SELECTED_COLOUR = new Color(1, 1, 0);
//	private static final Color GRABBED_COLOUR = new Color(0, 1, 0);
//	private static final Color NO_PLACE_COLOUR = new Color(1, 0, 0);

	private Entity currentlyMousedOver;
	private Entity currentlySelected;
	private Entity currentlyGrabbed;

//	private Highlight primaryHighlight;
//	private Highlight secondaryHighlight;
	private Picker3D picker;
//	private EntityInfoGui openInfoGui;
	private boolean enabled = true;

	public EntitySelectionManager() {
		picker = new Picker3D(OpenGLView.camera);
//		this.primaryHighlight = WorldHighlights.getHighlights().getHighlight1();
//		this.secondaryHighlight = WorldHighlights.getHighlights().getHighlight2();
	}

	public int getPickerTexture() {
		return picker.getFboTexture();
	}

	public void update() {
		update3dPicker();
		if (!enabled) {
			returnToPickUp();
		} else if (currentlyGrabbed == null) {
			dealWithNotHeld();
		} else {
			//checkPlacement();
		}
	}

	public void enable(boolean enable) {
		this.enabled = enable;
	}

	public void cleanUp() {
		picker.cleanUp();
	}

	public void clear() {
		deselect();
		mouseOff();
		returnToPickUp();
	}
	
	public void reset(){
		picker.reset();
	}

	private Entity getPickedEntity() {
		return picker.getPickedEntity();
	}

	private void update3dPicker() {
		picker.update(OpenGLView.entities);
	}

	private void dealWithNotHeld() {
		if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
			deselect();
		}
		checkForNewPickedEntity();
	}

	private void checkForNewPickedEntity() {
		Entity pickedEntity = getPickedEntity();
		if (pickedEntity == null || pickedEntity == currentlySelected) {
			mouseOff();
		} else {
			checkForUserAction(pickedEntity);
		}
	}

//	private void checkPlacement() {
//		if (canBeDropped()) {
//			if (mouse.isRightClick()) {
//				place();
//			} else if (mouse.isLeftClick()) {
//				Entity placed = this.currentlyGrabbed;
//				place();
//				select(placed);
//			}
//		}
//	}

	private void checkForUserAction(Entity entity) {
		if (Mouse.isButtonDown(1)) {
			//grab(entity);
		} else if (Mouse.isButtonDown(0)) {
			select(entity);
		} else {
			mouseover(entity);
		}
	}

	private void select(Entity entity) {
		if (entity != currentlySelected) {
			deselect();
			this.currentlySelected = entity;
			//secondaryHighlight.hide();
			//primaryHighlight.followEntity(entity, SELECTED_COLOUR);
			//openInfoGui = new EntityInfoGui(entity);
		}
	}

	private void deselect() {
		if (currentlySelected != null) {
			this.currentlySelected = null;
			//primaryHighlight.hide();
			//closeInfoGui();
		}
	}

	private void mouseover(Entity entity) {
		if (entity != currentlyMousedOver && entity != currentlySelected) {
			mouseOff();
			this.currentlyMousedOver = entity;
			//secondaryHighlight.followEntity(entity, MO_COLOUR);
		}
	}

	private void mouseOff() {
		if (currentlyMousedOver != null) {
			//secondaryHighlight.hide();
			currentlyMousedOver = null;
		}
	}

//	private void place() {
//		if (currentlyGrabbed != null) {
//			//currentlyGrabbed.place();
//			this.currentlyGrabbed = null;
//			//primaryHighlight.hide();
//		}
//	}

	private void returnToPickUp() {
		if (currentlyGrabbed != null) {
			//currentlyGrabbed.returnToPickUpPosition();
			this.currentlyGrabbed = null;
			//primaryHighlight.hide();
		}
	}

//	private void grab(Entity entity) {
//		deselect();
//		currentlyMousedOver = null;
//		this.currentlyGrabbed = entity;
//		//currentlyGrabbed.grab();
//		//secondaryHighlight.hide();
//		//primaryHighlight.followEntity(entity, GRABBED_COLOUR);
//	}

//	private void closeInfoGui() {
//		if (openInfoGui != null) {
//			openInfoGui.remove();
//		}
//	}

}
