import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.lang.reflect.Array;
import java.net.CacheResponse;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;
import java.util.zip.GZIPInputStream;

class MinHeapDisjunct {
	private DisjunctObj[] heap;
	private int size;
	private int maxSize;
	private static final int FRONT = 1;

	public MinHeapDisjunct(int maxSize){
	    this.heap = new DisjunctObj[maxSize+1];
	    heap[0] = null;
	    this.size = 0;
	}

	private int getParent(int position){
	    return position/2;
	}

	private int getLeftChild(int position){
	    return 2*position;
	}

	private int getRightChild(int position){
	    return 2*position+1;
	}

	private void swap(int position1, int position2){
		DisjunctObj temp = heap[position1];
	    heap[position1] = heap[position2];
	    heap[position2] = temp;
	}

	private boolean isLeaf(int position){

	    if(position > size/2){
	        return true;
	    }
	    return false;
	}

	public void insert(DisjunctObj data){
	    heap[++size] = data;
	    int currentItem = size;
	    while(size > 1 && getParent(currentItem) != 0 && heap[getParent(currentItem)].compareTo(heap[currentItem]) > 0 ){
	        swap(getParent(currentItem),currentItem);
	        currentItem = getParent(currentItem);
	    }
	}

	public DisjunctObj delete(){
		DisjunctObj itemPopped = heap[FRONT];
	    heap[FRONT] = heap[size--];
	    heapify(FRONT);
	    return itemPopped;
	}
	
	public DisjunctObj getFront() {
		return heap[FRONT];
	}
	
	public int getSize(){
		return size;
	}
	
	public void replaceFront(DisjunctObj data) {
		heap[FRONT] = data;
		heapify(FRONT);
	}

	public void heapify(int position){
	    if(isLeaf(position)){
	        return;
	    }
	    if (getRightChild(position) > size) {
	    	if ( (heap[position].compareTo(heap[getLeftChild(position)]) > 0)) {
	    		swap(position, getLeftChild(position));
	    	}
	    } else {
		    if ( (heap[position].compareTo(heap[getLeftChild(position)]) > 0) || (heap[position].compareTo(heap[getRightChild(position)]) > 0)){

		        if(heap[getLeftChild(position)].compareTo(heap[getRightChild(position)]) < 0){
		            swap(position , getLeftChild(position));
		            heapify(getLeftChild(position));
		        }
		        else{
		            swap(position , getRightChild(position));
		            heapify(getRightChild(position));
		        }
		    }
	    }
	}

}


class DisjunctObj implements Comparable<DisjunctObj> {
	public int did;
	public int termNr;
	
	public DisjunctObj(int did, int termNr) {
		this.did = did;
		this.termNr = termNr;
	}
	public int compareTo(DisjunctObj obj2) {
		if (this.did > obj2.did) {
			return 1;
		} else if (this.did < obj2.did) {
			return -1;
		} else {
			return 0;
		}
	}
}

class urlInfo {
	public String url;
	public int freqSum;
	
	public urlInfo(String url, int freqSum) {
		this.url = url;
		this.freqSum = freqSum;
	}
}

class resultUrlScore implements Comparable<resultUrlScore>{
//	Comment later - Can remove docId field
	public String url;
	public double similarity;
	public int docId;
	public ArrayList<termInfo> termFreq;
	
	public resultUrlScore(String url, double similarity, int docId, ArrayList<termInfo> termFreq) {
		this.url = url;
		this.similarity = similarity;
		this.docId = docId;
		this.termFreq = termFreq;
	}
	
	public int compareTo(resultUrlScore obj2) {
		if (this.similarity < obj2.similarity) {
			return -1;
		} else if (this.similarity > obj2.similarity) {
			return 1;
		} else return 0;
	}
}


class CacheObject {
	public ArrayList<Integer> lastDocId;
	public ArrayList<Integer> chunkSize;
	public byte[] blockBytes ; 
	public int ttlChunks;
	public int chunkStartPos;
	public int frequency;
	private int blockSize = 65536;
	
	public CacheObject(ArrayList<Integer> lastDocId,  ArrayList<Integer> chunkSize, byte[] blockBytes, int ttlChunks,
			int chunkStartPos) {
		this.frequency = 1;
		this.lastDocId = lastDocId;
		this.chunkSize = chunkSize;
		this.blockBytes = new byte[blockSize];
//		for (int i = 0)
		this.blockBytes = blockBytes;
		this.ttlChunks = ttlChunks;
		this.chunkStartPos = chunkStartPos;
	}
	
