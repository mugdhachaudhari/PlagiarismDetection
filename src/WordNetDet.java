import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.IStemmer;

public class WordNetDet {
	private String wnhome = "C:\\Program Files (x86)\\WordNet\\2.1";
	private Map<String, Integer> POSIdentifier = new HashMap<String, Integer>();
	private IDictionary dict;
	
	public WordNetDet(String POSFileName) {
		// construct the URL to the Wordnet dictionary directory
		String path = wnhome + java.io.File.separator + "dict";
		URL url = null;
		try{ 
			url = new URL("file", null, path); 
		} 
		catch(MalformedURLException e){ 
			e.printStackTrace(); 
		}
		if(url == null) 
			return;
// construct the dictionary object and open it
		dict = new Dictionary(url);
		try {
			dict.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
    	BufferedReader br = null;
    	String line = null;
    	try {
    		br = new BufferedReader(new FileReader(POSFileName));
			while ((line = br.readLine()) != null) {
				String[] lineDet = line.split(",");
				POSIdentifier.put(lineDet[0], Integer.parseInt(lineDet[2]));
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	    	try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public Map<String, Character> getSynset(String term, String pos) {
		Map<String, Character> synsetList = new HashMap<String, Character>();
		IIndexWord idxWord = null;
		int posNr = POSIdentifier.getOrDefault(pos, 9);
		switch(posNr) {
			case 0 :
				idxWord = dict.getIndexWord(term, POS.ADJECTIVE);
				break;
			case 1 :
				idxWord = dict.getIndexWord(term, POS.ADVERB);
				break;
			case 2 :
				
				break;
			case 3 :
				break;
			case 4 :
				idxWord = dict.getIndexWord(term, POS.ADJECTIVE);
				break;
			case 5 :
				idxWord = dict.getIndexWord(term, POS.NOUN);
				break;
			case 6 :
				break;
			case 7 :
				break;
			case 8 :
				idxWord = dict.getIndexWord(term, POS.VERB);
				break;
			default :
				break;
		}
		if (idxWord != null) {
//			int i = 0;
			for (IWordID wordID : idxWord.getWordIDs()) {
//				synsetList.put(wordID.getSynsetID().toString(), 'a');
				IWord word = dict.getWord(wordID);
				for (IWord w : word.getSynset().getWords()) {
					synsetList.put(w.getLemma(), 'a');
				}
//				System.out.println("Id = " + wordID);
//				System.out.println("Lemma = " + word.getLemma());
//				System.out.println("Synset = " + word.getSynset());
//				System.out.println("-----------------------");
//				i++;
//				if (i == 4) {
//					break;
//				}
			}
		}

		
		return synsetList;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WordNetDet wd = new WordNetDet(GenerateTextFromFile.returnPath("POS.csv"));
//		wd.getSynset("fail", "VBG");
//		System.out.println(wd.getSynset("allow", "VBG").toString());
		System.out.println("\n##########################\nDetails for word -- defeat\n##########################\n");
		System.out.println(wd.getSynset("defeat", "VBG").toString());
		System.out.println("\n##########################\nDetails for word -- fail\n##########################\n");
		System.out.println(wd.getSynset("fail", "VBG").toString());
	}

}
