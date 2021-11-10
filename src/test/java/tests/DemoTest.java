package tests;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.images.Eyes;
import com.applitools.eyes.images.Target;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;

public class DemoTest {

    @Test
    public void test() throws IOException {
        
        Eyes eyes = new Eyes();

        // Set file to whatever you want to test. Next steps: parameterize.
        File file = new File("/Users/casey/Desktop/Java_Misc/ImageToPDF_2/sample_pdf.pdf");

        PDDocument doc = null;
        doc = PDDocument.load(file);
        PDFRenderer pdfRenderer = new PDFRenderer(doc);
        int maxPage = doc.getNumberOfPages();
        System.out.println("Numpages: " + doc.getNumberOfPages());

        if (maxPage == 0)
            throw new IOException("Error reading PDF document");
        try {

            for (int pageNum = 1; pageNum <= maxPage; pageNum++) {
                BufferedImage bim = pdfRenderer.renderImage(pageNum - 1);
                if (!eyes.getIsOpen())
                    eyes.open("Pdfs Java", "Test PDF", new RectangleSize(bim.getWidth(), bim.getHeight()));

                eyes.check(String.format("Page-%s", pageNum), Target.image(bim));
                bim.getGraphics().dispose();
                bim.flush();
            }

            // End visual UI testing.
            eyes.close();
        } catch(Exception ex){
            System.out.println("Exception: " + ex);
        }
        finally {
            try {
                if (doc != null)
                    doc.close();
            } catch (Exception e) {
                //Do nothing
            }
            // If the test was aborted before eyes.close was called, ends the test as aborted.
            eyes.abortIfNotClosed();
        }
    }
}