	public void incrementFreq() {
		this.frequency++;
	}
}

class FreqOrder implements Comparable<FreqOrder> {
	public int frequency;
	public int blockId;
	
	public FreqOrder(int blockId, int frequency) {
		this.blockId = blockId;
		this.frequency = frequency;
	}
	
	public void incrementFreq() {
		this.frequency++;
	}
	
	public int compareTo(FreqOrder obj2) {
		if (this.frequency > obj2.frequency) {
			return 1;
		} else if (this.frequency < obj2.frequency) {
			return -1;
		} else {
			return 0;
		}
	}
}

class PrevRefObj {
	public String term;
	public int termNr;
	
	public PrevRefObj(String term, int termNr) {
		this.term = term;
		this.termNr = termNr;
	}
}

class listPointer {
	public String term;
	public ArrayList<Integer> lastDocId;
	public ArrayList<Integer> size;
	public int ttlChunks;
	//Current position will point to start of chunk in which docPostingPos is pointed to
	public int curPosition; 
	public int curDocPostingPos;
	public int curFreqPostingPos;
	public int curPostingNrInChunk;
	public int ttlPostingsRead;
	public int currentChunkNr;
	public int prevDocId = 0;
	public byte[] blockBytes;
	public int uncompressFreqPostingInChunk;
	public int curBlockNr;
//	For disjunctive queries
	public ArrayList<Integer> disjunctDoc;
	public int prevDisjDocId = -1;
	
	public listPointer() {
		lastDocId = new ArrayList<Integer>();
		size = new ArrayList<Integer>();
	}
	
}



public class DetectPlagiarism {

	private static HashMap<Integer, urlInfo> docUrlMap = new HashMap<Integer, urlInfo>();
	private static HashMap<String, termInfo> termQuery = new HashMap<String, termInfo>();
	private static ArrayList<listPointer> lpList = new ArrayList<listPointer>();
	private static RandomAccessFile invIndBuffer;
	private static int maxDocId;
	private static double avgDocSize;
	private static int blockSize = 65536;
	private static String defaultType = "conjunctive";
	private static PriorityQueue<resultUrlScore> pq;
	private static boolean isDebug = true;
	private static HashMap<Integer, CacheObject> cacheBlocks = new HashMap<Integer, CacheObject>();
	private static PriorityQueue<FreqOrder> pqFreq = new PriorityQueue<FreqOrder>();
	private static int maxCacheSize = 200;
	
	
//	Lexicon
	
	private static char[] charList = new char[600000000];
	private static int[] startPos = new int[50000000];
	private static int[] lenTerm = new int[50000000];
	private static int[] prevRef = new int[50000000];
	private static int[] lexBlockNr = new int[50000000];
	private static int[] lexChunkNr = new int[50000000];
	private static int[] lexPostNr = new int[50000000];
	private static int[] lexDocFreq = new int[50000000];
	private static int activeRefPos = 0;
	private static int cnt = 0;
	private static ArrayList<PrevRefObj> refList = new ArrayList<PrevRefObj>();
	
	
	
	private static void addInPQ(double sim, int did, ArrayList<termInfo> termFreq) {
//		if (sim > 1) {
//			sim = 1;
//		}
		if (pq.size() >= 20) {
			if (pq.peek().similarity < sim) {
				pq.remove();
				pq.add(new resultUrlScore(docUrlMap.get(did).url, sim, did, termFreq));
			}	
		} else {
			if (sim >= 0.50) {
				pq.add(new resultUrlScore(docUrlMap.get(did).url, sim, did, termFreq));
			}
			
		}
		
		
//		if (sim >= 0.60) {
//			pq.add(new resultUrlScore(docUrlMap.get(did).url, sim, did, termFreq));
//		}

		
	}
	
	private static void closeList(ArrayList<listPointer> lpList) {
//		Deallocate all structures
		pq = new PriorityQueue<resultUrlScore>();
		lpList = new ArrayList<listPointer>();	
//		termQuery = new HashMap<String, termInfo>();
	}
	
