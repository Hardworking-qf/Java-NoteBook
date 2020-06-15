package notebook;

import javafx.stage.Stage;

public abstract class SecondWinController {
	protected MainWinController parentWin;
	protected Stage stage;

	public void setParentWin(MainWinController parentWin) {
		this.parentWin = parentWin;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	public abstract void Show();
	public abstract void Hide();
	public void Minimize(boolean isMinimize) {
		stage.setIconified(isMinimize);
	}

	public void setOnTop(boolean isOnTop) {
		stage.setAlwaysOnTop(isOnTop);
	}
}
