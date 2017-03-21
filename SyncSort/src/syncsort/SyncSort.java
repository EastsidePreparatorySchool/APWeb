
package syncsort;

import java.util.Arrays;
import java.util.Random;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author gunnar
 */
public class SyncSort extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Sort large array");
        btn.setOnAction((e)->makeLargeArrayAndSort());
        
        ProgressIndicator p = new ProgressIndicator();

        VBox root = new VBox();
        root.getChildren().addAll(btn, p);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Synchronous sort on UI thread");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    public static void main(String[] args) {
        launch(args);
    }

    public static void bubbleSort(int[] a) {
        boolean workDone;
        int size = a.length;
        do {
            workDone = false;
            for (int i = 0; i < size - 1; i++) {
                if (a[i] > a[i + 1]) {
                    int temp = a[i];
                    a[i] = a[i + 1];
                    a[i + 1] = temp;
                    workDone = true;
                }
            }
        } while (workDone);
    }

    
    void makeLargeArrayAndSort() {
        int size = 1000;
        int[] a = new int[size];
        Random r = new Random();
        for (int i = 0; i < size; i++) {
            a[i] = r.nextInt(10000);
        }
        
        bubbleSort(a);
        //Arrays.sort(a);
        System.out.println("Done sorting.");
    }
}