	private static int getFrequency(listPointer lp) {
		int numberOfPostingToUncompress = lp.curPostingNrInChunk - 1 - lp.uncompressFreqPostingInChunk + 1;
		PostingNext nextFreqObj = null;
		for (int i = 0; i < numberOfPostingToUncompress; i++) {
			nextFreqObj = GenerateIndex.getNextId(lp.blockBytes, lp.curFreqPostingPos);
			lp.curFreqPostingPos = nextFreqObj.position;
			lp.uncompressFreqPostingInChunk++;
		}
		return nextFreqObj.nextId;
	}
	
	
	public static BufferedReader getGzReader(File f, int size) {
		try {
			InputStream fileStream = new FileInputStream(f);
			InputStream gzipStream = new GZIPInputStream(fileStream);
			Reader decoder = new InputStreamReader(gzipStream);
			return new BufferedReader(decoder, size);
		} catch(Exception e) {
			System.out.println("Error in creating gzip Buffered Reader");
			System.exit(1);
		}
		return null;		
	}
	
	private static double getMaxSim(int numerator, int freqSumInput, int did) {

		int freqSumDoc = docUrlMap.get(did).freqSum;
//		System.out.println("Input doc Frequency is " + freqSumInput);
//		System.out.println("Compared doc " + did + " Frequency is " + freqSumDoc + " numerator is " + numerator);
		//Comment later
//		System.out.println("MaxSim" + Math.max(((double) numerator)/freqSumInput, ((double)numerator)/freqSumDoc));
		return Math.max(((double) numerator)/freqSumInput, ((double)numerator)/freqSumDoc);
	}
	
	private static void getResults(String path, String type) {
		String t = path.substring(path.lastIndexOf('.') + 1);
		if (t.equals(path)) {
			t = "txt";
		}
		String[] terms = splitQuery(path, t.toLowerCase());
		HashMap<String, Integer> inputDetails = new HashMap<String, Integer>();
		int freqSum = CollectData.generateTermCount(terms, true, inputDetails);
//		System.out.println(inputDetails.toString());
		ArrayList<termInfo> termSort = new ArrayList<termInfo>();  //Input Term Frequency
		List<String> inputList = new ArrayList<String>(inputDetails.keySet());
		Collections.sort(inputList);
//		Comment later
//		System.out.println("Input Word Size " + inputList.size());
		lpList = new ArrayList<listPointer>();
		for (String term : inputList) {
			termInfo tInfo = searchTerm(term);
			if (tInfo != null) {
//				Add frequency
				termSort.add(new termInfo(term, inputDetails.get(term)));
				termQuery.put(term, tInfo);
				lpList.add(openList(term));
				
			}
		}
//		No need to sort terms by length of posting list for plagiarism
//		Collections.sort(termSort);


		if (termSort.size() == 0) {
			printResults(pq);
			return;
		}
		MinHeapDisjunct minHeap = new MinHeapDisjunct(termSort.size());
		int did = 0;
		ArrayList<termInfo> termFreq;
		for (int i = 0; i < termSort.size(); i++) {
			did = nextGEQ(lpList.get(i), 0);
			minHeap.insert(new DisjunctObj(did, i));
		}
		int prevDid = -1;
//		double bm25 = 0;
		int numerator = 0;
		termFreq = new ArrayList<termInfo>();
		DisjunctObj front;
		while (minHeap.getSize() > 1) {
			front = minHeap.getFront();
			if (front.did == prevDid) {
				numerator += calculateRf(front.termNr, termSort);
			} else {
				if (prevDid != -1) {
					addInPQ(getMaxSim(numerator, freqSum, prevDid), prevDid, termFreq);
				}
				numerator = 0;
				termFreq = new ArrayList<termInfo>();
				prevDid = front.did;
				numerator += calculateRf(front.termNr, termSort);

			}
			int d = nextGEQ(lpList.get(front.termNr), front.did + 1);
			if (d > maxDocId) {
				minHeap.delete();
			} else {
				minHeap.replaceFront(new DisjunctObj(d, front.termNr));
			}
		}

		front = minHeap.getFront();
		if (front.did == prevDid) {
			numerator += calculateRf(front.termNr, termSort);
			addInPQ(getMaxSim(numerator, freqSum, prevDid), prevDid, termFreq);
		} else {
			if (prevDid != -1) {
				addInPQ(getMaxSim(numerator, freqSum, prevDid), prevDid, termFreq);
			}
			termFreq = new ArrayList<termInfo>();
			numerator = 0;
			numerator += calculateRf(front.termNr, termSort);
			addInPQ(getMaxSim(numerator, freqSum, front.did), front.did, termFreq);
		}
		// fetch record from remaining term list
		int remainingTerm = front.termNr;
		prevDid = front.did;
		while (true) {
			prevDid = nextGEQ(lpList.get(remainingTerm), prevDid + 1);
			if (prevDid > maxDocId) {
				break;
			}
			termFreq = new ArrayList<termInfo>();
			numerator = 0;
			numerator += calculateRf(remainingTerm, termSort);
			addInPQ(getMaxSim(numerator, freqSum, prevDid), prevDid, termFreq);
		}

		printResults(pq);
		closeList(lpList);
	}
	
