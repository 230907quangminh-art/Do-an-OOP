import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("Xin chào JavaFX!");
        Button button = new Button("Bấm tôi");

        button.setOnAction(e -> label.setText("Bạn vừa bấm nút!"));

        VBox root = new VBox(10, label, button);
        Scene scene = new Scene(root, 300, 200);

        stage.setTitle("Demo JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
