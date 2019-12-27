package es.uvigo.esei.dai.hybridserver.parser;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SimpleErrorHandler implements ErrorHandler {

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		System.out.println("CIGa");
		exception.printStackTrace();
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		System.out.println("CIG");
		throw exception;
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		System.out.println("CIGc");
		throw exception;
	}

}
