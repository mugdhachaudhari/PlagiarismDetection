import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.tartarus.snowball.ext.englishStemmer;



//Class to represent posting object
class Posting {
	private int docId;
	private int freq;
	
	public Posting(int docId, int freq) {
		this.docId = docId;
		this.freq = freq;
	}
	
	int getDocId() {
		return docId;
	}
	
	int getFreq() {
		return freq;
	}
	
	public String toString() {
		return String.format(docId + ":" + freq);
	}
}

//Class to represent Page table
class DocMap {
	public int docId;
	public String url;
	public int contentLength;
	
	public DocMap(int docId, String url, int contentLength) {
		this.docId = docId;
		this.url = url;
		this.contentLength = contentLength;
	}
}

//Main class with all the functionality
public class CollectData {
//	To print console level info
	private static boolean isDebug = true;
//  It will write both binary and ascii files
//	private static String mode = "ascii";
//Intial value for doc id and term id
	private static int DocId = 0;
	private static int termId = -1;
	private static int processedFiles = 0;
//	Stemmer
	private static englishStemmer stemmer = new englishStemmer();
//	Maximum number of files to be used to create subindex
	private static int maxSubIndexSize = 0;
// 	Total number of subindex files created at the end of program
	private static int partialIndexCnt = 0;
	private static ArrayList<DocMap> DocUrl = new ArrayList<DocMap>();
	private static HashMap<String, Integer> termMap = new HashMap<String, Integer>();
	private static HashSet<String> httpErrorSet = new HashSet<String>();
	private static HashSet<String> stopSet = new HashSet<String>();
	private static HashMap<Integer, Integer> termCount = new HashMap<Integer, Integer>();
//	Inverted Indexstructure
	private static HashMap<Integer, ArrayList<Posting>> invInd = new HashMap<Integer, ArrayList<Posting>>();
	
