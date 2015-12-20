package com.emprovise.util.documents;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.util.PDFMergerUtility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @Deprecated after upgrading to PDFBox version 2.0
 */
@Deprecated
public class PDFUtil {

    private PDDocument document;
    private PDPage currentPage;
    private PDPageContentStream currentContentStream;

    public PDFUtil(String title){
        // Create a document and add a page to it
        document = new PDDocument();
        // PDPage.PAGE_SIZE_LETTER is also possible
        currentPage = new PDPage(PDPage.PAGE_SIZE_A4);
        document.addPage(currentPage);
    }

    public PDPage getCurrentPage() {
        return this.currentPage;
    }

    public void createNewPage(String title) throws IOException {
        closeContentStream();
        currentPage = new PDPage(PDPage.PAGE_SIZE_A4);
        document.addPage(currentPage);
    }

    public void writeLine(String text, PDFont fontPlain, int fontSize, float xPosition, int lineNumber) throws IOException {

        openContentStream();
        PDRectangle rect = currentPage.getMediaBox();
        currentContentStream.beginText();
        currentContentStream.setFont(fontPlain, fontSize);
        currentContentStream.moveTextPositionByAmount(xPosition, rect.getHeight() - fontSize*(lineNumber));
        currentContentStream.drawString(text);
        currentContentStream.endText();
    }

    public void addImage(File imageFile) throws IOException {
        openContentStream();
        BufferedImage awtImage = ImageIO.read(imageFile);
        PDXObjectImage ximage = new PDPixelMap(document, awtImage);
        float scale = 0.5f; // alter this value to set the image size
        currentContentStream.drawXObject(ximage, 100, 400, ximage.getWidth()*scale, ximage.getHeight()*scale);
    }

    public void savePdf(String outputFileName) throws IOException, COSVisitorException {
        closeContentStream();
        document.save(outputFileName);
        document.close();
    }

    public void mergePDFs(String destinationFile, File... pdfFiles) throws IOException, COSVisitorException {

        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        pdfMerger.setDestinationFileName(destinationFile);

        for (File pdfFile : pdfFiles) {
            pdfMerger.addSource(pdfFile);
        }
        pdfMerger.mergeDocuments();
    }

    public void drawTable(String[][] content, float margin, float y, PDFont font, int fontSize) throws IOException {

        final int rows = content.length;
        final int cols = content[0].length;
        final float rowHeight = 1.8f * fontSize;
        final float tableHeight = rowHeight * rows;
        final float tableWidth = currentPage.findMediaBox().getWidth()-(2*margin);
        final float colWidth = tableWidth/(float)cols;
        final float cellMargin=5f;

        openContentStream();

        //draw the rows
        float nexty = y ;
        for (int i = 0; i <= rows; i++) {
            currentContentStream.drawLine(margin,nexty,margin+tableWidth,nexty);
            nexty-= rowHeight;
        }

        //draw the columns
        float nextx = margin;
        for (int i = 0; i <= cols; i++) {
            currentContentStream.drawLine(nextx,y,nextx,y-tableHeight);
            nextx += colWidth;
        }

        //now add the text
        currentContentStream.setFont(font, fontSize);

        float textx = margin+cellMargin;
        float texty = y-15;
        for(int i = 0; i < content.length; i++){
            for(int j = 0 ; j < content[i].length; j++){
                String text = content[i][j];
                currentContentStream.beginText();
                currentContentStream.moveTextPositionByAmount(textx,texty);
                currentContentStream.drawString(text);
                currentContentStream.endText();
                textx += colWidth;
            }
            texty-=rowHeight;
            textx = margin+cellMargin;
        }
    }

    private void openContentStream() throws IOException {
        if(currentContentStream == null) {
            currentContentStream = new PDPageContentStream(document, currentPage);
        }
    }

    private void closeContentStream() throws IOException {
        if(currentContentStream != null) {
            currentContentStream.close();
        }
    }


    /**
     * This method for include the footer to the each page in pdf document.
     *
     * Set the pdf document.
     * @param reportName
     * Set the report name.
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    public void addReportFooter(String reportName) throws IOException {

        PDPageContentStream footerContentStream = null;
        final float FONT_SIZE = 10f;
        final float X0 = 5f;
        final float PADDING_BOTTOM_OF_DOCUMENT = 30f;

        java.util.List allPages = document.getDocumentCatalog().getAllPages();

        for (int i = 0; i < allPages.size(); i++) {
            PDPage page = ((PDPage) allPages.get(i));
            footerContentStream = new PDPageContentStream(document, page, true, true);
            footerContentStream.beginText();
            footerContentStream.setFont(PDType1Font.HELVETICA_BOLD, FONT_SIZE);
            footerContentStream.moveTextPositionByAmount(X0, (PDPage.PAGE_SIZE_A4.getLowerLeftY() + PADDING_BOTTOM_OF_DOCUMENT));
            footerContentStream.drawString(reportName);
            footerContentStream.moveTextPositionByAmount((PDPage.PAGE_SIZE_A4.getUpperRightX() / 2), (PDPage.PAGE_SIZE_A4.getLowerLeftY()));
            footerContentStream.drawString((i + 1) + " - " + allPages.size());
            footerContentStream.endText();
            footerContentStream.close();
        }
    }

    public static void main(String[] args) throws Exception {
        PDFUtil util = new PDFUtil("test");

        util.writeLine("Hello World !!", PDType1Font.HELVETICA, 12, 100, 1);
        util.writeLine("Hello World !!", PDType1Font.HELVETICA_BOLD, 12, 100, 2);
        util.writeLine("Hello World !!", PDType1Font.HELVETICA_OBLIQUE, 12, 100, 4);
        util.writeLine("Hello World !!", PDType1Font.COURIER, 12, 100, 6);

        String[][] content = {{"a","b", "1"},
                {"c","d", "2"},
                {"e","f", "3"},
                {"g","h", "4"},
                {"i","j", "5"}} ;

        util.drawTable(content, 100, 700, PDType1Font.HELVETICA_BOLD, 12);
        util.savePdf("C:/test.pdf");
    }
}
