package it.progess.print.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import it.progess.model.PrintDocument;
import it.progess.model.PrintFoot;
import it.progess.model.PrintHead;
import it.progess.model.PrintRow;



/***************************
 * Get ProgessDocument files to transform in pdf
 * @author luca
 *
 */
public class PrintEngine {
	public static final String FIELD_ROW_START = "riga_codice";
	public static final String FIELD_ROW_END = "riga_fine";
	public static final int FIELD_ROW_BOTTOM_PADDING = 8;
	
	public static final String FIELD_COMPANY_NAME = "azienda";
	public static final String FIELD_COMPANY_ADDRESS = "azienda_indirizzo";
	public static final String FIELD_COMPANY_ADDRESS2 = "azienda_localita";
	public static final String FIELD_COMPANY_PI = "azienda_pi";
	public static final String FIELD_COMPANY_CF = "azienda_cf";
	public static final String FIELD_COMPANY_CODE = "azienda_cciaa";
	public static final String FIELD_CUSTOMER_NAME = "cliente";
	public static final String FIELD_CUSTOMER_ADDRESS = "cliente_indirizzo";
	public static final String FIELD_CUSTOMER_ADDRESS2 = "cliente_localita";
	public static final String FIELD_CUSTOMER_PI = "cliente_pi";
	public static final String FIELD_CUSTOMER_CF = "cliente_cf";
	public static final String FIELD_DOCUMENT_NAME = "tipo_documento";
	public static final String FIELD_DOCUMENT_NUMBER = "numero";
	public static final String FIELD_DOCUMENT_DATE = "data";
	public static final String FIELD_DOCUMENT_PAYMENT = "pagamento";
	public static final String FIELD_DOCUMENT_IBAN = "iban";
	
	public static final String FIELD_ROW_CODE = "riga_codice";
	public static final String FIELD_ROW_DECRIPTION = "riga_descrizione";
	public static final String FIELD_ROW_UM = "riga_um";
	public static final String FIELD_ROW_QUANTITY = "riga_qta";
	public static final String FIELD_ROW_PRICE = "riga_prezzo";
	public static final String FIELD_ROW_AMOUNT = "riga_importo";
	public static final String FIELD_ROW_TAXRATE = "riga_iva";
	
	public static final String FIELD_FOOTER_TAX="imponibile";
	public static final String FIELD_FOOTER_TAX_4="iva_4";
	public static final String FIELD_FOOTER_PRICE_4="impo_4";
	public static final String FIELD_FOOTER_TOT_4="tot_4";
	public static final String FIELD_FOOTER_PRICE="imposta";
	public static final String FIELD_FOOTER_HOLDING="ritenuta";
	public static final String FIELD_FOOTER_PRICE_10="impo_10";
	public static final String FIELD_FOOTER_TOT_10="tot_10";
	public static final String FIELD_FOOTER_TAX_10="iva_10";
	public static final String FIELD_FOOTER_TOT_22="tot_22";
	public static final String FIELD_FOOTER_TAX_22="iva_22";
	public static final String FIELD_FOOTER_PRICE_22="impo_22";
	public static final String FIELD_FOOTER_NOTE="note";
	public static final String FIELD_FOOTER_TOT="totale";
	
	public static final int MAX_LENGTH_ROW_DESCRIPTION = 100;
	public static final float DISTANCE_BETWEEN_ROW = new Float("8");
	
