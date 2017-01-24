import java.awt.BorderLayout;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class PlagiarismHighlighter {
	protected static Highlighter.HighlightPainter painter;
	protected static Highlighter.HighlightPainter painter1;
	public static Highlighter highlighter;
	public static Highlighter highlighter1;

	public PlagiarismHighlighter() {
		// TODO Auto-generated constructor stub
		painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
		painter1 = new DefaultHighlighter.DefaultHighlightPainter(Color.PINK);
		highlighter = new DefaultHighlighter();
		highlighter1 = new DefaultHighlighter();

	}		
	
	public void displayDocs(List<String> text, List<String> text1, List<Integer> indexList, List<Integer> indexList1) 
			 throws FileNotFoundException, BadLocationException {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception evt) {
		}
		String fileText = "";
		String fileText1 = "";
		List<HighlightIndex> highlightIndexs = new ArrayList<HighlightIndex>();
		List<HighlightIndex> highlightIndexs1 = new ArrayList<HighlightIndex>();
		JFrame f = new JFrame("Plagiarism Detection");
		final JTextPane textPane = new JTextPane();
		textPane.setHighlighter(highlighter);
		textPane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Source",
				TitledBorder.CENTER, TitledBorder.TOP));
		final JTextPane textPane1 = new JTextPane();
		textPane1.setHighlighter(highlighter1);
		textPane1.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Suspicious",
				TitledBorder.CENTER, TitledBorder.TOP));

		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(textPane), new JScrollPane(textPane1));
		f.getContentPane().add(pane, BorderLayout.CENTER);
		
		for (int i = 0; i < indexList.size(); i++) {
			String s = text.get(indexList.get(i));
			String s1 = text1.get(indexList1.get(i));
			s = "(" + i + ") " + s;
			s1 = "(" + i + ") " + s1;
			text.set(indexList.get(i), s);
			text1.set(indexList1.get(i), s);

		}

		int counter = 0, start = 0, end = -1;
		for (String line : text) {
			int offset = line.length() + 1;
			start = end + 1;
			end += offset;
			if (indexList.contains(counter))
				highlightIndexs.add(new HighlightIndex(start, end));
			fileText += line + "\n";
			counter++;
		}

		// System.out.println(highlightIndexs);

		int counter1 = 0, start1 = 0, end1 = -1;
		for (String line1 : text1) {
			int offset1 = line1.length() + 1;
			start1 = end1 + 1;
			end1 += offset1;
			if (indexList1.contains(counter1))
				highlightIndexs1.add(new HighlightIndex(start1, end1));
			fileText1 += line1 + "\n";
			counter1++;
		}

		InputStream is = new ByteArrayInputStream(fileText.getBytes());
		InputStream is1 = new ByteArrayInputStream(fileText1.getBytes());

		try {
			textPane.read(is, null);
			textPane1.read(is1, null);
		} catch (Exception e) {
			System.out.println(e);
		}

		for (HighlightIndex highlightIndex : highlightIndexs) {
			// System.out.println(fileText.substring(highlightIndex.start,
			// highlightIndex.end));
			highlighter.addHighlight(highlightIndex.start, highlightIndex.end,
					painter);
		}

		for (HighlightIndex highlightIndex1 : highlightIndexs1) {
			// System.out.println(fileText1.substring(highlightIndex1.start,
			// highlightIndex1.end));
			highlighter1.addHighlight(highlightIndex1.start,
					highlightIndex1.end, painter1);
		}
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		// f.setSize(1000, 750);
		f.setVisible(true);
		pane.setResizeWeight(0.5);
		//pane.setDividerLocation(0.5f);
	}


	

	public static void main(String[] args) {

		PlagiarismHighlighter ph = new PlagiarismHighlighter();

		List<String> text = new ArrayList<String>();
		List<String> text1 = new ArrayList<String>();
		List<Integer> indexList = new ArrayList<Integer>();
		List<Integer> indexList1 = new ArrayList<Integer>();



		text.add("This is  test 1");
		text.add("This is li test 2");
		text.add("This is lin test 3aadasdasdasd");
		text.add("This is lin test 4aada");
		indexList.add(0);
		indexList.add(1);
		indexList.add(3);

		text1.add("This is l 1");
		text1.add("This is line test 2");
		text1.add("This is line t 3");
		text1.add("This is line test 4");
		indexList1.add(3);
		indexList1.add(2);
		indexList1.add(1);
		
		try {
			ph.displayDocs(text, text1, indexList, indexList1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		/*
		 * 
		 * Un-comment this block for reading contents from files
		 */
		// String fileName = "links.html";
		// String fileName1 = "links1.html";
		// InputStream file =
		// HighlightExample.class.getResourceAsStream(fileName);
		// InputStream file1 =
		// HighlightExample.class.getResourceAsStream(fileName1);

		// BufferedReader r = new BufferedReader(new InputStreamReader(file));
		// try {
		// String line;
		// while ((line=r.readLine()) != null) {
		// text.add(line);
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// BufferedReader r1 = new BufferedReader(new InputStreamReader(file1));
		// try {
		// String line1;
		// while ((line1=r1.readLine()) != null) {
		// text1.add(line1);
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// }



		


	}

}

class HighlightIndex {
	int start;
	int end;
	Highlighter.HighlightPainter painter;

	public HighlightIndex(int start, int end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Start: " + start + " , End: " + end;
	}

}