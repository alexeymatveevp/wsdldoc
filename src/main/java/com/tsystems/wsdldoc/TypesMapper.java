package com.tsystems.wsdldoc;

import com.predic8.schema.Annotation;
import com.predic8.schema.ComplexContent;
import com.predic8.schema.ComplexType;
import com.predic8.schema.Derivation;
import com.predic8.schema.Documentation;
import com.predic8.schema.Element;
import com.predic8.schema.Extension;
import com.predic8.schema.Schema;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.Sequence;
import com.predic8.schema.SimpleContent;
import com.predic8.schema.SimpleType;
import com.predic8.schema.TypeDefinition;
import com.predic8.schema.restriction.BaseRestriction;
import com.predic8.schema.restriction.facet.EnumerationFacet;
import com.predic8.schema.restriction.facet.Facet;
import com.predic8.schema.restriction.facet.FractionDigits;
import com.predic8.schema.restriction.facet.LengthFacet;
import com.predic8.schema.restriction.facet.MaxExclusiveFacet;
import com.predic8.schema.restriction.facet.MaxInclusiveFacet;
import com.predic8.schema.restriction.facet.MaxLengthFacet;
import com.predic8.schema.restriction.facet.MinExclusiveFacet;
import com.predic8.schema.restriction.facet.MinInclusiveFacet;
import com.predic8.schema.restriction.facet.MinLengthFacet;
import com.predic8.schema.restriction.facet.PatternFacet;
import com.predic8.schema.restriction.facet.TotalDigitsFacet;
import com.predic8.schema.restriction.facet.WhiteSpaceFacet;
import groovy.xml.QName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * By: Alexey Matveev
 * Date: 25.08.2016
 * Time: 19:28
 */
public class TypesMapper {

