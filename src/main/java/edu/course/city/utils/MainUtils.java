package edu.course.city.utils;

import edu.course.city.db.model.Coordinate;
import org.primefaces.model.TreeNode;
import org.primefaces.model.map.LatLng;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public final class MainUtils {

    private MainUtils() {
    }

    public static String convertMapCoordinate(Coordinate coordinate) {
        return coordinate != null ? convertMapCoordinate(coordinate.getLatitude(), coordinate.getLongitude()) : null;
    }

    public static String convertMapCoordinate(LatLng coordinate) {
        return coordinate != null ? convertMapCoordinate(coordinate.getLat(), coordinate.getLng()) : null;
    }

    public static String convertMapCoordinate(Double latitude, Double longitude) {
        if (latitude != null && longitude != null) {
            return String.format("%s, %s", latitude, longitude);
        } else {
            return null;
        }
    }

    public static String convertMapCoordinates(List<Coordinate> coordinates) {
        if (!coordinates.isEmpty()) {
            String result = "";
            for (Coordinate item : coordinates) {
                if (!result.isEmpty()) {
                    result = result + ", ";
                }
                result += "[" + MainUtils.convertMapCoordinate(item) + "]";
            }
            return result;
        }
        return null;
    }

    public static TreeNode findDataObject(TreeNode treeNode, Object data) {
        if (treeNode.getData() == data) {
            return treeNode;
        }
        for (TreeNode child : treeNode.getChildren()) {
            TreeNode result = findDataObject(child, data);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public static byte[] resizeAndConvertToPng(InputStream imageStream, int targetSize) throws IOException {
        BufferedImage image = ImageIO.read(imageStream);
        if (image.getHeight() > targetSize || image.getWidth() > targetSize) {
            int targetWidth;
            int targetHeight;
            if (image.getWidth() >= image.getHeight()) {
                targetWidth = targetSize;
                targetHeight = (int) (image.getHeight() / (image.getWidth() / (double) targetSize));
            } else {
                targetHeight = targetSize;
                targetWidth = (int) (image.getWidth() / (image.getHeight() / (double) targetSize));
            }

            int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
            BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, type);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(image, 0, 0, targetWidth, targetHeight, null);
            g.dispose();

            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            image = resizedImage;
        }

        ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", imageOutputStream);
        return imageOutputStream.toByteArray();
    }
}
