package io.carml.model.impl;

import com.google.common.collect.ImmutableSet;
import io.carml.model.PredicateMap;
import io.carml.model.Resource;
import io.carml.vocab.Rdf;
import java.util.Set;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;

@SuperBuilder
@NoArgsConstructor
public class CarmlPredicateMap extends CarmlTermMap implements PredicateMap {

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, new MultilineRecursiveToStringStyle()).toString();
  }

  @Override
  public Set<Resource> getReferencedResources() {
    return ImmutableSet.<Resource>builder()
        .addAll(getReferencedResourcesBase())
        .build();
  }

  @Override
  public void addTriples(ModelBuilder modelBuilder) {
    modelBuilder.subject(getAsResource())
        .add(RDF.TYPE, Rdf.Rr.PredicateMap);

    addTriplesBase(modelBuilder);
  }
}
