import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class TSP extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Group group = new Group();
        Scene scene = new Scene(group);

        // Set the Window title
        stage.setHeight(1000);
        stage.setWidth(1000);
        stage.setTitle("TSP");
        stage.resizableProperty().setValue(Boolean.FALSE);
        stage.setScene(scene);

        TSPGenetic tsp = new TSPGenetic();
        tsp.simulate();
        Point[] points = tsp.getPoints();

        for (var point : points) {
            Circle circle = new Circle(3, Color.RED);
            circle.setCenterX(point.x());
            circle.setCenterY(point.y());

            group.getChildren().add(circle);
        }

       var route = tsp.getRoute();

        for (int i = 0; i < route.length; i++) {
            var p1 = points[route[i]];
            var p2 = points[route[(i + 1) % route.length]];

            Line line = new Line(p1.x(), p1.y(), p2.x(), p2.y());
            line.setStroke(Color.SPRINGGREEN);
            line.setStrokeWidth(3);

            group.getChildren().add(line);
        }

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}