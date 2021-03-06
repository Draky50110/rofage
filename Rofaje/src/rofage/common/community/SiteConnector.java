package rofage.common.community;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import rofage.common.Engine;
import rofage.common.object.Comment;
import rofage.common.object.Credentials;
import rofage.common.object.sitemessage.SiteBestOfMessage;
import rofage.common.object.sitemessage.SiteCommentMessage;
import rofage.common.object.sitemessage.SiteSimpleMessage;
import rofage.common.object.sitemessage.SiteVersionMessage;

public abstract class SiteConnector {
	private final static String UTF8 = "UTF-8";	
	
	private final static String BASE_URL = "http://schyzophrenic.free.fr/RoFage/";
	
	private final static String CREATE_ACCNT 		= "createAccnt.php";
	private final static String CHECK_CREDS	 		= "checkCreds.php";
	private final static String SAVE_COMMENT		= "saveComment.php";
	private final static String GET_SINGLE_COMMENT	= "getSingleComment.php";
	private final static String GET_ALL_COMMENTS	= "getAllComments.php";
	private final static String SYNC_AVG_NOTES		= "syncAvgNotes.php";
	private final static String SYNC_MY_NOTES		= "syncMyNotes.php";
	private final static String CHECK_VER			= "version.php";
	private final static String RESET_PWD			= "resetPwd.php";
	private final static String ADD_USEFUL_COMMENT	= "addUsefulComment.php";
	private final static String GET_BESTOF			= "getBestOf.php";
	
	private final static String PARAM_LOGIN		= "login";
	private final static String PARAM_PWD		= "pwd";
	private final static String PARAM_EMAIL		= "email";
	private final static String PARAM_NOTE		= "note";
	private final static String PARAM_COMMENT	= "comment";
	private final static String PARAM_CRC		= "crc";
	private final static String PARAM_NEWPWD	= "newPwd";
	private final static String PARAM_SPOILER	= "spoiler";
	private final static String PARAM_IDCOMMENT = "idComment";
	private final static String PARAM_CRCINLIST	= "crcInList";
	
	
	/**
	 * Retrieve the best games based on the community ratings
	 * The param inList gives the CRC list of games we search in. It is important
	 * to note that this list is already SQL formatted, ie : 'crc1','crc2' (note the quotes
	 * and the ,)
	 * @param creds
	 * @param inList
	 * @param engine
	 * @return
	 */
	public static SiteBestOfMessage getBestOf (Credentials creds, String inList, Engine engine) {
		String params = new String();
		String request = BASE_URL + GET_BESTOF;
		
		params = addCredentialsToParams(params, creds);
		params = addParameter(params, PARAM_CRCINLIST, inList);
		return new SiteBestOfMessage(sendRequest(request, params), engine);
	}
	
	public static SiteSimpleMessage addUsefulComment (Credentials creds, double idComment) {
		String params = new String();
		String request = BASE_URL + ADD_USEFUL_COMMENT;
		
		params = addCredentialsToParams(params, creds);
		params = addParameter(params, PARAM_IDCOMMENT, idComment);
		return new SiteSimpleMessage(sendRequest(request, params));
	}
	
	public static SiteSimpleMessage resetPwd (Credentials creds, String newPwd) {
		String params = new String();
		String request = BASE_URL + RESET_PWD;
		
		params = addCredentialsToParams(params, creds);
		params = addParameter(params, PARAM_NEWPWD, newPwd);
		return new SiteSimpleMessage(sendRequest(request, params));
	}
	
	public static SiteVersionMessage checkVersion () {
		String params = new String();
		String request = BASE_URL + CHECK_VER;
		return new SiteVersionMessage(sendRequest(request, params));
	}
	
	/** 
	 * Retrieve all crc/avgNotes couples from the database
	 * @param creds
	 * @param crc
	 * @return
	 */
	public static SiteCommentMessage syncAvgNotes (Credentials creds) {
		String params = new String();
		String request = BASE_URL + SYNC_AVG_NOTES;
		params = addCredentialsToParams(params, creds);
				
		return new SiteCommentMessage(sendRequest (request, params));
	}
	
