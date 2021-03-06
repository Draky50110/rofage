package rofage.common.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import rofage.common.object.Game;

public class DatParser {
	// Please keep this in natural order (the value, not the variable name) !
	private final static String XML_NODE_CONFIGURATION	= "configuration";
	private final static String XML_NODE_CANOPEN		= "canOpen";
	public final static String XML_NODE_COMMENT			= "comment";
	private final static String XML_NODE_DATNAME 		= "datName";
	private final static String XML_NODE_NEWDATURL		= "datURL";
	private final static String XML_NODE_DATVERSION 	= "datVersion";
	private final static String XML_NODE_DATVERSIONURL	= "datVersionURL";
	private final static String XML_NODE_DUPLICATEID	= "duplicateid";
	private final static String XML_NODE_EXTENSION		= "extension";
	private final static String XML_ATTR_FILENAME		= "fileName";
	private final static String XML_NODE_FILES			= "files";
	private final static String XML_NODE_GAME			= "game";
	private final static String XML_NODE_GAMES			= "games";
	private final static String XML_NODE_GENRE			= "genre";
	private final static String XML_NODE_ICONCRC		= "icoCRC";
	private final static String XML_NODE_ICONURL		= "icURL";
	private final static String XML_NODE_IMAGE1CRC		= "im1CRC";
	private final static String XML_NODE_IMAGE2CRC		= "im2CRC";
	private final static String XML_NODE_IMAGENB		= "imageNumber";
	private final static String XML_NODE_IMFOLDER		= "imFolder";
	private final static String XML_NODE_IMAGEURL		= "imURL";
	private final static String XML_NODE_INTERNALNAME	= "internalname";
	private final static String XML_NODE_LANGUAGE		= "language";
	private final static String XML_NODE_LOCATION		= "location";
	private final static String XML_NODE_NEWDAT			= "newDat";
	private final static String XML_NODE_PUBLISHER		= "publisher";
	public final static String XML_NODE_RELEASENB		= "releaseNumber";
	private final static String XML_NODE_ROMCRC			= "romCRC";
	private final static String XML_NODE_ROMSIZE		= "romSize";
	private final static String XML_NODE_SERIAL			= "serial";
	private final static String XML_NODE_SOURCEROM		= "sourceRom";
	private final static String XML_NODE_TITLE			= "title";
	private final static String XML_NODE_WIFI			= "wifi";
	private final static String XML_NODE_ROMTITLE		= "romTitle"; // Default title pattern
	private final static String XML_NODE_SCREENSHOTW	= "screenshotsWidth";
	private final static String XML_NODE_SCREENSHOTH	= "screenshotsHeight";
	
	// Fields that may be used to determine the release nb
	public final static TreeSet<String> availableReleaseNbFields = new TreeSet<String>();
	
	static {
		availableReleaseNbFields.add(XML_NODE_RELEASENB);
		availableReleaseNbFields.add(XML_NODE_COMMENT);
	}
	
	Element racine;
	Element confNode;
	Element gamesNode;
	Element newDatNode;
	Element canOpenNode;
		
