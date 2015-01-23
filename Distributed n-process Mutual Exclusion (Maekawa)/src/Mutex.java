
//This interface is used by a process when it wants to enter a Critical Section or leave a Critical Section.
public interface Mutex {

	public void csEnter();

	public void csLeave();

}
