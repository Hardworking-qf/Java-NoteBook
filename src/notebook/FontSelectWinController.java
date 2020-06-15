package notebook;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

// 参考改进自 https://gitee.com/forai/font_selection_dialog/tree/master
// JavaFX对字体的支持简直是土豆 我写你m
public class FontSelectWinController extends SecondWinController {
	@Override
	public void Show() {
		setFxFont(parentWin.font);
		stage.show();
	}

	@Override
	public void Hide() {
		stage.hide();
	}

	@FXML
	private void Confirm() {
		parentWin.font = getFxFont();
		parentWin.RefreshFont();
	}

	@FXML
	private void Cancel() {
		Hide();
	}

	// 字体、样式、字号选项
	String[] families = Font.getFamilies().toArray(new String[] {});// FontFamily
	String[] styles = { "常规 Aa", "倾斜 Aa", "粗体 Aa", "粗偏斜体 Aa" };// FontWeight + FontPosture
	String[] sizes = { "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72", //
			"初号", "小初", "一号", "小一", "二号", "小二", "三号", "小三", "四号", "小四", "五号", "小五", "六号", "小六", "七号", "八号" };// FontSize

	// 字号值
	double sizeValue[] = { 8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72, //
			42, 36, 26, 24, 22, 18, 16, 15, 14, 12, 10.5, 9, 7.5, 6.5, 5.5, 5 };

	// StringProperties
	public SimpleStringProperty family = new SimpleStringProperty();
	public SimpleStringProperty style = new SimpleStringProperty();
	public SimpleStringProperty size = new SimpleStringProperty();

	// 选择结果显示
	@FXML
	private TextField familyShow;

	@FXML
	private TextField styleShow;

	@FXML
	private TextField sizeShow;

	// 选择列表显示
	@FXML
	private ListView<String> familyList;

	@FXML
	private ListView<String> styleList;

	@FXML
	private ListView<String> sizeList;

	// 预览Label
	@FXML
	private Label preview;

	public void initialize() {
		// 初始化
		initFamily();
		initStyle();
		initSize();

		// 绑定
		family.bind(familyShow.textProperty());
		style.bind(styleShow.textProperty());
		size.bind(sizeShow.textProperty());

		// 用以更新预览的event
		family.addListener((observable, oldValue, newValue) -> {
			updatePreview();
		});

		style.addListener((observable, oldValue, newValue) -> {
			updatePreview();
		});

		size.addListener((observable, oldValue, newValue) -> {
			updatePreview();
		});
	}

	// 更新预览
	private void updatePreview() {
		if (family.getValue() != null && style.getValue() != null && size.getValue() != null) {
			preview.setFont(getFxFont());
		}
	}

	// 初始化
	private void initFamily() {
		familyShow.textProperty().bind(familyList.getSelectionModel().selectedItemProperty());
		familyList.setItems(FXCollections.observableArrayList(families));
		familyList.setCellFactory(list -> new FamilyCell());

		familyList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			familyShow.setStyle("-fx-font-family: '" + newValue.toString() + "';");
		});
	}

	private void initStyle() {
		styleShow.textProperty().bind(styleList.getSelectionModel().selectedItemProperty());
		styleList.setItems(FXCollections.observableArrayList(styles));
		styleList.setCellFactory(list -> new StyleCell());
	}

	private void initSize() {// 没有使用CellFactory
		sizeShow.textProperty().bind(sizeList.getSelectionModel().selectedItemProperty());
		sizeList.setItems(FXCollections.observableArrayList(sizes));
	}

	// 字体、字型、大小选择和javaFX中font互转
	public Font getFxFont() {
		String name = family.getValue();
		FontWeight weight;
		FontPosture posture;
//		System.out.println(family + "," + style + "," + size);
		switch (style.getValue()) {
		case "常规 Aa":
			weight = FontWeight.NORMAL;
			posture = FontPosture.REGULAR;
			break;
		case "倾斜 Aa":
			weight = FontWeight.NORMAL;
			posture = FontPosture.ITALIC;
			break;
		case "粗体 Aa":
			weight = FontWeight.BOLD;
			posture = FontPosture.REGULAR;
			break;
		case "粗偏斜体 Aa":
			weight = FontWeight.BOLD;
			posture = FontPosture.ITALIC;
			break;
		default:
			weight = FontWeight.NORMAL;
			posture = FontPosture.REGULAR;
			break;
		}

		double dsize = getFxFontSize(size.getValue());
//		System.out.println(Font.font(name, weight, posture, dsize).getStyle());
		return Font.font(name, weight, posture, dsize);
	}

	// 字号字符转对应数字（如"四号"转14）
	private double getFxFontSize(String sizeStr) {
		// 首先试图直接找到对应字符串
		for (int i = 0; i < sizes.length; ++i)
			if (sizeStr.equals(sizes[i]))
				return sizeValue[i];

		// 找不到则使用转换，转换失败则使用默认大小15（小三）
		double size = 15;
		try {
			size = Double.parseDouble(sizeStr);
		} catch (Exception e) {
		}
		return size;
	}

	// 设置字体，用在打开字体页面时
	public void setFxFont(Font font) {
		familyList.getSelectionModel().select(font.getFamily());

		String fontSelect;
		switch (font.getStyle()) {
		case "Regular":
			fontSelect = "常规 Aa";
			break;
		case "Italic":
			fontSelect = "倾斜 Aa";
			break;
		case "Bold":
			fontSelect = "粗体 Aa";
			break;
		case "Bold Italic":
			fontSelect = "粗偏斜体 Aa";
			break;
		default:
			fontSelect = "常规 Aa";
			break;
		}
		styleList.getSelectionModel().select(fontSelect);

		setFxFontSize(font.getSize());
	}

	private void setFxFontSize(double size) {
		// 试图直接找到数字对应的sizeStr
		for (int i = 0; i < sizeValue.length; ++i) {
			if (sizeValue[i] == size) {
				sizeList.getSelectionModel().select(i);
				return;
			}
		}

		// 若找不到，直接将该double的值放在框里
		sizeShow.setText("" + size);
	}
}

class FamilyCell extends ListCell<String> {
	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		setFont(javafx.scene.text.Font.font(item));
		setText(item);
	}
}

class StyleCell extends ListCell<String> {
	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if (empty)
			return;
		Font f = getFont();
		switch (item) {
		case "常规 Aa":
			f = Font.font("Times New Roman", FontWeight.NORMAL, FontPosture.REGULAR, f.getSize());
			break;
		case "倾斜 Aa":
			f = Font.font("Times New Roman", FontWeight.NORMAL, FontPosture.ITALIC, f.getSize());
			break;
		case "粗体 Aa":
			f = Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, f.getSize());
			break;
		case "粗偏斜体 Aa":
			f = Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, f.getSize());
			break;
		default:
			f = Font.font("Times New Roman", FontWeight.NORMAL, FontPosture.REGULAR, f.getSize());
			break;
		}
		setFont(f);
//		System.out.println(getFont());
		setText(item);
	}
}
