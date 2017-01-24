import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.BeginIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreNLPProtos.Sentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.WordNetConnection;
import edu.stanford.nlp.util.CoreMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Object;


public class StanfordLemmatizer {

    protected StanfordCoreNLP pipeline;
    protected WordNetDet wordNet;
    protected int wordId;
	protected HashSet<String> stopSet = new HashSet<String>();
    public StanfordLemmatizer(String POSFileName, String stopListName) {
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        this.pipeline = new StanfordCoreNLP(props);
        wordNet = new WordNetDet(POSFileName);
        wordId = 0;
//      Ignore stop words
		try {
			BufferedReader stopBr = new BufferedReader(new FileReader(stopListName));
			String[] s = stopBr.readLine().split(" ");
			for (String sw : s) {
				stopSet.add(sw);
			}
			
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
        
        
        
    }
    
    private int getwordId() {
    	return wordId++;
    }

    public boolean isValidWord(String str) {
    	String regex = "~@#$%^&*:;<>.,/}{+";
    	if (stopSet.contains(str)) {
    		return false;
    	} else if (str.length() < 3) {
    		return false;
    	} else if (str.matches("[" + regex + "]+")) {
			return false;
		} else {
			return true;
		}
    }
    
    public DocSplit lemmatize(String documentText, Map<String, Integer> wordMap, Map<Integer, Map<Integer, Character>> synsetMap)
    {
        List<Map<Integer, Character>> doc = new ArrayList<Map<Integer, Character>>();
        List<String> sentences = new ArrayList<String>();
        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        this.pipeline.annotate(document);
        // Iterate over all of the sentences found
        // List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        int i = 0;
        for(CoreMap sentence: document.get(SentencesAnnotation.class)) {
        	//Comment later
//        	if (i == 10) {
//        		break;
//        	}
//        	i++;


        	
            // Iterate over all tokens in a sentence
        	Map<Integer, Character> words = new HashMap<Integer, Character>();
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the
                // list of lemmas
//            	System.out.print(token.get(PartOfSpeechAnnotation.class) + ", ");
//            	System.out.print(token.get(LemmaAnnotation.class) + " ; ");
            	
//            	Ignore word if it is a not valid
            	if (! isValidWord(token.get(LemmaAnnotation.class))) {
            		continue;
            	}
            	
            	if (! wordMap.containsKey(token.get(LemmaAnnotation.class))) {
            		wordMap.put(token.get(LemmaAnnotation.class), getwordId());
            	}
            	//Comment later
//            	System.out.print(token.get(LemmaAnnotation.class) + " , ");
            	words.put(wordMap.get(token.get(LemmaAnnotation.class)), 'a');
            	boolean IsPresentSynset = false;
            	
            	Map<String, Character> synsetWordList = wordNet.getSynset(token.get(LemmaAnnotation.class), token.get(PartOfSpeechAnnotation.class));
            	Map<Integer, Character> synsetMapList = new HashMap<Integer, Character>();;
//            	Check if synsetMap already contains synsets for given word
            	if (synsetMap.containsKey(wordMap.get(token.get(LemmaAnnotation.class)))) {
//            		If size is same then no need to modify
            		if (synsetMap.get(wordMap.get(token.get(LemmaAnnotation.class))).size() != synsetWordList.size()) {
            			synsetMapList = synsetMap.get(wordMap.get(token.get(LemmaAnnotation.class)));
//            			System.out.println("Hi");
            		} else {
            			IsPresentSynset = true;
//            			System.out.println("Hello");
            		}
            	}
            	
            	if (! IsPresentSynset) {
//            		System.out.println("Word is " + token.get(LemmaAnnotation.class));
//            		System.out.println("Synset word list is " + synsetWordList.keySet().toString());
            		for (String w : synsetWordList.keySet()) {
                    	if (! wordMap.containsKey(w)) {
                    		wordMap.put(w, getwordId());
                    	}
                		if (wordMap.get(w) != wordMap.get(token.get(LemmaAnnotation.class))) {
                			synsetMapList.put(wordMap.get(w), 'a');
                		}
            		}
                	
//                	System.out.println("Synset map list is " + synsetMapList.toString());
                	if (synsetMapList.size() != 0) {
                    	synsetMap.put(wordMap.get(token.get(LemmaAnnotation.class)), synsetMapList);
                	}
            	}
            	
            
            }
//            System.out.println();
            
            doc.add(words);
//        	System.out.println(sentence.toString());
        	sentences.add(sentence.toString());
        }
//        System.out.println(synsetMap.size());
//        System.out.println("Synset Map is  " + synsetMap.toString());
//        System.out.println("Word Map is  " + wordMap.keySet().toString());
        return new DocSplit(doc, sentences);
    }
    
//    public void getSynset() {
////	    WordNetConnection
////    	Morphology.stemStatic(word, tag)
//    }


    public static void main(String[] args) {
//        System.out.println("Starting Stanford Lemmatizer");
        String text = "permit permit permit allow";
//		String sourceFolder = "SourcePdf";
//		String suspiciousFolder = "SuspiciousPdf";
//		String sourceFileName = "Original.pdf";
//		String suspiciousFileName = "20100825.pdf";
//		String type = "pdf";
//        String text = GenerateTextFromFile.fileToText(sourceFolder + java.io.File.separator + sourceFileName, type);
        
        StanfordLemmatizer slem = new StanfordLemmatizer(GenerateTextFromFile.returnPath("POS.csv"), GenerateTextFromFile.returnPath("stopWordList"));
        Map<String, Integer> wordMap = new HashMap<String, Integer>();
        Map<Integer, Map<Integer, Character>> synsetMap = new HashMap<Integer, Map<Integer,Character>>();
//        slem.lemmatize(text.toLowerCase(), wordMap, synsetMap);
        DocSplit ds = slem.lemmatize(text.toLowerCase().replaceAll("[\n]+", " "), wordMap, synsetMap);
        System.out.println(ds.sentences.toString());
        System.out.println(ds.wordSentList.toString());
        System.out.println("WordMap " + wordMap.toString());
        System.out.println("Word Map size " + wordMap.size());
        System.out.println("Synset Map " + synsetMap.toString());
        System.out.println(ds.sentences.size());
        System.out.println(ds.wordSentList.size());
    }

}