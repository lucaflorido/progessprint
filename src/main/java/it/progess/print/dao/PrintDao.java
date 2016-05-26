package it.progess.print.dao;

import it.progess.model.PrintDocument;
import it.progess.model.PrintFoot;
import it.progess.model.PrintHead;
import it.progess.model.PrintRow;
import it.progess.print.engine.PrintEngine;

import java.io.File;
import java.util.TreeSet;

import javax.servlet.ServletContext;

public class PrintDao {
	public static void printTest(ServletContext context){
		PrintEngine.createPDF(getTestDocument(), context.getRealPath("report/testPDF.pdf"), context.getRealPath("report/resultPDF.pdf"));
	}
	public static String printSingle(ServletContext context,PrintDocument pd){
		PrintEngine.createPDF(pd, context.getRealPath("report/testPDF.pdf"), context.getRealPath("report/resultPDF.pdf"));
		return "http://localhost:8080/ProgessPrint/report/resultPDF.pdf";
	}
	private static PrintDocument getTestDocument(){
		PrintDocument pd = new PrintDocument();
		pd.setHead(new PrintHead());
		PrintHead head = pd.getHead();
		head.setCompany_name("TEST");
		head.setCompany_address("Address1");
		head.setCompany_fax("FAX");
		pd.setRows(new TreeSet<PrintRow>());
		//for (int i =0;i<19;i++){
		for (int i =0;i<9;i++){
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
}
