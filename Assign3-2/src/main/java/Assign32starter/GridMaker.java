package Assign32starter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
import javax.imageio.ImageIO;

public class GridMaker {
    
    public GridMaker() {
        
    }

    public BufferedImage cropImage(BufferedImage bufferedImage, int x, int y, int width, int height) {
        BufferedImage croppedImage = bufferedImage.getSubimage(x, y, width, height);
        return croppedImage;
    }
  
    public BufferedImage resize(BufferedImage image, int width, int height) {
        if (width < 1 || height < 1)
            return null;
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }
  
    public Stack<String> createImages(String filename, int dimension) throws IOException {
        Stack<String> images = new Stack<String>();
        File file = new File(filename);
        FancyPath path = new FancyPath(file);
        if (!file.exists()) {
            System.err.println("Cannot find file: " + file.getAbsolutePath());
            System.exit(-1);
        }
    
        BufferedImage img = ImageIO.read(file);
        int divisibleHeight = img.getHeight() - (img.getHeight() % dimension);
        int divisibleWidth = img.getWidth() - (img.getWidth() % dimension);
        img = resize(img, divisibleWidth, divisibleHeight);
    
        int cellHeight = divisibleHeight / dimension;
        int cellWidth = divisibleWidth / dimension;
    
        String oldFilename = path.getFilename();
        for(int r = 0; r < dimension; ++r) {
            for (int c = 0; c < dimension; ++c) {
                BufferedImage output = cropImage(img, c*cellWidth, r*cellHeight, cellWidth, cellHeight);
                images.push(oldFilename + "_" + r + "_" + c + ".jpg");
                path.setFilename(oldFilename + "_" + r + "_" + c);
                path.setExtension("jpg");
                File pathFile = new File(path.toString());
                ImageIO.write(output,"jpg", pathFile);
            }
        } 
        System.out.println("Output image dimension: " + new Dimension(img.getWidth(), img.getHeight()));
        System.out.println("Cell output dimension: " +  new Dimension(cellWidth, cellHeight));
        return images;
    }
  
    public class FancyPath {
        private String delimiter;
        private String absolutePath;
        private String folderPath;
        private String filename;
        private String extension;
    
        public FancyPath(File file) {
            absolutePath = file.getAbsolutePath();
            delimiter = absolutePath.startsWith("/") ? "/" : "\\";
            folderPath = absolutePath.substring(0, absolutePath.lastIndexOf(delimiter) + 1);
            String filenameWithExt = absolutePath.substring(absolutePath.lastIndexOf(delimiter) + 1);
            int lastPeriod = filenameWithExt.lastIndexOf('.');
            if (lastPeriod > 0) {
                filename = filenameWithExt.substring(0, lastPeriod);
                extension = filenameWithExt.substring(lastPeriod + 1);
            } else {
                filename = filenameWithExt; 
                extension = "";
            }
        }
    
        public String getFilename() {
            return filename;
        }
    
        public void setFilename(String newFilename) {
            filename = newFilename;
        }
    
        public String getExtension() {
            return extension;
        }
    
        public void setExtension(String newExtension) {
            extension = newExtension;
        }
    
        @Override
        public String toString() {
            return folderPath + filename + "." + extension;
        }
    }
}