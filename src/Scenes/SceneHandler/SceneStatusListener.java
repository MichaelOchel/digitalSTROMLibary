package Scenes.SceneHandler;

import Scenes.Scene.InternalScene;

/**
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
    
    public String getID();

    
}
