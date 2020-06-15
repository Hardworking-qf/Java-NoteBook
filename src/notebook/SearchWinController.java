package notebook;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class SearchWinController extends SecondWinController{
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

	public void initialize() {
		// ��������仯event
		searchText.textProperty().addListener((observable, oldValue, newValue) -> {
				parentWin.SearchText = searchText.getText();
				SearchBtn.setDisable(parentWin.SearchText.length() == 0);
		});

		// ����Сд�仯event
		isCaseSensitiveSet.selectedProperty().addListener((observable, oldValue, newValue) -> {
				parentWin.isCaseSensitive = isCaseSensitiveSet.isSelected();
		});

		// ����ظ��仯event
		isRepeatSet.selectedProperty().addListener((observable, oldValue, newValue) -> {
				parentWin.isRepeat = isRepeatSet.isSelected();
		});

		// �����ҷ���仯event
		Direction.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
				parentWin.isUpward = UpwardSet.isSelected();
		});
	}

	@Override
	public void Show() {
		searchText.setText(parentWin.SearchText);
		isCaseSensitiveSet.setSelected(parentWin.isCaseSensitive);
		isRepeatSet.setSelected(parentWin.isRepeat);
		UpwardSet.setSelected(parentWin.isUpward);
		DownwardSet.setSelected(!parentWin.isUpward);
		SearchBtn.setDisable(parentWin.SearchText.length() == 0);

		stage.show();
	}
	
	@Override
	public void Hide() {
		stage.hide();
	}

	@FXML
	private void SearchNext() {
		parentWin.SearchNext();
	}

	@FXML
	private void Cancel() {
		Hide();
	}
}