	/** 
	 * Retrieve all crc/myNotes couples from the database
	 * @param creds
	 * @param crc
	 * @return
	 */
	public static SiteCommentMessage syncMyNotes (Credentials creds) {
		String params = new String();
		String request = BASE_URL + SYNC_MY_NOTES;
		params = addCredentialsToParams(params, creds);
				
		return new SiteCommentMessage(sendRequest(request, params));
	}
	
	/** 
	 * Retrieve all comments from the database for a given game
	 * @param creds
	 * @param crc
	 * @return
	 */
	public static SiteCommentMessage getAllVotes (Credentials creds, String crc) {
		String params = new String();
		String request = BASE_URL + GET_ALL_COMMENTS;
		params = addCredentialsToParams(params, creds);
		params = addParameter(params, PARAM_CRC, crc);
		
		return new SiteCommentMessage(sendRequest(request, params));
	}
	
	/** 
	 * Retrieve a single comment from the database
	 * @param creds
	 * @param crc
	 * @return
	 */
	public static SiteCommentMessage getSingleVote (Credentials creds, String crc) {
		String params = new String();
		String request = BASE_URL + GET_SINGLE_COMMENT;
		params = addCredentialsToParams(params, creds);
		params = addParameter(params, PARAM_CRC, crc);
		
		return new SiteCommentMessage(sendRequest(request, params));
	}
	
	public static SiteSimpleMessage addVote (Credentials creds, Comment comment) {
		String params = new String();
		String request = BASE_URL + SAVE_COMMENT;
		params = addCredentialsToParams(params, creds);
		params = addParameter(params, PARAM_COMMENT, comment.getText());
		params = addParameter(params, PARAM_NOTE, String.valueOf(comment.getNote()));
		params = addParameter(params, PARAM_CRC, comment.getCrc());
		params = addParameter(params, PARAM_SPOILER, comment.isSpoiler());
		
		return new SiteSimpleMessage(sendRequest(request, params));
	}
	
	public static SiteSimpleMessage checkCredentials (Credentials creds) {
		String params = new String();
		String request = BASE_URL+CHECK_CREDS;
		params = addCredentialsToParams(params, creds);
		
		return new SiteSimpleMessage (sendRequest(request, params));
	}
	
	/**
	 * Send the request to create an account
	 * <b>No check is performed in this function !!</b>
	 * @param username
	 * @param password
	 * @param email
	 * @return
	 */
	public static SiteSimpleMessage createAccount (String username, String password, String email) {
		String params = new String();
		String request = BASE_URL+CREATE_ACCNT;
		params = addParameter(params, PARAM_LOGIN, username);
		params = addParameter(params, PARAM_PWD, password);
		params = addParameter(params, PARAM_EMAIL, email);
		
		return new SiteSimpleMessage(sendRequest(request, params));
	}
	
	/**
	 * Adds a parameter to a url in a post method
	 * @param paramName
	 * @param paramValue
	 * @return
	 */
	private static String addParameter (String params, String paramName, String paramValue) {
		if (params != null && params.length()>0) {
			// If it's not the first param we add the &
			params += "&";
		}
		try {
			params += URLEncoder.encode(paramName, UTF8)
				+ "="
				+ URLEncoder.encode(paramValue, UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return params;
	}
	
	private static String addParameter (String params, String paramName, double paramValue) {
		return addParameter(params, paramName, String.valueOf(paramValue));
	}
	
	private static String addParameter (String params, String paramName, boolean paramValue) {
		// We convert a boolean value to a string value
		String newParamValue = "";
		if (paramValue) {
			newParamValue = "1";
		} else {
			newParamValue = "0";
		}
		return addParameter(params, paramName, newParamValue);
	}
	
	private static String sendRequest (String request, String params) {
		String result = "";
		OutputStreamWriter writer = null;
		BufferedReader reader = null;
		
		try {
			URL url = new URL(request);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			// We send the request
			writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(params);
			writer.flush();

			// We read the answer
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				result += line;
				result += "\n"; // We add the \n manually because we read line by line and this caracter is not read...
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			result = "E:;:Community.siteDown";
		} finally {
			try{writer.close();}catch(Exception e){}
		    try{reader.close();}catch(Exception e){}
		}
		return result;
	}
	
	private static String addCredentialsToParams (String params, Credentials creds) {
		params = addParameter(params, PARAM_LOGIN, creds.getUsername());
		params = addParameter(params, PARAM_PWD, creds.getPassword());
		return params;
	}
}
