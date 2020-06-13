package notebook;

import java.util.Locale;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class ReplaceWinController {
	private MainWinController parentWin;

	private Stage stage;

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

	public void setParentWin(MainWinController parentWin) {
		this.parentWin = parentWin;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void initialize() {
		// ��������仯event
		searchText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue,
					final String newValue) {
				parentWin.SearchText = searchText.getText();
				SearchBtn.setDisable(parentWin.SearchText.length() == 0);
				ReplaceBtn.setDisable(parentWin.SearchText.length() == 0);
				ReplaceAllBtn.setDisable(parentWin.SearchText.length() == 0);
			}
		});

		replaceText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue,
					final String newValue) {
				parentWin.ReplaceText = replaceText.getText();
				SearchBtn.setDisable(parentWin.SearchText.length() == 0);
				ReplaceBtn.setDisable(parentWin.SearchText.length() == 0);
				ReplaceAllBtn.setDisable(parentWin.SearchText.length() == 0);
			}
		});

		// ����Сд�仯event
		isCaseSensitiveSet.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				parentWin.isCaseSensitive = isCaseSensitiveSet.isSelected();
			}
		});

		// ����ظ��仯event
		isRepeatSet.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				parentWin.isRepeat = isRepeatSet.isSelected();
			}
		});
	}

	public void Show() {
		searchText.setText(parentWin.SearchText);
		isCaseSensitiveSet.setSelected(parentWin.isCaseSensitive);
		isRepeatSet.setSelected(parentWin.isRepeat);
		SearchBtn.setDisable(parentWin.SearchText.length() == 0);
		ReplaceBtn.setDisable(parentWin.SearchText.length() == 0);
		ReplaceAllBtn.setDisable(parentWin.SearchText.length() == 0);

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
	public void SearchNext() {// ֻ���²���
		int SearchResult;
		String allText = parentWin.textArea.getText();
		String tarText = parentWin.SearchText;
		if (!parentWin.isCaseSensitive) {
			allText = allText.toLowerCase(Locale.ROOT);
			tarText = tarText.toLowerCase(Locale.ROOT);
		}
		// ���²���
		SearchResult = allText.indexOf(tarText, parentWin.textArea.getCaretPosition());
		if (SearchResult == -1 && parentWin.isRepeat) {// ������Ҳ��������ѿ���ѭ������
			SearchResult = allText.indexOf(tarText);
		}
		if (SearchResult == -1) {// ������Ҳ���
			setOnTop(false);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("���±�");
			alert.setHeaderText("�Ҳ��� \"" + parentWin.SearchText + "\"");
			alert.showAndWait();
			setOnTop(true);
		} else {// �ҵ��Ļ�����
			parentWin.textArea.selectRange(SearchResult, SearchResult + parentWin.SearchText.length());
		}
	}

	@FXML
	public void Replace() {
		if (parentWin.textArea.getSelectedText().equals(parentWin.SearchText)) {// ѡ�����������ҵ�������ͬ
			parentWin.textArea.deletePreviousChar();
			parentWin.textArea.insertText(parentWin.textArea.getAnchor(), parentWin.ReplaceText);
		}
		SearchNext();
	}

	@FXML
	public void ReplaceAll() {
		parentWin.textArea.setText(parentWin.textArea.getText().replace(parentWin.SearchText, parentWin.ReplaceText));
	}

	@FXML
	public void Cancle() {
		Hide();
	}
}
