/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package console;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 *
 * @author gmein
 */
public class MetaBadge extends StackPane {

    private final int width = 200;
    private final int height = 110;
    private final String[] channelLabels = {"SEI", "BEI1", "BEI2", "AEI"};
    Rectangle badge;
    Text channel;
    Text kv;
    Text mag;
    Text wd;
    Text ops;
    VBox vbox;

    MetaBadge(int channel, int kv, int mag, int wd, String[] ops) {
        this.channel = new Text("Channel: " + (channel == -1 ? "unknown" : channelLabels[channel]));
        this.channel.setFill(Color.GOLD);
        this.kv = new Text("Accelerating Voltage: " + (kv == -1 ? "unknown" : kv + "kv"));
        this.kv.setFill(Color.GOLD);
        this.mag = new Text("Magnification: " + (mag == -1 ? "unknown" : mag + "x"));
        this.mag.setFill(Color.GOLD);
        this.wd = new Text("Working Depth: " + (wd == -1 ? "unknown" : wd + "mm"));
        this.wd.setFill(Color.GOLD);
        String opsText = "Operators:";
        for (String op : ops) {
            opsText += " " + op;
        }
        this.ops = new Text(opsText);
        this.ops.setFill(Color.GOLD);

        this.vbox = new VBox();
        this.vbox.getChildren().addAll(this.channel, this.kv, this.mag, this.wd, this.ops);
        this.vbox.setAlignment(Pos.CENTER_LEFT);
        this.vbox.setPadding(new Insets(10, 15, 10, 15));
        this.vbox.setMinSize(width, height);
        this.vbox.setMaxSize(width, height);

        this.badge = new Rectangle(width, height);
        this.badge.setFill(Color.BLUE);
        this.badge.setOpacity(0.5);
        this.badge.setArcWidth(10);
        this.badge.setArcHeight(10);

        this.getChildren().addAll(this.badge, this.vbox);
        this.setMinSize(width + 20, height + 20);
        this.setMaxSize(width + 20, height + 20);
        this.setPadding(new Insets(10, 10, 10, 10));
    }

    MetaBadge(SEMImage si, int channel, String[] ops) {
        this(channel, si.kv, si.magnification, si.wd, ops);
    }

}
