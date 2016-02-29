package eu.xlime.mapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.xml.bind.annotation.XmlRootElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;

/**
 * 
 * @author Nuria Garcia (ngarcia@expertsystem.com)
 *
 */

@XmlRootElement
public class Resource {

	private List<DataResource> data;

	public List<DataResource> getData() {
		return data;
	}

	public void setData(List<DataResource> data) {
		this.data = data;
	}

	public void executeQueryDBpedia(String endpoint, String input, String q){		
		String query_string = q;	
		String input2 = "";
		this.data = new ArrayList<DataResource>();

		if(query_string.equals("")){
			//input with whole URL
			if(!input.startsWith("http://")){
				//input with abbreviations 'dbpedia:...', 'dbpedia-owl:...', 'dbo:..,'
				if(input.contains(":")){
					String word = input.substring(input.lastIndexOf(":") + 1);
					input = "http://dbpedia.org/ontology/" + word;
				}
				//input only the name of the resource 'i.e Madrid'
				else{
					input = "http://dbpedia.org/ontology/" + input;
				}
			}		
			input2 = input.replaceAll("/ontology/", "/resource/");
			String filter = "FILTER (regex(str(?wikidata), '^http://wikidata') || regex(str(?wikidata), '^http://www.wikidata'))";

			query_string = "select distinct * where {{<" + input + "> ?p ?wikidata. }"				
					+ "UNION {<" + input2 + "> ?p ?wikidata. }" + filter + "}";
		}	

		ResultSet results = null;		
		Query query = QueryFactory.create(query_string, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
		results = qexec.execSelect();

		QuerySolution soln1;
		String resource = "";
		String wikidata_resource;
		String property_resource;
		while (results.hasNext()) {			
			soln1 = results.nextSolution();
			if(!q.equals("")){
				resource =soln1.get("s").toString();
			}
			wikidata_resource = soln1.get("wikidata").toString();
			property_resource = soln1.get("p").toString();

			if(property_resource.equals("http://www.w3.org/2002/07/owl#equivalentClass")){
				DataResource dr = new DataResource();
				dr.setResource(input);
				if(!resource.equals("")){
					dr.setResource(resource);
				}
				if(!data.contains(dr)){					
					dr.setProperty("http://www.w3.org/2002/07/owl#equivalentClass");
					dr.setType("class");					
					dr.getWikidata().add(wikidata_resource);
					data.add(dr);
				}
				else{
					int index = data.indexOf(dr);
					data.get(index).getWikidata().add(wikidata_resource);
				}
			}	

			if(property_resource.equals("http://www.w3.org/2002/07/owl#equivalentProperty")){
				DataResource dr = new DataResource();
				dr.setResource(input);
				if(!resource.equals("")){
					dr.setResource(resource);
				}
				if(!data.contains(dr)){					
					dr.setProperty("http://www.w3.org/2002/07/owl#equivalentProperty");
					dr.setType("property");					
					dr.getWikidata().add(wikidata_resource);
					data.add(dr);
				}
				else{
					int index = data.indexOf(dr);
					data.get(index).getWikidata().add(wikidata_resource);
				}
			}

			if(property_resource.equals("http://www.w3.org/2002/07/owl#sameAs")){				
				DataResource dr = new DataResource();
				dr.setResource(input2);
				if(!resource.equals("")){
					dr.setResource(resource);
				}
				if(!data.contains(dr)){									
					dr.setProperty("http://www.w3.org/2002/07/owl#sameAs");
					dr.setType("instance");					
					dr.getWikidata().add(wikidata_resource);
					data.add(dr);
				}
				else{
					int index = data.indexOf(dr);
					data.get(index).getWikidata().add(wikidata_resource);
				}
			}
		}        
	}

	public void executeQueryFreebase(String url_str, String input){
		this.data = new ArrayList<DataResource>();
		if(input.contains("/")){
			input = input.substring(input.lastIndexOf('/') + 1);
		}
		if(input.contains(":")){
			input = input.substring(input.lastIndexOf(':') + 1);
		}
		if(input.contains(".")){
			input = input.substring(input.lastIndexOf('.') + 1);
		}
		input = "/m/" + input;		
		url_str = url_str + "?q=string[646:\"" + input + "\"]";
		//System.out.println(url_str);

		URL url;
		try {
			url = new URL(url_str);
			HttpURLConnection conn;
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				StringBuffer response = new StringBuffer();
				String str;
				//Printing response
				while ((str = rd.readLine()) != null) {
					response.append(str);
				}
				rd.close();				
				//System.out.println(response.toString());

				JSONObject json = new JSONObject(response.toString());
				JSONArray items = json.getJSONArray("items");
				for(int i=0; i<items.length(); i++){
					//System.out.println(items.get(i));
					String wikidata = "https://www.wikidata.org/wiki/Q" + items.get(i);
					DataResource dr = new DataResource();
					dr.setResource("https://www.freebase.com" + input);
					dr.setProperty("http://www.w3.org/2002/07/owl#sameAs");
					dr.setType("instance");	
					dr.getWikidata().add(wikidata);
					data.add(dr);
				}
			}			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}	
	}

