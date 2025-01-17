package io.carml.model.impl;

import com.google.common.collect.ImmutableSet;
import io.carml.model.DatatypeMap;
import io.carml.model.LanguageMap;
import io.carml.model.ObjectMap;
import io.carml.model.Resource;
import io.carml.rdfmapper.annotations.RdfProperty;
import io.carml.rdfmapper.annotations.RdfType;
import io.carml.vocab.Rdf;
import io.carml.vocab.Rml;
import java.util.Set;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;

@SuperBuilder
@NoArgsConstructor
public class CarmlObjectMap extends CarmlTermMap implements ObjectMap {

  @Setter
  private DatatypeMap datatypeMap;

  @Setter
  private LanguageMap languageMap;

  @RdfProperty(Rml.datatypeMap)
  @RdfType(CarmlDatatypeMap.class)
  @Override
  public DatatypeMap getDatatypeMap() {
    return datatypeMap;
  }

  @RdfProperty(Rml.languageMap)
  @RdfType(CarmlLanguageMap.class)
  @Override
  public LanguageMap getLanguageMap() {
    return languageMap;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, new MultilineRecursiveToStringStyle()).toString();
  }

  @Override
  public Set<Resource> getReferencedResources() {
    ImmutableSet.Builder<Resource> builder = ImmutableSet.<Resource>builder()
        .addAll(getReferencedResourcesBase());

    if (datatypeMap != null) {
      builder.add(datatypeMap);
    }

    return builder.build();
  }

  @Override
  public void addTriples(ModelBuilder modelBuilder) {
    modelBuilder.subject(getAsResource())
        .add(RDF.TYPE, Rdf.Rr.ObjectMap);

    addTriplesBase(modelBuilder);

    if (datatypeMap != null) {
      modelBuilder.add(Rml.datatypeMap, datatypeMap.getAsResource());
    }
    if (languageMap != null) {
      modelBuilder.add(Rml.languageMap, languageMap.getAsResource());
    }
  }
}
