package eu.xlime.mapping;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 
 * @author Nuria Garcia (ngarcia@expertsystem.com)
 *
 */

@Path("/mapping")
public class InputResource {
	
    @Context
    private ServletContext context;

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response service(@QueryParam("resource") String resource, @QueryParam("source") String source, @QueryParam("format") String format) {
		String resource_string = "";
		String source_string = "dbpedia";
		if (resource != null) {
			resource_string = resource;
		}	
		if (source != null) {
			source_string = source;
		}

		Resource r = new Resource();	
		if(source_string.equals("dbpedia")){
			r.executeQueryDBpedia("http://live.dbpedia.org/sparql", resource_string, "", false);
		}
		if(source_string.equals("freebase")){
			r.executeQueryFreebase("https://wdq.wmflabs.org/api", resource_string);
		}
		if(source_string.equals("imagenet")){
			//r.executeQueryImageNet(context, resource_string);
			r.executeQueryImageNetBabelNet(context, resource_string);
		}
		if(source_string.equals("wikipedia")){
			r.executeQueryWikipedia("http://live.dbpedia.org/sparql", resource_string);
		}

		return Response
				// Set the status, entity and media type of the response.
				.ok(r, "json".equals(format) ? MediaType.APPLICATION_JSON : MediaType.APPLICATION_XML)
				.build();
	}	
}

//Examples DBpedia
//http://expertsystem:8080/wikidataMapper/services/mapping?resource=Madrid&format=json
//http://localhost:8080/wikidataMapper/services/mapping?resource=Madrid
//http://localhost:8080/wikidataMapper/services/mapping?resource=http://dbpedia.org/resource/Madrid&format=json
//http://localhost:8080/wikidataMapper/services/mapping?resource=dbpedia:Madrid
//http://localhost:8080/wikidataMapper/services/mapping?resource=Event&format=json
//http://localhost:8080/wikidataMapper/services/mapping?resource=http://dbpedia.org/ontology/Person&format=json
//http://localhost:8080/wikidataMapper/services/mapping?resource=http://dbpedia.org/ontology/Place&format=json
//http://localhost:8080/wikidataMapper/services/mapping?resource=dbpedia-owl:team&format=json
//http://localhost:8080/wikidataMapper/services/mapping?resource=date&format=json
//http://localhost:8080/wikidataMapper/services/mapping?resource=dbpedia-owl:birthDate&format=json

//Examples Freebase
//http://localhost:8080/wikidataMapper/services/mapping?resource=http://rdf.freebase.com/ns/m.02mjmr&source=freebase
//http://localhost:8080/wikidataMapper/services/mapping?resource=/m/0dgw9r&source=freebase
//http://localhost:8080/wikidataMapper/services/mapping?resource=freebase:m.02k6pd&source=freebase

//Examples ImageNet
//http://localhost:8080/wikidataMapper/services/mapping?resource=n02935658&source=imagenet

//Examples Wikipedia
//http://localhost:8080/wikidataMapper/services/mapping?resource=https://en.wikipedia.org/w/index.php?title=Criticism_of_the_United_States_government&diff=707311006&oldid=706767407&source=wikipedia