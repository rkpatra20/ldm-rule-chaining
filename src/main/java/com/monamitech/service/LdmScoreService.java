package com.monamitech.service;

import java.util.HashMap;
import java.util.Map;

public class LdmScoreService {

	public Map<String, Object> evaluateScore(Map<String,String> input) {
		String product=input.get("product");
		switch (product) {
		case "p1":
			return evaluateP1();
		case "p2":
			return evaluateP2();
		case "p3":
			return evaluateP3();
		case "p4":
			return evaluateP4();
		default:
			return evaluateDefault();
		}
	}

	private Map<String, Object> evaluateDefault() {
		return null;
	}

	private Map<String, Object> evaluateP4() {
		Map<String, Object> map = getMap();
		Map<String, String> outputMap = (Map<String, String>) map.get("output");
		outputMap.put("score", "41");
		outputMap.put("maxScore", "100");
		
		return map;
	}

	private Map<String, Object> evaluateP3() {
		Map<String, Object> map = getMap();
		Map<String, String> outputMap = (Map<String, String>) map.get("output");
		outputMap.put("score", "31");
		outputMap.put("maxScore", "100");
		
		return map;
	}

	private Map<String, Object> evaluateP2() {
		// TODO Auto-generated method stub
		Map<String, Object> map = getMap();
		Map<String, String> outputMap = (Map<String, String>) map.get("output");
		outputMap.put("score", "21");
		outputMap.put("maxScore", "100");
		
		return map;
	}

	private Map<String, Object> evaluateP1() {
		Map<String, Object> map = getMap();
		Map<String, String> outputMap = (Map<String, String>) map.get("output");
		outputMap.put("score", "11");
		outputMap.put("maxScore", "100");
		
		return map;
	}

	private Map<String, Object> getMap() {
		Map<String, String> input = new HashMap<String, String>();
		Map<String, String> output = new HashMap<>();

		Map<String, Object> resultMap = new HashMap<>();

		resultMap.put("input", input);
		resultMap.put("output", output);

		return resultMap;
	}

}
