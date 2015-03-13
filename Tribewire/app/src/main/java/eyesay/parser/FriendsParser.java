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

public class FriendsParser extends TenFourParser {

	public Vector<ContentValues> vFriends = new Vector<ContentValues>();
	public boolean error =false; 
	public FriendsParser() {
		// TODO Auto-generated constructor stub
		super();
		error = false;
	}

	@Override
	public void parceXML(String XMLResponse) {
		// TODO Auto-generated method stub
		parceForError(XMLResponse);
		if (!isError()) {   
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(XMLResponse));

				Document doc = db.parse(is);
				NodeList nodes = doc.getElementsByTagName("user");

				for (int k = 0; k < nodes.getLength(); k++) {
					ContentValues friends = new ContentValues();
					try {
						Element element2 = (Element) nodes.item(k);

						NodeList name;
						Element line;
						try {
							name = element2.getElementsByTagName("name");
							line = (Element) name.item(0);
							friends.put("name",
									getCharacterDataFromElement(line));

							name = element2.getElementsByTagName("number_sent");
							line = (Element) name.item(0);
							friends.put("number_sent",
									getCharacterDataFromElement(line));
						} catch (Exception e) 
						{
						}
						try {
							name = element2.getElementsByTagName("reg_number");
							line = (Element) name.item(0);
							friends.put("reg_number",
									getCharacterDataFromElement(line));
						} catch (Exception e) {
							// TODO: handle exception
						}
						try {
							name = element2.getElementsByTagName("reg_email");
							line = (Element) name.item(0);
							friends.put("reg_email",
									getCharacterDataFromElement(line));
						} catch (Exception e) {
							// TODO: handle exception
						}

					} catch (Exception e) {
						Debugger.debugE("in parser" + e.getMessage());
						// TODO: handle exception
					} finally {
						vFriends.add(friends);
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		else
		{
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