	static {
//		Read stop word list
		String FileName = GenerateTextFromFile.returnPath("stopWordList");
		try {
			BufferedReader stopBr = new BufferedReader(new FileReader(FileName));
			String[] s = stopBr.readLine().split(" ");
			for (String sw : s) {
				stopSet.add(sw);
			}
			
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	}
	
	
//	Initialize method will set maximum number of index files to be used to create subindex
//	Also it will initialize stop set to ignore all stop words
	private static void initialize(int subIndexSize) {
		maxSubIndexSize = subIndexSize;		

	}
	
//	Return byte representation of integer which is less than 128
	private static byte generateByte(int rem, int isSet) {
		byte b = (byte) rem;
		int set = (isSet << 7);
		return (byte)(b | set);
	}
	
//	After processing page, generate posting for each term found in the given page.
//	If that term is already present in inverted index then append it to existing posting array
	private static void generatePosting() {
		for (Entry<Integer, Integer> e : termCount.entrySet()) {
			Posting ps = new Posting(DocId, e.getValue());
			if (invInd.containsKey(e.getKey())) {
				ArrayList<Posting> psArray = invInd.get(e.getKey());
				psArray.add(ps);
				invInd.put(e.getKey(), psArray);
			} else {
				ArrayList<Posting> psArray = new ArrayList<Posting>();
				psArray.add(ps);
				invInd.put(e.getKey(), psArray);
			}
		}
	}
	
// Process parsed content, validate terms and increment term count
	public static int generateTermCount(String[] terms, boolean isSingleFlag, HashMap<String, Integer> termDetails) {
//		String[] terms = content.split("\\s+");
//		System.out.println(Arrays.toString(terms));
		boolean isFirst = true;
		String prev = new String();
		for (String term : terms) {
			if (stopSet.contains(term) || term.length() < 3 || term.isEmpty() || term.equals("")) {
				continue;
			} else {
				String stemTerm = getStemTerm(term);
				addTermCount(stemTerm, isSingleFlag, termDetails);
//				Generate Bigram
				if (! isFirst) {
					StringBuilder st = new StringBuilder();
					addTermCount(st.append(prev).append(",").append(stemTerm).toString(), isSingleFlag, termDetails);
				} else {
					isFirst = false;
				}
				prev = stemTerm;
			}
		}
		int sum = 0;
		if (! isSingleFlag) {
			for (int e : termCount.values()) {
				sum += (1*1*e*e);
			}
		} else {
			for (int e : termDetails.values()) {
				sum += (1*1*e*e);
			}
		}
		return sum;
	}
		
	private static void addTermCount(String stemTerm, boolean isSingleFlag, HashMap<String, Integer> termDetails) {
		if (! isSingleFlag) {
			Integer termId = getTermId(stemTerm);
			termCount.put(termId, (termCount.get(termId) == null)?1:(termCount.get(termId) + 1));
		} else {
			termDetails.put(stemTerm, (termDetails.get(stemTerm) == null)?1:(termDetails.get(stemTerm) + 1));
		}
	}
	
	public static String getStemTerm(String term) {
		stemmer.setCurrent(term);
		if (stemmer.stem()){
			return stemmer.getCurrent();
		}
		return term;
	}
	
//	Returns BufferedOutput stream for given file name
	private static BufferedOutputStream getBinaryWriteBuffer(String FileName) {
		try {
			OutputStream fileStream = new FileOutputStream(FileName);
//			OutputStream gzipStream = new GZIPOutputStream(fileStream);
			return new BufferedOutputStream(fileStream, 65536);
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
		return null;
	}
	
	
//	Convert given integer to vbyte code
	public static byte[] getByteCode(int num) {
		Stack<Byte> st = new Stack<Byte>();
		byte[] bytes;
		int isSet = 0;
		while (num > 0) {
			byte b = generateByte(num % 128, isSet);
			st.add(b);
			num = num / 128;
			isSet = 1;
		}
		bytes = new byte[st.size()];
		int i = 0;
		while (st.size() > 0) {
			bytes[i] = st.pop();
			i++;
		}
		return bytes;
	}
	
	private static Integer getNextDocId() {
		return ++DocId;
	}
	
	private static Integer getNextTermId() {
		return ++termId;
	}
	
	private static Integer getTermId(String term) {
		Integer termId = termMap.get(term); 
		if (termId == null) {
			termId = getNextTermId();
			termMap.put(term, termId);
		}
		return termId;
	}
	
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
		if (filter.equals("")) {
			return dir.listFiles();
		}
		return dir.listFiles(fileNameFilter);
	}
	
	private static void convertToText(String dirName, String type) {
		File[] list = listFiles(GenerateTextFromFile.returnPath(dirName), new String());
		for (File f : list) {
			try {
//				System.out.println(f.toString());
				String t = f.toString().substring(f.toString().lastIndexOf('.') + 1);
				if (t.equals(f.toString())) {
					t = "txt";
				}
				String[] content = GenerateTextFromFile.fileToTextNormalize(f.toString(), t.toLowerCase()).split("\\s+");
				termCount = new HashMap<Integer, Integer>();
				int freqSum = generateTermCount(content, false, new HashMap<String, Integer>());
//				System.out.println("Doc is " + f.toString());
//				System.out.println(termCount.toString());
//				System.out.println(termMap.toString());
				DocUrl.add(new DocMap(getNextDocId(), f.toString(), freqSum));
				generatePosting();
				processedFiles++;
				if (processedFiles >= maxSubIndexSize) {
					writeTempInvIndFile();
				}
			} catch(Exception e) {
				System.out.println("Error in parser");
			}
		}
		writeTempInvIndFile();
	}

	



//	Create bufferedstream for given file
	private static BufferedInputStream readFile(File fileName) {
		try {
			InputStream fileStream = new FileInputStream(fileName);
			InputStream gzipStream = new GZIPInputStream(fileStream);
			BufferedInputStream buffered = (new BufferedInputStream(gzipStream, 65536));
			return buffered;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println(e);
			System.exit(1);
			return null;
		}

	}


	private static BufferedWriter getBufferedWriter(String FileNamePrefix) {
		try {
			String FileName = GenerateTextFromFile.returnPath("PostingOutput") + java.io.File.separator + FileNamePrefix + partialIndexCnt;
			OutputStream fileStream = new FileOutputStream(FileName);
			OutputStream gzipStream = new GZIPOutputStream(fileStream);
			Writer encoder = new OutputStreamWriter(gzipStream);
			return new BufferedWriter(encoder, 65536);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Error in creating buffered writer");
			System.exit(1);
		}
		return null;
	}
	
//	Write sub inverted index to file and initialize all variables again
	private static void writeTempInvIndFile() {
		if (DocUrl.size() == 0) {
			return;
		}
		System.out.println("Writing subindex to file");
		try {
			BufferedOutputStream buffered = getBinaryWriteBuffer(GenerateTextFromFile.returnPath("PostingOutput") +java.io.File.separator + "invIndex_" + partialIndexCnt);
			
			StringBuilder du = new StringBuilder();
			BufferedWriter docBuffered = getBufferedWriter("docUrl_");
			for (DocMap obj : DocUrl) {
				docBuffered.write(obj.docId + "\t" + obj.url + "\t" + obj.contentLength + "\n");
			}
			docBuffered.close();
			StringBuilder inv = new StringBuilder();
			StringBuilder lex = new StringBuilder();
			StringBuilder lexAscii = new StringBuilder();
			int lexiconAsciiPos = 0;
			int lexiconPos = 0;
			BufferedWriter lexBuffered = getBufferedWriter("lexicon_");
			BufferedWriter lexAsciiBuffered = getBufferedWriter("lexiconAscii_");
			BufferedWriter invAsciiBuffered = getBufferedWriter("invIndexAscii_");
			
			ArrayList<String> termsList = new ArrayList<String>(termMap.keySet());
			Collections.sort(termsList);
			for (String term : termsList) {
				int termId = termMap.get(term);
				ArrayList<Posting> ps = invInd.remove(termId);
				lexBuffered.write(term + "\t" + lexiconPos + "\t" + ps.size() + "\n");
				lexAsciiBuffered.write(term + "\t" + lexiconAsciiPos + "\t" + ps.size() + "\n");
				lexiconAsciiPos++;
				if (ps == null ){
					System.out.println(term);
				}
				int prevDocId = 0;
				for (Posting p : ps) {
					int curDocId = p.getDocId();
					invAsciiBuffered.write(curDocId - prevDocId + ":" + p.getFreq() + ",");
					byte[] docByte = getByteCode(curDocId - prevDocId);
					buffered.write(docByte);
					byte[] freqByte = getByteCode(p.getFreq());
					buffered.write(freqByte);
					lexiconPos = lexiconPos + docByte.length + freqByte.length;
//					System.out.println("DocId " + Arrays.toString(docByte) + " Freq " + Arrays.toString(freqByte));
					prevDocId = curDocId;
				}
				invAsciiBuffered.write("\n");
			}
			buffered.close();
			lexBuffered.close();
			lexAsciiBuffered.close();
			invAsciiBuffered.close();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
		partialIndexCnt++;
		invInd = new HashMap<Integer, ArrayList<Posting>>();
		termMap = new HashMap<String, Integer>();
		termId = -1;
		DocId = 0;
		DocUrl = new ArrayList<DocMap>();
		processedFiles = 0;

	}
	

	public static void main(String[] args) {
		
		String type = "txt";
		
		Scanner sc = new Scanner(System.in);  
		System.out.println("Folder should be at path where java code resides.");
		System.out.println("Enter source folder name (just name of folder without path)");
		String inputFolder = sc.nextLine();
		sc.close();
//		Example
//		inputFolder = "Source";
		
		
		System.out.println("Start Time " + Calendar.getInstance().getTime());
		initialize(500);
		convertToText(inputFolder, type);

		
		System.out.println("End Time " + Calendar.getInstance().getTime());

		
	}

}
