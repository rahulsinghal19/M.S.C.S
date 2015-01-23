//This class is called by the process when it executes Critical section. We will append the Critical section code given by professor here in this class .
public class DummyCriticalSection extends CriticalSection {

	private int id;

	public DummyCriticalSection(int id) {
		super();
		this.id = id;
	}

	@Override
	public void execute() {
		System.out.println(String.format("%s[%d] - I'm in CS!%s",
				AnsiColorConstants.ANSI_GREEN, id,
				AnsiColorConstants.ANSI_RESET));
	}
}
