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

// �ο��Ľ��� https://gitee.com/forai/font_selection_dialog/tree/master
// JavaFX�������֧�ּ�ֱ������ ��д��m
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

	// ���塢��ʽ���ֺ�ѡ��
	String[] families = Font.getFamilies().toArray(new String[] {});// FontFamily
	String[] styles = { "���� Aa", "��б Aa", "���� Aa", "��ƫб�� Aa" };// FontWeight + FontPosture
	String[] sizes = { "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72", //
			"����", "С��", "һ��", "Сһ", "����", "С��", "����", "С��", "�ĺ�", "С��", "���", "С��", "����", "С��", "�ߺ�", "�˺�" };// FontSize

	// �ֺ�ֵ
	double sizeValue[] = { 8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72, //
			42, 36, 26, 24, 22, 18, 16, 15, 14, 12, 10.5, 9, 7.5, 6.5, 5.5, 5 };

	// StringProperties
	public SimpleStringProperty family = new SimpleStringProperty();
	public SimpleStringProperty style = new SimpleStringProperty();
	public SimpleStringProperty size = new SimpleStringProperty();

	// ѡ������ʾ
	@FXML
	private TextField familyShow;

	@FXML
	private TextField styleShow;

	@FXML
	private TextField sizeShow;

	// ѡ���б���ʾ
	@FXML
	private ListView<String> familyList;

	@FXML
	private ListView<String> styleList;

	@FXML
	private ListView<String> sizeList;

	// Ԥ��Label
	@FXML
	private Label preview;

	public void initialize() {
		// ��ʼ��
		initFamily();
		initStyle();
		initSize();

		// ��
		family.bind(familyShow.textProperty());
		style.bind(styleShow.textProperty());
		size.bind(sizeShow.textProperty());

		// ���Ը���Ԥ����event
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

	// ����Ԥ��
	private void updatePreview() {
		if (family.getValue() != null && style.getValue() != null && size.getValue() != null) {
			preview.setFont(getFxFont());
		}
	}

	// ��ʼ��
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

	private void initSize() {// û��ʹ��CellFactory
		sizeShow.textProperty().bind(sizeList.getSelectionModel().selectedItemProperty());
		sizeList.setItems(FXCollections.observableArrayList(sizes));
	}

	// ���塢���͡���Сѡ���javaFX��font��ת
	public Font getFxFont() {
		String name = family.getValue();
		FontWeight weight;
		FontPosture posture;
//		System.out.println(family + "," + style + "," + size);
		switch (style.getValue()) {
		case "���� Aa":
			weight = FontWeight.NORMAL;
			posture = FontPosture.REGULAR;
			break;
		case "��б Aa":
			weight = FontWeight.NORMAL;
			posture = FontPosture.ITALIC;
			break;
		case "���� Aa":
			weight = FontWeight.BOLD;
			posture = FontPosture.REGULAR;
			break;
		case "��ƫб�� Aa":
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

	// �ֺ��ַ�ת��Ӧ���֣���"�ĺ�"ת14��
	private double getFxFontSize(String sizeStr) {
		// ������ͼֱ���ҵ���Ӧ�ַ���
		for (int i = 0; i < sizes.length; ++i)
			if (sizeStr.equals(sizes[i]))
				return sizeValue[i];

		// �Ҳ�����ʹ��ת����ת��ʧ����ʹ��Ĭ�ϴ�С15��С����
		double size = 15;
		try {
			size = Double.parseDouble(sizeStr);
		} catch (Exception e) {
		}
		return size;
	}

	// �������壬���ڴ�����ҳ��ʱ
	public void setFxFont(Font font) {
		familyList.getSelectionModel().select(font.getFamily());

		String fontSelect;
		switch (font.getStyle()) {
		case "Regular":
			fontSelect = "���� Aa";
			break;
		case "Italic":
			fontSelect = "��б Aa";
			break;
		case "Bold":
			fontSelect = "���� Aa";
			break;
		case "Bold Italic":
			fontSelect = "��ƫб�� Aa";
			break;
		default:
			fontSelect = "���� Aa";
			break;
		}
		styleList.getSelectionModel().select(fontSelect);

		setFxFontSize(font.getSize());
	}

	private void setFxFontSize(double size) {
		// ��ͼֱ���ҵ����ֶ�Ӧ��sizeStr
		for (int i = 0; i < sizeValue.length; ++i) {
			if (sizeValue[i] == size) {
				sizeList.getSelectionModel().select(i);
				return;
			}
		}

		// ���Ҳ�����ֱ�ӽ���double��ֵ���ڿ���
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
		case "���� Aa":
			f = Font.font("Times New Roman", FontWeight.NORMAL, FontPosture.REGULAR, f.getSize());
			break;
		case "��б Aa":
			f = Font.font("Times New Roman", FontWeight.NORMAL, FontPosture.ITALIC, f.getSize());
			break;
		case "���� Aa":
			f = Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, f.getSize());
			break;
		case "��ƫб�� Aa":
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
