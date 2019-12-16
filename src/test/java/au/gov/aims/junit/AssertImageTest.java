/*
 *  Copyright (C) 2017 Australian Institute of Marine Science
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

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * This test class test the TestUtils class.
 * Some methods are quite intense...
 */
public class AssertImageTest {

    @Test
    public void testAssertEqualsSameImage() throws Exception {
        File whiteImageFile              = AssertImage.getResourceFile("imageDiff/white.png");

        // Wrong dimensions
        AssertImage.assertEquals("Same image must be reported as equals.", whiteImageFile, whiteImageFile, 0);
    }

    @Test (expected = AssertionError.class)
    public void testAssertEqualsWrongDimensions() throws Exception {
        File whiteImageFile              = AssertImage.getResourceFile("imageDiff/white.png");
        File white20x5ImageFile          = AssertImage.getResourceFile("imageDiff/white_20x5.png");

        // Wrong dimensions
        AssertImage.assertEquals("Incompatible dimensions must fail.", whiteImageFile, white20x5ImageFile, 0);
    }

    @Test
    public void testAssertNotEqualsWrongDimensions() throws Exception {
        File whiteImageFile              = AssertImage.getResourceFile("imageDiff/white.png");
        File white20x5ImageFile          = AssertImage.getResourceFile("imageDiff/white_20x5.png");

        // Wrong dimensions
        AssertImage.assertNotEquals("Incompatible dimensions must be reported as not equals.", whiteImageFile, white20x5ImageFile, 0);
    }

    @Test (expected = AssertionError.class)
    public void testAssertNotEqualsSameImage() throws Exception {
        File whiteImageFile              = AssertImage.getResourceFile("imageDiff/white.png");

        // Wrong dimensions
        AssertImage.assertNotEquals("Same image must be reported as equals.", whiteImageFile, whiteImageFile, 0);
    }


    @Test (expected = IOException.class)
    public void testGetImageDifferenceWrongDimensions() throws Exception {
        File whiteImageFile              = AssertImage.getResourceFile("imageDiff/white.png");
        File white20x5ImageFile          = AssertImage.getResourceFile("imageDiff/white_20x5.png");

        // Wrong dimensions
        AssertImage.getImageDifference(whiteImageFile, white20x5ImageFile);
        Assert.fail("Incompatible dimensions must throw an IOException.");
    }

