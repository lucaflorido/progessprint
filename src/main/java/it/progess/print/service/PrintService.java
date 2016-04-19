package it.progess.print.service;

import it.progess.model.PrintDocument;
import it.progess.model.PrintFoot;
import it.progess.model.PrintHead;
import it.progess.model.PrintRow;
import it.progess.print.dao.PrintDao;

import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
@Path("print")
public class PrintService {
	@Context
	ServletContext context;
	@Path("test")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
    public String test(){
		PrintDao.printTest(context);
		return "ok";
	}
	@Path("single")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public String singlePrint(String data){
		Gson gson = new Gson();
		PrintDocument pd = gson.fromJson(data, PrintDocument.class);
		String returnStr = PrintDao.printSingle(context, pd);
		return gson.toJson(returnStr);
	}
	
}
