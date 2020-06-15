package notebook;

import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class ReplaceWinController extends SecondWinController {
	@FXML
	private TextField searchText;

	@FXML
	private TextField replaceText;

	@FXML
	private CheckBox isCaseSensitiveSet;

	@FXML
	private CheckBox isRepeatSet;

	@FXML
	private Button SearchBtn;

	@FXML
	private Button ReplaceBtn;

	@FXML
	private Button ReplaceAllBtn;

	public void initialize() {
		// 检测输入框变化event
		searchText.textProperty().addListener((observable, oldValue, newValue) -> {
			parentWin.SearchText = searchText.getText();
			SearchBtn.setDisable(parentWin.SearchText.length() == 0);
			ReplaceBtn.setDisable(parentWin.SearchText.length() == 0);
			ReplaceAllBtn.setDisable(parentWin.SearchText.length() == 0);

		});

		replaceText.textProperty().addListener((observable, oldValue, newValue) -> {
			parentWin.ReplaceText = replaceText.getText();
			SearchBtn.setDisable(parentWin.SearchText.length() == 0);
			ReplaceBtn.setDisable(parentWin.SearchText.length() == 0);
			ReplaceAllBtn.setDisable(parentWin.SearchText.length() == 0);
		});

		// 检测大小写变化event
		isCaseSensitiveSet.selectedProperty().addListener((observable, oldValue, newValue) -> {
			parentWin.isCaseSensitive = isCaseSensitiveSet.isSelected();
		});

		// 检测重复变化event
		isRepeatSet.selectedProperty().addListener((observable, oldValue, newValue) -> {
			parentWin.isRepeat = isRepeatSet.isSelected();
		});
	}

	@Override
	public void Show() {
		searchText.setText(parentWin.SearchText);
		isCaseSensitiveSet.setSelected(parentWin.isCaseSensitive);
		isRepeatSet.setSelected(parentWin.isRepeat);
		SearchBtn.setDisable(parentWin.SearchText.length() == 0);
		ReplaceBtn.setDisable(parentWin.SearchText.length() == 0);
		ReplaceAllBtn.setDisable(parentWin.SearchText.length() == 0);

		stage.show();
	}

	@Override
	public void Hide() {
		stage.hide();
	}

	@FXML
	private void SearchNext() {// 只向下查找
		int SearchResult;
		String allText = parentWin.textArea.getText();
		String tarText = parentWin.SearchText;
		if (!parentWin.isCaseSensitive) {
			allText = allText.toLowerCase(Locale.ROOT);
			tarText = tarText.toLowerCase(Locale.ROOT);
		}
		// 向下查找
		SearchResult = allText.indexOf(tarText, parentWin.textArea.getCaretPosition());
		if (SearchResult == -1 && parentWin.isRepeat) {// 如果查找不到但是已开启循环查找
			SearchResult = allText.indexOf(tarText);
		}
		if (SearchResult == -1) {// 最后还是找不到
			setOnTop(false);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("记事本");
			alert.setHeaderText("找不到 \"" + parentWin.SearchText + "\"");
			alert.showAndWait();
			setOnTop(true);
		} else {// 找到的话高亮
			parentWin.textArea.selectRange(SearchResult, SearchResult + parentWin.SearchText.length());
		}
	}

	@FXML
	private void Replace() {
		if (parentWin.textArea.getSelectedText().equals(parentWin.SearchText)) {// 选择的文字与查找的文字相同
			parentWin.textArea.deletePreviousChar();
			parentWin.textArea.insertText(parentWin.textArea.getAnchor(), parentWin.ReplaceText);
		}
		SearchNext();
	}

	@FXML
	private void ReplaceAll() {
		parentWin.textArea.setText(parentWin.textArea.getText().replace(parentWin.SearchText, parentWin.ReplaceText));
	}

	@FXML
	private void Cancel() {
		Hide();
	}
}
