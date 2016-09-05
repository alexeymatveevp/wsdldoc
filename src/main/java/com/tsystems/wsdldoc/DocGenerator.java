package com.tsystems.wsdldoc;

import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.Sequence;
import com.predic8.schema.TypeDefinition;
import com.predic8.wsdl.AbstractPortTypeMessage;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Input;
import com.predic8.wsdl.Message;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.Output;
import com.predic8.wsdl.Part;
import com.predic8.wsdl.WSDLParser;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The main service for HTML documentation generation.
 * Takes parsed parameters, downloads and parses the input WSDL, processes the FTL.
 *
 * By: Alexey Matveev
 * Date: 31.08.2016
 * Time: 9:36
 */
public class DocGenerator {

    /**
     * Generates the documentation out of the parameters.
     *
     * @param sourceWsdlLocations - the list of WSDL URL's
     * @param outputFile - the output file
     * @param title - title of the document
     * @throws Exception
     */
    public static void generateDoc(String[] sourceWsdlLocations, File outputFile, String title) throws Exception {
        WSDLParser parser = new WSDLParser();
        List<Definitions> defsList = new ArrayList<>();
        for (String sourceWsdlLocation : sourceWsdlLocations) {
            Definitions defs = parser.parse(sourceWsdlLocation);
            defsList.add(defs);
        }

        Configuration cfg = new Configuration();
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        // loading templates
        FileTemplateLoader ftl1 = new FileTemplateLoader(new File("C:\\dev\\workspace\\wsdldoc\\src\\main\\resources\\"));
        cfg.setTemplateLoader(ftl1);

        Map<String, Object> rootMap = new HashMap<>();

        List<ServiceData> services = new ArrayList<>();
        Map<String, TypeData> finalTypesMap = new HashMap<>();

        for (Definitions defs : defsList) {
            Map<String, TypeDefinition> mapOfOriginalTypes = TypesLocator.createMapOfOriginalTypes(defs);
            Map<String, Element> mapOfElements = TypesLocator.createMapOfElements(defs);
            Map<String, TypeData> typesMap = TypesLocator.createMapOfTypes(defs, mapOfOriginalTypes, mapOfElements);

            List<ServiceData> servicesList = defs.getPortTypes().stream().map(p -> {
                ServiceData serviceData = new ServiceData();
                serviceData.setName(p.getName());
                ArrayList<MethodData> methods = new ArrayList<>();
                for (Operation op : p.getOperations()) {
                    MethodData md = new MethodData();
                    md.setName(op.getName());
                    // request
                    Input input = op.getInput();
                    md.setRequest(getComplexTypeNameOfInputOutput(input, defs, mapOfOriginalTypes, mapOfElements));
                    // response
                    Output output = op.getOutput();
                    md.setResponse(getComplexTypeNameOfInputOutput(output, defs, mapOfOriginalTypes, mapOfElements));
                    methods.add(md);
                }
                serviceData.setMethods(methods);
                return serviceData;
            }).collect(Collectors.toList());
            services.addAll(servicesList);

            // temporary delete some EI* types
            Set<String> eiNames = typesMap.keySet().stream().filter(k -> k.startsWith("EI")).collect(Collectors.toSet());
            for (String eiName : eiNames) {
                typesMap.remove(eiName);
            }
            // also remove SOABPException2
            typesMap.remove("SOABPException");
            typesMap.remove("SOABPException2");

            for (String name : typesMap.keySet()) {
                finalTypesMap.put(name, typesMap.get(name));
            }

        }
        rootMap.put("services", services);
        rootMap.put("types", finalTypesMap);
        rootMap.put("title", title);

        // process the template
        Template template = cfg.getTemplate("wsdldoc.ftl");

//        List<TypeData> collect = finalTypesMap.values().stream().filter(t -> t instanceof ComplexTypeData).filter(ct -> {
//            return ((ComplexTypeData) ct).getSequence() != null && ((ComplexTypeData) ct).getSequence().size() != 0 && ((ComplexTypeData) ct).getSequence().contains(null);
//        }).collect(Collectors.toList());

        template.process(rootMap, new FileWriter(outputFile));
    }

    public static ComplexTypeData getComplexTypeNameOfInputOutput(AbstractPortTypeMessage portTypeMessage, Definitions defs,
                                                         Map<String, TypeDefinition> mapOfOriginalTypes, Map<String, Element> mapOfElements) {
        String messageName = portTypeMessage.getMessagePrefixedName().getLocalName();
        String typeName = messageName;
        for (Message message : defs.getLocalMessages()) {
            if (message.getName().equals(messageName)) {
                if (message.getParts() != null) {
                    for (Part part : message.getParts()) {
                        Element element = part.getElement();
                        if (element != null) {
                            if (element.getType() != null) {
                                typeName = element.getType().getLocalPart();
                            } else if (element.getRef() != null) {
                                typeName = element.getRef().getLocalPart();
                            } else {
                                typeName = element.getName();
                            }
                        }
                    }
                }
            }
        }
        TypeDefinition originalRequestType = mapOfOriginalTypes.get(typeName);
        ComplexTypeData result = null;
        if (originalRequestType != null) {
            result = TypesMapper.map2ComplexType((ComplexType) originalRequestType, mapOfOriginalTypes, mapOfElements);
        } else {
            // check it's an element
            Element element = mapOfElements.get(typeName);
            if (element != null) {
                result = new ComplexTypeData();
                result.setName(typeName);
                TypeDefinition embeddedType = element.getEmbeddedType();
                if (embeddedType != null) {
                    if (embeddedType instanceof ComplexType) {
                        SchemaComponent model = ((ComplexType) embeddedType).getModel();
                        if (model instanceof Sequence) {
                            List<SchemaComponent> particles = ((Sequence) model).getParticles();
                            if (particles != null) {
                                ArrayList<ElementData> sequence = new ArrayList<>();
                                for (SchemaComponent particle : particles) {
                                    if (particle instanceof Element) {
                                        Element particleElement = (Element) particle;
                                        ElementData elementData = TypesMapper.map2Element(particleElement);
                                        sequence.add(elementData);
                                    }
                                }
                                result.setSequence(sequence);
                            }
                        }
                    }
                }
            }
        }
        if (result == null) {
            // fallback solution
            System.out.println("Input/Output " + messageName + " type and element " + typeName + " was not found in schemas");
            result = new ComplexTypeData();
            result.setName(typeName);
        }

        return result;
    }

}
