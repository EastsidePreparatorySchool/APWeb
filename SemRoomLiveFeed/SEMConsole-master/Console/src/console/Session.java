package console;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;

public class Session {

    private class SessionImage {

        SEMImage si;
        Image thumbNail;
        String fileName;

        public void dehydrate() {
        }

        public void rehydrate() {
        }
    }

    private String folder;
    private ArrayList<SessionImage> asi;
    private int imgCounter;
    private Console consoleInstance;
    private String operators;

    Session(String folderPath, Console instance, String operators) {
        this.folder = folderPath;
        this.imgCounter = 1;
        asi = new ArrayList<>();
        consoleInstance = instance;
        this.operators = operators;
    }

    private void addFolderThumbnail(String imageFileName) {
        Image thumbnail = new Image(imageFileName, 400, 300, false, false);

    }

    public String generatePartialImageName() {
        return "img-" + this.imgCounter++;
    }

    public void saveImageSetAndAdd(SEMImage si, final String partialName, final String suffix, boolean upload) {
        Thread t = new Thread(() -> {
            for (int i = 0; i < si.channels; i++) {
                File file;
                String fullName;
                if (partialName == null) {
                    fullName = this.generatePartialImageName();
                } else {
                    fullName = partialName;
                    if (suffix != null) {
                        fullName += suffix;
                    }
                }
                Platform.runLater(() -> {
                    this.consoleInstance.addThumbnail(si);
                });

                // add metadata to file name
                fullName = decorateFileName(fullName, si, i);
                si.fileName = fullName;
                try {
                    file = new File(this.folder + System.getProperty("file.separator") + fullName);
                    ImageIO.write(SwingFXUtils.fromFXImage(si.images[i], null), "png", file);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }

                if (i == 0 && upload) {
                    fullName = fullName.substring(0, fullName.length() - 4); // take of ".png"
                    fullName += ".jpg";
                    si.fileName = fullName;
                    try {
                        // make lower-resolution jpg from image, then save and upload
                        file = new File(this.folder + System.getProperty("file.separator") + fullName);
                        ImageView iv = new ImageView(si.images[i]);
                        Pane p = new Pane();
                        p.getChildren().add(iv);
                        p.setMinSize(1080 / 3 * 4, 1080);
                        p.setMaxSize(1080 / 3 * 4, 1080);
                        WritableImage wi = new WritableImage(1080 / 3 * 4, 1080);
                        p.snapshot(null, wi);

                        ImageIO.write(SwingFXUtils.fromFXImage(wi, null), "jpg", file);

                        FileUpload.uploadFileAndMetaDataToServer(fullName,
                                this.operators,
                                Console.channelNames[si.capturedChannels[i]],
                                si.kv,
                                si.magnification,
                                si.wd);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        });
        t.start();

    }

    public static String decorateFileName(String fileName, SEMImage si, int channel) {
        return fileName + "_channel-" + si.capturedChannels[channel]
                + "_kv-" + si.kv
                + "_mag-" + si.magnification
                + "_wd-" + si.wd
                + "_operators-" + si.operators
                + "_.png";
    }

    public static void parseFileName(String fileName, SEMImage si) {
        int slash = fileName.lastIndexOf(System.getProperty("file.separator"));
        if (slash != -1) {
            fileName = fileName.substring(slash + 1);
        }

        String[] parts = fileName.split("[_.]");
//        for (int i = 0; i < parts.length; i++) {
//            System.out.println("part: " + parts[i]);
//        }
        String partName = null;
        String partValue = null;
        for (int i = 0; i < parts.length - 1; i++) {
            try {
                partName = parts[i].substring(0, parts[i].indexOf("-"));
                partValue = parts[i].substring(parts[i].indexOf("-") + 1);
                switch (partName) {
                    case "channel":
                        si.capturedChannels[0] = Integer.parseInt(partValue);
                        break;
                    case "kv":
                        si.kv = Integer.parseInt(partValue);
                        break;
                    case "mag":
                        si.magnification = Integer.parseInt(partValue);
                        break;
                    case "wd":
                        si.wd = Integer.parseInt(partValue);
                        break;
                    case "operators":
                        si.operators = partValue;
                        break;
                    default:
                        //System.out.println("Unregonized file part name " + partName);
                        break;
                }
            } catch (Exception e) {
                System.out.println("Part exception: " + partName + ":" + partValue);
            }

        }
    }

    public SEMImage loadFile(String fileName) {
        SEMImage si = new SEMImage(fileName);
        try {
            Console.println("Loading file " + fileName);
            File file = new File(this.folder + System.getProperty("file.separator") + fileName);
            BufferedImage bi = ImageIO.read(file);
            Console.println("succesfully read file " + fileName);

            si.images[0] = SwingFXUtils.toFXImage(bi, null);
            Console.println("successfully converted file " + fileName);

            si.width = (int) si.images[0].getWidth();
            si.height = (int) si.images[0].getHeight();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return si;
    }

    public String[] gatherSlideshowFiles() {
        ArrayList<String> files = new ArrayList<>();
        Console.println("Scanning " + this.folder);

        scanFolder(this.folder, files);
        Console.println("Slides found: " + files.size());
        return files.toArray(new String[files.size()]);

    }

    public void scanFolder(String folder, ArrayList<String> picFiles) {
        try {
            File folderFile = new File(folder);

            File[] files = folderFile.listFiles();

            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        // recurse
                        scanFolder(folder + f.getName() + System.getProperty("file.separator"), picFiles);
                    } else if ((f.getName().toLowerCase().endsWith(".png"))) {
                        picFiles.add(f.getName());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void addStereoImage(SEMImage siLeft, SEMImage siRight, String name, boolean upload) {
        SEMImage siStereo = new SEMImage(siLeft, siRight);
        siStereo.knitStereoImage();
        this.consoleInstance.displayPhoto(siStereo);
        this.saveImageSetAndAdd(siStereo, name, null, upload);
    }

}
