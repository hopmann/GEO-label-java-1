/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.geolabel.commons;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;


@XmlRootElement(name = "geoLabel")
public class Label {

	private static class FacetHolder {
		@XmlElementRef
		protected ProducerProfileFacet producerProfileFacet = new ProducerProfileFacet();
		@XmlElementRef
		protected ProducerCommentsFacet producerCommentsFacet = new ProducerCommentsFacet();
		@XmlElementRef
		protected LineageFacet lineageFacet = new LineageFacet();
		@XmlElementRef
		protected StandardsComplianceFacet standardsComplianceFacet = new StandardsComplianceFacet();
		@XmlElementRef
		protected QualityInformationFacet qualityInformationFacet = new QualityInformationFacet();
		@XmlElementRef
		protected UserFeedbackFacet userFeedbackFacet = new UserFeedbackFacet();
		@XmlElementRef
		protected ExpertFeedbackFacet expertFeedbackFacet = new ExpertFeedbackFacet();
		@XmlElementRef
		protected CitationsFacet citationsFacet = new CitationsFacet();
	}

	final static Logger log = LoggerFactory.getLogger(Label.class);

	@XmlElement(name = "facets")
	private FacetHolder facetHolder = new FacetHolder();

	private static Unmarshaller unmarshaller;
	private static Marshaller marshaller;
	private static Template svgTemplate;

	public ProducerProfileFacet getProducerProfileFacet() {
		return facetHolder.producerProfileFacet;
	}

	public LineageFacet getLineageFacet() {
		return facetHolder.lineageFacet;
	}

	public ProducerCommentsFacet getProducerCommentsFacet() {
		return facetHolder.producerCommentsFacet;
	}

	public StandardsComplianceFacet getStandardsComplianceFacet() {
		return facetHolder.standardsComplianceFacet;
	}

	public QualityInformationFacet getQualityInformationFacet() {
		return facetHolder.qualityInformationFacet;
	}

	public FeedbackFacet getUserFeedbackFacet() {
		return facetHolder.userFeedbackFacet;
	}

	public FeedbackFacet getExpertFeedbackFacet() {
		return facetHolder.expertFeedbackFacet;
	}

	public CitationsFacet getCitationsFacet() {
		return facetHolder.citationsFacet;
	}

	public static Label fromXML(InputStream input) throws IOException {
		try {
			if (unmarshaller == null)
				unmarshaller = JAXBContext.newInstance(Label.class).createUnmarshaller();

			Label label = (Label) unmarshaller.unmarshal(input);
			return label;
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}

	public void toXML(OutputStream outputStream) throws IOException {
		try {
			if (marshaller == null)
				marshaller = JAXBContext.newInstance(Label.class).createMarshaller();

			marshaller.marshal(this, outputStream);
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Obtains freemarker template for geolabel from classpath and caches
	 * result.
	 * 
	 * @return
	 * @throws IOException
	 */
	private static Template getSVGTemplate() throws IOException {
		if (svgTemplate == null) {
			Configuration configuration = new Configuration();
			configuration.setClassForTemplateLoading(Label.class, "");
			svgTemplate = configuration.getTemplate("geolabel.ftl");
		}
		return svgTemplate;
	}

	public void toSVG(Writer writer, final String id, final int size) throws IOException {
		try {
			SimpleHash model = new SimpleHash();
			model.put("size", size);
			model.put("id", id);
			model.put("label", this);

			getSVGTemplate().process(model, writer);
		} catch (TemplateException e) {
			log.error("Error in geolabel template", e);
			throw new IOException("Unable to process SVG template");
		}
	}

}
