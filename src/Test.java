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

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.IStemmer;


public class Test {
	
	private static void print(String st) {
//		Read stop word list
		System.out.println(st);
	}

	public static void runExample(){
// construct the URL to the Wordnet dictionary directory
		String wnhome = "C:\\Program Files (x86)\\WordNet\\2.1";
		String path = wnhome + java.io.File.separator + "dict";
		URL url = null;
		try{ 
			url = new URL("file", null, path); 
		} 
		catch(MalformedURLException e){ 
			e.printStackTrace(); 
		}
		System.out.println(url);
		if(url == null) 
			return;
// construct the dictionary object and open it
		IDictionary dict = new Dictionary(url);
		try {
			dict.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
// look up first sense of the word "dog"
//		System.out.println(POS.);
		
		IIndexWord idxWord = dict.getIndexWord("require", POS.VERB);
		IWordID wordID = idxWord.getWordIDs().get(0);
		IWord word = dict.getWord(wordID);
		System.out.println("Id = " + wordID);
		System.out.println("Lemma = " + word.getLemma());
		System.out.println("Gloss = " + word.getSynset().getGloss());
		System.out.println("Synset = " + word.getSynset());
		System.out.println(wordID.getSynsetID().toString());

		

		idxWord = dict.getIndexWord("nice", POS.NOUN);

		wordID = idxWord.getWordIDs().get(0);
		word = dict.getWord(wordID);
		System.out.println("Id = " + wordID);
		System.out.println("Lemma = " + word.getLemma());
		System.out.println("Gloss = " + word.getSynset().getGloss());
		System.out.println("Synset = " + word.getSynset());
	}


	

	

	

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		pdfToText("Papers");
//		StringBuilder st = new StringBuilder();
//		print(st.append("Hi").append(" ").append("Hello").toString());
		
//		String s = "Produced by Andrea Ball and PG Distributed Proofreaders  Produced fromimages provided by the Library of Congress  Manuscript Division [TR: ***] = Transcriber Note[HW: ***] = Handwritten NoteSLAVE NARRATIVESA Folk History of Slavery in the United StatesFrom Interviews with Former SlavesTYPEWRITTEN RECORDS PREPARED BYTHE FEDERAL WRITERS' PROJECT1936-1938ASSEMBLED BYTHE LIBRARY OF CONGRESS PROJECTWORK PROJECTS ADMINISTRATIONFOR THE DISTRICT OF COLUMBIASPONSORED BY THE LIBRARY OF CONGRESSWASHINGTON 1941VOLUME VIIIMARYLAND NARRATIVESPrepared bythe Federal Writers' Project ofthe Works Progress Administrationfor the State of MarylandINFORMANTSBrooks  Lucy [TR: and Lafayette Brooks]Coles  CharlesDeane  James V Fayman  Mrs  M S Foote  ThomasGassaway  MenellisHammond  CarolineHarris  PageHenson  Annie YoungJackson  Rev  SilasJames  James CalhartJames  Mary Moriah Anne SusannaJohnson  PhillipJones  GeorgeLewis  AliceLewis  PerryMacks  RichardRandall  TomSimms  DennisTaylor  JimWiggins  JamesWilliams  Rezin (Parson)[TR: Interviews were stamped at left side with state name  date  and interviewer's name  These stamps were often partially cut off  Where month could not be determined [--] substituted  Interviewers' names reconstructed from other  complete entries ]Maryland[--]-23-37GuthrieAUNT LUCY [HW: BROOKS] References: Interview with Aunt Lucy and her son  Lafayette Brooks Aunt Lucy  an ex-slave  lives with her son  Lafayette Brooks  in a shackon the Carroll Inn Springs property at Forest Glen  Montgomery County Md To go to her home from Rockville  leave the Court House going east onMontgomery Ave  and follow US Highway No  240  otherwise known as theRockville Pike  in its southeasterly direction  four and one half milesto the junction with it on the left (east) of the Garrett Park Road This junction is directly opposite the entrance to the GeorgetownPreparatory School  which is on the west of this road  Turn left on theGarrett Park Road and follow it through that place and crossing RockCreek go to Kensington  Here cross the tracks of the B &O  R R  andparallel them onward to Forest Glen  From the railroad station in thisplace go onward to Forest Glen  From the railroad station in this placego onward on the same road to the third lane branching off to the left This lane will be identified by the sign  Carroll Springs Inn   Turnleft here and enter the grounds of the inn  But do not go up in front ofthe inn itself which is one quarter of a mile from the road  Instead where the drive swings to the right to go to the inn  bear to the leftand continue downward fifty yards toward the swimming pool  Lucy's shackis on the left and one hundred feet west of the pool  It is about elevenmiles from Rockville Lucy is an usual type of Negro and most probably is a descendant of lessremotely removed African ancestors than the average plantation Negroes She does not appear to be a mixed blood--a good guess would be that sheis pure blooded Senegambian  She is tall and very thin  and consideringher evident great age  very erect  her head is very broad  overhangingears  her forehead broad and not so receeding as that of the average Her eyes are wide apart and are bright and keen  She has no defect inhearing Following are some questions and her answers: Lucy  did you belong to the Carrolls before the war?   Nosah  I didnelib around heah den  Ise born don on de bay   How old are you?  Dunno sah  Miss Anne  she had it written down in her book  but she saidtwas too much trouble for her to be always lookin it up   (Her son Lafayette  says he was her eldest child and that he was born on theSevern River  in Maryland  the 15th day of October  1872  Supposing themother was twenty-five years old then  she would be about ninety now Some think she is more than a hundred years old)  Who did you belong to?  I belonged to Missus Ann Garner   Did she have many slaves?  Yassuh  She had seventy-five left she hadnt sold when the war ended   What kind of work did you have to do?  O  she would set me to pickin up feathers round de yaird  She had apowerful lot of geese  Den when I got a little bigger she had me set thetable  I was just a little gal then  Missus used to say that she wasgoing to make a nurse outen me  Said she was gwine to sen me to Baltimoto learn to be a nurse   And what did you think about that?  Oh; I thought that would be fine  but he war came befo I got big enoughto learn to be a nurse   I remebers when the soldiers came  I think they were Yankee soldiers De never hurt anybody but they took what they could find to eat and theymade us cook for them  I remebers that me and some other lil gals had aplay house  but when they came nigh I got skeered  I just ducked througha hole in the fence and ran out in the field  One of the soldiers seedme and he hollers 'look at that rat run'   I remebers when the Great Eastern (steamship which laid the Atlanticcable) came into the bay  Missus Ann  and all the white folks went downto Fairhaven wharf to see dat big shep   I stayed on de plantation awhile after de war and heped de Missus in dehouse  Den I went away   Ise had eight chillun  Dey all died and thisun and his brother(referring to Lafayette)  Den his brother died too  I said he ought terdied instid o his brother   Why?  Because thisun got so skeered when he was little bein carried on a hosthat he los his speech and de wouldt let me see im for two days  It wasa long time befor he learned to talk again   (To this day he has such animpediment of speech that it is painful to hear him make the effort totalk)  What did you have to eat down on the plantation  Aunt Lucy?  I hab mostly clabber  fish and corn bread  We gets plenty of fish downon de bay   When we cum up here we works in the ole Forest Glen hotel  MistahCharley Keys owned the place then  We stayed there after Mr  Cassidycome  (Mr  Cassidy was the founder of the National Park Seminary  aschool for girls)  My son Lafayette worked there for thirty five years Then we cum to Carroll Springs Inn  Maryland11/15/37RogersCHARLES COLES  Ex-slave Reference: Personal interview with Charles Coles at his home            1106 Sterling St   Baltimore  Md  I was born near Pisgah  a small village in the western part of CharlesCounty  about 1851  I do not know who my parents were nor my relatives I was reared on a large farm owned by a man by the name of Silas Dorsey a fine Christian gentleman and a member of the Catholic Church  Mr  Dorsey was a man of excellent reputation and character  was lovedby all who knew him  black and white  especially his slaves  He wasnever known to be harsh or cruel to any of his slaves  of which he hadmore than 75  The slaves were Mr  Dorsey's family group  he and his wife were veryconsiderate in all their dealings  In the winter the slaves wore goodheavy clothes and shoes and in summer they were dressed in fine clothes  I have been told that the Dorseys' farm contained about 3500 acres  onwhich were 75 slaves  We had no overseers  Mr  and Mrs  Dorsey managedthe farm  They required the farm hands to work from 7 A M  to 6:00 P M ;after that their time was their own";
//		String[] sArr = s.split("\\s");
//		for (int i = 0; i < sArr.length; i++) {
//			
//			System.out.println("Original " + sArr[i] + " Stemmed " + CollectData.getStemTerm(sArr[i]));
//		}
		
//		System.out.println("Original " + "require" + " Stemmed " + CollectData.getStemTerm("require"));
		runExample();
	}

}
