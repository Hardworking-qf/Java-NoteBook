package notebook;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NoteBook extends Application {
	public static String[] args;

	public static void main(String[] args) {
		NoteBook.args = args;
		if (args.length == 0)
			args = new String[] { null };
		NoteBook.args = new String[] { "1.txt", "2.txt", "3.txt" };// 测试
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		for (String arg : args) {
			try {
				Stage stage = new Stage();
				FXMLLoader fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(getClass().getResource("MainWin.fxml"));
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				Parent root = fxmlLoader.load();

				Scene scene = new Scene(root);
				stage.setTitle("无标题 - 记事本");
				stage.setScene(scene);

				MainWinController controller = ((MainWinController) fxmlLoader.getController());
				controller.setStage(stage);
				controller.setPath(arg);
				stage.show();
				controller.load();
			} catch (Exception e) {
			}
		}
	}
}
