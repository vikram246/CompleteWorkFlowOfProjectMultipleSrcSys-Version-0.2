package demo.Maven.vikram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class New_SchedulerMain {
	public static void main(String args[]) throws IOException, InterruptedException, ParserConfigurationException, SAXException {

		String Source_System;
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the SourceSystem : ");
		Source_System = sc.next();
		run(Source_System);

		return;

	}

	public static void run(String Source_System) throws InterruptedException, IOException, ParserConfigurationException, SAXException {


		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		org.w3c.dom.Document dom = null;
		db = dbf.newDocumentBuilder();
		dom = db.parse(new FileInputStream("configuration.xml"));
		Element docEle = dom.getDocumentElement();
		NodeList sourceSystems_Account = docEle.getElementsByTagName("Properties");
		Element Queries_SQL = (Element) sourceSystems_Account.item(0);
		Integer timer = Integer.valueOf(Queries_SQL.getAttribute("scheduler_Timer"));

		for (;;) {
			System.out.println("Execution in Main Thread....");
			Thread.sleep((timer));

			New_ScheduledTask st = new New_ScheduledTask(Source_System); // Instantiate ScheduledTask class
			//System.out.println("SS: "+ Source_System);
			st.run();

			System.out.println("Run Completed");
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ms");
			String currentExec = formatter.format(date);
			System.out.println("Date Format with yyyy-MM-dd HH:mm:ss.ms : " + currentExec);

			FileWriter fileWriter = new FileWriter("MyLog.txt");
			fileWriter.write(currentExec);
			fileWriter.close();

		}
	}
}