    /**
     * This test only test the TestUtils.getImageDifference.
     * This method is used to quantify the difference (in percentage) between the pixels of 2 images.
     * @throws Exception
     */
    @Test
    public void testGetImageDifference() throws Exception {
        // Load all the test images
        File blackImageFile              = AssertImage.getResourceFile("imageDiff/black.png");
        File black10White90HorzImageFile = AssertImage.getResourceFile("imageDiff/black-10_white-90_horz.png");
        File black10White90VertImageFile = AssertImage.getResourceFile("imageDiff/black-10_white-90_vert.png");
        File chessboardImageFile         = AssertImage.getResourceFile("imageDiff/black-50_white-50_chessboard.png");
        File chessboardInvImageFile      = AssertImage.getResourceFile("imageDiff/black-50_white-50_chessboard-inv.png");
        File black90White10HorzImageFile = AssertImage.getResourceFile("imageDiff/black-90_white-10_horz.png");
        File black90White10VertImageFile = AssertImage.getResourceFile("imageDiff/black-90_white-10_vert.png");
        File cmykImageFile               = AssertImage.getResourceFile("imageDiff/cmyk_horz.png");
        File rgbImageFile                = AssertImage.getResourceFile("imageDiff/rgb_horz.png");
        File whiteImageFile              = AssertImage.getResourceFile("imageDiff/white.png");
        File whiteImageFileWithMetadata  = AssertImage.getResourceFile("imageDiff/white_with-metadata.png");

        File rgbCirclesPNGImageFile      = AssertImage.getResourceFile("imageDiff/rgb-circles.png");
        File rgbCirclesPNGLowSatHighBrightImageFile = AssertImage.getResourceFile("imageDiff/rgb-circles_low-sat_high-bright.png");
        File rgbCirclesJPG100ImageFile   = AssertImage.getResourceFile("imageDiff/rgb-circles_100.jpg");
        File rgbCirclesJPG75ImageFile    = AssertImage.getResourceFile("imageDiff/rgb-circles_75.jpg");
        File rgbCirclesJPG15ImageFile    = AssertImage.getResourceFile("imageDiff/rgb-circles_15.jpg");

        double diff;


        // Basic tests (black and white)

        // White vs white = 0% different
        diff = AssertImage.getImageDifference(whiteImageFile, whiteImageFileWithMetadata);
        Assert.assertEquals("White images are reported as been different.", 0, diff, AssertImage.SMALL_VALUE);

        // Black vs white = 100% different
        diff = AssertImage.getImageDifference(whiteImageFile, blackImageFile);
        Assert.assertEquals("White image and black image are not reported as been completely different.", 1, diff, AssertImage.SMALL_VALUE);

        // White vs black 10% white 90% = 10% different
        diff = AssertImage.getImageDifference(whiteImageFile, black10White90HorzImageFile);
        Assert.assertEquals("White image and 90% white image (horizontal) should be 10% different.", 0.1, diff, AssertImage.SMALL_VALUE);

        // White vs black 10% white 90% = 10% different
        diff = AssertImage.getImageDifference(whiteImageFile, black10White90VertImageFile);
        Assert.assertEquals("White image and 90% white image (vertical) should be 10% different.", 0.1, diff, AssertImage.SMALL_VALUE);

        // Black 10% (horizontal) vs Black 10% (vertical) = 9x9 white matching pixel + 1 black matching pixel = 82%, 18% difference
        diff = AssertImage.getImageDifference(black10White90HorzImageFile, black10White90VertImageFile);
        Assert.assertEquals("90% white image (horizontal) and 90% white image (vertical) should be 18% different.", 0.18, diff, AssertImage.SMALL_VALUE);

        // Chessboard vs Inverted chessboard = No matching pixels = 100% different
        diff = AssertImage.getImageDifference(chessboardImageFile, chessboardInvImageFile);
        Assert.assertEquals("Chessboard image and inverted chessboard image should be 100% different.", 1, diff, AssertImage.SMALL_VALUE);

        // Chessboard vs White = 50% different
        diff = AssertImage.getImageDifference(chessboardImageFile, whiteImageFile);
        Assert.assertEquals("Chessboard image and white image should be 50% different.", 0.5, diff, AssertImage.SMALL_VALUE);

        // Chessboard vs black = 50% different
        diff = AssertImage.getImageDifference(chessboardImageFile, blackImageFile);
        Assert.assertEquals("Chessboard image and black image should be 50% different.", 0.5, diff, AssertImage.SMALL_VALUE);

        // White 90% (horizontal) vs White 10% (horizontal) = 100% difference
        diff = AssertImage.getImageDifference(black10White90HorzImageFile, black90White10HorzImageFile);
        Assert.assertEquals("90% white image (horizontal) and 10% white image (horizontal) should be 100% different.", 1, diff, AssertImage.SMALL_VALUE);

        // White 90% (vertical) vs White 10% (vertical) = 100% difference
        diff = AssertImage.getImageDifference(black10White90VertImageFile, black90White10VertImageFile);
        Assert.assertEquals("90% white image (vertical) and 10% white image (vertical) should be 100% different.", 1, diff, AssertImage.SMALL_VALUE);

        // White 90% (vertical) vs Black 10% (horizontal) = 9x9 + 1 = 82% difference
        diff = AssertImage.getImageDifference(black10White90VertImageFile, black90White10HorzImageFile);
        Assert.assertEquals("90% white image (vertical) and 10% white image (vertical) should be 82% different.", 0.82, diff, AssertImage.SMALL_VALUE);


        // Test colored images (RGB and CMYK)

        // RGB vs CYMK = 100% difference
        diff = AssertImage.getImageDifference(rgbImageFile, cmykImageFile);
        Assert.assertEquals("RGB and CMYK should be 100% different.", 1, diff, AssertImage.SMALL_VALUE);

        // RGB vs white = Red: 66% diff * 30% coverage, Green: 66% diff * 30% coverage, Blue: 66% diff * 30% coverage, White: 0% diff * 10% coverage
        //   = 60% difference
        diff = AssertImage.getImageDifference(rgbImageFile, whiteImageFile);
        Assert.assertEquals("RGB and white should be 60% different.", 0.6, diff, AssertImage.SMALL_VALUE);

        // CMYK vs white = Cyan: 33% diff * 30% coverage, Magenta: 33% diff * 30% coverage, Yellow: 33% diff * 30% coverage, Black: 100% diff * 10% coverage
        //   = 30% + 10% = 40% difference
        diff = AssertImage.getImageDifference(cmykImageFile, whiteImageFile);
        Assert.assertEquals("CMYK and white should be 40% different.", 0.4, diff, AssertImage.SMALL_VALUE);


        // Test real image (with different level of JPG compression)

        // Full coloured PNG image vs JPG equivalent (100% quality).
        // NOTE: About 68% of pixels are identical
        //   Difference should be extremely low. (0.0005896710897900826)
        diff = AssertImage.getImageDifference(rgbCirclesPNGImageFile, rgbCirclesJPG100ImageFile);
        Assert.assertTrue(String.format("RGB circles (PNG) and RGB circles (JPG 100%%) should be less than 0.06%% different. Actual: %.2f", diff),
                diff < 0.0006);
        Assert.assertTrue(String.format("RGB circles (PNG) and RGB circles (JPG 100%%) should be bigger than 0.05%% different. Actual: %.2f", diff),
                diff > 0.0005);

        // Full coloured PNG image vs JPG equivalent (75% quality).
        // NOTE: More than 50% of pixels are identical
        //   Difference should be pretty low. (0.0012994129943644124)
        diff = AssertImage.getImageDifference(rgbCirclesPNGImageFile, rgbCirclesJPG75ImageFile);
        Assert.assertTrue(String.format("RGB circles (PNG) and RGB circles (JPG 75%%) should be less than 0.13%% different. Actual: %.2f", diff),
                diff < 0.0013);
        Assert.assertTrue(String.format("RGB circles (PNG) and RGB circles (JPG 75%%) should be bigger than 0.12%% different. Actual: %.2f", diff),
                diff > 0.0012);

        // Full coloured PNG image vs JPG equivalent (15% quality).
        // NOTE: Only about 0.65% of pixels are identical (black pixels in the 15% quality are very dark gray)
        //   Difference should be considerable. (0.009342493507819793)
        diff = AssertImage.getImageDifference(rgbCirclesPNGImageFile, rgbCirclesJPG15ImageFile);
        Assert.assertTrue(String.format("RGB circles (PNG) and RGB circles (JPG 15%%) should be less than 0.94%% different. Actual: %.2f", diff),
                diff < 0.0094);
        Assert.assertTrue(String.format("RGB circles (PNG) and RGB circles (JPG 15%%) should be bigger than 0.93%% different. Actual: %.2f", diff),
                diff > 0.0093);

        // JPG (75% quality) vs JPG (15% quality).
        // NOTE: Less than 0.5% of pixels are identical
        //   Difference should be very similar to PNG vs JPG 15%. (0.009447174051912432)
        diff = AssertImage.getImageDifference(rgbCirclesJPG75ImageFile, rgbCirclesJPG15ImageFile);
        Assert.assertTrue(String.format("RGB circles (JPG 75%%) and RGB circles (JPG 15%%) should be less than 0.95%% different. Actual: %.2f", diff),
                diff < 0.0095);
        Assert.assertTrue(String.format("RGB circles (JPG 75%%) and RGB circles (JPG 15%%) should be bigger than 0.94%% different. Actual: %.2f", diff),
                diff > 0.0094);

        // Full coloured PNG image vs Low saturation and high brightness version.
        // NOTE: Every pixels are different
        //   Difference should be quite high. (0.10364632180997399)
        diff = AssertImage.getImageDifference(rgbCirclesPNGImageFile, rgbCirclesPNGLowSatHighBrightImageFile);
        Assert.assertTrue(String.format("RGB circles (PNG) and RGB circles (PNG low saturation and high brightness) should be less than 10.4%% different. Actual: %.2f", diff),
                diff < 0.104);
        Assert.assertTrue(String.format("RGB circles (PNG) and RGB circles (PNG low saturation and high brightness) should be bigger than 10.3%% different. Actual: %.2f", diff),
                diff > 0.103);
    }
}
