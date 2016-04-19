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
		pd.getHead().setCompany_name("TEST");
		pd.setRows(new TreeSet<PrintRow>());
		for (int i =0;i<19;i++){
			PrintRow pr = new PrintRow();
			pr.setRow_position(i);
			pr.setRow_code("code "+i);
			pd.getRows().add(pr);
		}
		pd.setFoot(new PrintFoot());
		pd.getFoot().setFooter_note("note");
		return pd;
	}
}
