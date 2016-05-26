import it.progess.model.PrintDocument;
import it.progess.model.PrintFoot;
import it.progess.model.PrintHead;
import it.progess.model.PrintRow;
import it.progess.print.engine.PrintEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;



public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/* SPLIT PDF
		 try {
			
            String inFile = "Testmulti.pdf";
            System.out.println ("Reading " + inFile);
            PdfReader reader = new PdfReader(inFile);
            int n = reader.getNumberOfPages();
            System.out.println ("Number of pages : " + n);
            int i = 0;
            while ( i < n ) {
                String outFile = inFile.substring(0, inFile.indexOf(".pdf"))
                    + "-" + String.format("%03d", i + 1) + ".pdf";
                System.out.println ("Writing " + outFile);
                Document document = new Document(reader.getPageSizeWithRotation(1));
                PdfCopy writer = new PdfCopy(document, new FileOutputStream(outFile));
                document.open();
                PdfImportedPage page = writer.getImportedPage(reader, ++i);
                writer.addPage(page);
                document.close();
                writer.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }*/
		 /*List<InputStream> list = new ArrayList<InputStream>();
	        try {
	            // Source pdfs
	            list.add(new FileInputStream("D:/Projects/Progess/progess-print/src/main/java/Testmulti.pdf"));
	            list.add(new FileInputStream("D:/Projects/Progess/progess-print/src/main/java/testPDF.pdf"));

	            // Resulting pdf
	            OutputStream out = new FileOutputStream("result.pdf");

	            doMerge(list, out);

	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (DocumentException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }	*/
		PrintEngine.createPDF(getDocument(), "D:/testPDF.pdf", "D:/resultPDF.pdf");
	}
	
	public static PrintDocument getDocument(){
		PrintDocument pd = new PrintDocument();
		pd.setHead(new PrintHead());
		PrintHead head = pd.getHead();
		head.setCompany_name("TEST");
		head.setCompany_address("Address1");
		head.setCompany_fax("FAX");
		pd.setRows(new TreeSet<PrintRow>());
		for (int i =0;i<8;i++){
			PrintRow pr = new PrintRow();
			pr.setRow_position(i);
			pr.setRow_code("code "+i);
			pr.setRow_um("B"+i);
			pr.setRow_description("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse vestibulum lectus vitae nullam.Si vis pacem para bellum");
			pd.getRows().add(pr);
		}
		pd.setFoot(new PrintFoot());
		PrintFoot foot = pd.getFoot();
		foot.setFooter_note("note");
		foot.setFooter_price("price");
		foot.setFooter_price10("price(10)");
		foot.setFooter_price22("price(22)");
		foot.setFooter_price4("price(4)");
		foot.setFooter_tax("tax");
		foot.setFooter_tax10("tax 10");
		foot.setFooter_tax22("tax 22");
		foot.setFooter_tax4("tax 4");
		foot.setFooter_total("total");
		foot.setFooter_total10("total 10");
		foot.setFooter_total22("total 22");
		foot.setFooter_total4("total 4");
		foot.setFooter_withholding("Holding");
		return pd;
	}
	/* while ( ii < pagesToPrint ) {
    String outFile = sourcePath.substring(0, sourcePath.indexOf(".pdf"))
        + "-" + String.format("%03d", ii + 1) + ".pdf";
    System.out.println ("Writing " + outFile);
    Document document = new Document(reader.getPageSizeWithRotation(1));
    PdfCopy writer = new PdfCopy(document, new FileOutputStream(outFile));
    document.open();
    PdfImportedPage page = writer.getImportedPage(reader, 1);
    
    writer.addPage(page);
    document.close();
    writer.close();
    ii++;
}*/
}