	/**
	 * Creation of a datParser. It initializes the basic root nodes
	 * - racine (root node)
	 * - confNode (configuration node)
	 * - guiNode (gui node)
	 * - gamesNode (games node)
	 * @param fullPath to the dat File
	 */
	public DatParser (String fullPath) {
		try {
			SAXBuilder sxb = new SAXBuilder();
			Document document = sxb.build(new File(fullPath));
			racine = document.getRootElement();
			confNode = racine.getChild(XML_NODE_CONFIGURATION);
			gamesNode = racine.getChild(XML_NODE_GAMES);
			newDatNode = confNode.getChild(XML_NODE_NEWDAT);
			canOpenNode = confNode.getChild(XML_NODE_CANOPEN);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves the name of the DAT file provided. 
	 * The name is a specific xml node of the dat file
	 * @param fullPath
	 * @return datName
	 */
	public String getDatName () {
		return confNode.getChildText(XML_NODE_DATNAME);
	}
	
	public String getImageFolderName () {
		String imFolder = confNode.getChildText(XML_NODE_IMFOLDER);
		if (imFolder!=null && !imFolder.trim().isEmpty()) {
			return imFolder;
		} else {
			// In some DATs the image folder may be missing
			// We replace it by the conf name
			return getDatName();
		}
	}
	
	public int getVersion () {
		return Integer.parseInt(confNode.getChildText(XML_NODE_DATVERSION));
	}
	
	public String getNewDatUrl () {
		return newDatNode.getChildText(XML_NODE_NEWDATURL);
	}
	
	public String getImageUrl () {
		return newDatNode.getChildText(XML_NODE_IMAGEURL);
	}
	
	public String getIcoUrl () {
		return newDatNode.getChildText(XML_NODE_ICONURL);
	}
	
	public String getScreenshotHeight () {
		return confNode.getChildText(XML_NODE_SCREENSHOTH);
	}
	
	public String getScreenshotWidth () {
		
		return confNode.getChildText(XML_NODE_SCREENSHOTW);
	}
	
	public String getNewDatVersionUrl () {
		return newDatNode.getChildText(XML_NODE_DATVERSIONURL);
	}
	
	public String getFileName () {
		return newDatNode.getChild(XML_NODE_NEWDATURL).getAttributeValue(XML_ATTR_FILENAME);
	}
	
	public String getRomTitle () {
		return confNode.getChildText(XML_NODE_ROMTITLE);
	}
	
	public List<String> getAllowedExtensions () {
		List<String> listExtensions = new ArrayList<String>();
		
		Iterator iterExtensionNodes = canOpenNode.getChildren(XML_NODE_EXTENSION).iterator();
		while (iterExtensionNodes.hasNext()) {
			Element curExtensionNode = (Element) iterExtensionNodes.next();
			listExtensions.add(curExtensionNode.getText());
		}
		
		return listExtensions;
	}
	
	/**
	 * Retrieves the games collection and returns it as a HashTable where 
	 * the key is the release number (String) and the Value is a Game Bean
	 * @return the Game Collection (Hashtable <Integer:releaseNb, Game>)
	 */
	public HashMap<Integer, Game> retrieveGameCollection () {
		HashMap<Integer, Game> gameCollection = new HashMap<Integer, Game> ();
		// First of all let's get the games node
		Element gamesNode = racine.getChild(XML_NODE_GAMES);

		// Then we get all the game nodes
		// We have to construct an iterator an get the element 1 by 1 to avoid "unchecked warnings"
		List<Element> listGameNodes = new ArrayList<Element>();
		Iterator iterElement = gamesNode.getChildren(XML_NODE_GAME).iterator();
		while (iterElement.hasNext()) {
			listGameNodes.add((Element) iterElement.next());
		}
					
		Iterator<Element> iterGame = listGameNodes.iterator();
		while (iterGame.hasNext()) {
			Element gameNode = iterGame.next();
			// Let's create the game !
			Game game = new Game();
			game.setCrc(gameNode.getChild(DatParser.XML_NODE_FILES).getChildText(DatParser.XML_NODE_ROMCRC));
			game.setImage1crc(gameNode.getChildText(DatParser.XML_NODE_IMAGE1CRC));
			game.setImage2crc(gameNode.getChildText(DatParser.XML_NODE_IMAGE2CRC));
			game.setImageNb(gameNode.getChildText(DatParser.XML_NODE_IMAGENB));
			game.setLanguage(gameNode.getChildText(DatParser.XML_NODE_LANGUAGE));
			game.setLocation(gameNode.getChildText(DatParser.XML_NODE_LOCATION));
			game.setPublisher(gameNode.getChildText(DatParser.XML_NODE_PUBLISHER));
			game.setReleaseNb(gameNode.getChildText(DatParser.XML_NODE_RELEASENB));
			game.setRomSize(gameNode.getChildText(DatParser.XML_NODE_ROMSIZE));
			game.setSourceRom(gameNode.getChildText(DatParser.XML_NODE_SOURCEROM));
			game.setTitle(gameNode.getChildText(DatParser.XML_NODE_TITLE));
			game.setIcoCrc(gameNode.getChildText(DatParser.XML_NODE_ICONCRC));
			game.setGenre(gameNode.getChildText(XML_NODE_GENRE));
			game.setInternalName(gameNode.getChildText(XML_NODE_INTERNALNAME));
			game.setSerial(gameNode.getChildText(XML_NODE_SERIAL));
			game.setComment(gameNode.getChildText(XML_NODE_COMMENT));
			if (gameNode.getChildText(XML_NODE_WIFI)!=null) {
				if ("Yes".equals(gameNode.getChildText(XML_NODE_WIFI))) {
					game.setWifi(true);
				} else {
					game.setWifi(false);
				}
			}
			game.setDuplicateId(gameNode.getChildText(XML_NODE_DUPLICATEID));
			
			int releaseNb = Integer.parseInt(game.getReleaseNb());
			while (gameCollection.get(releaseNb)!=null) {
				releaseNb++;
			}
			
			gameCollection.put(releaseNb, game);
		}
		
		return gameCollection;
	}
	
}
