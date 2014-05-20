package com.vineeth.CalaisTrainer;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseHelper.
 * 
 * @author <a href="mailto:vineethmohan@gmail.com">Vineeth</a>
 */

public class BaseHelper {

	static Logger logger = Logger.getLogger(BaseHelper.class.getClass()
			.getName());

	/**
	 * DataInputStream Convert to type. Converts a string to the requested type
	 * 
	 * @param object
	 *            the object
	 * @param type
	 *            the type
	 * @return the object
	 */
	public static Object convertToType(String object, String type) {
		Object newObj = object;
		if (type.equals("double")) {
			newObj = Double.parseDouble(object);
		} else if (type.equals("integer")) {
			newObj = Integer.parseInt(object);
		}
		return newObj;
	}

	/**
	 * Convert Stack trace to string.
	 * 
	 * @param e
	 *            the exception variable
	 * @return the string with all the stack trace
	 */
	public static String stackTraceToString(final Exception e) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		PrintStream p = new PrintStream(b);
		e.printStackTrace(p);
		p.flush();
		return b.toString();
	}

	/**
	 * Convert Stack trace to string.
	 * 
	 * @param e
	 *            the exception variable
	 * @return the string with all the stack trace
	 */
	public static String stackTraceToString(final Throwable e) {
		StringWriter stringWritter = new StringWriter();
		PrintWriter printWritter = new PrintWriter(stringWritter, true);
		e.printStackTrace(printWritter);
		printWritter.flush();
		stringWritter.flush();

		return stringWritter.toString();
	}

	/**
	 * Correct angle.
	 * 
	 * @param angle
	 *            the angle to be corrected
	 * @return the corrected angle
	 */
	public static Double correctAngle(double angle) {
		if (Math.abs(angle) > 90)
			return 180 - Math.abs(angle);
		return Math.abs(angle);
	}

	/**
	 * Gets the from last.
	 * 
	 * @param <E>
	 *            the element type
	 * @param vertices
	 *            the vertices
	 * @param i
	 *            the i
	 * @return the from last
	 */
	public static <E> E getFromLast(List<E> vertices, int i) {
		return vertices.get(vertices.size() + i);
	}

	/**
	 * Gets the list from end.
	 * 
	 * @param <E>
	 *            the element type
	 * @param verticesOfRank
	 *            the vertices of rank
	 * @param i
	 *            the i
	 * @param j
	 *            the j
	 * @return the list from end
	 */
	public static <E> List<E> getListFromEnd(List<E> verticesOfRank, int i,
			int j) {
		i++;
		j++;
		return verticesOfRank.subList(verticesOfRank.size() + i,
				verticesOfRank.size() + j);
	}

	/**
	 * Gets the bytes from string.
	 * 
	 * @param dataOut
	 *            the commandline output
	 * @return the bytes from string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static byte[] getBytesFromString(Process proc) throws IOException {
		String line;
		String content = null;
		DataInputStream dataOut = new DataInputStream(proc.getInputStream());

		while ((line = dataOut.readLine()) != null) {
			content = content + "\n" + line;

		}
		return content.getBytes();
	}

	/**
	 * Gets the bytes from console.
	 * 
	 * @return the bytes from console output
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static byte[] getBytesFromConsole(Process p) throws IOException {

		byte[] buffer = new byte[1024];

		int bytes_read = 0;
		ByteArrayOutputStream baout = new ByteArrayOutputStream();
		InputStream in = p.getInputStream();

		while ((bytes_read = in.read(buffer)) != -1) {
			baout.write(buffer, 0, bytes_read);
		}
		return baout.toByteArray();
	}

	/**
	 * Gets the bytes from PDF.
	 * 
	 * @return the bytes from PDF file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static byte[] convertDocToByteArray(File file) throws IOException {

		byte[] buf = new byte[1024];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			FileInputStream fis = new FileInputStream(file);

			for (int readNum; (readNum = fis.read(buf)) != -1;) {
				bos.write(buf, 0, readNum);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bos.toByteArray();
	}

	/**
	 * 
	 * @param str_date
	 *            , date in 'Oct 12 2011' format
	 * @return 'dd-mm-yyyy' formatted date as string
	 */
	public static String formatDate(String str_date) {
		Date date = null;
		String value = null;
		SimpleDateFormat format1 = new SimpleDateFormat("MMM dd yyyy");

		SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");

		if (str_date != null) {

			try {
				date = format1.parse(str_date);
				value = format2.format(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return value;

	}

	public static boolean isNullOrEmpty(Object input) {
		if (input == null || input == "")
			return true;
		if (input.toString().length() == 0)
			return true;
		return false;

	}

	/**
	 * retrieving string representation of a date object.
	 * 
	 * @return the date
	 */
	public static String getDate(Date dateNow) {
		//
		// Get various information from the Date object.
		//
		/*
		 * SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		 * StringBuilder dateToday = new StringBuilder(date.format(dateNow));
		 * return dateToday.toString();
		 */

		String dateToday = null;
		if (dateNow != null) {
			Format formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			dateToday = formatter.format(dateNow);
		}
		return dateToday;

	}

	public static String getDateFormat(Date dateNow) {
		//
		// Get various information from the Date object.
		//
		/*
		 * SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		 * StringBuilder dateToday = new StringBuilder(date.format(dateNow));
		 * return dateToday.toString();
		 */

		String dateToday;
		Format formatter = new SimpleDateFormat("dd-MM-yyyy");
		dateToday = formatter.format(dateNow);
		return dateToday;

	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Class[] getClasses(String packageName, Class parentClass)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file,
						packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName
						+ '.'
						+ file.getName().substring(0,
								file.getName().length() - 6)));
			}
		}
		return classes;
	}

	/**
	 * replace nonUTF8 character in string with Corresponding UTFhexvalue
	 */
	public static String removeNonUtf8Characters(String content) {
		logger.debug("ENCO ENCODING String obtained at encoding is " + content);
		if (content == null || content.isEmpty()) {
			return "";
		}
		String parsed = null;
		try {
			parsed = new String(content.getBytes("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.info(BaseHelper.stackTraceToString(e));
		}
		return parsed;
	}

	public static String removeNonUtf8Chars(String content) {
		logger.debug("ENCO ENCODING String obtained at encoding is " + content);
		if (content == null || content.isEmpty()) {
			return "";
		}
		String parsed = null;
		try {
			parsed = new String(content.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.info(BaseHelper.stackTraceToString(e));
		}
		return parsed;
	}

	/**
	 * generate hex Value for character
	 * 
	 * @param i
	 * @return hex value with leading zeros
	 */
	public static String getHexValue(int i) {
		String hexCode = Integer.toHexString(i).toUpperCase();
		String hexCodeWithAllLeadingZeros = "0000" + hexCode;
		String hexCodeWithLeadingZeros = hexCodeWithAllLeadingZeros
				.substring(hexCodeWithAllLeadingZeros.length() - 4);
		return hexCodeWithLeadingZeros;
	}

	public static String getDiff(String s1, String s2) {
		String s = "";
		char s1array[] = s1.toCharArray();
		char s2array[] = s2.toCharArray();
		if (s1.length() != s2.length()) {
			return null;
		} else {
			for (int i = 0; i < s1.length(); i++) {
				if (s1array[i] != s2array[i]) {
					s = s + s1array[i];
				}
			}
		}
		return s;
	}

	public static String escapeSpecial(String original) {
		char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
				'b', 'c', 'd', 'e', 'f' };
		if (original == null)
			return "";
		StringBuffer out = new StringBuffer("");
		char[] chars = original.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			boolean found = true;
			switch (chars[i]) {
			case 38:
				out.append("&amp;");
				break; // &
			case 198:
				out.append("&AElig;");
				break; // Æ
			case 193:
				out.append("&Aacute;");
				break; // Á
			case 194:
				out.append("&Acirc;");
				break; // Â
			case 192:
				out.append("&Agrave;");
				break; // À
			case 197:
				out.append("&Aring;");
				break; // Å
			case 195:
				out.append("&Atilde;");
				break; // Ã
			case 196:
				out.append("&Auml;");
				break; // Ä
			case 199:
				out.append("&Ccedil;");
				break; // Ç
			case 208:
				out.append("&ETH;");
				break; // Ð
			case 201:
				out.append("&Eacute;");
				break; // É
			case 202:
				out.append("&Ecirc;");
				break; // Ê
			case 200:
				out.append("&Egrave;");
				break; // È
			case 203:
				out.append("&Euml;");
				break; // Ë
			case 205:
				out.append("&Iacute;");
				break; // Í
			case 206:
				out.append("&Icirc;");
				break; // Î
			case 204:
				out.append("&Igrave;");
				break; // Ì
			case 207:
				out.append("&Iuml;");
				break; // Ï
			case 209:
				out.append("&Ntilde;");
				break; // Ñ
			case 211:
				out.append("&Oacute;");
				break; // Ó
			case 212:
				out.append("&Ocirc;");
				break; // Ô
			case 210:
				out.append("&Ograve;");
				break; // Ò
			case 216:
				out.append("&Oslash;");
				break; // Ø
			case 213:
				out.append("&Otilde;");
				break; // Õ
			case 214:
				out.append("&Ouml;");
				break; // Ö
			case 222:
				out.append("&THORN;");
				break; // Þ
			case 218:
				out.append("&Uacute;");
				break; // Ú
			case 219:
				out.append("&Ucirc;");
				break; // Û
			case 217:
				out.append("&Ugrave;");
				break; // Ù
			case 220:
				out.append("&Uuml;");
				break; // Ü
			case 221:
				out.append("&Yacute;");
				break; // Ý
			case 225:
				out.append("&aacute;");
				break; // á
			case 226:
				out.append("&acirc;");
				break; // â
			case 230:
				out.append("&aelig;");
				break; // æ
			case 224:
				out.append("&agrave;");
				break; // à
			case 229:
				out.append("&aring;");
				break; // å
			case 227:
				out.append("&atilde;");
				break; // ã
			case 228:
				out.append("&auml;");
				break; // ä
			case 231:
				out.append("&ccedil;");
				break; // ç
			case 233:
				out.append("&eacute;");
				break; // é
			case 234:
				out.append("&ecirc;");
				break; // ê
			case 232:
				out.append("&egrave;");
				break; // è
			case 240:
				out.append("&eth;");
				break; // ð
			case 235:
				out.append("&euml;");
				break; // ë
			case 237:
				out.append("&iacute;");
				break; // í
			case 238:
				out.append("&icirc;");
				break; // î
			case 236:
				out.append("&igrave;");
				break; // ì
			case 239:
				out.append("&iuml;");
				break; // ï
			case 241:
				out.append("&ntilde;");
				break; // ñ
			case 243:
				out.append("&oacute;");
				break; // ó
			case 244:
				out.append("&ocirc;");
				break; // ô
			case 242:
				out.append("&ograve;");
				break; // ò
			case 248:
				out.append("&oslash;");
				break; // ø
			case 245:
				out.append("&otilde;");
				break; // õ
			case 246:
				out.append("&ouml;");
				break; // ö
			case 223:
				out.append("&szlig;");
				break; // ß
			case 254:
				out.append("&thorn;");
				break; // þ
			case 250:
				out.append("&uacute;");
				break; // ú
			case 251:
				out.append("&ucirc;");
				break; // û
			case 249:
				out.append("&ugrave;");
				break; // ù
			case 252:
				out.append("&uuml;");
				break; // ü
			case 253:
				out.append("&yacute;");
				break; // ý
			case 255:
				out.append("&yuml;");
				break; // ÿ
			case 162:
				out.append("&cent;");
				break; // ¢
			default:
				found = false;
				break;
			}
			if (!found) {
				if (chars[i] > 127) {
					char c = chars[i];
					int a4 = c % 16;
					c = (char) (c / 16);
					int a3 = c % 16;
					c = (char) (c / 16);
					int a2 = c % 16;
					c = (char) (c / 16);
					int a1 = c % 16;
					out.append("&#x" + hex[a1] + hex[a2] + hex[a3] + hex[a4]
							+ ";");
				} else {
					out.append(chars[i]);
				}
			}
		}
		return out.toString();
	}

	/*
	 * public static void main(String args[]) {
	 * 
	 * try{ // Create file FileWriter fstream = new FileWriter("content");
	 * BufferedWriter out = new BufferedWriter(fstream); String str="&#x00a5;";
	 * str=StringEscapeUtils.unescapeHtml(str);
	 * out.write("{\"content\":\""+str+"\"}"); //Close the output stream
	 * out.close(); }catch (Exception e){//Catch exception if any
	 * System.err.println("Error: " + e.getMessage()); }
	 * 
	 * 
	 * formatDate("2011-03-29"); }
	 */

}