	public void executeQueryImageNet(ServletContext context, String input){		
		InputStream is = context.getResourceAsStream("/WEB-INF/resources/words.properties");
		Properties props = new Properties();
		try {
			props.load(is);
			is.close();

			String synset = props.getProperty(input);

			if(synset != null){				
				String[] words = synset.split(", ");
				words[0] = words[0].substring(0, 1).toUpperCase() + words[0].substring(1);
				String query_string = "select distinct * where {{?s <http://www.w3.org/2000/01/rdf-schema#label> \"" + words[0] + "\"@en.} ";				

				for (int i=1; i<words.length; i++) {
					words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);					
					query_string += "UNION {?s <http://www.w3.org/2000/01/rdf-schema#label> \"" + words[i] + "\"@en.} ";
				}
				String filter = "FILTER (regex(str(?wikidata), '^http://wikidata') || regex(str(?wikidata), '^http://www.wikidata'))";
				query_string += "?s ?p ?wikidata." + filter + "}";

				this.executeQueryDBpedia("http://live.dbpedia.org/sparql", "", query_string);				
			}						
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void executeQueryWikipedia(String endpoint, String input){
		this.data = new ArrayList<DataResource>();
		Pattern p = Pattern.compile("title=.*");
		Matcher matcher = p.matcher(input);
		String url = input;

		if (matcher.find())
		{
			String concept = matcher.group(0);
			concept = concept.substring(concept.indexOf("=")+1);
			url = input.substring(0, input.lastIndexOf('/')) + "iki/" + concept;
			
		}
		url = url.replace("https", "http");
		
		String foaf = "<http://xmlns.com/foaf/0.1/isPrimaryTopicOf>";
		String owl = "<http://www.w3.org/2002/07/owl#sameAs>";
		String filter = "FILTER (regex(str(?wikidata), '^http://wikidata') || regex(str(?wikidata), '^http://www.wikidata'))";
		String query_string = "select distinct * where {?s " + foaf + " <" + url + ">. ?s " + owl + " ?wikidata. " + filter + "}";
		
		ResultSet results = null;		
		Query query = QueryFactory.create(query_string, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
		results = qexec.execSelect();

		QuerySolution soln1;
		String resource;
		String wikidata_resource;
		while (results.hasNext()) {	
			soln1 = results.nextSolution();
			resource = soln1.get("s").toString();
			wikidata_resource = soln1.get("wikidata").toString();
				
			DataResource dr = new DataResource();
			dr.setResource(resource);
			if(!data.contains(dr)){
				dr.setProperty("http://www.w3.org/2002/07/owl#sameAs");
				dr.setType("instance");
				dr.getWikidata().add(wikidata_resource);								
				data.add(dr);				
			}
			else{
				int index = data.indexOf(dr);
				data.get(index).getWikidata().add(wikidata_resource);				
			}
		}
	}
}
