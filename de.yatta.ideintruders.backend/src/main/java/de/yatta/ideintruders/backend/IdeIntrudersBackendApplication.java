package de.yatta.ideintruders.backend;

import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@RestController
public class IdeIntrudersBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdeIntrudersBackendApplication.class, args);
	}

   @GetMapping("/queryLicense")
   @CrossOrigin(origins = "*")
   public boolean queryLicense(@RequestParam(value = "userId", required = false) String userId,
         @RequestParam(value = "sessionToken", required = false) String sessionToken)
         throws JsonMappingException, JsonProcessingException {

      // String url = "http://localhost:8013/chckout/v1/vendor/license/checkout";
      String url = "https://stage.platform.yatta.de/chckout/v1/vendor/license/checkout";
      // String url = "https://www.yatta.de/checkout/v1/vendor/license/checkout";
      // String productId = "de.softwarevendor.product";
      String productId = "577a4546-dc60-41a9-a8ca-3929a055213a"; // "c1bae4a0-3b25-45bf-9d3f-994631dec3cc";
      // String productId = "330da901-e116-4ca2-a301-2903552bf51d";

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBasicAuth(productId, "DEMO");

      JSONObject jsonObject = new JSONObject();
      jsonObject.put("environment", "preview");
      jsonObject.put("productId", productId);
      //jsonObject.put("featureId", "de.softwarevendor.product");
      //jsonObject.put("version", "1.0.0");
      jsonObject.put("accountId", userId);
      jsonObject.put("sessionToken", sessionToken);
      jsonObject.put("durationMinutes", 120);

      HttpEntity<String> request = new HttpEntity<String>(jsonObject.toString(), headers);

      RestTemplate restTemplate = new RestTemplate();
      String result;
      try {
         result = restTemplate.postForObject(url, request, String.class);
      } catch (RestClientException e) {
         System.out.println("Not Found");
         return false;
      }
      ObjectMapper mapper = new ObjectMapper();
      JsonNode tree = mapper.readTree(result);

      System.out.println(tree.get("validity").asText());

      return tree.get("validity").asText().equals("LICENSED");
   }
}
