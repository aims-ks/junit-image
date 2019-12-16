/*
 *  Copyright (C) 2019 Australian Institute of Marine Science
 *
 *  Contact: Gael Lafond <g.lafond@aims.gov.au>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package au.gov.aims.junit;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AssertImage {
    public static final double SMALL_VALUE = 0.00000001;

    public static void assertEquals(File expected, File actual, double delta) {
        assertEquals(null, expected, actual, delta);
    }

    public static void assertEquals(String message, File expected, File actual, double delta) {
        try {
            double difference = AssertImage.getImageDifference(expected, actual);

            if (difference > delta) {
                throw new AssertionError(getFailMessage(message, expected, actual, difference));
            }
        } catch(Exception ex) {
            throw new AssertionError(getFailMessage(message, expected, actual, null), ex);
        }
    }

    public static void assertNotEquals(File expected, File actual, double delta) {
        assertNotEquals(null, expected, actual, delta);
    }

    public static void assertNotEquals(String message, File expected, File actual, double delta) {
        try {
            double difference = AssertImage.getImageDifference(expected, actual);

            if (difference <= delta) {
                throw new AssertionError(getFailMessage(message, expected, actual, difference));
            }
        } catch(Exception ex) {
            // If exception occur, images are considered different
        }
    }

    private static String getFailMessage(String message, File expected, File actual, Double difference) {
        return String.format(
                "%s%n" +
                "Expected  : %s%n" +
                "Actual    : %s%n" +
                "Difference: %s",
                message == null ? "" : message,
                expected,
                actual,
                difference == null ? "N/A" : String.format("%.2f%%", difference * 100));
    }


    public static File getResourceFile(String resource) throws Exception {
        URL resourceUrl = AssertImage.class.getClassLoader().getResource(resource);
        if (resourceUrl != null) {
            if (resourceUrl.getProtocol().equals("file")) {
                return new File(resourceUrl.toURI());
            }
        }
        return null;
    }

    /**
     * Compare two images, pixel by pixel, and return the difference in percent.
     * This is used in Unit tests to validate that the generated image match expectations.
     * @param expectedImageFile The reference image file.
     * @param actualImageFile The generated image file.
     * @return The percentage of difference between the two images; value between [0,1].
     * @throws IOException If the image files are null, dimensions are different, not readable or not an image.
     */
    public static double getImageDifference(File expectedImageFile, File actualImageFile) throws IOException {
        if (expectedImageFile == null) {
            throw new IOException("Expected image file must not be null.");
        }
        if (actualImageFile == null) {
            throw new IOException("Actual image file must not be null.");
        }

        if (!expectedImageFile.isFile() || !expectedImageFile.canRead()) {
            throw new IOException(String.format("Expected image file is invalid. %s", expectedImageFile));
        }
        if (!actualImageFile.isFile() || !actualImageFile.canRead()) {
            throw new IOException(String.format("Actual image file is invalid. %s", actualImageFile));
        }

        BufferedImage expectedRGBAImage = ImageIO.read(expectedImageFile);
        if (expectedRGBAImage == null) {
            throw new IOException(String.format("Expected image file is not an image. %s", expectedImageFile));
        }

        BufferedImage actualRGBAImage = ImageIO.read(actualImageFile);
        if (actualRGBAImage == null) {
            throw new IOException(String.format("Actual image file is not an image. %s", actualImageFile));
        }

        int width = expectedRGBAImage.getWidth(),
            height = expectedRGBAImage.getHeight();
        if (actualRGBAImage.getWidth() != width || actualRGBAImage.getHeight() != height) {
            throw new IOException(String.format(
                    "Images dimensions are incompatible. " +
                    "Expected image: [%dpx x %dpx]. " +
                    "Actual image: [%dpx x %dpx]. " +
                    "Expected image file: %s. " +
                    "Actual image file: %s.",
                    width, height,
                    actualRGBAImage.getWidth(), actualRGBAImage.getHeight(),
                    expectedImageFile,
                    actualImageFile));
        }

        // Get the image pixels
        // One int per pixel (values of Red, Green, Blue encoded as an int)
        int[] expectedRGBArray = expectedRGBAImage.getRGB(0, 0, width, height, null, 0, width);
        int[] actualRGBArray = actualRGBAImage.getRGB(0, 0, width, height, null, 0, width);

        // Ensure the pixel arrays are of the same dimensions.
        // NOTE: This is very unlikely to fail since we already checked the images width x height
        //   and we are requesting pixels using the exact same parameters.
        if (expectedRGBArray.length != actualRGBArray.length) {
            throw new IOException(String.format(
                    "Images dimensions are incompatible. " +
                    "Expected image: [%d values]. " +
                    "Actual image: [%d values]. " +
                    "Expected image file: %s. " +
                    "Actual image file: %s.",
                    expectedRGBArray.length,
                    actualRGBArray.length,
                    expectedImageFile,
                    actualImageFile));
        }


        long redDiff = 0, greenDiff = 0, blueDiff = 0;

        // NOTE: These objects will be re-used a lot, better declare them outside the loop
        //   to same precious garbage collection time.
        Color expectedColour, actualColour;

        for (int i=0; i<expectedRGBArray.length; i++) {
            // Decode the int as actual RGB values.
            expectedColour = new Color(expectedRGBArray[i]);
            actualColour = new Color(actualRGBArray[i]);

            // Diff domain: [-255, 255]
            // After Math.abs: [0, 255]
            redDiff += Math.abs(expectedColour.getRed() - actualColour.getRed());
            greenDiff += Math.abs(expectedColour.getGreen() - actualColour.getGreen());
            blueDiff += Math.abs(expectedColour.getBlue() - actualColour.getBlue());
        }

        // Each pixels contains 3 colours (Red, Green, Blue), each with 256 possible values [0, 255]
        return (redDiff + greenDiff + blueDiff) / 3.0 / expectedRGBArray.length / 255.0;
    }
}
