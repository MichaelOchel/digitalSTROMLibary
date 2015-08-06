package digitalSTROMListener;

import digitalSTROMStructure.digitalSTROMScene.InternalScene;

/**
 * The {@link SceneStatusListener} is notified when a {@link InternalScene} status has changed or a {@link InternalScene} has been removed or added.
 * 
 * @author Michael Ochel - Initial contribution
 * @author Mathias Siegele - Initial contribution
 *
 */
public interface SceneStatusListener {

	public final static String SCENE_DESCOVERY = "SceneDiscovey";
	
	/**
     * This method is called whenever the state of the given scene has changed.
     * 
     * @param device
     * 
     */
    public void onSceneStateChanged(boolean flag);
    
    /**
     * This method is called whenever a scene is removed.
     * 
     * @param device
     * 
     */
    public void onSceneRemoved(InternalScene scene);

    /**
     * This method is called whenever a scene is added.
     * 
     * @param device
     * 
     */
    public void onSceneAdded(InternalScene scene);
    
    /**
     * Return the id of this {@link SceneStatusListener}.
     * @return listener id
     */
    public String getID();

    
}
