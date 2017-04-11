package com.ecreditpal.maas.common.log.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.qos.logback.core.pattern.parser.Node;
import com.google.gson.Gson;

public class AnalysisPattern {
	private static Gson gson = new Gson();
	
	public static String convertKeyWords(Map<String,String> keyConverts, Map<String,Object> convertValues){
		Map<String,Object> keyWords = new HashMap<String,Object>();
		for(String key : keyConverts.keySet() ){
			keyWords.put(key, convertValues.get(keyConverts.get(key)));
		}
		return gson.toJson(keyWords);
	}
	
	public static Map<String,String> getKeywordConvertMaps(Map<String,String> convertMaps,Node t){
		List<String> keys = new ArrayList<>();
		Node tmp = t;
		final int SIMPLE_KEYWORD = 1;
		while(tmp.getNext() != null){
			if(SIMPLE_KEYWORD == tmp.getType() ){
				keys.add( tmp.getValue().toString());
			}
			tmp = tmp.getNext();
		}
		
		Map<String,String> keyConverts = new HashMap<String,String>();
		for(String convertKey : convertMaps.keySet() ){
			if(keys.contains(convertKey)){
				for(String key : keys ){
					if(key.equals(convertKey) ){
						keyConverts.put(key, convertMaps.get(key) );
					}
				}
			}
		}
		return keyConverts;
	}

}
