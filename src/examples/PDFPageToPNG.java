package examples;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class PDFPageToPNG {
    /**
     * Reads a pdf file and saves a page as png image
     * @param args args[0]: pdf file, args[1]: page
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        if(args.length != 2 || !args[0].endsWith(".pdf")) {
            throw new IllegalArgumentException();
        }

        // open file and get page
        PDFFile pdfFile = new PDFFile(
                ByteBuffer.wrap(Files.readAllBytes(Paths.get(args[0]))));
        PDFPage page = pdfFile.getPage(Integer.parseInt(args[1]));

        // find out orientation of page
        Paper paper = new Paper();
        int formatOrientation = page.getAspectRatio() > 1
                ? PageFormat.LANDSCAPE
                : PageFormat.PORTRAIT;
        if(formatOrientation == PageFormat.LANDSCAPE) {
            paper.setSize(page.getHeight(), page.getWidth());
        }
        else {
            paper.setSize(page.getWidth(), page.getHeight());
        }
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPaper(paper);
        pageFormat.setOrientation(formatOrientation);

        // set image bounds as rectangle (in page coordinates)
        Rectangle imgbounds = new Rectangle(
                0,
                0,
                (int)pageFormat.getWidth(),
                (int)pageFormat.getHeight());

        // get image from page
        Image img = page.getImage(
                (int)page.getWidth(),   // target width
                (int)page.getHeight(),  // target height
                imgbounds,              // clipping rect
                null,                   // no image observer
                true,                   // print white background
                true);                  // wait until finished

        // save image as png file
        File outputfile = new File(
                args[0].substring(0, args[0].lastIndexOf(".pdf")) + ".png");
        ImageIO.write((RenderedImage) img, "PNG", outputfile);
    }
}
