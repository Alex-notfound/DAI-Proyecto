package es.uvigo.esei.dai.hybridserver.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class SAXParsing {

	public static void parseAndValidateWithInternalXSD(String xmlPath, ContentHandler handler)
			throws ParserConfigurationException, SAXException, IOException {
		// ConstrucciÃ³n del parser del documento. Se activa
		// la validaciÃ³n y comprobaciÃ³n de namespaces
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		parserFactory.setNamespaceAware(true);

		// Se aÃ±ade el manejador de errores y se activa la validaciÃ³n por schema
		SAXParser parser = parserFactory.newSAXParser();
		parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
				XMLConstants.W3C_XML_SCHEMA_NS_URI);
		XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(handler);
		xmlReader.setErrorHandler(new SimpleErrorHandler());

		// Parsing
		try (FileReader fileReader = new FileReader(new File(xmlPath))) {
			xmlReader.parse(new InputSource(fileReader));
		}
	}

	public static void parseAndValidateWithExternalXSD(Reader characterStream, Reader schemaPath,
			ContentHandler handler) throws ParserConfigurationException, SAXException, IOException {
		// ConstrucciÃ³n del schema
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(new StreamSource(schemaPath));
		// ConstrucciÃ³n del parser del documento. Se establece el esquema y se activa
		// la validaciÃ³n y comprobaciÃ³n de namespaces
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(false);
		parserFactory.setNamespaceAware(true);
		parserFactory.setSchema(schema);

		// Se aÃ±ade el manejador de errores
		SAXParser parser = parserFactory.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(handler);
		xmlReader.setErrorHandler(new SimpleErrorHandler());

		// Parsing
		xmlReader.parse(new InputSource(characterStream));
	}
}
