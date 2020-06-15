package notebook;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.filechooser.FileSystemView;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainWinController {
	// ȫ��
	private Stage stage;// ����stage

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private String path = null;// �ĵ���ַ

	public void setPath(String path) {
		this.path = path;
	}

	@FXML
	public TextArea textArea;// ��������

	@FXML
	private BorderPane pane;
	@FXML
	private MenuBar menuBar;
	@FXML
	private HBox stageBar;// ״̬��
	@FXML
	private Label RowColCountLabel;// ���м���
	@FXML
	private Label LetterCountLabel;// ����ͳ��
	@FXML
	private Label ZoomSizeShowLabel;// ���ű���

	// �Ӵ��ڹ������ҡ��滻����ֻ��������һ������תֻ�ܳ���һ����
	@FXML
	private SearchWinController searchWin;

	@FXML
	private ReplaceWinController replaceWin;

	@FXML
	private GotoWinController gotoWin;

	@FXML
	private FontSelectWinController fontSelectWin;

//	private boolean isCtrlHold = false;

	// ��ʼ��
	public void initialize() throws Exception {
		// ��������仯event
		textArea.textProperty().addListener((observable, oldValue, newValue) -> TextAreaUpdate());

		// �������TextArea��ʱ��F3��F5������Ӧ�� �������Ӵ˺���
		textArea.setOnKeyPressed(event -> {
//			System.out.println(event.getCode());
			if (event.getCode() == KeyCode.F3 && !SearchNextBtn.isDisable()) {
				SearchNext();
			} else if (event.getCode() == KeyCode.F5) {
				DateTime();
			} else if (event.isControlDown()) {
//				isCtrlHold = true;
			} else if (event.getCode() == KeyCode.ALT) {
				menuBar.requestFocus();
			}
		});

		// ������
		// ֻҪ�Ҳ��� ��û��BUG
//		textArea.setOnKeyReleased(event -> {
//			if (event.getCode() == KeyCode.CONTROL)
//				isCtrlHold = false;
//		});
//
//		
//		textArea.scrollTopProperty().addListener((observable, oldValue, newValue) -> {
//			if (isCtrlHold) {
//				// textArea.setScrollTop(oldValue.doubleValue());
//			}
//		});
//
//		pane.setOnScroll(event -> {
//			System.out.println(event.isControlDown());
//			if (event.isControlDown()) {
//				if (event.getDeltaY() > 0) {
//					ZoomIn();
//				} else {
//					ZoomOut();
//				}
//				event.consume();
//			}
//		});

		// anchorλ�ñ仯
		textArea.caretPositionProperty().addListener((observable, oldValue, newValue) -> {
			List<Integer> charEachRow = java.util.Arrays.asList(textArea.getText().split("\n"))//
					.stream()//
					.map(str -> str.length())//
					.collect(Collectors.toList());
			currRow = 0;
			currCol = (int) newValue;
			for (int charInRow : charEachRow) {
				if (currCol - charInRow > 0) {
					currCol -= charInRow + 1;// magic number
					++currRow;
				} else
					break;
			}
			++currRow;
			++currCol;
			RowColCountLabel.setText("�� " + currRow + " ��, �� " + currCol + " ��");
		});

		Platform.runLater(() -> {
			// ���ڹر�event
			stage.setOnCloseRequest(event -> {
				Exit();
				event.consume();
			});

			// ������С��event
			stage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
				searchWin.Minimize(newValue);
				replaceWin.Minimize(newValue);
				gotoWin.Minimize(newValue);
				fontSelectWin.Minimize(newValue);
			});
		});

		// ��ʼ����������
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("SearchWin.fxml"));
			fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
			Parent root = fxmlLoader.load();

			Stage searchStage = new Stage();
			Scene scene = new Scene(root);
			searchStage.setTitle("����");
			searchStage.setScene(scene);
			searchStage.setAlwaysOnTop(true);
			searchStage.setResizable(false);

			searchWin = fxmlLoader.getController();
			searchWin.setStage(searchStage);
			searchWin.setParentWin(this);

		} catch (IOException e) {
		}

		// ��ʼ���滻����
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("ReplaceWin.fxml"));
			fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
			Parent root = fxmlLoader.load();

			Stage replaceStage = new Stage();
			Scene scene = new Scene(root);
			replaceStage.setTitle("�滻");
			replaceStage.setScene(scene);
			replaceStage.setAlwaysOnTop(true);
			replaceStage.setResizable(false);

			replaceWin = fxmlLoader.getController();
			replaceWin.setStage(replaceStage);
			replaceWin.setParentWin(this);
		} catch (IOException e) {
		}

		// ��ʼ����ת����
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("GotoWin.fxml"));
			fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
			Parent root = fxmlLoader.load();

			Stage gotoStage = new Stage();
			Scene scene = new Scene(root);
			gotoStage.setTitle("ת��ָ����");
			gotoStage.setScene(scene);
			gotoStage.setAlwaysOnTop(true);
			gotoStage.setResizable(false);

			gotoWin = fxmlLoader.getController();
			gotoWin.setStage(gotoStage);
			gotoWin.setParentWin(this);
		} catch (IOException e) {
		}

		// ��ʼ������ѡ�񴰿�
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("fontSelectWin.fxml"));
			fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
			Parent root = fxmlLoader.load();

			Stage fontSelectStage = new Stage();
			Scene scene = new Scene(root);
			fontSelectStage.setTitle("����");
			fontSelectStage.setScene(scene);
			fontSelectStage.setAlwaysOnTop(true);
			fontSelectStage.setResizable(false);

			fontSelectWin = fxmlLoader.getController();
			fontSelectWin.setStage(fontSelectStage);
			fontSelectWin.setParentWin(this);
		} catch (IOException e) {
		}

		// ��ʼ��״̬��
		RowColCountLabel.setText("�� 1 ��, �� 1 ��");
		ZoomSizeShowLabel.setText("100%");

		// ��ʼ������
		Platform.runLater(() -> {
			font = textArea.getFont();
		});
	}

	public void load() {
		// ��ͼ���ļ�
		if (path != null) {// ·����Ϊ�� ���ļ�
			if (!new File(path).exists()) {// ·����Ϊ��ȴ��Ч ѡ���Ƿ񴴽��ļ�
				if (!path.contains(".")) {
					path += ".txt";
					if (!new File(path).exists()) {// �ļ����Ǵ��� ���ļ�
						DirectOpenFile();
						return;
					}
					// �ļ������� ��������
				}
				// ��������
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("���±�");
				alert.setHeaderText("�Ҳ����ļ�: " + path + "��\n\nҪ�������ļ���");
				alert.getButtonTypes().setAll(//
						new ButtonType("��(_Y)", ButtonData.YES), //
						new ButtonType("��(_N)", ButtonData.NO), //
						new ButtonType("ȡ��", ButtonData.CANCEL_CLOSE));
				// ��ȡѡ��
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get().getButtonData() == ButtonData.YES) {// �������ļ�
					DirectCreateFile();
					return;
				} else if (result.get().getButtonData() == ButtonData.NO) {// ���������ļ����򿪿հ��ļ�
					DirectBlankFile();
					return;
				} else {// ֱ�ӹرմ���
					System.exit(0);
				}
			} else {// ���ļ�
				DirectOpenFile();
				return;
			}
		} else {// ·��Ϊ�� �½��հ�ҳ
			DirectBlankFile();
			return;
		}
	}

	private void DirectOpenFile() {
		File file = new File(path);
		byte[] filecontent = new byte[(int) file.length()];// �ڴ򿪷ǳ�����ļ�ʱ���ܻ����
		try (FileInputStream fis = new FileInputStream(file)) {
			fis.read(filecontent);
		} catch (Exception e) {// ��Ҳ��֪���������ܳ�ʲôexception ֱ��������
		}
		textArea.setText(new String(filecontent));
		RefreshTitle();
		textArea.requestFocus();

		hasSave = true;
	}

	private void DirectCreateFile() {
		try {
			new File(path).createNewFile();
			RefreshTitle();
			textArea.requestFocus();
		} catch (IOException e) {
		}

		hasSave = true;
	}

	private void DirectBlankFile() {
		path = null;
		RefreshTitle();
		textArea.setText("");
		textArea.requestFocus();
		hasSave = true;
	}

	private void RefreshTitle() {
		Platform.runLater(() -> {
			stage.setTitle("���±� - " + (path == null ? "�ޱ���" : new File(path).getName()));
		});
	}

	private ButtonData isSaveAlert() {
		Alert alert = new Alert(Alert.AlertType.NONE);
		alert.setTitle("���±�");
		alert.setHeaderText("�Ƿ���ı��浽 " + (path == null ? "�ޱ���" : new File(path).getName()) + "��");
		alert.getButtonTypes().setAll(//
				new ButtonType("����(_S)", ButtonData.YES), //
				new ButtonType("������(_N)", ButtonData.NO), //
				new ButtonType("ȡ��", ButtonData.CANCEL_CLOSE));
		return alert.showAndWait().get().getButtonData();
	}

	// �˵�����
	// �ļ�(F)
	private boolean hasSave;// �Ƿ��ѱ���

	@FXML
	private void NewFile() {// �½�
		if (!hasSave) {
			// ��������
			ButtonData result = isSaveAlert();

			// ��ȡѡ��
			if (result == ButtonData.YES) {// ����
				if (path == null) {// SaveAs
					SaveAs();
					return;
				} else {// Save
					Save();
				}
			} else if (result == ButtonData.NO) {// ������
				// ʲôҲ����
			} else {// ȡ��
				return;
			}
		}
		DirectBlankFile();
		RefreshTitle();
	}

	@FXML
	private void OpenFile() {// ��
		if (!hasSave) {
			// ��������
			ButtonData result = isSaveAlert();

			// ��ȡѡ��
			if (result == ButtonData.YES) {// ����
				if (path == null) {// SaveAs
					SaveAs();
					return;
				} else {// Save
					Save();
				}
			} else if (result == ButtonData.NO) {// ������
				// ʲôҲ����
			} else {// ȡ��
				return;
			}
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("��");
		fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
		fileChooser.getExtensionFilters().addAll(//
				new FileChooser.ExtensionFilter("�ı��ĵ�", "*.txt"), //
				new FileChooser.ExtensionFilter("�����ļ�", "*.*"));
		File file = fileChooser.showOpenDialog(stage);
		if (file == null) {
			return;
		} else {
			path = file.getAbsolutePath();
		}
		DirectOpenFile();
		RefreshTitle();
	}

	@FXML
	private void Save() {// ����
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path))))) {
			bw.write(textArea.getText());
			hasSave = true;
		} catch (Exception e) {
		}
	}

	@FXML
	private void SaveAs() {// ���Ϊ
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("���Ϊ");
		fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
		fileChooser.setInitialFileName(new File(path == null ? "�½��ı��ĵ�.txt" : path).getName());
		fileChooser.getExtensionFilters().addAll(//
				new FileChooser.ExtensionFilter("�ı��ĵ�", "*.txt"), //
				new FileChooser.ExtensionFilter("�����ļ�", "*.*"));
		File file = fileChooser.showSaveDialog(stage);
		if (file == null) {
			return;
		} else {
			path = file.getAbsolutePath();
			Save();
			RefreshTitle();
		}
	}

	@FXML
	private void Exit() {// �˳�
		if (hasSave) {// �ѱ���ֱ���˳�
			stage.hide();// �������д��ڶ�hide��ʱ����̻��Զ��ر�
			// �ʲ��õ������д��ڹرպ���ռ����Դ
		} else {
			// ��������
			ButtonData result = isSaveAlert();

			// ��ȡѡ��
			if (result == ButtonData.YES) {// ����
				if (path == null) {// SaveAs
					SaveAs();
				} else {// Save
					Save();
				}
			} else if (result == ButtonData.NO) {// ������
				// ʲôҲ����
			} else {// ȡ��
				return;
			}
			stage.hide();
		}
	}

	// �༭(E)
	@FXML
	private MenuItem UndoBtn;

	@FXML
	private MenuItem RedoBtn;

	@FXML
	private MenuItem CutBtn;

	@FXML
	private MenuItem CopyBtn;

	@FXML
	private MenuItem PasteBtn;

	@FXML
	private MenuItem DeleteBtn;

	@FXML
	private MenuItem SearchBtn;

	@FXML
	private MenuItem SearchNextBtn;

	@FXML
	private void EditMenuOnShowing() {
		UndoBtn.setDisable(!textArea.isUndoable());
		RedoBtn.setDisable(!textArea.isRedoable());
		CutBtn.setDisable(textArea.getSelectedText().length() == 0);
		CopyBtn.setDisable(textArea.getSelectedText().length() == 0);
		PasteBtn.setDisable(!Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null)
				.isDataFlavorSupported(DataFlavor.stringFlavor));
		DeleteBtn.setDisable(textArea.getSelectedText().length() == 0);
		SearchBtn.setDisable(textArea.getText().length() == 0);
		SearchNextBtn.setDisable(textArea.getText().length() == 0);
	}

	@FXML
	private void Undo() {// ����
		textArea.undo();
	}

	@FXML
	private void Redo() {// ����
		textArea.redo();
	}

	@FXML
	private void Cut() {// ����
		textArea.cut();
	}

	@FXML
	private void Copy() {// ����
		textArea.copy();
	}

	@FXML
	private void Paste() {// ճ��
		textArea.paste();
	}

	@FXML
	private void Delete() {// ɾ��
		textArea.deletePreviousChar();
	}

	@FXML
	private void Baidu() {// ʹ�ðٶ�����
		try {
			Runtime.getRuntime().exec("cmd /c start https://www.baidu.com/s?wd="//
					+ textArea.getSelectedText().replaceAll(" ", "%20"));
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("���±�");
			alert.setHeaderText("�����������");
			alert.showAndWait();
		}
	}

	// ��public����Ϊ�Ͳ��ҡ��滻���ڽ�����
	public String SearchText = "";
	public String ReplaceText = "";
	public boolean isCaseSensitive = false;// �Ƿ��Сд����
	public boolean isRepeat = false;// �Ƿ�ѭ��
	public boolean isUpward = false;// �Ƿ�����

	@FXML
	private void Search() {// ����
		replaceWin.Hide();
		searchWin.Show();
	}

	@FXML
	public void SearchNext() {// ������һ��
		if (SearchText == "") {// �����������Ϊ������������ڽ�������
			Search();
		} else {// ���в���
			int SearchResult;
			String allText = textArea.getText();
			String tarText = SearchText;
			if (!isCaseSensitive) {
				allText = allText.toLowerCase(Locale.ROOT);
				tarText = tarText.toLowerCase(Locale.ROOT);
			}
			if (isUpward) {// ���ϲ���
				SearchResult = allText.lastIndexOf(tarText, textArea.getCaretPosition() - 2);// magic number
			} else {// ���²���
				SearchResult = allText.indexOf(tarText, textArea.getCaretPosition());
			}
			if (SearchResult == -1 && isRepeat) {// ������Ҳ��������ѿ���ѭ������
				if (isUpward) {
					SearchResult = allText.lastIndexOf(tarText);
				} else {
					SearchResult = allText.indexOf(tarText);
				}
			}
			if (SearchResult == -1) {// ������Ҳ���
				searchWin.setOnTop(false);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("���±�");
				alert.setHeaderText("�Ҳ��� \"" + SearchText + "\"");
				alert.showAndWait();
				searchWin.setOnTop(true);
			} else {// �ҵ��Ļ�����
				textArea.selectRange(SearchResult, SearchResult + SearchText.length());
			}
		}
	}

	@FXML
	private void Replace() {// �滻
		searchWin.Hide();
		replaceWin.Show();
	}

	@FXML
	private void Goto() {// ת��
		gotoWin.Show();
	}

	@FXML
	private void SelectAll() {// ȫѡ
		textArea.selectAll();
	}

	@FXML
	private void DateTime() {// ʱ��/����
		textArea.deletePreviousChar();
		textArea.insertText(textArea.getAnchor(), new SimpleDateFormat("HH:mm yyyy/MM/dd").format(new Date()));
	}

	// ��ʽ(O)
	@FXML
	private CheckMenuItem AutoLineFeedBtn;

	@FXML
	private CheckMenuItem StageBarBtn;

	@FXML
	private void AutoLineFeed() {// �Զ�����
		textArea.setWrapText(AutoLineFeedBtn.isSelected());
	}

	public Font font;

	int ZoomSize = 100;

	@FXML
	private void SelectFont() {// ����
		fontSelectWin.Show();
	}

	public void RefreshFont() {
		textArea.setFont(Font.font(font.getName(), font.getSize() * (ZoomSize / 100.0)));
//		System.out.println(textArea.getFont());
		ZoomSizeShowLabel.setText("" + ZoomSize + "%");
	}

	// �鿴(V)
	@FXML
	private void ZoomIn() {// �Ŵ�
		if (ZoomSize < 500)
			ZoomSize += 10;
		RefreshFont();
	}

	@FXML
	private void ZoomOut() {// ��С
		if (ZoomSize > 10)
			ZoomSize -= 10;
		RefreshFont();
	}

	@FXML
	private void DefaultZoom() {// �ָ�Ĭ������
		ZoomSize = 100;
		RefreshFont();
	}

	@FXML
	private void StageBar() {// ״̬��
		pane.setBottom(StageBarBtn.isSelected() ? stageBar : null);
	}

	// ����(H)
	@FXML
	private void Help() {// �鿴����
		try {
			Runtime.getRuntime().exec("cmd /c start https://www.baidu.com/s?wd=���ʹ�ü��±�");
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("���±�");
			alert.setHeaderText("�����������");
			alert.showAndWait();
		}
	}

	@FXML
	private void About() {// ���ڼ��±�
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("����");
		alert.setHeaderText("Java����ҵ By Hardworking_qf");
		alert.setContentText("JavaFX������");
		alert.showAndWait();
	}

	// ��������
	private int tolCount = 0;// ����ͳ��
	public int currRow = 1;// ��λ��
	private int currCol = 1;// ��λ��

	@FXML
	private void TextAreaUpdate() {
		tolCount = textArea.getText().length();
		hasSave = false;
		LetterCountLabel.setText(tolCount + " ��");
	}
}
