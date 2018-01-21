package com.tsystems.wsdldoc;

import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.Schema;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.Sequence;
import com.predic8.schema.SimpleType;
import com.predic8.schema.TypeDefinition;
import com.predic8.schema.restriction.BaseRestriction;
import com.predic8.schema.restriction.facet.EnumerationFacet;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Message;
import com.predic8.wsdl.Part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * By: Alexey Matveev
 * Date: 25.08.2016
 * Time: 17:53
 */
public class TypesLocator {

    /**
     * Creates map of destination DTO types to be handled by FTL.
     * @param defs
     * @param mapOfOriginalTypes
     * @return
     */
    public static Map<String, TypeData> createMapOfTypes(Definitions defs, Map<String, TypeDefinition> mapOfOriginalTypes, Map<String, Element> mapOfElements) {
        Map<String, TypeData> result = new HashMap<>();
        // get all messages
        // for each get parts
        // part -> element -> schema
        // if schema != null -> get complex types
        // complex type.model is the place
        // complex type name is unique
        // simple type name
        // schema has imports -> other import schemas
        HashSet<String> alreadyCheckedSchemas = new HashSet<>();
        for (Message message : defs.getMessages()) {
            for (Part part : message.getParts()) {
                Element element = part.getElement();
                if (element != null) {
                    Schema schema = element.getSchema();
                    fillMapRecursively(result, schema, alreadyCheckedSchemas, mapOfOriginalTypes, mapOfElements);
                }
            }
        }
        return result;
    }
    public static void fillMapRecursively(Map<String, TypeData> result, Schema schema, Set<String> alreadyCheckedSchemas, Map<String, TypeDefinition> mapOfOriginalTypes, Map<String, Element> mapOfElements) {
        if (schema != null) {
            List<ComplexType> complexTypes = schema.getComplexTypes();
            if (complexTypes != null) {
                for (ComplexType ct : complexTypes) {
                    result.put(ct.getName(), TypesMapper.map2ComplexType(ct, mapOfOriginalTypes, mapOfElements));
                }
            }
            List<SimpleType> simpleTypes = schema.getSimpleTypes();
            if (simpleTypes != null) {
                for (SimpleType st : simpleTypes) {
                    result.put(st.getName(), TypesMapper.map2SimpleType(st));
                }
            }
            List<Schema> importedSchemas = schema.getImportedSchemas();
            if (importedSchemas != null) {
                for (Schema importedSchema : importedSchemas) {
                    if (!alreadyCheckedSchemas.contains(importedSchema.getSchemaLocation())) {
                        alreadyCheckedSchemas.add(importedSchema.getSchemaLocation());
                        fillMapRecursively(result, importedSchema, alreadyCheckedSchemas, mapOfOriginalTypes, mapOfElements);
                    }
                }
            }
        }
    }

    /**
     * Map of original complex and simple typed found in schemas.
     * Map is created for lookup types.
     * @param defs
     * @return
     */
    public static Map<String, TypeDefinition> createMapOfOriginalTypes(Definitions defs) {
        Map<String, TypeDefinition> result = new HashMap<>();
        HashSet<String> alreadyCheckedSchemas = new HashSet<>();
        for (Message message : defs.getMessages()) {
            for (Part part : message.getParts()) {
                Element element = part.getElement();
                if (element != null) {
                    Schema schema = element.getSchema();
                    fillOriginalMapRecursively(result, schema, alreadyCheckedSchemas);
                }
            }
        }
        return result;
    }
    public static void fillOriginalMapRecursively(Map<String, TypeDefinition> result, Schema schema, Set<String> alreadyCheckedSchemas) {
        if (schema != null) {
            List<ComplexType> complexTypes = schema.getComplexTypes();
            if (complexTypes != null) {
                for (ComplexType ct : complexTypes) {
                    result.put(ct.getName(), ct);
                }
            }
            List<SimpleType> simpleTypes = schema.getSimpleTypes();
            if (simpleTypes != null) {
                for (SimpleType st : simpleTypes) {
                    result.put(st.getName(), st);
                }
            }
            List<Schema> importedSchemas = schema.getImportedSchemas();
            if (importedSchemas != null) {
                for (Schema importedSchema : importedSchemas) {
                    if (!alreadyCheckedSchemas.contains(importedSchema.getSchemaLocation())) {
                        alreadyCheckedSchemas.add(importedSchema.getSchemaLocation());
                        fillOriginalMapRecursively(result, importedSchema, alreadyCheckedSchemas);
                    }
                }
            }
        }
    }

    /**
     * Map of elements found in schemas.
     * @param defs
     * @return
     */
    public static Map<String, Element> createMapOfElements(Definitions defs) {
        Map<String, Element> result = new HashMap<>();
        HashSet<String> alreadyCheckedSchemas = new HashSet<>();
        for (Message message : defs.getMessages()) {
            for (Part part : message.getParts()) {
                Element element = part.getElement();
                if (element != null) {
                    Schema schema = element.getSchema();
                    fillMapOfElementsRecursively(result, schema, alreadyCheckedSchemas);
                }
            }
        }
        return result;
    }
    public static void fillMapOfElementsRecursively(Map<String, Element> result, Schema schema, Set<String> alreadyCheckedSchemas) {
        if (schema != null) {
            List<Element> allElements = schema.getAllElements();
            if (allElements != null) {
                for (Element element : allElements) {
                    if (element.getRef() != null) {
                        System.out.println("Unhandled reference for element " + element.getName() + " in schema " + schema.getSchemaLocation());
                    }
                    result.put(element.getName(), element);
                }
            }
            List<Schema> importedSchemas = schema.getImportedSchemas();
            if (importedSchemas != null) {
                for (Schema importedSchema : importedSchemas) {
                    if (!alreadyCheckedSchemas.contains(importedSchema.getSchemaLocation())) {
                        alreadyCheckedSchemas.add(importedSchema.getSchemaLocation());
                        fillMapOfElementsRecursively(result, importedSchema, alreadyCheckedSchemas);
                    }
                }
            }
        }
    }

}
