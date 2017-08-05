import javax.swing.SwingUtilities;


public class swing_safe {
	public swing_safe(Runnable r) {
		if (SwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			SwingUtilities.invokeLater(r);
		}
	}
}
