package me.ram.bedwarsscoreboardaddon.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class URLUtil {

	public static String getDocumentAt(String urlString) {
		StringBuffer document = new StringBuffer("");
		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				document.append(line + " ");
			}
			reader.close();
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		return document.toString();
	}
}
