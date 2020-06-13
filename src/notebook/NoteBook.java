package notebook;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NoteBook extends Application {
	public static String args;

	public static void main(String[] args) {
		NoteBook.args = args.length == 0 ? null : args[0];
		NoteBook.args = "1.txt";// 测试
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {		
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("MainWin.fxml"));
		fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
		Parent root = fxmlLoader.load();

		Scene scene = new Scene(root);
		primaryStage.setTitle("无标题 - 记事本");
		primaryStage.setScene(scene);

		((MainWinController) fxmlLoader.getController()).setStage(primaryStage);
		primaryStage.show();
	}

}