	private static int nextGEQ(listPointer lp, int k) {
		termInfo tDet = termQuery.get(lp.term);
		boolean stayInCurrentChunk = false;
		while (lp.ttlPostingsRead < tDet.docFreq) {
//			if number of postings remaining to read are less than 128 then stay in current chunk.
//			OR If k is less than lastDocId then stay in current chunk
			if (! stayInCurrentChunk) {
				if ((tDet.docFreq - lp.ttlPostingsRead <= 128 - lp.curPostingNrInChunk) || (k <= lp.lastDocId.get(lp.currentChunkNr - 1)) ) {
					stayInCurrentChunk = true;
				} 
			}

//			If finished reading chunk then move to next chunk
			if (! stayInCurrentChunk) {
//				System.out.println("Moving to next chunk");
				lp.ttlPostingsRead += (128 - lp.curPostingNrInChunk);  //If wrong result then add 1 later Change
				lp.prevDocId = lp.lastDocId.get(lp.currentChunkNr - 1); 
//				Finished reading block
				if (lp.currentChunkNr  >= lp.ttlChunks) {
//					If finished reading block then call next block	
					readBlock(tDet, lp);
				} else {
					lp.curPosition += (lp.size.get(2*(lp.currentChunkNr - 1)) + lp.size.get(2*(lp.currentChunkNr - 1) + 1));
					lp.curDocPostingPos = lp.curPosition;
					lp.curPostingNrInChunk = 0;
					lp.currentChunkNr++;
				}

				lp.uncompressFreqPostingInChunk = 0;
				lp.curFreqPostingPos = lp.curPosition + lp.size.get(2*(lp.currentChunkNr - 1));

				continue;
			}
			
//			Read docId from next posting
			PostingNext nextDocIdObj = GenerateIndex.getNextId(lp.blockBytes, lp.curDocPostingPos);
			lp.curDocPostingPos = nextDocIdObj.position;
			
//			Add difference in previous term to get current term and store it as previous term for next iteration
			lp.prevDocId += nextDocIdObj.nextId;
			lp.curPostingNrInChunk++;
			lp.ttlPostingsRead++;
//			Check if current found term is greater than or equal to k
			if (lp.prevDocId >= k) {
				return lp.prevDocId;
			}
			
			if (lp.ttlPostingsRead >= tDet.docFreq) {
				return maxDocId + 1;
			}
		}
//		If already read all postings of given term then return max id to stop process
		return maxDocId + 1;
	}

	private static listPointer openList(String term) {
		termInfo tInfo = termQuery.get(term);
//		read block
		listPointer lp = readBlock(tInfo, null);
		return lp;
	}
	
	private static void printResults(PriorityQueue<resultUrlScore> pq) {
		if (pq.size() == 0) {
			System.out.println("No result found");
		} else {
			Stack<resultUrlScore> st = new Stack<resultUrlScore>();
			while (pq.size() > 0) {
				st.add(pq.remove());
			}
			while (st.size() > 0) {
				resultUrlScore result = st.pop();
				System.out.println("URL : " + result.url + " Similarity " + result.similarity);
			}
		}
	}
	
