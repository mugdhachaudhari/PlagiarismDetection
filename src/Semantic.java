import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.text.BadLocationException;


public class Semantic {
	protected Map<String, Integer> wordMap = new HashMap<String, Integer>();
	protected Map<Integer, Map<Integer, Character>> synsetMap = new HashMap<Integer, Map<Integer,Character>>();
	protected double constPermissionThreshold = 0.65;
	protected double constVarThreshold = 0.15;
	
//	Comment later 
//	protected HashMap<String, String> synsetPairs = new HashMap<String, String>();
	
	
	public Semantic() {
		wordMap = new HashMap<String, Integer>();
		synsetMap = new HashMap<Integer, Map<Integer,Character>>();
	}
	
	
	public void printDoc(List<HashMap<String, Character>> input) {
		for(HashMap<String, Character> sentence : input) {
			System.out.println(sentence.keySet().toString());
		}
	}
	
	public void findSimilarity(DocSplit suspicious, DocSplit source, Map<Integer, Map<Integer, Double>> scorePairs, 
			Map<Integer, Integer> matchPairs) {
		
		
		int k = 0; //Start searching next sentence from this index in source document
		double mu = 0;
		double sumMu = 0.0;
		double simSuspSourceTemp = 0;
		double simSuspSourceMax = 0;
//		For each sentence in suspicious document find if there is any similar sentence present in source.
		for (int j = 0; j < suspicious.wordSentList.size(); j++) {
			Map<Integer, Character> suspSentence = suspicious.wordSentList.get(j);
			int maxSourceSentNr = -1;
			int maxSourceSentSize = 0;
			simSuspSourceMax = 0;
			Map<Integer, Double> inScorePair = new HashMap<Integer, Double>();
			for (int i = k; i < source.wordSentList.size(); i++) {
				Map<Integer, Character> sourceSentence = source.wordSentList.get(i);
				mu = 0;
				sumMu = 0.0;
				for (int w : suspSentence.keySet()) {
					mu = 0;
//					Check if exact word present in a sentence of source document 
					if (sourceSentence.containsKey(w)) {
//						Calculate Fuzzy score for exact match
						mu = 1;
						
					} else {
						Map<Integer, Character> synsetListIn = synsetMap.get(w); 
						if (synsetListIn == null) {
							mu = 0;
							continue;
						}
						boolean isSynsetPresent = false;
						for (int syn : synsetListIn.keySet()) {
							if (sourceSentence.containsKey(syn)) {
								isSynsetPresent = true;
								break;
							}
						}
//						If synset present then calculate fuzzy score accordingly
						if (isSynsetPresent) {
							mu = 0.5;
							
						} //Else Fuzzy score is 0
					}
					sumMu += mu;
				}
				simSuspSourceTemp = sumMu/suspSentence.size();
//				System.out.println("Temp score is " + simSuspSourceTemp + " Suspicious Sentence number is " + j + " Source Sentence number is " + i);

				if ((simSuspSourceTemp > simSuspSourceMax) || simSuspSourceMax == 0 || ((simSuspSourceTemp == simSuspSourceMax) && 
						Math.abs(suspicious.wordSentList.get(j).size() - source.wordSentList.get(i).size()) < 
						Math.abs(suspicious.wordSentList.get(j).size() - source.wordSentList.get(maxSourceSentNr).size()))) {
					simSuspSourceMax = simSuspSourceTemp;
					maxSourceSentNr = i;
					maxSourceSentSize = source.wordSentList.get(i).size();

				}
			}
			
			if (simSuspSourceMax >= constPermissionThreshold) {
////				Comment later
//				if (maxSourceSentNr == 9 || j == 9) {
//					System.out.println("Or Score is " + simSuspSourceMax + " Suspicious Sentence number is " + j + " Source Sentence number is " + maxSourceSentNr);
//					System.out.println("Max Score is  " + simSuspSourceMax);
//				} 
				if (matchPairs == null) {
					inScorePair.put(maxSourceSentNr, simSuspSourceMax);
				} else if (matchPairs != null) {
					if (scorePairs.containsKey(maxSourceSentNr)) {
						double sc = scorePairs.get(maxSourceSentNr).getOrDefault(j, 0.0);
						if (sc != 0.0 && (true || Math.abs(sc - simSuspSourceMax) <= constVarThreshold)) {
							matchPairs.put(maxSourceSentNr, j);
//							matchPairs.put(source.sentences.get(maxSourceSentNr), suspicious.sentences.get(j));
						}

					}
				}
			}
			
			if (matchPairs == null && inScorePair.size() > 0) {
				scorePairs.put(j, inScorePair);
			
			}
//			if (simSuspSourceMax >= 0.65) {
//				System.out.println("\nSuspicious score is " + simSuspSourceMax + 
//						" Suspicious Sentence number is " + j + " Max score Source Sentence number is " + maxSourceSentNr + "\n");
//				System.out.println("Suspicious Sentence " + suspicious.sentences.get(j));
//				System.out.println("Source Sentence " + source.sentences.get(maxSourceSentNr));
//				
//			}
			
		}

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String mode = "display";
		
		Scanner sc=new Scanner(System.in);  
		System.out.println("Enter source folder name ");
		String sourceFolder = sc.nextLine();
		System.out.println("Enter suspicious folder name ");
		String suspiciousFolder = sc.nextLine();
		System.out.println("Enter source file name ");
		String sourceFileName = sc.nextLine();
		System.out.println("Enter suspicious file name ");
		String suspiciousFileName = sc.nextLine();
		sc.close();

//		Example
//		sourceFolder = "Source"
//		suspiciousFolder  = "Suspicious"
//		sourceFileName = "source-document06800.txt"
//		suspiciousFileName = "suspicious-document03910.txt"
		
		
		Semantic sm = new Semantic();
		StanfordLemmatizer slem = new StanfordLemmatizer(GenerateTextFromFile.returnPath("POS.csv"), GenerateTextFromFile.returnPath("stopWordList"));
		DocSplit sourceList = new DocSplit(new ArrayList<Map<Integer,Character>>(), new ArrayList<String>());
		DocSplit suspiciousList = new DocSplit(new ArrayList<Map<Integer,Character>>(), new ArrayList<String>());

		String t = sourceFileName.substring(sourceFileName.lastIndexOf('.') + 1);
		if (t.equals(sourceFileName)) {
			t = "txt";
		}
		
		String sourceData = GenerateTextFromFile.fileToText(sourceFolder
				+ java.io.File.separator + sourceFileName, t);
		
		 t = suspiciousFileName.substring(suspiciousFileName.lastIndexOf('.') + 1);
		if (t.equals(suspiciousFileName)) {
			t = "txt";
		}
		
		String suspiciousData = GenerateTextFromFile.fileToText(
				suspiciousFolder + java.io.File.separator + suspiciousFileName,
				t);
	
		
		sourceList = slem.lemmatize(sourceData, sm.wordMap, sm.synsetMap);
		
		suspiciousList = slem.lemmatize(suspiciousData, sm.wordMap,
				sm.synsetMap);
	
		
		Map<Integer, Map<Integer, Double>> scorePairs = new HashMap<Integer, Map<Integer, Double>>();
		sm.findSimilarity(suspiciousList, sourceList, scorePairs, null);
		Map<Integer, Integer> matchPairs = new HashMap<Integer, Integer>();
		sm.findSimilarity(sourceList, suspiciousList, scorePairs, matchPairs);
		
		
		List<Integer> srcL = new ArrayList<Integer>();
		List<Integer> suspL = new ArrayList<Integer>();
		for (Map.Entry<Integer, Integer> e : matchPairs.entrySet()) {
			System.out.println(suspiciousList.sentences.get(e.getKey()) + " = " + sourceList.sentences.get(e.getValue()));
			suspL.add(e.getKey());
			srcL.add(e.getValue());
//			System.out.println(e.toString());
		}
		
		System.out.println("\n\n\n\nTotal Source Sentences " + sourceList.sentences.size() + " Total Suspicious Sentences " + suspiciousList.sentences.size());
		System.out.println("Matched Pairs " + matchPairs.size() + "\n\n\n\n");
			
		if(mode.equals("display")) {
			
			PlagiarismHighlighter ph = new PlagiarismHighlighter();
			try {
				ph.displayDocs(sourceList.sentences, suspiciousList.sentences, srcL, suspL);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

}