    public static ComplexTypeData map2ComplexType(ComplexType ct, Map<String, TypeDefinition> mapOfOriginalTypes, Map<String, Element> mapOfElements) {
        ComplexTypeData result = new ComplexTypeData();
        result.setName(ct.getName());
        result.setSchema(ct.getSchema().getSchemaLocation());
        result.setDescription(getTypeDescription(ct));
        Sequence sequence = ct.getSequence();
        if (sequence != null) {
            List<SchemaComponent> particles = sequence.getParticles();
            result.setSequence(mapComplexTypeParticles(particles, mapOfOriginalTypes, mapOfElements));
        }
        // extensions
        List<QName> superTypes = ct.getSuperTypes();
        if (superTypes != null) {
            for (QName superTypeName : superTypes) {
                TypeDefinition superType = mapOfOriginalTypes.get(superTypeName.getLocalPart());
                if (superType == null) {
                    // then it's "string" or something, so we just ignore it
                } else {
                    List<String> superTypesList = result.getSuperTypes();
                    if (superTypesList == null) {
                        superTypesList = new ArrayList<>();
                        result.setSuperTypes(superTypesList);
                    }
                    superTypesList.add(superType.getName());
                }
            }
        }

        /** case when type extends some other type and also has additional sequence
        <xs:complexType name="S_SubOrderContact">
            <xs:complexContent>
                <xs:extension base="suborder:S_SubOrder">
                    <xs:sequence>
                        <xs:element maxOccurs="1" minOccurs="1" name="contact" type="contact:S_Contact"/>
                    </xs:sequence>
                </xs:extension>
            </xs:complexContent>
        </xs:complexType>
         */
        SchemaComponent model = ct.getModel();
        if (model != null) {
            if (model instanceof ComplexContent) {
                ComplexContent complexContent = (ComplexContent) model;
                Derivation derivation = complexContent.getDerivation();
                if (derivation != null) {
                    if (derivation instanceof Extension) {
                        Extension extension = (Extension) derivation;
                        SchemaComponent extensionModel = extension.getModel();
                        if (extensionModel != null) {
                            if (extensionModel instanceof Sequence) {
                                Sequence extensionModelSequence = (Sequence) extensionModel;
                                List<SchemaComponent> particles = extensionModelSequence.getParticles();
                                // add extension sequence to the base sequence if exist
                                List<ElementData> extensionSequenceMapped = mapComplexTypeParticles(particles, mapOfOriginalTypes, mapOfElements);
                                if (extensionSequenceMapped != null) {
                                    List<ElementData> existingSequence = result.getSequence();
                                    if (existingSequence == null) {
                                        result.setSequence(extensionSequenceMapped);
                                    } else {
                                        existingSequence.addAll(extensionSequenceMapped);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public static List<ElementData> mapComplexTypeParticles(List<SchemaComponent> particles, Map<String, TypeDefinition> mapOfOriginalTypes, Map<String, Element> mapOfElements) {
        List<ElementData> sequenceResult = null;
        if (particles != null && particles.size() != 0) {
            for (SchemaComponent particle : particles) {
                if (particle instanceof Element) {
                    Element p = (Element) particle;
                    // some parts can have "ref" but no "type"
                    ElementData ed = null;
                    if (p.getType() != null) {
                        TypeDefinition typeDefinition = mapOfOriginalTypes.get(p.getType().getLocalPart());
                        ed = map2Element(p, typeDefinition);
                        // checks whether the type is embedded like "string", "int", or custom one
                        ed.setNativeType(typeDefinition == null);
                    } else if (p.getRef() != null) {
                        Element el = mapOfElements.get(p.getRef().getLocalPart());
                        if (el != null) {
                            ed = map2Element(el);
                            // checks whether the type is embedded like "string", "int", or custom one
                            ed.setNativeType(el.getType() == null);
                        } else {
                            System.out.println("Cannot find element " + p.getRef().getLocalPart() + " in schemas for mapping");
                        }
                    } else if (p.getEmbeddedType() != null) {
//                            TypeDefinition embeddedType = p.getEmbeddedType();
//                            if (embeddedType instanceof ComplexType) {
//                                ComplexType embeddedComplexType = (ComplexType) embeddedType;
//                                if (embeddedComplexType.getModel() != null) {
//                                    SchemaComponent model = embeddedComplexType.getModel();
//                                    if (model instanceof SimpleContent) {
//                                        SimpleContent simpleContent = (SimpleContent) model;
//                                        if (simpleContent.getExtension() != null) {
//                                            simpleContent.getExtension().getAllAttributes();
//                                        }
//                                    }
//                                }
//                            } else {
//                                System.out.println("Unhandled embedded type's type in particle " + p.getName());
//                            }
                        System.out.println("Unhandled embedded type in particle " + p.getName());
                    } else {
                        ed = map2Element(p);
                    }
                    if (ed != null) {
                        if (sequenceResult == null) {
                            sequenceResult = new ArrayList<>();
                        }
                        sequenceResult.add(ed);
                    }
                } else {
                    System.out.println("Particle is of some other type: " + particle.getClass().getName());
                }
            }
        }
        return sequenceResult;
    }

    public static SimpleTypeData map2SimpleType(SimpleType st) {
        SimpleTypeData result = new SimpleTypeData();
        result.setName(st.getName());
        result.setSchema(st.getSchema().getSchemaLocation());
        BaseRestriction restriction = st.getRestriction();
        if (restriction != null) {
            result.setBase(restriction.getBase().getLocalPart());
            List<Facet> facets = restriction.getFacets();
            if (facets != null) {
                for (Facet facet : facets) {
                    if (facet instanceof EnumerationFacet) {
                        List<String> enumerations = result.getEnumerations();
                        if (enumerations == null) {
                            enumerations = new ArrayList<>();
                            result.setEnumerations(enumerations);
                        }
                        enumerations.add(facet.getValue());
                    } else if (facet instanceof MinLengthFacet) {
                        result.setMinLength(facet.getValue());
                    } else if (facet instanceof MaxLengthFacet) {
                        result.setMaxLength(facet.getValue());
                    } else if (facet instanceof LengthFacet) {
                        result.setLength(facet.getValue());
                    } else if (facet instanceof PatternFacet) {
                        result.setPattern(facet.getValue());
                    } else if (facet instanceof TotalDigitsFacet) {
                        result.setTotalDigits(facet.getValue());
                    } else if (facet instanceof WhiteSpaceFacet) {
                        result.setWhiteSpace(facet.getValue());
                    } else if (facet instanceof MinInclusiveFacet) {
                        result.setMinInclusive(facet.getValue());
                    } else if (facet instanceof MinExclusiveFacet) {
                        result.setMinExclusive(facet.getValue());
                    } else if (facet instanceof MaxInclusiveFacet) {
                        result.setMaxInclusive(facet.getValue());
                    } else if (facet instanceof MaxExclusiveFacet) {
                        result.setMaxExclusive(facet.getValue());
                    } else if (facet instanceof FractionDigits) {
                        result.setFractionDigits(facet.getValue());
                    } else {
                        System.out.println("Unhandled facet type: " + facet.getName() + " for simple type: " + st.getName());
                    }
                }

            }
        }
        return result;
    }

    public static ElementData map2Element(Element element) {
        ElementData result = new ElementData();
        result.setName(element.getName() == null ? "" : element.getName());
        if (element.getType() != null) {
            result.setTypeName(element.getType().getLocalPart());
        }
        result.setMinOccurs(mapOccurs(element.getMinOccurs()));
        result.setMaxOccurs(mapOccurs(element.getMaxOccurs()));
        result.setDescription(getAnnotationDescription(element.getAnnotation()));
        Schema schema = element.getSchema();
        if (schema != null) {
            result.setSchema(schema.getSchemaLocation());
        }
        return result;
    }

    public static ElementData map2Element(Element element, TypeDefinition type) {
        ElementData result = new ElementData();
        result.setName(element.getName());
        if (type != null) {
            result.setTypeName(type.getName());
        }
        result.setMinOccurs(mapOccurs(element.getMinOccurs()));
        result.setMaxOccurs(mapOccurs(element.getMaxOccurs()));
        result.setDescription(getTypeDescription(type));
        if (type != null) {
            Schema schema = type.getSchema();
            if (schema != null) {
                result.setSchema(schema.getSchemaLocation());
            }
        }
        return result;
    }

    public static Integer mapOccurs(String occurs) {
        if (occurs.equals("unbounded")) {
            return null;
        }
        return Integer.valueOf(occurs);
    }

    public static String getTypeDescription(TypeDefinition type) {
        if (type == null) {
            return null;
        }
        return getAnnotationDescription(type.getAnnotation());
    }

    public static String getAnnotationDescription(Annotation annotation) {
        if (annotation != null) {
            Object contents = annotation.getContents();
            if (contents != null && contents instanceof Collection) {
                Iterator it = ((Collection) contents).iterator();
                while (it.hasNext()) {
                    Object next = it.next();
                    if (next != null && next instanceof Documentation) {
                        return ((Documentation) next).getContent();
                    }
                }
            }
        }
        return null;
    }

}