	private static listPointer readBlock(termInfo tInfo, listPointer lp) {
		boolean isLpNull = false;
		if (lp == null) {
			isLpNull = true;
		}
		long blockStartPos;
		try {
			if (isLpNull) {
				blockStartPos = tInfo.blockId * (long) (blockSize);
				
				lp = new listPointer();
				lp.blockBytes = new byte[blockSize];
				lp.term = tInfo.term;
				lp.curPostingNrInChunk = tInfo.postingNr;
				lp.disjunctDoc = new ArrayList<Integer>();
				lp.curBlockNr = tInfo.blockId;
				
			} else {
				blockStartPos =  (lp.curBlockNr + 1) * (long)(blockSize);
				lp.blockBytes = new byte[blockSize]; //So that when last block Size would be less than blockSize there wont be garbage elements
				lp.currentChunkNr = 1;
				lp.lastDocId = new ArrayList<Integer>();
				lp.size = new ArrayList<Integer>();
				lp.curPostingNrInChunk = 0;
				lp.curBlockNr++;
			}
			if (cacheBlocks.containsKey(lp.curBlockNr)) {
//			if (1 == 2) {
				CacheObject blockObj = cacheBlocks.get(lp.curBlockNr);
				blockObj.incrementFreq();
				pqFreq.add(new FreqOrder(lp.curBlockNr, blockObj.frequency));
//				Set lp.curPosition
				lp.curPosition = blockObj.chunkStartPos;
				lp.ttlChunks = blockObj.ttlChunks;
				lp.lastDocId = blockObj.lastDocId;
				lp.size = blockObj.chunkSize;
				lp.blockBytes = blockObj.blockBytes;
				
			} else {
				
				invIndBuffer.seek(blockStartPos);
				invIndBuffer.read(lp.blockBytes);
				lp.curPosition = 0;
//				Read number of chunks present in the block
				PostingNext ttlChunkObj = GenerateIndex.getNextId(lp.blockBytes, lp.curPosition);
//				Current position will point to start of lastDocId array
				lp.curPosition = ttlChunkObj.position;
				lp.ttlChunks = ttlChunkObj.nextId;

//				Read lastDocId array
				for (int i = 0; i < lp.ttlChunks; i++) {
					PostingNext lastDocObj = GenerateIndex.getNextId(lp.blockBytes, lp.curPosition);
					lp.lastDocId.add(lastDocObj.nextId);
					lp.curPosition = lastDocObj.position;

				}
				
//				Read chunkSize array
				for (int i = 0; i < lp.ttlChunks; i++) {
//					Read docId chunk size
					PostingNext chunkSizeObj = GenerateIndex.getNextId(lp.blockBytes, lp.curPosition);
					lp.size.add(chunkSizeObj.nextId);
					lp.curPosition = chunkSizeObj.position;
//					Read freq chunk size
					chunkSizeObj = GenerateIndex.getNextId(lp.blockBytes, lp.curPosition);
					lp.size.add(chunkSizeObj.nextId);
					lp.curPosition = chunkSizeObj.position;
				}

				if (pqFreq.size() >= maxCacheSize) {
					FreqOrder fo = pqFreq.remove();
					cacheBlocks.remove(fo.blockId);					
				}
				CacheObject blockObj = new CacheObject(lp.lastDocId, lp.size, lp.blockBytes, lp.ttlChunks, lp.curPosition);
				cacheBlocks.put(lp.curBlockNr, blockObj);
				pqFreq.add(new FreqOrder(lp.curBlockNr, 1));
				

			}

			int skipBytes = 0;
			if (isLpNull) {
//				Go to chunk containing first posting of term. Multiplied by 2 to read both docChunSize and FreqChunkSize
				for (int i = 0; i < 2*(tInfo.chunkNr - 1); i++) {
					skipBytes += lp.size.get(i);
				}
				lp.curPosition += skipBytes;
				lp.currentChunkNr = tInfo.chunkNr;

//				Go to first posting of the term in current chunk
				PostingNext docPosObj = null;
				lp.curDocPostingPos = lp.curPosition;
				for (int i = 0; i < tInfo.postingNr; i++) {
					docPosObj = GenerateIndex.getNextId(lp.blockBytes, lp.curDocPostingPos);
					lp.curDocPostingPos = docPosObj.position;				
				}
			} else {
				lp.curDocPostingPos = lp.curPosition;
			}
			lp.curFreqPostingPos = lp.curPosition + lp.size.get(2*(lp.currentChunkNr - 1));
//			lp.uncompressFreqPostingInChunk = 0;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error in setting pointer position in readBlock");
			System.exit(1);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return lp;
	}
	
	private static void readFiles(String docUrlFile, String lexiconFile, String invIndFile) {
		
		BufferedReader docUrlBuffer = getGzReader(new File(docUrlFile), 33554432);

		String line;
		String[] splitLine;
		try {
//			Store docUrl Map in memory
			long sumContentLength = 0;
			int docId;
			urlInfo ui;
			while ((line = docUrlBuffer.readLine()) != null) {
				splitLine = line.split("\t");
				docId = Integer.parseInt(splitLine[0]);
				if (docId > maxDocId) {
					maxDocId = docId;
				}
				sumContentLength += Integer.parseInt(splitLine[2]);
				ui = new urlInfo(splitLine[1], Integer.parseInt(splitLine[2]));
				docUrlMap.put(docId, ui);
			}
			System.out.println("Read dictionary");
			System.out.println("Total Documents used " + maxDocId);
			avgDocSize = sumContentLength/docUrlMap.size();

			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in converting string to number in docUrlMap");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Error in reading file docUrlMap");
			System.exit(1);
		} finally {
			try {
				docUrlBuffer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		Comment later
		System.out.println("Start reading lexicon file");
		readLexicon(lexiconFile);

		System.out.println("Read lexicon");
//		Create buffered stream for inverted index
		try {
			invIndBuffer = new RandomAccessFile(invIndFile, "r");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in creating inverted index buffer");
			System.exit(1);
		}	
		
//		Initialize max priority queue
		pq = new PriorityQueue<resultUrlScore>();
		
	}
	
	private static void readLexicon(String lexiconFile) {
		BufferedReader lexBuffer = getGzReader(new File(lexiconFile), 67108864);
		String line;
		String[] splitLine;
//		Store lexicon in memory

		try {
			int j = 0;
			while((line = lexBuffer.readLine()) != null) {
				splitLine = line.split("\t");
//				System.out.println(splitLine[0]);
				for (int p = Math.min(activeRefPos, splitLine[0].length()); p >= 3; p--) {
					
					if (refList.get(p) != null && (splitLine[0].substring(0, p)).equals(refList.get(p).term)) {
//						System.out.println("Matched Substring " + splitLine[0].substring(0, p));
//						System.out.println("Assigned PrevRef as " + refList.get(p).termNr);
//						System.out.println("Value of P is " + p);
						prevRef[cnt] = refList.get(p).termNr;
						lenTerm[cnt] = splitLine[0].length() - p;
						
						break;
					}
				}
				if (splitLine[0].length() >= 3) {
					if (refList.size() <= splitLine[0].length()) {
						for (int p = refList.size(); p <= splitLine[0].length(); p++) {
							refList.add(p, null);
						}
					}
					refList.set(splitLine[0].length(), new PrevRefObj(splitLine[0], cnt));
					activeRefPos = splitLine[0].length();
				} else {
					activeRefPos = 0;
				}
				startPos[cnt] = j;
				if (prevRef[cnt] <= 0) {
					lenTerm[cnt] = splitLine[0].length();
					prevRef[cnt] = -1;
				}
				for (int p = splitLine[0].length() - lenTerm[cnt]; p < splitLine[0].length(); p++) {
					charList[j] = splitLine[0].charAt(p);
					j++;
				}
				lexBlockNr[cnt] = Integer.parseInt(splitLine[1]);
				lexChunkNr[cnt] = Integer.parseInt(splitLine[2]);
				lexPostNr[cnt] = Integer.parseInt(splitLine[3]);
				lexDocFreq[cnt] = Integer.parseInt(splitLine[4]);
				
				cnt++;
//				Comment later
//				if (cnt%50000 == 0) {
//					System.out.println("Count is " + cnt);
//				}
			}
			System.out.println("Total number of terms read in lexicon " + cnt);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in converting string to integer in lexicon buffer");
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in creating lexicon buffer");
			System.exit(1);
		} 

		finally {
			try {
				lexBuffer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static String[] splitQuery(String path, String type) {
//		File f = new File(path);
		return GenerateTextFromFile.fileToTextNormalize(path, type).split("\\s+");
	}
	
	private static int calculateRf(int termNr, ArrayList<termInfo> termSort) {
//		Here doc frequency refers to term frequency. Number of times that term occurred in input document
		int freq1 = termSort.get(termNr).docFreq;
		int freq2 = getFrequency(lpList.get(termNr));
		int numerator = 0;
		double e = 2.5;
		double closeness = e - (((double) freq1/freq2) + ((double) freq2/freq1));
		
		if (closeness > 0) {
			// 1 is value of alpha
			//Comment later
//			System.out.println(termSort.get(termNr).term);
//			System.out.println("Closeness greater than 0 for " + termSort.get(termNr).term + " score is " + closeness);
			numerator = (1 * 1 * freq1 * freq2);
		}
		return numerator;
	}
	
	private static termInfo searchTerm(String term) {
		int pos = searchTerm(term, 0, cnt - 1);
//		System.out.println("Position is " + pos);
		if (pos != -1) {
			return new termInfo(term, lexBlockNr[pos], lexChunkNr[pos], lexPostNr[pos], lexDocFreq[pos]);
		} else return null;
		
	}
	
	private static int searchTerm(String term, int low, int high) {
//		System.out.println("Low is " + low + "High is " + high);
		if (low > high) {
			return -1;
		}
		int mid = (low + high)/2;
//		System.out.println("Mid is " + mid);
		if (prevRef[mid] == -1) {
//			Check complete String char by char..If doesn't match then break and return whether to move right or left.
			int charPos = startPos[mid];
			for (int i = 0; i < Math.min(lenTerm[mid], term.length()); i++) {
				if (charList[charPos + i] > term.charAt(i)) {
					return searchTerm(term, low, mid - 1);
				} else if (charList[charPos + i] < term.charAt(i)) {
					return searchTerm(term, mid + 1, high);
				}
			}
			if (lenTerm[mid] < term.length()) {
				return searchTerm(term, mid + 1, high);
			} else if (lenTerm[mid] > term.length()) {
				return searchTerm(term, low, mid - 1);
			}
			return mid; //Get block nr, chunk nr and posting nr using mid position
		} else {
			ArrayList<Integer> lastPos = new ArrayList<Integer>();
			lastPos.add(mid);
			int prevRefVal = prevRef[mid];
			while (prevRefVal != -1) {
				lastPos.add(prevRefVal);
				prevRefVal = prevRef[prevRefVal];
			}
			int termJ = -1;
			for (int i = lastPos.size() - 1; i >= 0; i--) {
				for (int j = 0; j < lenTerm[lastPos.get(i)]; j++) {
					termJ++;
					if (termJ >= term.length()) {
//						System.out.println("Moved to left due to length criteria ");
						return searchTerm(term, low, mid - 1);
					}
					if (charList[startPos[lastPos.get(i)] + j] > term.charAt(termJ)) {
//						System.out.println("Moved to left");
						return searchTerm(term, low, mid - 1);
					} else if (charList[startPos[lastPos.get(i)]  + j] < term.charAt(termJ)) {
//						System.out.println("Moved to right");
						return searchTerm(term, mid + 1, high);
					}
				}
			}
			if (termJ < term.length() - 1) {
				return searchTerm(term, mid + 1, high);
			}
			return mid;
		}

		
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			String type = "txt";
			
			Scanner sc=new Scanner(System.in);  
			System.out.println("Folder should be at path where java code resides.");
			System.out.println("Enter folder name where final inverted index present (name of MergeOut_* folder with highest index)");
			String InputFolder = sc.nextLine();
			System.out.println("Enter suspicious folder name ");
			String suspiciousFolder = sc.nextLine();
			System.out.println("Enter suspicious file name ");
			String suspiciousFile = sc.nextLine();
			sc.close();
			//Examples
//			InputFolder = "MergeOut_1"
//			suspiciousFolder = "Suspicious"
//			suspiciousFile = "suspicious-document03910.txt";

			
			
			String SuspicousFile = GenerateTextFromFile.returnPath(suspiciousFolder) 
					+ java.io.File.separator + suspiciousFile;
			
			long startTime = System.currentTimeMillis();
			readFiles(GenerateTextFromFile.returnPath(InputFolder) + java.io.File.separator + "docUrl_0", 
					GenerateTextFromFile.returnPath(InputFolder) + java.io.File.separator + "lexicon_0",
					GenerateTextFromFile.returnPath(InputFolder) + java.io.File.separator + "invIndex_0");
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Time taken to read lexicon and dictionary in main memory " + totalTime);
	
			
			startTime = System.currentTimeMillis();
			getResults(SuspicousFile, type);
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("Total time to fetch results in milliseconds " + totalTime);
			System.out.println("\n\n###############################################\n\n");
		} finally {
			try {
				invIndBuffer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