	public static void createPDF(PrintDocument doc,String sourcePath,String destinationPath){
		/**LOAD PDF TEMPLATE**/
		try{
		//Template pages count
		PdfReader reader = new PdfReader(sourcePath);
        int templatePages = reader.getNumberOfPages();
		//if pages == 1
        if (templatePages == 1){
			//Calculate number of ROWS per page
        	Rectangle pageSize = reader.getPageSize(1);
        	AcroFields fields = reader.getAcroFields();
        	//Calculate Rows per page
        				
        	int rowsPerPage = getRowsPerPage(pageSize,fields);
			//Calculate number of page to print
        	int pagesToPrint = getPagesToPrint(doc,rowsPerPage);
        	System.out.println("PAGES TO PRINT: "+pagesToPrint);
        	//Create main PDF
        	if (pagesToPrint == 1){
        		FileOutputStream fileOutputStream = new FileOutputStream(destinationPath);
    			PdfStamper pdfs = new PdfStamper(reader, fileOutputStream);
    			pdfs.setFormFlattening(true);
    			pdfs.getAcroFields().setGenerateAppearances(true);
    			printHead(doc,pdfs.getAcroFields());
    			Map<Integer,TreeSet<PrintRow>> map =getRowListPerPage(doc, pagesToPrint, rowsPerPage);
    			PdfContentByte canvas = pdfs.getOverContent(1);
    			printRows(map.get(0), pdfs.getAcroFields(), canvas);
    			printFooter(doc, pdfs.getAcroFields());
    			pdfs.close();
    			reader.close();
        	}else{
			//From 0 to page number
        		int ii = 0;
        		int i = 0;
        		//Create PDFSingle
        		Map<Integer,TreeSet<PrintRow>> map =getRowListPerPage(doc, pagesToPrint, rowsPerPage);
                reader.close();   
                List<String> list = new ArrayList<String>();
                while ( i < pagesToPrint ) {     
                    /**PRINT DATA**/
                    String outFileTofill = destinationPath.substring(0, destinationPath.indexOf(".pdf"))
                            + "-" + String.format("%03d", i + 1) + ".pdf";
                    FileOutputStream fileOutputStream = new FileOutputStream(outFileTofill);
                    PdfReader readerSingle = new PdfReader(sourcePath);
        			PdfStamper pdfs = new PdfStamper(readerSingle, fileOutputStream);
        			pdfs.setFormFlattening(true);
        			pdfs.getAcroFields().setGenerateAppearances(true);
        			printHead(doc,pdfs.getAcroFields());
        			PdfContentByte canvas = pdfs.getOverContent(1);
        			printRows(map.get(i), pdfs.getAcroFields(), canvas);
        			pdfs.close();
        			readerSingle.close();
        			list.add(outFileTofill);
    	            i++;
                }
                OutputStream out = new FileOutputStream(destinationPath);
                doMerge(list, out); 
	            deleteFiles(list);
			}
			//return pdfDestination
        	
        }else{
        	reader.close();
		//else
			//TODO
        }
        
		}catch(IOException ioe){
			ioe.printStackTrace();
		}catch(DocumentException de){
			de.printStackTrace();
		}
	}
	private static int getRowsPerPage(Rectangle pageSize,AcroFields fields){
		/***** calcola posizione *******/
		float pageHeight = pageSize.getTop();
		/****START POSITION******/
		List<AcroFields.FieldPosition> positionsStart = fields.getFieldPositions(FIELD_ROW_START);
		Rectangle rectStart = positionsStart.get(0).position; // In points:
	    float bTopStart   = rectStart.getTop();
	    float heightStart = rectStart.getHeight();
	    float topStart = pageHeight - bTopStart;
	    /****END POSITION******/
	    List<AcroFields.FieldPosition> positionsEnd = fields.getFieldPositions(FIELD_ROW_END);
		Rectangle rectEnd = positionsEnd.get(0).position; // In points:
	    float bTopEnd   = rectEnd.getTop();
	    float heightEnd = rectEnd.getHeight();
		float topEnd = pageHeight - bTopEnd;
		/**** CALCULATE NUM OF ROWS*****/
		float lastRowposition = topEnd+heightEnd;
	    System.out.println("Last Position:"+lastRowposition);
	    float heightRows = (lastRowposition - topStart)/(heightStart+FIELD_ROW_BOTTOM_PADDING);
	    System.out.println("Rows Real:"+heightRows);
	    int intpart = (int) Math.floor(heightRows);
	    System.out.println("Rows:"+intpart);
	    
		return intpart;
	}
	private static int getPagesToPrint(PrintDocument doc,long rowsPerPage){
		int pages = 0;
		int rows = doc.getRows().size();
		if (rows <= rowsPerPage){
			pages = 1;
		}else{
			double pagesF = (double)rows/rowsPerPage;
			pages = (int) Math.ceil(pagesF);
		}
		return pages;
	}
	private static void printHead(PrintDocument doc,AcroFields field) throws DocumentException,IOException{
		PrintHead printHead = doc.getHead();
		setField(field, FIELD_COMPANY_NAME, printHead.getCompany_name());
		setField(field, FIELD_COMPANY_ADDRESS, printHead.getCompany_address());
		setField(field, FIELD_COMPANY_ADDRESS2, printHead.getCompany_address2());
		setField(field, FIELD_COMPANY_PI, printHead.getCompany_ID());
		setField(field, FIELD_COMPANY_CF, printHead.getCompany_taxcode());
		setField(field, FIELD_COMPANY_CODE, printHead.getCompany_code());
		setField(field, FIELD_CUSTOMER_ADDRESS, printHead.getCustomer_address());
		setField(field, FIELD_CUSTOMER_ADDRESS2, printHead.getCustomer_address2());
		setField(field, FIELD_CUSTOMER_CF, printHead.getCustomer_taxcode());
		setField(field, FIELD_CUSTOMER_NAME, printHead.getCustomer_name());
		setField(field, FIELD_COMPANY_PI, printHead.getCustomer_ID());
		setField(field, FIELD_DOCUMENT_NAME, printHead.getDocument_name());
		setField(field, FIELD_DOCUMENT_NUMBER, printHead.getDocument_number());
		setField(field, FIELD_DOCUMENT_DATE, printHead.getDocument_date());
		setField(field, FIELD_DOCUMENT_IBAN, printHead.getDocument_iban());
		setField(field, FIELD_DOCUMENT_PAYMENT, printHead.getDocument_payment());
		
	}
	private static void printRows(Set<PrintRow> rows,AcroFields field,PdfContentByte canvas) throws DocumentException,IOException{
		List<AcroFields.FieldPosition> positions = field.getFieldPositions(FIELD_ROW_CODE);
		Rectangle rect = positions.get(0).position; // In points:
		float left   = rect.getLeft();
        float bTop   = rect.getTop();
        float height = rect.getHeight();
        float top = bTop-height;
        List<AcroFields.FieldPosition> positions_description = field.getFieldPositions(FIELD_ROW_DECRIPTION);
        Rectangle rect_description = positions_description.get(0).position;
        List<AcroFields.FieldPosition> positions_um = field.getFieldPositions(FIELD_ROW_UM);
        Rectangle rect_um = positions_um.get(0).position;
        List<AcroFields.FieldPosition> positions_price = field.getFieldPositions(FIELD_ROW_PRICE);
        Rectangle rect_price = positions_price.get(0).position;
        List<AcroFields.FieldPosition> positions_amount = field.getFieldPositions(FIELD_ROW_AMOUNT);
        Rectangle rect_amount = positions_amount.get(0).position;
        List<AcroFields.FieldPosition> positions_taxrate = field.getFieldPositions(FIELD_ROW_TAXRATE);
        Rectangle rect_taxrate = positions_taxrate.get(0).position;
        List<AcroFields.FieldPosition> positions_qty = field.getFieldPositions(FIELD_ROW_QUANTITY);
        Rectangle rect_qty = positions_qty.get(0).position;
        float width  = rect_description.getWidth();
        for (Iterator<PrintRow> it = rows.iterator();it.hasNext();){
        	PrintRow row = it.next();
        	FontFactory.registerDirectories();
        	
        	Font font = FontFactory.getFont("Arial");
        	font.setSize(10);
        	Font font8 = FontFactory.getFont("Arial");
        	font8.setSize(6);
        	
        	String desc=row.getRow_description();
        	int length =desc.length();
        	if (length >= MAX_LENGTH_ROW_DESCRIPTION){
        		desc=desc.substring(0,MAX_LENGTH_ROW_DESCRIPTION);
        		length =desc.length();
        	}	
        	
        	BaseFont baseFont = font8.getBaseFont();
        	float w = baseFont.getWidthPoint(desc,6);
        	
        	float nLine=1;
        	if(w>width){
        		nLine=w/width;
        		nLine=new Float(Math.ceil(nLine));
        		
        	}
      	
        	ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,new Phrase(row.getRow_code(),font), left,top , 0);
        	ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,new Phrase(row.getRow_code(),font), left,top , 0);
        	
