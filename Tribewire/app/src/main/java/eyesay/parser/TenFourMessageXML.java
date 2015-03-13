package eyesay.parser;

import java.io.StringReader;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.ContentValues;

import com.eyesayapp.Utils.Debugger;

public class TenFourMessageXML extends TenFourParser {

	public Vector<ContentValues> tenFourMessages = new Vector<ContentValues>();
	public int totalMessage = 0;
	public boolean error = false;

	public TenFourMessageXML() {
		super();
		error = false;
	}

	@Override
	public void parceXML(String XMLResponse) {
		parceForError(XMLResponse);
		if (!isError()) {
			try {

				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(XMLResponse));

				Document doc = db.parse(is);
				NodeList nodes = doc.getElementsByTagName("messages");
				Element i = (Element) nodes.item(0);
				totalMessage = Integer.parseInt(i.getAttribute("total"));

				NodeList con = doc.getElementsByTagName("conversation");

				for (int k = 0; k < con.getLength(); k++) {
					ContentValues messages = new ContentValues();
					try {
						Element element2 = (Element) con.item(k);

						NodeList name;
						Element line;
						try {
							name = element2.getElementsByTagName("message_id");
							line = (Element) name.item(0);
							messages.put("message_id",
									getCharacterDataFromElement(line));

							name = element2.getElementsByTagName("sender");
							line = (Element) name.item(0);
							messages.put("sender",
									getCharacterDataFromElement(line));

							name = element2.getElementsByTagName("message_id");
							line = (Element) name.item(0);
							messages.put("message_id",
									getCharacterDataFromElement(line));

							name = element2.getElementsByTagName("sender");
							line = (Element) name.item(0);
							messages.put("sender",
									getCharacterDataFromElement(line));

						} catch (Exception e) {
						}
						try {
							name = element2.getElementsByTagName("receiver");
							line = (Element) name.item(0);
							messages.put("receiver",
									getCharacterDataFromElement(line));
						} catch (Exception e) {
						}
						try {
							name = element2
									.getElementsByTagName("message_type");
							line = (Element) name.item(0);
							messages.put("message_type",
									getCharacterDataFromElement(line));
						} catch (Exception e) {
						}
						try {
							name = element2.getElementsByTagName("file_url");
							line = (Element) name.item(0);
							messages.put("file_url",
									getCharacterDataFromElement(line));
						} catch (Exception e) {
						}
						
						try {
							//textmessage My Database column name so that is converting it  to lower case
							name = element2.getElementsByTagName("textMessage");
							line = (Element) name.item(0);
							messages.put("textMessage".toLowerCase(),
									getCharacterDataFromElement(line));
						} catch (Exception e) {
						}

						// try {
						// name = element2.getElementsByTagName("date_time");
						// line = (Element) name.item(0);
						//
						// String idate = getCharacterDataFromElement(line);
						//
						// String tmp[] = idate.split(" ");
						//
						// if (tmp.length == 2) {
						// String[] time = tmp[0].split(":");
						// String[] date = tmp[1].split("/");
						//
						// Calendar cal = Calendar.getInstance();
						// cal.set(Calendar.MONTH, Integer
						// .parseInt(date[0])-1);
						// cal.set(Calendar.DAY_OF_MONTH, Integer
						// .parseInt(date[1]));
						// cal.set(Calendar.YEAR, Integer
						// .parseInt(date[2]));
						// cal.set(Calendar.HOUR_OF_DAY, Integer
						// .parseInt(time[0]));
						// cal.set(Calendar.MINUTE, Integer
						// .parseInt(time[1]));
						// cal.set(Calendar.SECOND, Integer
						// .parseInt(time[2]));
						
						// TODO EDITED BY GARRY TODAY
						//messages.put("date_time", System.currentTimeMillis());

						try {
							name = element2
									.getElementsByTagName("date_time");
							line = (Element) name.item(0);
							messages.put("date_time",
									getCharacterDataFromElement(line));
						} catch (Exception e) {
						}
						
						// } else {
						// messages
						// .put("date_time", System.currentTimeMillis());
						// }
						// Date d1 = new Date(idate);
						// // String d = (String) new DateFormat().format(
						// // "yyyy-MM-d h:m:s", d1);
						// } catch (Exception e) {
						// Debugger.debugE("exception in date"
						// + e.getMessage());
						// }
						// try {
						// name = element2
						// .getElementsByTagName("conversation_Id");
						// line = (Element) name.item(0);
						// messages.put("conversation_Id",
						// getCharacterDataFromElement(line));
						// } catch (Exception e) {
						// }

						try {
							name = element2
									.getElementsByTagName("conversation_Id");
							line = (Element) name.item(0);
							messages.put("conversation_Id",
									getCharacterDataFromElement(line));
						} catch (Exception e) {
						}

					} catch (Exception e) {
						Debugger.debugE("in parser" + e.getMessage());
					} finally {
						tenFourMessages.add(messages);
					}
				}

			} catch (Exception e) {
			}

		} else {
			error = true;
		}
	}

	private String getCharacterDataFromElement(Element line) {
		Node child = ((Node) line).getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";

	}
}
