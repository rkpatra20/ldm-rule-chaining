package com.monamitech.mgr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monamitech.model.RuleChainTuple;
import com.monamitech.model.RuleChainTupleActionType;
import com.monamitech.service.LdmScoreService;

public class LdmRuleChainManager {

	private final ObjectMapper mapper = new ObjectMapper();

	private final Map<String, List<RuleChainTuple>> ruleChainTuplesMap = new HashMap<>();

	public void loadRuleChainTuple() throws IOException {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("rule-chaining.json");
		RuleChainTuple[] chainTuples = mapper.readValue(is, RuleChainTuple[].class);
		if (is != null) {
			is.close();
		}
		for (RuleChainTuple ruleChainTuple : chainTuples) {
			System.out.println(ruleChainTuple);
			List<RuleChainTuple> ruleChainTupleList = ruleChainTuplesMap.get(ruleChainTuple.getProduct());
			if (ruleChainTupleList != null) {
				ruleChainTupleList.add(ruleChainTuple);
			} else {
				ruleChainTupleList = new ArrayList<>();
				ruleChainTupleList.add(ruleChainTuple);
				ruleChainTuplesMap.put(ruleChainTuple.getProduct(), ruleChainTupleList);
			}
		}

	}

	public String getLdmChainScore(Map<String, String> input) throws JsonProcessingException {
	     return mapper.writeValueAsString(evaluateLdmChainScore(input));
	}

	public Map<String, Object> evaluateLdmChainScore(Map<String, String> input) {
		Map<String, Object> scoresByProduct = new LinkedHashMap<>();

		Double sum_actualScore = 0.0;
		Double sum_maxScoreSum = 0.0;
		LdmScoreService ldmScoreService = new LdmScoreService();

		Set<String> productsEvaluated = new HashSet<>();
		while (true) {
			String product=input.get("product");
			System.out.println("evaluting product: "+product);
			Map<String, Object> productScore = ldmScoreService.evaluateScore(input);
			productsEvaluated.add(product);
			Double[] scores = getActualScore_MaxScoreSum(productScore);
			sum_actualScore = sum_actualScore + scores[0];
			sum_maxScoreSum = sum_maxScoreSum + scores[1];

			RuleChainTuple ruleChainTuple = getRuleChainTuple(product, scores[0]);
			if (ruleChainTuple.getActionType().equals(RuleChainTupleActionType.PRODUCT.name())) {
				scoresByProduct.put(product, productScore);
				product = ruleChainTuple.getAction();
				proceedIfProductIsNotEvaluated(product, productsEvaluated);
				input.put("product", ruleChainTuple.getAction());
				continue;
			} else {
				scoresByProduct.put(product, productScore);
				scoresByProduct.put("sum_actualScore", sum_actualScore);
				scoresByProduct.put("sum_maxScoreSum", sum_maxScoreSum);
				scoresByProduct.put("action", ruleChainTuple.getAction());
				break;

			}

		}
		return scoresByProduct;
	}

	private void proceedIfProductIsNotEvaluated(String product, Set<String> productsEvaluated) {
		if (productsEvaluated.add(product) == false) {
			throw new RuntimeException("product has been evaluated: " + " product: " + product
					+ " and evaluated_products: " + productsEvaluated);
		}
	}

	private RuleChainTuple getRuleChainTuple(String product, Double score) {
		List<RuleChainTuple> ruleChainTuples = ruleChainTuplesMap.get(product);
		for (RuleChainTuple chainTuple : ruleChainTuples) {
			if (score >= chainTuple.getMinValue() && score <= chainTuple.getMaxValue()) {
				return chainTuple;
			}
		}
		throw new RuntimeException("no rule_chain_tuple_found for score: "+score+" and product: "+product);
	}

	private Double[] getActualScore_MaxScoreSum(Map<String, Object> productScore) {
		Map<String, String> output = (Map<String, String>) productScore.get("output");
		Double score = Double.valueOf(output.get("score"));
		Double maxScoreSum = Double.valueOf(output.get("maxScore"));

		Double[] scores = new Double[] { score, maxScoreSum };
		return scores;
	}
}
