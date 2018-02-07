/*
 * This work is licensed under a Creative Commons Attribution-NonCommercial 3.0 United States License.
 * For more information go to http://creativecommons.org/licenses/by-nc/3.0/us/
 */
package console;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.platform.win32.User32;

/**
 *
 * @author gmein, stolen from StackOverflow member skiwi
 */
public class ConsolePane extends VBox {

    private final TextArea textArea = new TextArea();
    private final TextField textField = new TextField();

    private final List<String> history = new ArrayList<>();
    private int historyPointer = 0;
    private int msgCounter = 0;

    private Consumer<String> onMessageReceivedHandler;

    public ConsolePane() {

        textArea.setEditable(false);
        textArea.setStyle("-fx-control-inner-background: black;");
        textField.setStyle("-fx-background-color: black; -fx-text-fill: red;");
        this.setStyle("-fx-background-color: black;");
        textArea.setPrefRowCount(3);

        this.getChildren().addAll(textArea, textField);

        this.textField.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            switch (keyEvent.getCode()) {
                case ENTER:
                    String text = textField.getText();
                    textArea.appendText(text + System.lineSeparator());
                    history.add(text);
                    historyPointer++;
                    if (onMessageReceivedHandler != null) {
                        onMessageReceivedHandler.accept(text);
                    }
                    textField.clear();
                    break;
                case UP:
                    if (historyPointer == 0) {
                        break;
                    }
                    historyPointer--;
                    runSafe(() -> {
                        textField.setText(history.get(historyPointer));
                        textField.selectAll();
                    });
                    break;
                case DOWN:
                    if (historyPointer == history.size() - 1) {
                        break;
                    }
                    historyPointer++;
                    runSafe(() -> {
                        textField.setText(history.get(historyPointer));
                        textField.selectAll();
                    });
                    break;
                default:
                    break;
            }
        });

        this.setPadding(new Insets(4, 4, 4, 4));

    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        textField.requestFocus();
    }

    private void setOnMessageReceivedHandler(final Consumer<String> onMessageReceivedHandler) {
        this.onMessageReceivedHandler = onMessageReceivedHandler;
    }

    public void clear() {
        runSafe(() -> textArea.clear());
        history.clear();
    }

    public void print(final String text) {
        Objects.requireNonNull(text, "text");
        runSafe(() -> textArea.appendText(text));
    }

    public void println(final String text) {
        if (++msgCounter > 100) {
            msgCounter = 0;
            clear();
        }
        Objects.requireNonNull(text, "text");
        runSafe(() -> textArea.appendText(text + System.lineSeparator()));
    }

    public void println() {
        runSafe(() -> textArea.appendText(System.lineSeparator()));
    }

    public static void runSafe(final Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    public static void MoveSystemConsole() {
        try {
            WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, "SEM");

            User32.INSTANCE.INSTANCE.MoveWindow(hwnd, 0, 0, 100, 100, true);
        } catch (Exception e) {
        }
    }
    
    /* another example:
  import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.*;

public class JnaTest {
   public interface User32 extends StdCallLibrary {
      User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);
      HWND GetForegroundWindow();  // add this
      int GetWindowTextA(PointerType hWnd, byte[] lpString, int nMaxCount);
   }

   public static void main(String[] args) throws InterruptedException {
      byte[] windowText = new byte[512];

      PointerType hwnd = User32.INSTANCE.GetForegroundWindow(); // then you can call it!
      User32.INSTANCE.GetWindowTextA(hwnd, windowText, 512);
      System.out.println(Native.toString(windowText));
   }
}  
*/

}
