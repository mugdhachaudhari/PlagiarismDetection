import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


public class GenerateTextFromFile  {
	
	
	public static String readFile(String fileName) throws IOException {
//		System.out.println("File name is " + fileName);
		byte[] data = Files.readAllBytes(Paths.get(fileName));
//		System.out.println(Arrays.toString(data));
		return (new String(data, Charset.defaultCharset()));
	}
	
	public static String fileToText(String s, String type) {
		try {
//		    return getText(s, type).replaceAll("\\r\\n", " ").toLowerCase();
			String st = getText(s, type).replaceAll("\\n", " ").toLowerCase().replaceAll("^ +| +$|( )+", " ");
			return st;
		} 
		catch (IOException e) {
		    System.out.println("Exception in converting file " + s + " of type " + type + " to Text");
			System.exit(1);
		}
		return new String();
	}
	
	public static String fileToTextNormalize(String s, String type) {
		return fileToText(s, type).replaceAll("[.]+", " ").replaceAll("[^.a-zA-Z0-9 ]+", "");
	}
	
	private static String getText(String filename, String type) throws IOException {
		if (type.equals("pdf")) {
		    PDDocument doc = PDDocument.load(new File(filename));
		    return new PDFTextStripper().getText(doc);
		} else if (type.equals("txt")) {
			return readFile(filename);
		}
		return new String();
    
	}
	
	
	// Return and create (if not exist) path of directory specified in argument
	public static String returnPath(String pathStr) {
		String FileName = System.getProperty("user.dir") + java.io.File.separator + pathStr;
		File Dir = new File(FileName);
		if (!Dir.exists()) {
			try {
				Dir.mkdir();
			} catch (Exception e) {
				System.out.println(e);
				System.out.println("Error in creating Directory " + pathStr);
				System.exit(1);
			}
		}
		return FileName;
	}
	


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sourceFolder = "Source";
		String suspiciousFolder = "Suspicious";
		String sourceFileName = "source-document00336.txt";
		String suspiciousFileName = "suspicious-document00217.txt";
		String type = "txt";
		String s = fileToText(sourceFolder + java.io.File.separator + sourceFileName, type);
//		System.out.println(s);
//			System.out.println(readFile(returnPath("Source" + java.io.File.separator + "source-document00001.txt")));
//			System.out.println(readFile(returnPath("Suspicious" + java.io.File.separator + "suspicious-document00001.txt")));
//			System.out.println(fileToText(returnPath("SourcePdf" + java.io.File.separator + "Original.pdf"), "pdf"));

		
//		pdfToText("F:\\MSCSNyuPoly\\Fall2016\\WebSearchEngine\\Project\\DatasetsResearchPapers\\Wei93");
//		System.out.println(pdfToText(new File("F:\\MSCSNyuPoly\\Fall2016\\WebSearchEngine\\Project\\DatasetsResearchPapers\\Wei93.pdf")));
//		String s = new String();
//		System.out.println("s is " + s);
//		s.replaceAll("[^a ]+", "");
//		System.out.println(s);
	}

}
