package notebook;

import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class GotoWinController extends SecondWinController {
	@FXML
	private TextField rowIndexInput;

	public void initialize() {
		// 检查输入是否符合要求（纯数字）
		rowIndexInput.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("^[0-9]*$"))
				rowIndexInput.setText(oldValue);
		});
	}

	@Override
	public void Show() {
		rowIndexInput.setText("" + parentWin.currRow);
		stage.show();
	}

	@Override
	public void Hide() {
		stage.hide();
	}

	@FXML
	private void Goto() {
		try {
			int lineIndex = Integer.parseInt(rowIndexInput.getText());
			if (lineIndex > parentWin.textArea.getText().split("\n").length) {
				throw new Exception();
			}
			List<Integer> charEachRow = java.util.Arrays.asList(parentWin.textArea.getText().split("\n"))//
					.stream()//
					.map(str -> str.length())//
					.collect(Collectors.toList());
			int Anchor = 0;
			for (; lineIndex > 1; --lineIndex)
				Anchor += charEachRow.get(lineIndex - 2) + 1;// magic number, it works
			parentWin.textArea.selectRange(Anchor, Anchor);
			Hide();
		} catch (Exception e) {
			setOnTop(false);
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("记事本 - 跳行");
			alert.setHeaderText("行数超过了总行数");
			alert.showAndWait();
			setOnTop(true);
		}
	}

	@FXML
	private void Cancel() {
		Hide();
	}
}
