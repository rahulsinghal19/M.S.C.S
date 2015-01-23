
public class RangePolicy {
	int maxRange;
	int currentRange = 1;
	
	public RangePolicy(int maxRange) {
		this.maxRange = maxRange;
	}
	
	public void recordEliminationSuccess() {
		if(currentRange < maxRange) 
			currentRange++;
	}
	
	public void recordEliminationTimeout() {
		if(currentRange > 1) {
			currentRange--;
		}
	}
	
	public int getRange() {
		return currentRange;
	}
}
