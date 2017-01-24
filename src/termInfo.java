
public class termInfo implements Comparable<termInfo> {
	public String term;
	public int blockId;
	public int chunkNr;
	public int postingNr;
	public int docFreq;
	
	public termInfo(String term, int blockId, int chunkNr, int postingNr, int docFreq) {
		this.term = term;
		this.blockId = blockId;
		this.chunkNr = chunkNr;
		this.postingNr = postingNr;
		this.docFreq = docFreq;
	}
	
//	Could be used in two ways - one to store term  and it's docFreq (in how many different documents
//	term occurred) second way - store term and its frequency in particular document
	public termInfo(String term, int docFreq) {
		this.term = term;
		this.docFreq = docFreq;
	}
	
	public int compareTo(termInfo obj2) {
		if (this.docFreq > obj2.docFreq) {
			return 1;
		} else if (this.docFreq < obj2.docFreq) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public String toString() {
		return this.term + " : " + this.docFreq;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
