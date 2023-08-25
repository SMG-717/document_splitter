package smg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;


public class SplitDocument {

    public SplitDocument(Path document) {
        sourceDoc = document;
        cwd = sourceDoc.toAbsolutePath().getParent();
        out = Paths.get(cwd + "\\" + getTimeString());
        logger = s -> System.out.print(s);
        progressor = i -> System.out.println(i + "%");
    }

    final SimpleDateFormat fm = new SimpleDateFormat("yyyyMMddhhmmss");
    final SimpleDateFormat fms = new SimpleDateFormat("yyyyMMddhhmmssSSS");
    final Path cwd;
    final Path sourceDoc;
    final Path out;
    Consumer<String> logger;
    Consumer<Integer> progressor;

    public void setLogger(Consumer<String> l) {
        logger = l;
    }

    public void setProgressMarker(Consumer<Integer> i) {
        progressor = i;
    }

    private void log(String message) {
        logger.accept(message);
    }

    private void logln() {
        logger.accept("\n");
    }

    private void logln(String message) {
        log(message);
        logln();
    }

    private void progress(int percentage) {
        progressor.accept(percentage);
    }

    private String getTimeString() { 
        return fm.format(Date.from(Instant.now())); 
    }

    private String getTimeStringMilli() { 
        return fms.format(Date.from(Instant.now())); 
    }

    private XWPFDocument clearDocument(XWPFDocument doc) {
        while (doc.getBodyElements().size() > 0) {
            doc.removeBodyElement(0); 
        }
        return doc;
    }

