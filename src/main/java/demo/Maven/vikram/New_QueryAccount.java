package demo.Maven.vikram;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

//import demo.testing;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.apache.kafka.clients.producer.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class New_QueryAccount extends Thread {
	String query;
	String p_sourceSystemName;
	String lastExeTime;

	ArrayList<String> p_accountResultList;

	public New_QueryAccount(String query, String lastExeTime, String p_sourceSystemName) {
		this.query = query;
		this.lastExeTime = lastExeTime;
		this.p_sourceSystemName = p_sourceSystemName;
	}

	ArrayList<String> GetAccountResultList() {
		return p_accountResultList;
	}

//	New_QueryAccount(String sourceSystemName) {
//		p_sourceSystemName = sourceSystemName;
//		//String lastExeTime = null;
//		//p_lastExeTime = lastExeTime;
//	}

	@Override
	public void run() {

		try {

			// XML and Mapper Properties:
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = null;
			org.w3c.dom.Document dom = null;
//			Object obj = new Object();
			db = dbf.newDocumentBuilder();
			dom = db.parse(new FileInputStream("configuration.xml"));
			Element docEle = dom.getDocumentElement();
//			NodeList sourceSystems_Account = docEle.getElementsByTagName("Properties");

			NodeList sourceSystems = docEle.getElementsByTagName("SourceSystem");
			// for loop
			int w;
			for (w = 0; w < sourceSystems.getLength(); w++) {
				Element sourceSystemname = (Element) sourceSystems.item(w);
				String systmName = sourceSystemname.getAttribute("Name");

				// System.out.println("Sysname inMapper"+ systmName);
				if (systmName.equalsIgnoreCase(p_sourceSystemName)) {
					NodeList Prop = sourceSystemname.getElementsByTagName("Properties");

					// System.out.println("here--"+ p_sourceSystemName);

					NodeList Queries = sourceSystemname.getElementsByTagName("Queries");
				//	System.out.println(p_sourceSystemName + "--" + Queries);

					int i;
					for (i = 0; i < Prop.getLength(); i++) {
						Element sourceSystem_acc = (Element) Prop.item(i);

						String driver = sourceSystem_acc.getAttribute("db.driver");
						String user = sourceSystem_acc.getAttribute("db.user");
						String url = sourceSystem_acc.getAttribute("db.url");
						String passwd = sourceSystem_acc.getAttribute("db.passwd");

						//System.out.println("URL" + url);

						Class.forName(driver);
						Connection con = DriverManager.getConnection(url, user, passwd);
						Statement st1 = con.createStatement();

						Element Queries_SQL = (Element) Queries.item(0);
						String queryMain = Queries_SQL.getAttribute(query);
						// System.out.println(query);
						// String LastModified = p_lastExeTime;
						// System.out.println("Last:"+LastModified);
						queryMain = queryMain.replace("%1", "2019-06-15T14:47:55");

						// queryMain = queryMain.replace("%1", lastExeTime);
						// System.out.println("quer:"+queryMain);

						ResultSet rs1 = st1.executeQuery(queryMain);
						ResultSetMetaData rsmd = rs1.getMetaData();
						int columnCount = rsmd.getColumnCount();
						p_accountResultList = new ArrayList<String>(columnCount);

						while (rs1.next()) {
							int l = 1;
							while (l <= columnCount) {
								p_accountResultList.add(rs1.getString(l++));

							}
						}
						System.out.println(p_accountResultList);
					}
				}
			}

		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}
}
