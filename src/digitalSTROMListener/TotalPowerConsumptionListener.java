package digitalSTROMListener;

/**
 * 

 * 
 * @author Michael Ochel - Initial contribution
 * @author Mathias Siegele - Initial contribution
 *
 */
public interface TotalPowerConsumptionListener {

	/**
	 * This method is called when ever the total power consumption of the digitalSTROM-System has changed.
	 *  
	 * @param newPowerConsumption
	 */
	public void onTotalPowerConsumptionChanged(int newPowerConsumption);
	
}
