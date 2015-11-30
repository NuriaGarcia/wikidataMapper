package eu.xlime.mapping;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Nuria Garcia (ngarcia@expertsystem.com)
 *
 */

@XmlRootElement
public class DataResource {

	private String resource;	
	private String type;	
	private String property;
	private List<String> wikidata;

	public DataResource(){
		this.wikidata = new ArrayList<String>();
	}

	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}	
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public List<String> getWikidata() {
		return wikidata;
	}	
	public void setWikidata(List<String> wikidata) {
		this.wikidata = wikidata;
	}	

	@Override
	public boolean equals(Object o){
		DataResource dr = (DataResource) o;
		if(this.resource.equals(dr.getResource())){
			return true;
		}
		return false;
	}
}
