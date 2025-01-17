package io.carml.model.impl;

import io.carml.model.Resource;
import io.carml.rdfmapper.annotations.RdfProperty;
import io.carml.rdfmapper.annotations.RdfResourceName;
import io.carml.util.RdfValues;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.ModelCollector;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

@SuperBuilder
@NoArgsConstructor
public abstract class CarmlResource implements Resource {

  private static final ValueFactory VF = SimpleValueFactory.getInstance();

  @Setter
  String id;

  @Setter
  String label;

  @Builder.Default
  private Map<Resource, Model> modelCache = new HashMap<>();

  CarmlResource(String id, String label) {
    this.id = id;
    this.label = label;
  }

  @Override
  @RdfResourceName
  public String getId() {
    return id;
  }

  @Override
  @RdfProperty("http://www.w3.org/2000/01/rdf-schema#label")
  public String getLabel() {
    return label;
  }

  @Override
  public org.eclipse.rdf4j.model.Resource getAsResource() {

    if (id == null) {
      return VF.createBNode();
    }

    if (RdfValues.isValidIri(id)) {
      return VF.createIRI(id);
    }

    return VF.createBNode(id);
  }

  private void cacheModel(Model model) {
    modelCache = new HashMap<>();
    modelCache.put(this, model);
  }

  @Override
  public Model asRdf() {

    if (modelCache.containsKey(this)) {
      Model cachedModel = modelCache.get(this);
      if (!cachedModel.isEmpty()) {
        return cachedModel;
      }
    }

    var model = asRdfInternal(this, new HashSet<>());

    cacheModel(model);

    return model;
  }

  private Model asRdfInternal(Resource resource, Set<Resource> processed) {
    processed.add(resource);
    ModelBuilder builder = new ModelBuilder();
    addTriples(builder);
    if (label != null) {
      builder.add(RDFS.LABEL, label);
    }
    Model model = builder.build();

    Model nestedModel = getReferencedResources().stream()
        .filter(refResource -> !processed.contains(refResource))
        .flatMap(refResource -> asRdfInternal(refResource, processed).stream())
        .collect(ModelCollector.toModel());

    model.addAll(nestedModel);

    return model;
  }

}
