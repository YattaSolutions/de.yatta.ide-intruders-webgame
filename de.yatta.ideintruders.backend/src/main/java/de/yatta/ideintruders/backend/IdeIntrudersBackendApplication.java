package de.yatta.ideintruders.backend;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@ComponentScan(basePackageClasses = { SecurityConfig.class })
@RestController
public class IdeIntrudersBackendApplication
{
   @Value("${yatta.license.endpoint}")
   private String licenseEndpoint;
   
   @Value("${yatta.product_id.vendor_iam}")
   private String productIdVendorIAM;

   @Value("${yatta.product_id.yatta_iam}")
   private String productIdYattaIAM;

   @Value("${yatta.vendor.api_key}")
   private String vendorApiKey;

   public static void main(String[] args)
   {
      SpringApplication.run(IdeIntrudersBackendApplication.class, args);
   }

   @GetMapping("/queryLicense")
   @CrossOrigin(origins = "*")
   public boolean queryLicense(@RequestParam(value = "userId", required = false) String userId)
         throws JsonProcessingException
   {
      String productId = userId == null ? productIdYattaIAM : productIdVendorIAM;

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication instanceof OAuth2AuthenticationToken token)
      {
         userId = token.getName();
      }

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBasicAuth(productId, vendorApiKey);

      JSONObject jsonObject = new JSONObject();
      jsonObject.put("environment", "preview");
      jsonObject.put("productId", productId);
      //jsonObject.put("featureId", "de.softwarevendor.product");
      //jsonObject.put("version", "1.0.0");
      jsonObject.put("accountId", userId);
      jsonObject.put("durationMinutes", 120);

      HttpEntity<String> request = new HttpEntity<String>(jsonObject.toString(), headers);

      RestTemplate restTemplate = new RestTemplate();
      String result;
      try
      {
         result = restTemplate.postForObject(licenseEndpoint, request, String.class);
      }
      catch (RestClientException e)
      {
         System.out.println("Not Found: " + e.getMessage());
         return false;
      }
      ObjectMapper mapper = new ObjectMapper();
      JsonNode tree = mapper.readTree(result);

      System.out.println(tree.get("validity").asText());

      return tree.get("validity").asText().equals("LICENSED");
   }
}
