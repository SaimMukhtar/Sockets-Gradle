package Assign32starter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PicturePanel extends JPanel {

    private class ImageWithCoordinates {
        Image image;
        int x, y;

        ImageWithCoordinates(Image image, int x, int y) {
            this.image = image;
            this.x = x;
            this.y = y;
        }
    }

    private List<ImageWithCoordinates> images = new ArrayList<>();

    public void insertImage(String imagePath, int x, int y) {
        if (x < 0 || y < 0) {
            System.err.println("Coordinates must be non-negative. Received: (" + x + ", " + y + ")");
            x = 0;
            y = 0;
        }

        try {
            File file = new File(imagePath);
            if (!file.exists()) {
                throw new IOException("File does not exist: " + imagePath);
            }
            Image image = ImageIO.read(file);
            if (image == null) {
                throw new IOException("Failed to read image: " + imagePath);
            }
            images.add(new ImageWithCoordinates(image, x, y));
            repaint();
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        for (ImageWithCoordinates imgWithCoords : images) {
            g.drawImage(imgWithCoords.image, imgWithCoords.x, imgWithCoords.y, this);
        }
    }
}