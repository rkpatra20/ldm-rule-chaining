package com.monamitech;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.monamitech.mgr.LdmRuleChainManager;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws IOException {
		LdmRuleChainManager manager = new LdmRuleChainManager();
		manager.loadRuleChainTuple();

		System.out.println("loaded");

		Map<String, String> input = new HashMap<>();
		input.put("product", "p9");

		String result = manager.getLdmChainScore(input);

		System.out.println(result);
	}
}
