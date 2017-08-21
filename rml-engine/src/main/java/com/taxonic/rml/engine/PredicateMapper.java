package com.taxonic.rml.engine;

import java.util.List;
import java.util.function.Consumer;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;

class PredicateMapper {
	
	private TermGenerator<IRI> generator;
	private List<TermGenerator<Value>> objectGenerators;
	private List<RefObjectMapper> refObjectMappers;
	
	PredicateMapper(
		TermGenerator<IRI> generator,
		List<TermGenerator<Value>> objectGenerators,
		List<RefObjectMapper> refObjectMappers
	) {
		this.generator = generator;
		this.objectGenerators = objectGenerators;
		this.refObjectMappers = refObjectMappers;
	}

	void map(Model model, EvaluateExpression evaluate, Resource subject, Resource... contexts) {
		
		IRI predicate = generator.apply(evaluate);
		if (predicate == null) return;
		
		Consumer<Value> addObjectTriple =
			o -> model.add(subject, predicate, o, contexts);
		
		objectGenerators.stream()
			.map(g -> g.apply(evaluate))
			.filter(o -> o != null)
			.forEach(addObjectTriple);
		
		refObjectMappers.stream()
			.flatMap(r -> r.map(evaluate).stream())
			.forEach(addObjectTriple);
		
	}
}