    private XmlObject parseXml(String xml) {
        try {
            return XmlObject.Factory.parse(xml);
        } catch (XmlException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Courtesy of Gary Forbis from StackOverflow
    // https://stackoverflow.com/a/23136358
    private void cloneParagraph(XWPFParagraph clone, XWPFParagraph source) {
        final CTPPr pPr = clone.getCTP().isSetPPr() ? clone.getCTP().getPPr() : clone.getCTP().addNewPPr();
        pPr.set(source.getCTP().getPPr());
        for (XWPFRun r : source.getRuns()) {
            final XWPFRun nr = clone.createRun();
            cloneRun(nr, r);
        }
    }

    
    // Courtesy of... myself, SMG.
    private void cloneRun(XWPFRun clone, XWPFRun source) {
        final CTRPr rPr = clone.getCTR().isSetRPr() ? clone.getCTR().getRPr() : clone.getCTR().addNewRPr();
        rPr.set(source.getCTR().getRPr());
        clone.setText(source.getText(0));

        final CTR ctr = clone.getCTR();
        for (int i = 0; i < source.getCTR().sizeOfBrArray(); i += 1) {
            final CTBr br = ctr.sizeOfBrArray() <= i ? ctr.addNewBr() : ctr.getBrList().get(i);
            br.set(parseXml(source.getCTR().getBrList().get(i).xmlText()));
        }
    }

    // Courtesy of... myself, SMG.
    private void cloneTable(XWPFTable clone, XWPFTable source) {
        CTTblPr tPr = clone.getCTTbl().getTblPr() != null ? clone.getCTTbl().getTblPr() : clone.getCTTbl().addNewTblPr();
        tPr.set(source.getCTTbl().getTblPr());
        clone.removeRow(0);
        for (XWPFTableRow r : source.getRows()) {
            XWPFTableRow nr = clone.createRow();
            CTTrPr tTrPr = nr.getCtRow().getTrPr() != null ? nr.getCtRow().getTrPr() : nr.getCtRow().addNewTrPr();
            tTrPr.set(r.getCtRow().getTrPr());
            for (XWPFTableCell c : r.getTableCells()) {
                XWPFTableCell nc = null;
                if (nr.getTableCells().size() < r.getTableCells().size()) {
                    nc = nr.createCell();
                } else {
                    nc = nr.getTableCells().get(r.getTableCells().indexOf(c));
                }
                CTTcPr tTcPr = nc.getCTTc().getTcPr() != null ? nc.getCTTc().getTcPr() : nc.getCTTc().addNewTcPr();
                tTcPr.set(c.getCTTc().getTcPr());
                
                for (int i = 0; i < c.getParagraphs().size(); i += 1) {
                    XWPFParagraph p = c.getParagraphs().get(i);
                    XWPFParagraph np = nc.getParagraphs().size() > i ? nc.getParagraphs().get(i) : nc.addParagraph();
                    cloneParagraph(np, p);
                }
            }
        }
    }

    private String cloneDocumentParts(XWPFDocument source, XWPFDocument target, int start, int end) {

        final IBodyElement first = target.getBodyElements().size() > 0 ? target.getBodyElements().get(0) : target.createParagraph();
        String reference = null;
        for (int i = end - 1; i >= start; i -= 1) {
            final XmlCursor cursor = ((XWPFParagraph) first).getCTP().newCursor();
            cursor.toNextSibling();
            final IBodyElement copyable = source.getBodyElements().get(i);

            if (copyable instanceof XWPFParagraph) {
                final XWPFParagraph poriginal = (XWPFParagraph) copyable;
                final XWPFParagraph pclone = target.insertNewParagraph(cursor);
                cloneParagraph(pclone, poriginal);
                if (poriginal.getText().contains("Our Ref: ")) {
                    reference = poriginal.getText().replace("Our Ref: ", "");
                }
            } else if (copyable instanceof XWPFTable) {
                final XWPFTable tclone = target.insertNewTbl(cursor);
                cloneTable(tclone, (XWPFTable) copyable);
            }
        }

        target.removeBodyElement(0);

        return reference == null ? getTimeStringMilli() : reference;
    }
    
    private static String limitPath(String s) {
        try {
            
            String[] folders = s.split("\\\\");
            String construct = folders[folders.length - 1];
            if (folders.length > 2) construct = folders[folders.length - 2] + "\\" +  construct;
            if (folders.length > 3) construct = folders[folders.length - 3] + "\\" + construct;
            if (folders.length > 4) construct = "...\\" + construct;

            return folders[0] + "\\" + construct;
        } catch (Exception e) {
            return s;
        }
    }

    
    private void arrange() throws IOException, InvalidFormatException {
        final Instant start = Instant.now();

        logln("Reading document...");
        final String outfile = "Processed Stickers " + getTimeString() + ".docx";

        int i = 0;
        final int divisor = 21;
        logln("Arranging tiles...");

        {
            final XWPFDocument doc = getDocument();
            final int elements = doc.getBodyElements().size();
            while (i < elements) {
                final XWPFParagraph p = (XWPFParagraph) doc.getBodyElements().get(i);

                if ((i + 1) % divisor == 0) {
                    logln("Page " + (i / divisor + 1) + " done.");
                } else if (p.getCTPPr().isSetSectPr()) {
                    p.getCTPPr().unsetSectPr();
                }
                i += 1;
            }
            
            if ((i + 1) % divisor != 0) logln("Page " + (i / divisor + 1) + " done.");

            logln("Saving...");
            final FileOutputStream fs = new FileOutputStream(out.getParent() + "\\" + outfile);
            doc.write(fs);
            fs.close();
            // doc.close();
        }

        progress(100);
        logln();
        logln(String.format("Done. Time taken: %.3f s", (Instant.now().toEpochMilli() - start.toEpochMilli()) / 1000.0D));
        logln("The stickers have been generated in a file named \"" + outfile + "\"");
        logln("which you'll find in the same directory as your selected document.");
        logln("You can now close this window.");
    }

    public void split() throws IOException, InvalidFormatException {

        if (sourceDoc.getFileName().toString().toLowerCase().contains("sticker")) {
            arrange();
            return;
        }

        final Instant start = Instant.now();
        
        logln("Reading document...");
        final XWPFDocument doc = getDocument();
        
        logln("Cloning a template...");
        Files.createDirectory(new File(out.toAbsolutePath().toString()).toPath());
        final XWPFDocument template = clearDocument(getDocument());
        final List<IBodyElement> elements = doc.getBodyElements();
        
        template.write(new FileOutputStream(out + "\\template.docx"));
        
        boolean waiting = true;
        int copy_start = -1;
        int copy_count = 0;
        logln("Generating files...");
        for (int i = 0; i < elements.size(); i += 1) {

            final IBodyElement element = elements.get(i);
            if (element instanceof XWPFParagraph) {
                final XWPFParagraph p = (XWPFParagraph) element;
                if (!waiting && (p.getParagraphText()).equals("E")) {
                    final String reference = cloneDocumentParts(doc, template, copy_start, i);
                    final String docName = out + "\\" + reference + ".docx";
                    template.write(new FileOutputStream(docName));
                    logln(++copy_count + ". Generated " + limitPath(docName));
                    progress(i  * 100 / elements.size());
                    clearDocument(template);

                    waiting = true;
                    copy_start = -1;
                }
                else if (waiting && (p.getParagraphText()).equals("S")) {
                    waiting = false;
                    copy_start = i;
                }
            }
        }

        progress(100);
        logln();
        logln(String.format("Done. Time taken: %.3f s", (Instant.now().toEpochMilli() - start.toEpochMilli()) / 1000.0D));
        logln("The documents have been generated in a folder named " + out.getFileName().toString());
        logln("which you'll find in the same directory as your selected document.");
        logln("You can now close this window.");
    }

    private XWPFDocument getDocument() throws InvalidFormatException, IOException {
        return new XWPFDocument(OPCPackage.open(sourceDoc.toAbsolutePath().toString()));
    }
}
