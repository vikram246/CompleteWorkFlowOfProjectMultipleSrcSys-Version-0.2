package demo.Maven.vikram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.text.Format;
import java.text.SimpleDateFormat;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import demo.Maven.vikram.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author Vikram
 *
 */

public class testing_attributeMapper {

	public static void main(String args[]) {

		// public void PrepareCanonicalJSON(String sourceSystemName, Object accId) {
		// Loading from XML
		String sourceSystemName = "Siebel";
		Object accId = 1;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;

		org.w3c.dom.Document dom = null;
		try {
			db = dbf.newDocumentBuilder();

			// dom = db.parse(this.getClass().getResourceAsStream("config.xml"));
			dom = db.parse(new FileInputStream("configuration.xml"));
		} catch (Exception ex) {
			String error = ex.getMessage();
			System.out.println("The error is " + error);
		}

		try {
			Element docEle = dom.getDocumentElement();
			NodeList Prop = docEle.getElementsByTagName("Properties");
			Element Properties = (Element) Prop.item(0);

			String driver = Properties.getAttribute("db.driver");
			String user = Properties.getAttribute("db.user");
			String url = Properties.getAttribute("db.url");
			String passwd = Properties.getAttribute("db.passwd");
			String topicName = Properties.getAttribute("topicName");
			String key = Properties.getAttribute("key");
			String bootstarp = Properties.getAttribute("bootstrap");
			String host = Properties.getAttribute("host");
			String keyserial = Properties.getAttribute("keySerial");
			String valueserial = Properties.getAttribute("valueSerial");
			String keyserializer = Properties.getAttribute("keyserializer");
			String valueserializer = Properties.getAttribute("valueserializer");

			String acc_query = "";
			String con_query = "";
			String con_add_query = "";
			String acc_add_query = "";
//			String acc_WhereClause = "";
//			String con_WhereClause = "";
//			String add_WhereClause = "";

			Map<String, String> acc_fields = new HashMap<String, String>();
			Map<String, String> con_fields = new HashMap<String, String>();
			Map<String, String> acc_add_fields = new HashMap<String, String>();
			Map<String, String> con_add_fields = new HashMap<String, String>();
//			Element docEle = dom.getDocumentElement();

			// get a nodelist of elements
			NodeList sourceSystems = docEle.getElementsByTagName("SourceSystem");
			int i;

			for (i = 0; i < sourceSystems.getLength(); i++) {
				Element sourceSystem = (Element) sourceSystems.item(i);
				String systemName = sourceSystem.getAttribute("Name");
				if (systemName.equals(sourceSystemName)) {
					NodeList acc_Entities = sourceSystem.getElementsByTagName("RecordSnapshot_Queries");
					Element acc_Entity = null;

					for (int l = 0; l < acc_Entities.getLength(); l++) {
						acc_Entity = (Element) acc_Entities.item(l);
						acc_query = acc_Entity.getAttribute("SQL_Account");
						// acc_WhereClause = acc_Entity.getAttribute("AccountIdWhereClause");

						if (systemName.equals(sourceSystemName)) {
							NodeList acc_attribute = sourceSystem.getElementsByTagName("Account");
							Element acc_attri = null;

							for (int m = 0; m < acc_attribute.getLength(); m++) {
								acc_attri = (Element) acc_attribute.item(m);

								NodeList attributes = acc_attri.getChildNodes();
								for (int k = 0; k < attributes.getLength(); k++) {

									if (attributes.item(k).getNodeType() != Node.ELEMENT_NODE) {
										continue;
									}
									Element attribute = (Element) attributes.item(k);
									if (false == attribute.getNodeName().equals("Attribute")) {
										break;
									}
									String sourceName = attribute.getAttribute("SourceName");
									String targetName = attribute.getAttribute("TargetName");
									acc_fields.put(sourceName, targetName);

								}
								// for accountAddress
								NodeList acc_add_Entities = sourceSystem.getElementsByTagName("RecordSnapshot_Queries");
								Element acc_add_Entity = null;

								for (int r = 0; r < acc_add_Entities.getLength(); r++) {
									acc_add_Entity = (Element) acc_add_Entities.item(r);
									acc_add_query = acc_add_Entity.getAttribute("SQL_Account_Address");
									// System.out.println("Acc_Add:" + acc_add_query);
									// add_WhereClause = add_Entity.getAttribute("AddressIdWhereClause");

									if (systemName.equals(sourceSystemName)) {
										NodeList acc_add_attri = sourceSystem
												.getElementsByTagName("ChildEntity_Account_Address");
										Element acc_add_att = null;

										for (int n = 0; n < acc_add_attri.getLength(); n++) {
											acc_add_att = (Element) acc_add_attri.item(n);

											attributes = acc_add_att.getElementsByTagName("Attribute");
											for (int k = 0; k < attributes.getLength(); k++) {
												// System.out.println("Node Type:"+ attributes.item(k).getNodeType());
												if (attributes.item(k).getNodeType() != Node.ELEMENT_NODE) {
													continue;
												}
												Element attribute = (Element) attributes.item(k);
												if (false == attribute.getNodeName().equals("Attribute")) {
													break;
												}
												String sourceName = attribute.getAttribute("SourceName");
												String targetName = attribute.getAttribute("TargetName");
										System.out.println("Source:"+ sourceName);
										System.out.println("Target:"+targetName);
										System.out.println("Here--------------------------");
												acc_add_fields.put(sourceName, targetName);
											}

										}
										// System.out.println("test");
									} // for j acc_Entities
								}

								// For Contact
								NodeList con_Entities = sourceSystem.getElementsByTagName("RecordSnapshot_Queries");
								Element con_Entity = null;

								for (int j = 0; j < con_Entities.getLength(); j++) {
									con_Entity = (Element) con_Entities.item(j);
									con_query = con_Entity.getAttribute("SQL_Contact");
									// System.out.println("contact:" + con_query);
									// con_WhereClause = con_Entity.getAttribute("ContactIdWhereClause");

									if (systemName.equals(sourceSystemName)) {
										NodeList con_attri = sourceSystem.getElementsByTagName("ChildEntity_Contact");
										Element con_att = null;

										for (int n = 0; n < con_attri.getLength(); n++) {
											con_att = (Element) con_attri.item(n);

											attributes = con_att.getElementsByTagName("Attribute");
											for (int k = 0; k < attributes.getLength(); k++) {
												if (attributes.item(k).getNodeType() != Node.ELEMENT_NODE) {
													continue;
												}
												Element attribute = (Element) attributes.item(k);
												if (false == attribute.getNodeName().equals("Attribute")) {
													break;
												}
												String sourceName = attribute.getAttribute("SourceName");
												String targetName = attribute.getAttribute("TargetName");
												con_fields.put(sourceName, targetName);

											}
										}

										// for Contact_Address
										NodeList con_add_Entities = sourceSystem
												.getElementsByTagName("RecordSnapshot_Queries");
										Element con_add_Entity = null;

										// for loop here

										for (int p = 0; p < con_add_Entities.getLength(); p++) {
											con_add_Entity = (Element) con_add_Entities.item(p);
											con_add_query = con_add_Entity.getAttribute("SQL_Contact_Address");
											// System.out.println("contact:" + con_add_query);
											// con_WhereClause = con_Entity.getAttribute("ContactIdWhereClause");

											if (systemName.equals(sourceSystemName)) {
												// System.out.println("In Addresss---------------------------");
												NodeList con_add_attri = sourceSystem
														.getElementsByTagName("ChildEntity_Contact_Address");
												Element con_add_att = null;

												for (int n = 0; n < con_add_attri.getLength(); n++) {
													con_add_att = (Element) con_add_attri.item(n);

													attributes = con_add_att.getElementsByTagName("Attribute");
													for (int k = 0; k < attributes.getLength(); k++) {
														if (attributes.item(k).getNodeType() != Node.ELEMENT_NODE) {
															continue;
														}
														Element attribute = (Element) attributes.item(k);
														if (false == attribute.getNodeName().equals("Attribute")) {
															break;
														}
														String sourceName = attribute.getAttribute("SourceName");
														// System.out.println("SourceName:"+sourceName);
														String targetName = attribute.getAttribute("TargetName");
														// System.out.println("TargetName:"+targetName);
														con_add_fields.put(sourceName, targetName);

													}
												}
											}
										}

									} // for i sourceSystems
									Class.forName(driver);
									Connection con = DriverManager.getConnection(url, user, passwd);

									Statement st1 = con.createStatement();
									// System.out.println("AM Accun Query :" + acc_query + accID);

									ResultSet rs1 = st1.executeQuery(acc_query + accId);
									JSONObject json_account = new JSONObject();
									JSONObject json_contact = new JSONObject();
									ResultSetMetaData rsmd = rs1.getMetaData();
									JSONArray array_contact = new JSONArray();
									JSONArray array_contact_address = new JSONArray();
									JSONArray array_account_address = new JSONArray();

									// for Account
									while (rs1.next()) {
										int numColumns = rsmd.getColumnCount();
										for (int z = 1; z <= numColumns; z++) {
											String column_name = rsmd.getColumnName(z);
											// System.out.println("1---");
											json_account.put(acc_fields.get(column_name), rs1.getObject(column_name));
											// json_account.put((column_name), rs1.getObject(column_name));

										}
										System.out.println("JSON 1" + json_account.toString());
										System.out.println("this is = "+acc_add_query  );

										//Acc_Add
										Statement st5 = con.createStatement();
									//	ResultSet rs4 = st5.executeQuery(acc_add_query + rs1.getInt("accountid"));
										System.out.println("rs4 = "+acc_add_query + accId);
										ResultSet rs4 = st5.executeQuery(acc_add_query + accId);

										ResultSetMetaData rsmd_acc_Add = rs4.getMetaData();
										array_account_address = new JSONArray();

										while (rs4.next()) {
											int numColumns1 = rsmd_acc_Add.getColumnCount();
											// System.out.println("Col_count:" + numColumns1);
											JSONObject json_account_address = new JSONObject();
											for (int a = 1; a <= numColumns1; a++) {
												String colum_name = rsmd_acc_Add.getColumnName(a);
										System.out.println("col_name"+ colum_name);
										System.out.println("Key---->value"+ acc_add_fields.get(colum_name) +"----"+ rs4.getObject(colum_name));
										System.out.println("-----------------------------------------------");
										System.out.println("next:"+ acc_add_fields.get(colum_name));
									json_account_address.put(acc_add_fields.get(colum_name),rs4.getObject(colum_name));
												System.out.println("acc_add"+json_account_address);
											}
											array_account_address.add(json_account_address);
										}

										json_account.put("AccountAddress ", array_account_address);
										// System.out.println("AccountAddress :" + json_account.toString());

										// for contact
										Statement st2 = con.createStatement();

										// System.out.println("ContactResult" + con_query + accID);
										ResultSet rs2 = st2.executeQuery(con_query + accId);

										ResultSetMetaData rsmd1 = rs2.getMetaData();
										array_contact = new JSONArray();

										while (rs2.next()) {

											numColumns = rsmd1.getColumnCount();

											for (int z = 1; z <= numColumns; z++) {
												String column_name = rsmd1.getColumnName(z);
												json_contact.put(con_fields.get(column_name),
														rs2.getObject(column_name));

											}
											array_contact.add(json_contact);
											// json_account.put("Contact ", array_contact);

											// System.out.println("contant"+array_contact.toString());

											// for account_address
											Statement st3 = con.createStatement();

//											ResultSet rs3 = st3.executeQuery(con_add_query + rs2.getInt("accountid"));
											ResultSet rs3 = st3.executeQuery(con_add_query + accId);

											ResultSetMetaData rsmd2 = rs3.getMetaData();

											while (rs3.next()) {
												int numColumns1 = rsmd2.getColumnCount();
												JSONObject json_contact_address = new JSONObject();
												for (int a = 1; a <= numColumns1; a++) {
													String colum_name = rsmd2.getColumnName(a);
//													System.out.println("colum_name"+colum_name);
//													System.out.println("value:"+ con_add_fields.get(colum_name));
													json_contact_address.put(con_add_fields.get(colum_name),
															rs3.getObject(colum_name));
												}
												array_contact_address.add(json_contact_address);
											}
											json_contact.put("ContactAddress", array_contact_address);
											// Account_Address
										}

										json_account.put("Contact", json_contact);

										rs2.close();

									} // while rs1
									rs1.close();
									con.close();
									System.out.println("FINAL JSON:" + json_account.toString());

									XMLCreator createXML = new XMLCreator();
									createXML.createXML(json_account.toString(), accId);

									String value = json_account.toString();
									Date date = new Date();

									Format formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
									PrintWriter outFile = new PrintWriter(
											new FileWriter("AccID_" + accId + "_" + formatter.format(date) + ".json"));
									PrintWriter printWriter = new PrintWriter(outFile);
									printWriter.print(value);
									printWriter.close();

									// Kafka Producer:
									Properties props = new Properties();
									props.put(bootstarp, host);
									props.put(keyserial, keyserializer);
									props.put(valueserial, valueserializer);

									Producer<String, String> producer = new KafkaProducer<String, String>(props);
									ProducerRecord<String, String> record = new ProducerRecord<String, String>(
											topicName, key, value);
									producer.send(record);
									producer.close();
									System.out.println(
											"############### Canonical JSON pushed to topic successfully ################");

								}
							}
						}
					}
				}
			}
		}

		catch (

		Exception e) {
			e.printStackTrace();

		}

	}
}
