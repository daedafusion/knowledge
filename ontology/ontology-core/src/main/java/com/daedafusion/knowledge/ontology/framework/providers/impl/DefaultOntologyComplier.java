package com.daedafusion.knowledge.ontology.framework.providers.impl;

import com.daedafusion.knowledge.ontology.ClassDescription;
import com.daedafusion.knowledge.ontology.OntologyDescription;
import com.daedafusion.knowledge.ontology.PropertyDescription;
import com.daedafusion.knowledge.ontology.framework.OntologyCompiler;
import com.daedafusion.knowledge.ontology.framework.providers.OntologyCompilerProvider;
import com.daedafusion.knowledge.ontology.model.OntologyModel;
import com.daedafusion.sf.AbstractService;
import com.daedafusion.sf.LifecycleListener;
import com.daedafusion.sparql.Literal;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by mphilpot on 2/19/16.
 */
public class DefaultOntologyComplier extends AbstractService<OntologyCompilerProvider> implements OntologyCompiler
{
    private static final Logger log = Logger.getLogger(DefaultOntologyComplier.class);

    private String PREFIX_ANNOTATION = "http://www.daedafusion.com/editor_annotation#namespacePrefix";
    private String ABSTRACT_ANNOTATION = "http://www.daedafusion.com/editor_annotation#abstract";
    private String AUTO_ANNOTATION = "http://www.daedafusion.com/editor_annotation#autoGenerate";
    private String REIFIED_DOMAIN_ANNOTATION = "http://www.daedafusion.com/editor_annotation#reifiedDomain";

    public DefaultOntologyComplier()
    {
        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void init()
            {
                PREFIX_ANNOTATION = getProperty("prefix.annotation", PREFIX_ANNOTATION);
                ABSTRACT_ANNOTATION = getProperty("abstract.annotation", ABSTRACT_ANNOTATION);
                AUTO_ANNOTATION = getProperty("auto.annotation", AUTO_ANNOTATION);
                REIFIED_DOMAIN_ANNOTATION = getProperty("reified.annotation", REIFIED_DOMAIN_ANNOTATION);
            }

            @Override
            public void start()
            {

            }

            @Override
            public void stop()
            {

            }

            @Override
            public void teardown()
            {

            }
        });
    }

    @Override
    public OntologyDescription describeOntologyClosure(OntologyModel ontModel)
    {
        OntologyDescription description = new OntologyDescription();

        for(String classUri : ontModel.getClasses())
        {
            ClassDescription classDesc = new ClassDescription(classUri);

            Map<String, Set<Literal>> classAnnotations = ontModel.getAnnotationProperties(classUri);

            if(classAnnotations.containsKey(PREFIX_ANNOTATION) && !classAnnotations.get(PREFIX_ANNOTATION).isEmpty())
            {
                classDesc.setNamespacePrefix(classAnnotations.get(PREFIX_ANNOTATION).iterator().next().value);
            }
            else
            {
                classDesc.setNamespacePrefix("");
            }

            if(classAnnotations.containsKey(ABSTRACT_ANNOTATION) && !classAnnotations.get(ABSTRACT_ANNOTATION).isEmpty())
            {
                classDesc.setIsAbstract(Boolean.parseBoolean(classAnnotations.get(ABSTRACT_ANNOTATION).iterator().next().value));
            }

            classDesc.setLabels(ontModel.getLabels(classUri));
            classDesc.setComments(ontModel.getComments(classUri));
            classDesc.setParents(ontModel.getParentClasses(classUri));

            for(String dpUri : ontModel.getDataProperties(classUri))
            {
                PropertyDescription pd = new PropertyDescription(dpUri);

                pd.setLabels(ontModel.getLabels(dpUri));
                pd.setComments(ontModel.getComments(dpUri));
                pd.setRange(ontModel.getRangeOfProperty(classUri, dpUri));

                Map<String, Set<Literal>> dpAnnotations = ontModel.getAnnotationProperties(dpUri);

                if(dpAnnotations.containsKey(AUTO_ANNOTATION) && !dpAnnotations.get(AUTO_ANNOTATION).isEmpty())
                {
                    pd.setAuto(dpAnnotations.get(AUTO_ANNOTATION).iterator().next().value);
                }

                Integer[] cardinality = ontModel.getCardinality(classUri, dpUri);

                if(cardinality[0] != null)
                    pd.getCardinality().put(PropertyDescription.CARDINALITY_MIN, cardinality[0]);
                if(cardinality[1] != null)
                    pd.getCardinality().put(PropertyDescription.CARDINALITY_MAX, cardinality[1]);
                if(cardinality[2] != null)
                    pd.getCardinality().put(PropertyDescription.CARDINALITY_EXACT, cardinality[2]);

                // Is there a reification object
                // TODO fix later
                Set<String> rClasses = ontModel.getObjectsWithAnnotationPropertyValue(REIFIED_DOMAIN_ANNOTATION, dpUri);

                if(rClasses.size() > 1)
                    log.warn(String.format("Only support single reification objects for predicate %s", dpUri));

                if(!rClasses.isEmpty())
                {
                    pd.setReification(rClasses.iterator().next());
                }

                classDesc.getDataProperties().add(pd);
            }
            for(String opUri : ontModel.getObjectProperties(classUri))
            {
                PropertyDescription pd = new PropertyDescription(opUri);

                pd.setLabels(ontModel.getLabels(opUri));
                pd.setComments(ontModel.getComments(opUri));
                pd.setRange(ontModel.getRangeOfProperty(classUri, opUri));

                Integer[] cardinality = ontModel.getCardinality(classUri, opUri);

                if(cardinality[0] != null)
                    pd.getCardinality().put(PropertyDescription.CARDINALITY_MIN, cardinality[0]);
                if(cardinality[1] != null)
                    pd.getCardinality().put(PropertyDescription.CARDINALITY_MAX, cardinality[1]);
                if(cardinality[2] != null)
                    pd.getCardinality().put(PropertyDescription.CARDINALITY_EXACT, cardinality[2]);

                // Is there a reification object
                // TODO fix later
                Set<String> rClasses = ontModel.getObjectsWithAnnotationPropertyValue(REIFIED_DOMAIN_ANNOTATION, opUri);

                if(rClasses.size() > 1)
                    log.warn(String.format("Only support single reification objects for predicate %s", opUri));

                if(!rClasses.isEmpty())
                {
                    pd.setReification(rClasses.iterator().next());
                }

                classDesc.getObjectProperties().add(pd);
            }

            Collections.sort(classDesc.getDataProperties());
            Collections.sort(classDesc.getObjectProperties());

            description.getClasses().add(classDesc);
        }

        Collections.sort(description.getClasses());

        return description;
    }

    @Override
    public Class getProviderInterface()
    {
        return OntologyCompilerProvider.class;
    }
}
