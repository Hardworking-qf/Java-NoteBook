package notebook;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class SearchWinController {
	private MainWinController parentWin;

	private Stage stage;

	@FXML
	private TextField searchText;

	@FXML
	private CheckBox isCaseSensitiveSet;

	@FXML
	private CheckBox isRepeatSet;

	@FXML
	private ToggleGroup Direction;

	@FXML
	private RadioButton UpwardSet;

	@FXML
	private RadioButton DownwardSet;

	@FXML
	private Button SearchBtn;

	public void setParentWin(MainWinController parentWin) {
		this.parentWin = parentWin;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void initialize() {
		// 检测输入框变化event
		searchText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue,
					final String newValue) {
				parentWin.SearchText = searchText.getText();
				SearchBtn.setDisable(parentWin.SearchText.length() == 0);
			}
		});

		// 检测大小写变化event
		isCaseSensitiveSet.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				parentWin.isCaseSensitive = isCaseSensitiveSet.isSelected();
			}
		});

		// 检测重复变化event
		isRepeatSet.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				parentWin.isRepeat = isRepeatSet.isSelected();
			}
		});

		// 检测查找方向变化event
		Direction.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				parentWin.isUpward = UpwardSet.isSelected();
			}
		});
	}

	public void Show() {
		searchText.setText(parentWin.SearchText);
		isCaseSensitiveSet.setSelected(parentWin.isCaseSensitive);
		isRepeatSet.setSelected(parentWin.isRepeat);
		UpwardSet.setSelected(parentWin.isUpward);
		DownwardSet.setSelected(!parentWin.isUpward);
		SearchBtn.setDisable(parentWin.SearchText.length() == 0);

		stage.show();
	}

	public void Hide() {
		stage.hide();
	}

	public void Minimize(boolean isMinimize) {
		stage.setIconified(isMinimize);
	}
	
	public void setOnTop(boolean isOnTop) {
		stage.setAlwaysOnTop(isOnTop);
	}

	@FXML
	public void SearchNext() {
		parentWin.SearchNext();
	}

	@FXML
	public void Cancle() {
		Hide();
	}
}
