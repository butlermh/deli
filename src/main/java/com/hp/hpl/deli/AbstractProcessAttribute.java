package com.hp.hpl.deli;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

class AbstractProcessAttribute {
	
	/**
	 * @param statement
	 * @return Does this statement contain a simple attribute, sequence or bag?
	 */
	static String determineContainerType(RDFNode resource) {
		if (!(resource instanceof Literal)) {
			Resource typeNode = resource.asResource();
			if (isContainer(typeNode, Constants.SEQ)) {
				return Constants.SEQ;
			}
			if (isContainer(typeNode, Constants.BAG)) {
				return Constants.BAG;
			}
		}
		return Constants.SIMPLE;
	}
	
	/**
	 * @param resource
	 * @param container
	 * @return Does resource match the container type?
	 */
	static boolean isContainer(Resource resource, String container) {
		return resource.hasProperty(RDF.type,
				resource.getModel().createResource(RDF.getURI() + container));
	}
	
	/**
	 * @param resource
	 * @return an iterator to the container
	 */
	static NodeIterator getContainerIterator(Resource resource) throws ProfileProcessingException {
		Model model = resource.getModel();
		if (isContainer(resource, Constants.SEQ)) {
			return model.getSeq(resource).iterator();
		} else if (isContainer(resource, Constants.BAG)) {
			return model.getBag(resource).iterator();
		}
		throw new ProfileProcessingException("Could not determine container type");
	}
}
