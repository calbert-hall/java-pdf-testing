package tests;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.images.Eyes;
import com.applitools.eyes.images.Target;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;

public class DemoTest {

    private static BatchInfo batch;

    @DataProvider(name = "pdf_urls_1", parallel = true)
    public Object[][] pdfUrls_1() {
        return new Object[][]{
                {"https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf", "Dummy PDF"},
                {"https://en.unesco.org/inclusivepolicylab/sites/default/files/dummy-pdf_2.pdf", "Dummy PDF #2"}
        };
    }

    //TODO - replace this filePath with your own local filePaths.
    @DataProvider(name = "pdf_filePaths_1", parallel = true)
    public Object[][] pdfFilePaths_1() {
        return new Object[][]{
                {"/Users/casey/Desktop/Java_Misc/ImageToPDF_2/sample_pdf.pdf", "Sample PDF"}
        };
    }

    @BeforeSuite
    public static void setBatch() {
        batch = new BatchInfo("PDF Batch");
    }

    @Test(dataProvider = "pdf_urls_1")
    public void pdf_from_url(String url, String testName) throws IOException, InterruptedIOException {
        long id = Thread.currentThread().getId();
        System.out.println("URLs: Thread id is: " + id);
        // If you have a PDF from a URL, use this to get the file.
        URL docUrl = new URL(url);
        InputStream in = docUrl.openStream();

        File file = new File("/Users/casey/Desktop/PDF_Testing/Java_PDF_Images/pdf_resources/" + testName + ".pdf");
        FileOutputStream fos = new FileOutputStream(file);

        System.out.println("reading from resource and writing to file...");
        int length = -1;
        byte[] buffer = new byte[1024];// buffer for portion of data from connection
        while ((length = in.read(buffer)) > -1) {
            fos.write(buffer, 0, length);
        }
        fos.close();
        in.close();
        System.out.println("File downloaded");

        // This part prepares the PDF to be converted to a series of Images.
        Eyes eyes = new Eyes();
        eyes.setBatch(batch);
        PDDocument doc = null;
        doc = PDDocument.load(file);
        PDFRenderer pdfRenderer = new PDFRenderer(doc);
        int maxPage = doc.getNumberOfPages();
        //System.out.println("Number of Pages: " + doc.getNumberOfPages());

        if (maxPage == 0)
            throw new IOException("Error reading PDF document");

        try {
            for (int pageNum = 1; pageNum <= maxPage; pageNum++) {
                BufferedImage bim = pdfRenderer.renderImage(pageNum - 1);
                if (!eyes.getIsOpen())
                    eyes.open("Pdfs Java", testName, new RectangleSize(bim.getWidth(), bim.getHeight()));

                eyes.check(String.format("Page-%s", pageNum), Target.image(bim));
                bim.getGraphics().dispose();
                bim.flush();
            }

            // End visual testing.
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

    @Test(dataProvider = "pdf_filePaths_1")
    public void local_pdf_test(String filepath, String testName) throws IOException {

        long id = Thread.currentThread().getId();
        System.out.println("Filepaths: Thread id is: " + id);

        Eyes eyes = new Eyes();
        eyes.setBatch(batch);
        // Set file to whatever you want to test. Next steps: parameterize.
        File file = new File(filepath);

        PDDocument doc = null;
        doc = PDDocument.load(file);
        PDFRenderer pdfRenderer = new PDFRenderer(doc);
        int maxPage = doc.getNumberOfPages();
        //System.out.println("Numpages: " + doc.getNumberOfPages());

        if (maxPage == 0)
            throw new IOException("Error reading PDF document");
        try {

            for (int pageNum = 1; pageNum <= maxPage; pageNum++) {
                BufferedImage bim = pdfRenderer.renderImage(pageNum - 1);
                if (!eyes.getIsOpen())
                    eyes.open("Pdfs Java", testName, new RectangleSize(bim.getWidth(), bim.getHeight()));

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