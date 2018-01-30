/*
package syncsort;

import java.util.Arrays;
import java.util.Random;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class SyncSort extends Application {

    ProgressIndicator pin;
    Button btn;

    @Override
    public void start(Stage primaryStage) {
        btn = new Button();
        btn.setText("Sort large array");
        btn.setOnAction((e) -> makeLargeArrayAndSort());

        pin = new ProgressIndicator();
        pin.setProgress(0);

        VBox root = new VBox();
        root.getChildren().addAll(btn, pin);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Synchronous sort on UI thread");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest((e)->{System.exit(0);});
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void bubbleSort(int[] a) {
        boolean workDone;
        int size = a.length;
        int pass = 0;
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
        pass++;
        pin.setProgress((double)pass/(double)size);
        } while (workDone);
    }

    void makeLargeArrayAndSort() {
        btn.setDisable(true);
        btn.setText("Please be patient ...");
        new Thread(() -> {
            int size = 100000;
            int[] a = new int[size];
            Random r = new Random();
            int i;
            for ( i = 0; i < size/2; i++) {
                a[i] = r.nextInt(10000);
            }

            for (; i <size; i++) {
                a[i] = 10000;
            }
            bubbleSort(a);
            //Arrays.sort(a);
            System.out.println("Done sorting.");
            pin.setProgress(1);
            Platform.runLater(()->{btn.setDisable(false);btn.setText("Sort large array");});
        }).start();
    }
}
*/
