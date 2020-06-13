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
import java.util.Locale;
import java.util.Optional;

import javax.swing.filechooser.FileSystemView;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainWinController {
	// 全局
	private Stage stage;// 自身stage

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private String path = null;// 文档地址
	@FXML
	public TextArea textArea;// 文字区域
	@FXML
	private Label RowColCountLabel;// 行列计数
	@FXML
	private Label LetterCountLabel;// 字数统计
	@FXML
	private Label ZoomSizeShowLabel;// 缩放倍数

	// 子窗口管理（查找、替换两者只能最多出现一个，跳转只能出现一个）
	@FXML
	private SearchWinController searchWin;

	@FXML
	private ReplaceWinController replaceWin;

	// 初始化
	public void initialize() {
		// 检测输入框变化event
		textArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue,
					final String newValue) {
				TextAreaUpdate();
			}
		});

		// 焦点放在TextArea上时按F3和F5是无响应的 所以增加此函数
		textArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.F3 && !SearchNextBtn.isDisable()) {
					SearchNext();
				} else if (event.getCode() == KeyCode.F5) {
					DateTime();
				}
			}
		});

		Platform.runLater(() -> {
			// 窗口关闭event
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					Exit();
					event.consume();
				}
			});
			
			// 窗口最小化event
			stage.iconifiedProperty().addListener(new ChangeListener<Boolean>() {
			    @Override
			    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			       searchWin.Minimize(newValue);
			       replaceWin.Minimize(newValue);
			    }
			});
		});

		// 初始化搜索窗口
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("SearchWin.fxml"));
			fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
			Parent root = fxmlLoader.load();

			Stage searchStage = new Stage();
			Scene scene = new Scene(root);
			searchStage.setTitle("查找");
			searchStage.setScene(scene);
			searchStage.setAlwaysOnTop(true);

			searchStage.setResizable(false);
			searchWin = fxmlLoader.getController();
			searchWin.setStage(searchStage);
			searchWin.setParentWin(this);
		} catch (IOException e) {
		}

		// 初始化替换窗口
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("ReplaceWin.fxml"));
			fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
			Parent root = fxmlLoader.load();

			Stage replaceStage = new Stage();
			Scene scene = new Scene(root);
			replaceStage.setTitle("替换");
			replaceStage.setScene(scene);
			replaceStage.setAlwaysOnTop(true);

			replaceStage.setResizable(false);
			replaceWin = fxmlLoader.getController();
			replaceWin.setStage(replaceStage);
			replaceWin.setParentWin(this);
		} catch (IOException e) {

		}
		// 试图打开文件
		path = NoteBook.args;
		if (path != null) {// 路径不为空 打开文件
			if (!new File(path).exists()) {// 路径不为空却无效 选择是否创建文件
				if (!path.contains(".")) {
					path += ".txt";
					if (!new File(path).exists()) {// 文件还是存在 打开文件
						DirectOpenFile();
						return;
					}
					// 文件不存在 弹出窗口
				}
				// 弹出窗口
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("记事本");
				alert.setHeaderText("找不到文件: " + path + "。\n\n要创建新文件吗？");
				alert.getButtonTypes().setAll(//
						new ButtonType("是(_Y)", ButtonData.YES), //
						new ButtonType("否(_N)", ButtonData.NO), //
						new ButtonType("取消", ButtonData.CANCEL_CLOSE));
				// 获取选择
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get().getButtonData() == ButtonData.YES) {// 创建新文件
					DirectCreateFile();
					return;
				} else if (result.get().getButtonData() == ButtonData.NO) {// 不创建新文件，打开空白文件
					DirectBlankFile();
					return;
				} else {// 直接关闭窗口
					System.exit(0);
				}
			} else {// 打开文件
				DirectOpenFile();
				return;
			}
		} else {// 路径为空 新建空白页
			DirectBlankFile();
			return;
		}
	}

	private void DirectOpenFile() {
		File file = new File(path);
		byte[] filecontent = new byte[(int) file.length()];// 在打开非常大的文件时可能会出错
		try (FileInputStream fis = new FileInputStream(file)) {
			fis.read(filecontent);
		} catch (Exception e) {// 我也不知道到这了能出什么exception 直接跳过了
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
		textArea.setText(null);
		textArea.requestFocus();
		hasSave = true;
	}

	private void RefreshTitle() {
		Platform.runLater(() -> {
			stage.setTitle("记事本 - " + (path == null ? "无标题" : new File(path).getName()));
		});
	}

	private ButtonData isSaveAlert() {
		Alert alert = new Alert(Alert.AlertType.NONE);
		alert.setTitle("记事本");
		alert.setHeaderText("是否更改保存到 " + (path == null ? "无标题" : new File(path).getName()) + "？");
		alert.getButtonTypes().setAll(//
				new ButtonType("保存(_S)", ButtonData.YES), //
				new ButtonType("不保存(_N)", ButtonData.NO), //
				new ButtonType("取消", ButtonData.CANCEL_CLOSE));
		return alert.showAndWait().get().getButtonData();
	}

	// 菜单功能
	// 文件(F)
	private boolean hasSave;// 是否已保存

	@FXML
	private void NewFile() {// 新建
		if (!hasSave) {
			// 弹出窗口
			ButtonData result = isSaveAlert();

			// 获取选择
			if (result == ButtonData.YES) {// 保存
				if (path == null) {// SaveAs
					SaveAs();
					return;
				} else {// Save
					Save();
				}
			} else if (result == ButtonData.NO) {// 不保存
				// 什么也不做
			} else {// 取消
				return;
			}
		}
		DirectBlankFile();
		RefreshTitle();
	}

	@FXML
	private void OpenFile() {// 打开
		if (!hasSave) {
			// 弹出窗口
			ButtonData result = isSaveAlert();

			// 获取选择
			if (result == ButtonData.YES) {// 保存
				if (path == null) {// SaveAs
					SaveAs();
					return;
				} else {// Save
					Save();
				}
			} else if (result == ButtonData.NO) {// 不保存
				// 什么也不做
			} else {// 取消
				return;
			}
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("打开");
		fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
		fileChooser.getExtensionFilters().addAll(//
				new FileChooser.ExtensionFilter("文本文档", "*.txt"), //
				new FileChooser.ExtensionFilter("所有文件", "*.*"));
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
	private void Save() {// 保存
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path))))) {
			bw.write(textArea.getText());
			hasSave = true;
		} catch (Exception e) {
		}
	}

	@FXML
	private void SaveAs() {// 另存为
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("另存为");
		fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
		fileChooser.setInitialFileName(new File(path == null ? "新建文本文档.txt" : path).getName());
		fileChooser.getExtensionFilters().addAll(//
				new FileChooser.ExtensionFilter("文本文档", "*.txt"), //
				new FileChooser.ExtensionFilter("所有文件", "*.*"));
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
	private void Exit() {// 退出
		if (hasSave) {// 已保存直接退出
			System.exit(0);
		} else {
			// 弹出窗口
			ButtonData result = isSaveAlert();

			// 获取选择
			if (result == ButtonData.YES) {// 保存
				if (path == null) {// SaveAs
					SaveAs();
				} else {// Save
					Save();
				}
			} else if (result == ButtonData.NO) {// 不保存
				// 什么也不做
			} else {// 取消
				return;
			}
			System.exit(0);
		}
	}

	// 编辑(E)
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
	private void Undo() {// 撤销
		textArea.undo();
	}

	@FXML
	private void Redo() {// 重做
		textArea.redo();
	}

	@FXML
	private void Cut() {// 剪切
		textArea.cut();
	}

	@FXML
	private void Copy() {// 复制
		textArea.copy();
	}

	@FXML
	private void Paste() {// 粘贴
		textArea.paste();
	}

	@FXML
	private void Delete() {// 删除
		textArea.deletePreviousChar();
	}

	@FXML
	private void Baidu() {// 使用百度搜索
		try {
			Runtime.getRuntime().exec("cmd /c start https://www.baidu.com/s?wd="//
					+ textArea.getSelectedText().replaceAll(" ", "%20"));
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("记事本");
			alert.setHeaderText("打开浏览器出错");
			alert.showAndWait();
		}
	}

	// 用public是因为和查找、替换窗口交互用
	public String SearchText = "";
	public String ReplaceText = "";
	public boolean isCaseSensitive = false;// 是否大小写敏感
	public boolean isRepeat = false;// 是否循环
	public boolean isUpward = false;// 是否向上

	@FXML
	private void Search() {// 查找
		replaceWin.Hide();
		searchWin.Show();
	}

	@FXML
	public void SearchNext() {// 查找下一个
		if (SearchText == "") {// 如果搜索内容为空则打开搜索窗口进行设置
			Search();
		} else {// 进行查找
			int SearchResult;
			String allText = textArea.getText();
			String tarText = SearchText;
			if (!isCaseSensitive) {
				allText = allText.toLowerCase(Locale.ROOT);
				tarText = tarText.toLowerCase(Locale.ROOT);
			}
			if (isUpward) {// 向上查找
				SearchResult = allText.lastIndexOf(tarText, textArea.getCaretPosition() - 2);// magic number
			} else {// 向下查找
				SearchResult = allText.indexOf(tarText, textArea.getCaretPosition());
			}
			if (SearchResult == -1 && isRepeat) {// 如果查找不到但是已开启循环查找
				if (isUpward) {
					SearchResult = allText.lastIndexOf(tarText);
				} else {
					SearchResult = allText.indexOf(tarText);
				}
			}
			if (SearchResult == -1) {// 最后还是找不到
				searchWin.setOnTop(false);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("记事本");
				alert.setHeaderText("找不到 \"" + SearchText + "\"");
				alert.showAndWait();
				searchWin.setOnTop(true);
			} else {// 找到的话高亮
				textArea.selectRange(SearchResult, SearchResult + SearchText.length());
			}
		}
	}

	@FXML
	private void Replace() {// 替换
		searchWin.Hide();
		replaceWin.Show();
	}

	@FXML
	private void Goto() {// 转到

	}

	@FXML
	private void SelectAll() {// 全选
		textArea.selectAll();
	}

	@FXML
	private void DateTime() {// 时间/日期
		textArea.deletePreviousChar();
		textArea.insertText(textArea.getAnchor(), new SimpleDateFormat("HH:mm yyyy/MM/dd").format(new Date()));
	}

	// 格式(O)
	@FXML
	private void AutoLineFeed() {// 自动换行

	}

	@FXML
	private void Font() {// 字体

	}

	// 查看(V)
	@FXML
	private void ZoomIn() {// 放大

	}

	@FXML
	private void ZoomOut() {// 缩小

	}

	@FXML
	private void DefaultZoom() {// 恢复默认缩放

	}

	@FXML
	private void StageBar() {// 状态栏

	}

	// 帮助(H)
	@FXML
	private void Help() {// 查看帮助
		try {
			Runtime.getRuntime().exec("cmd /c start https://www.baidu.com/s?wd=如何使用记事本");
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("记事本");
			alert.setHeaderText("打开浏览器出错");
			alert.showAndWait();
		}
	}

	@FXML
	private void About() {// 关于记事本

	}

	// 其它功能
	@FXML
	private void TextAreaUpdate() {
		hasSave = false;
	}
}