        	int divide=length/(int)nLine;
        	float topTem=top;
        	int tempStart=0;
	    	int tempEnd=divide;
	    	String temp="";
 

    	    for(int i=0;i<nLine;i++){  	    	    	    
    	    	if(tempEnd>length){	    		   	    	
    	    		temp=desc.substring(tempStart,length);
    	    	}else{
    	    		temp=desc.substring(tempStart,tempEnd);
    	    	}	
    	    	float wTemp = baseFont.getWidthPoint(temp,6);
    	    	
    	    	while(wTemp<width && tempEnd<length ){
    	    		tempEnd++;
    	    		temp=desc.substring(tempStart,tempEnd);
    	    		wTemp = baseFont.getWidthPoint(temp,6);
    	    	}
    	    	ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,new Phrase(temp,font8), rect_description.getLeft(),topTem,0);  	    	
    	    	tempStart=tempEnd;
    	    	tempEnd= tempEnd+divide;
    	    	topTem -= height+new Float("0.1");
    	    	
    	    }
        	
    	    
//    	    if (row.getRow_description().length() >= 100)
//    	    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,new Phrase(row.getRow_description().substring(0,100),font8), rect_description.getLeft(),top,0);
//    	    else
//    	    	ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,new Phrase(row.getRow_description(),font8), rect_description.getLeft(),top,0);
//    	    
    	    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,new Phrase(row.getRow_um(),font), rect_um.getLeft(),top , 0);
    	    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,new Phrase(row.getRow_um(),font), rect_qty.getLeft(),top , 0);
    	    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,new Phrase(row.getRow_price(),font), rect_price.getLeft(),top , 0);
    	    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,new Phrase(row.getRow_total(),font), rect_amount.getLeft(),top , 0);
    	    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,new Phrase(row.getRow_taxrate(),font), rect_taxrate.getLeft(),top , 0);
	    	 
	       	
	    	top=topTem;
	    	top -= height+DISTANCE_BETWEEN_ROW;
        }
    	
    	
    	
    	
    	
	}
	
	private static void printFooter(PrintDocument doc,AcroFields field) throws DocumentException,IOException{
		PrintFoot footer = doc.getFoot();
		setField(field,FIELD_FOOTER_NOTE,footer.getFooter_note());
		setField(field,FIELD_FOOTER_PRICE,footer.getFooter_price());
		setField(field,FIELD_FOOTER_PRICE_10,footer.getFooter_price10());
		setField(field,FIELD_FOOTER_PRICE_22,footer.getFooter_price22());
		setField(field,FIELD_FOOTER_PRICE_4,footer.getFooter_price4());
		setField(field,FIELD_FOOTER_TAX,footer.getFooter_tax());
		setField(field,FIELD_FOOTER_TAX_10,footer.getFooter_tax10());
		setField(field,FIELD_FOOTER_TAX_22,footer.getFooter_tax22());
		setField(field,FIELD_FOOTER_TAX_4,footer.getFooter_tax4());
		setField(field,FIELD_FOOTER_TOT,footer.getFooter_total());
		setField(field,FIELD_FOOTER_TOT_10,footer.getFooter_total10());
		setField(field,FIELD_FOOTER_TOT_22,footer.getFooter_total22());
		setField(field,FIELD_FOOTER_TOT_4,footer.getFooter_total4());
		setField(field,FIELD_FOOTER_HOLDING,footer.getFooter_withholding());		
	}
	
	private static int getDescriptionRows(String description){
		int rows = 0;
		rows =(int)Math.floor(description.length()/20);
		return rows;
	}
	private static Map<Integer,TreeSet<PrintRow>> getRowListPerPage(PrintDocument doc,int pages,int rowsPerPage){
		Map<Integer,TreeSet<PrintRow>> map = new HashMap<Integer, TreeSet<PrintRow>>();
		for(int i=0;i<pages;i++){
			TreeSet<PrintRow> rl = new TreeSet<PrintRow>();
			map.put(i, rl);
			int firstRowIndex = i*rowsPerPage;
			int endIndexRow = 0;
			if (firstRowIndex+rowsPerPage > doc.getRows().size()){
				endIndexRow =doc.getRows().size();
			}else{
				endIndexRow = firstRowIndex+rowsPerPage;
			}
			for (int index = firstRowIndex; index < endIndexRow;index++){
				rl.add((PrintRow)doc.getRows().toArray()[index]);
			}
		}
		return map;
	}
	private static void setField(AcroFields field,String fieldName,String value)throws DocumentException,IOException{
		if (field.getField(fieldName) != null)
			field.setField(fieldName, value);
	}
	public static void doMerge(List<String> list, OutputStream outputStream)
            throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte cb = writer.getDirectContent();
       
        for (String path : list) {
        	InputStream in = new FileInputStream(path);
            PdfReader reader = new PdfReader(in);
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                document.newPage();
                //import the page from source pdf
                PdfImportedPage page = writer.getImportedPage(reader, i);
                //add the page to the destination pdf
                cb.addTemplate(page, 0, 0);
            }
        }
       
        outputStream.flush();
        document.close();
        outputStream.close();
        
    }
	private static void deleteFiles(List<String> paths){
		 for (String path : paths) {
			 try {
				
				 File file = new File(path);
		        	
		    		if(file.delete()){
		    			System.out.println(file.getName() + " is deleted!");
		    		}else{
		    			System.out.println("Delete operation is failed.");
		    		}
		    	   
				} catch (Exception x) {
				    System.err.format("%s: no such" + " file or directory%n", path);
				}
		 }
		
	}
}
