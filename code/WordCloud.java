import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;


public class WordCloud extends Canvas {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String[] singulars = {"child", "mouse", "calf", "leaf", "cactus"};
	private static final String[] plurals = {"children", "mice", "calves", "leaves", "cacti"};
	private static final String[] sEnding = {"across", "las", "vegas", "apparatus", "brass", "business", "canvas", "glass", "grass", "kiss", "loss", "mass", "news", "process", "chess", "dress", "scissors", "trousers", "bus", "was", "is", "this"};
	private static String[] orderedWords;

	public static void main(String[] args) {
		orderedWords = genCloud(System.getProperty("user.dir")+ "\\alicetext.txt");
		JFrame frame = new JFrame("Word Cloud");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Canvas canvas = new WordCloud();
        canvas.setSize(1920, 1080);
        frame.add(canvas);
        //frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        //frame.setUndecorated(true);
        frame.setVisible(true);
        Graphics2D g = (Graphics2D) canvas.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
        	    RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
        	    RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        painting(g, 1920, 1080);
	}
	
	public static void painting( Graphics g, int w, int h ) {
		System.out.println("len: " + orderedWords.length);
		boolean [] [] filled = new boolean [h][w];
		for(int y = 0; y < filled.length; y++) {
			for(int x = 0; x < filled[0].length; x++) {
				filled[y][x] = false;
			}
		}
		for(int i = 0; i < orderedWords.length; i++) {
			boolean found = false;
			//System.out.println("len: " + orderedWords.length);
			int size = (int)(300.0/((i*1.0/6.0)+1)) + 9; 
			g.setFont( new Font( "TimesRoman", Font.PLAIN, size) );
			//System.out.println(orderedWords[i] + " of size " + size);
			int x = 0;
			int y = 0;
			FontMetrics fm = g.getFontMetrics();
			while(!found) {
				x = (int) (Math.random() * (w - fm.stringWidth(orderedWords[i])));
				y = (int) (Math.random() * (h -(fm.getAscent()- fm.getDescent()))) + (fm.getAscent()- fm.getDescent());
				found = true;

				//System.out.println(x+ " ,"+ y);
				//System.out.println((x + fm.stringWidth(orderedWords[i]))+ " ,"+ (y - fm.getHeight()));
				for(int y1 = y; y1 > y - (fm.getAscent()- fm.getDescent()); y1-- ) {
					for(int x1 = x; x1 < x + fm.stringWidth(orderedWords[i]); x1++ ) {
						 if(filled[y1][x1]  == true ) {
							 found = false;
							 break;
						 }
					}
				}
			}
			for(int y1 = y; y1 > y - (fm.getAscent()- fm.getDescent()); y1-- ) {
				for(int x1 = x; x1 < x + fm.stringWidth(orderedWords[i]); x1++  ) {
					 filled[y1][x1]  = true;
				}
			} 
			
			System.out.println("Size of " + i + " is: " + size);
			g.drawString(orderedWords[i], x, y);
		}
		System.out.println(">>>>>>>>>> DONE! <<<<<<<<<<");
	}
	
	private static String[] genCloud(String fileN ) {
		File f = new File(fileN);
		Scanner s = null;
		try {
			s = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new String[0];
		}
		
		Map<String,Integer> m = new TreeMap<String, Integer>();
		
		while(s.hasNextLine()) {
			String line = s.nextLine();
			String [] words = line.split("\\s+");
			for( int i = 0; i < words.length; i++ ) {
				words[i] = words[i].replaceAll("[^\\w_]", "" );
				words[i] = words[i].replaceAll("[^\\D]", "" );
				words[i] = words[i].toLowerCase();
				int p = -1;
				for( int j = 0; j < plurals.length; j++) {
					if( words[i].equals(plurals[j])) {
						p = j;
						break;
					}
				}
				int endS = -1;
				for( int j = 0; j < sEnding.length; j++) {
					if( words[i].equals(sEnding[j])) {
						endS = j;
						break;
					}
				}
				
				if(words[i].equals("")) {
					//all numbers
				}
				//already singular and already in map
				else if( m.containsKey(words[i])) {
					m.replace(words[i], m.get(words[i]) + 1 );
				}
				//is a stored irregular plural
				else if( p >= 0 ) {
					if( m.containsKey(singulars[p])) {
						m.replace(singulars[p], m.get(singulars[p]) + 1 );
					}
					else {
						m.put(singulars[p], 1);
					}
				}
				//-ies ending
				else if( words[i].length() > 3 && words[i].substring(words[i].length() - 3).equals("ies") ) {
					String newWord = words[i].substring(0, words[i].length() - 3).concat("y");
					if( m.containsKey( newWord ) ) {
						m.replace(newWord, m.get(newWord) + 1 );
					}
					else {
						m.put(newWord, 1);
					}
				}
				//es
				else if( words[i].length() > 2 && words[i].substring(words[i].length() - 2).equals("es") ) {
					String newWord = words[i].substring(0, words[i].length() - 2);
					if( m.containsKey( newWord ) ) {
						m.replace(newWord, m.get(newWord) + 1 );
					}
					else {
						m.put(newWord, 1);
					}
				}
				//s
				else if(  words[i].length() > 1 && words[i].substring(words[i].length() - 1).equals("s") && endS < 0 ) {
					String newWord = words[i].substring(0, words[i].length() - 1 );
					if( m.containsKey( newWord ) ) {
						m.replace(newWord, m.get(newWord) + 1 );
					}
					else {
						m.put(newWord, 1);
					}
				}
				//assume singular and not mapped
				else {
					m.put(words[i], 1);
				}
			}
			
		}
		//System.out.println("All words gotten");
		List<Pair> entries = new ArrayList<Pair>();
		for(Entry<String, Integer> e: m.entrySet()) {
			entries.add( new Pair(e.getKey(), e.getValue()) );
		}
		//System.out.println("All Entrees gotten");
		List<Pair> sorted = mergeSort(entries);
		//System.out.println("All e sorted");
		/*
		Map<String, Integer> sortedByCount = m.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		*/
		String[] sortedArr = new String[sorted.size()];
		int i = 0;
		for(Pair e: sorted) {
			sortedArr[i++] = e.getKey();
			System.out.println(e.getKey() + ": " + e.getValue());
		}
		s.close();
		return sortedArr;
	}
	
	private static List<Pair> mergeSort(List<Pair> list){
		//System.out.println(list.toString());
		if(list.size() == 1 || list.size() == 0) return list;
		int m = list.size()/2;
		List<Pair> l = mergeSort(list.subList(0, m));
		List<Pair> r = mergeSort(list.subList(m, list.size()));
		List<Pair> ret = new ArrayList<Pair>();
		int li = 0;
		int ri = 0;
		while(li < l.size() || ri < r.size()) {
			if( li >= l.size()) {
				ret.addAll(r.subList(ri, r.size()));
				ri = r.size();
				
			}
			else if( ri >= r.size() ) {
				ret.addAll(l.subList(li, l.size()));
				li = l.size();
			}
			else {
				if(l.get(li).getValue() > r.get(ri).getValue()) {
					ret.add(l.get(li));
					li++;
				}
				else if(l.get(li).getValue() < r.get(ri).getValue()) {
					ret.add(r.get(ri));
					ri++;
				}
				else {
					if(l.get(li).getKey().compareTo(r.get(ri).getKey()) < 0) {
						ret.add(l.get(li));
						li++;
					}
					else {
						ret.add(r.get(ri));
						ri++;
					}
				}
			}
		}
		//System.out.println("Sorted: " +  ret.toString());
		return ret;
	}

}
