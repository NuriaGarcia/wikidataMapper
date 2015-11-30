# wikidataMapper
A resource mapping service that enables the matching of different entity resources to their equivalent in Wikidata

The purpose of this service is to provide an easy way to retrieve equivalent mappings to Wikidata for different resources. This allows individual partners to develop or use  annotation  services  targeting  various  different  knowledge  bases  such  as Wikipedia,  DBpedia  or ImageNet, while being able to relate these resources using Wikidata as a central linking hub. In essence, we standardize  the  URI  mapping  of  the  resources to  Wikidata  thanks  to  this  framework. Currently the service includes a set of mappings:

 - Mapping DBpedia to Wikidata
 - Mapping Freebase to Wikidata
 - Mapping ImageNet/WordNet to Wikidata
 
The basic functionality of the API is to receive a resource identifier as input and the system will return any output resources found in XML or JSON format.

API Usage

- Mapping DBpedia to Wikidata:

http://localhost:8080/wikidataMapper/services/mapping?resource=http://dbpedia.org/resource/Tim_Berners-Lee&format=json
http://localhost:8080/wikidataMapper/services/mapping?resource=dbpedia:Tim_Berners-Lee
http://localhost:8080/wikidataMapper/services/mapping?resource=Tim_Berners-Lee&format=json
http://localhost:8080/wikidataMapper/services/mapping?resource=http://dbpedia.org/ontology/country
http://localhost:8080/wikidataMapper/services/mapping?resource=dbpedia:Person&format=json

 - Mapping Freebase to Wikidata
 
http://localhost:8080/wikidataMapper/services/mapping?resource=http://rdf.freebase.com/ns/m.07d5b&source=freebase&format=json
http://localhost:8080/wikidataMapper/services/mapping?resource=freebase:m.07d5b&source=freebase
http://localhost:8080/wikidataMapper/services/mapping?resource=/m/07d5b&source=freebase&format=json
http://localhost:8080/wikidataMapper/services/mapping?resource=m.0dgw9r&source=freebase

- Mapping ImageNet to Wikidata

http://localhost:8080/wikidataMapper/services/mapping?resource=n00017222&source=imagenet&format=json
http://localhost:8080/wikidataMapper/services/mapping?resource=n00017222&source=imagenet
http://localhost:8080/wikidataMapper/services/mapping?resource=n00015388&source=imagenet&format=json
http://localhost:8080/wikidataMapper/services/mapping?resource=n00007846&source=imagenet&format=json
