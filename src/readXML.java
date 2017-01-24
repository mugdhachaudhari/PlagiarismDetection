import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class readXML {

	private static File[] listFiles(String dirName, String filter) {
		File dir = new File(dirName);
		// Filter to select only data files
		FilenameFilter fileNameFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.toLowerCase().endsWith(filter)) {
					return true;
				}
				return false;
			}
		};
		return dir.listFiles(fileNameFilter);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sourceRef, obfuscation, suspiciousDoc, sourceLength, suspiciousLength;
		
		String inputFolder = "Suspicious";

		FileWriter fw;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(GenerateTextFromFile.returnPath("")
					+ "suspiciousList.csv");
			bw = new BufferedWriter(fw);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		File[] fList = listFiles(GenerateTextFromFile.returnPath(inputFolder),
				".xml");
		// File f = new File(GenerateTextFromFile.returnPath(inputFolder) +
		// java.io.File.separator + "suspicious-document00002.xml");
		try {
			for (File f : fList) {
				StringBuilder st;

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(f);
				doc.getDocumentElement().normalize();
				suspiciousDoc = doc.getDocumentElement().getAttribute(
						"reference");
				NodeList nList = doc.getElementsByTagName("feature");
				Node nNode = nList.item(1);
				Element eElement = (Element) nNode;
				if ("language".equals(eElement.getAttribute("name"))
						&& "en".equals(eElement.getAttribute("value"))) {
					for (int temp = 2; temp < nList.getLength(); temp++) {
						nNode = nList.item(temp);
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							eElement = (Element) nNode;
							if ("artificial-plagiarism".equals(eElement
									.getAttribute("name"))
									&& "false".equals(eElement
											.getAttribute("translation"))) {
								sourceRef = eElement
										.getAttribute("source_reference");
								obfuscation = eElement.getAttribute("obfuscation");
								sourceLength = eElement.getAttribute("source_length");
								suspiciousLength = eElement.getAttribute("this_length");
								st = new StringBuilder();
								st.append(suspiciousDoc).append(",").append(sourceLength).append(",")
										.append(sourceRef).append(",").append(suspiciousLength).append(",")
										.append(obfuscation).append("\n");
								bw.write(st.toString());
							}
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
