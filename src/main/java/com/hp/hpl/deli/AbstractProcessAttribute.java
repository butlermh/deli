package com.hp.hpl.deli;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Abstract class for processing attributes.
 */
abstract class AbstractProcessAttribute {
	
	/**
	 * @param resource The RDF node with the container.
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
	 * Does this node have a container of a specific type?
	 * 
	 * @param resource The resource to test.
	 * @param container The container type.
	 * @return Does resource match the container type?
	 */
	static boolean isContainer(Resource resource, String container) {
		return resource.hasProperty(RDF.type,
				resource.getModel().createResource(RDF.getURI() + container));
	}
	
	/**
	 * @param resource The resource with the container.
	 * @return an iterator to the container
	 * @throws ProfileProcessingException Thrown if the container does not have a known type.
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
