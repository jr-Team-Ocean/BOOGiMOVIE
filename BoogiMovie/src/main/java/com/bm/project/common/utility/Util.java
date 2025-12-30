package com.bm.project.common.utility;

public class Util {

	public static String XSSHandling(String content) {
		
		content = content.replaceAll("&", "&amp;");
		content = content.replaceAll("<", "&lt;");
		content = content.replaceAll(">", "&gt;");
		content = content.replaceAll("\"", "&quot;");						
				
		return content;
	}	
	
}
