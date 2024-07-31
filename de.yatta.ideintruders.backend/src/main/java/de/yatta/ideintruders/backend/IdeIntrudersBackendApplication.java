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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

   @Value("${yatta.jwks.endpoint}")
   private String jwksEndpoint;

   @Value("${yatta.vendor.api_key}")
   private String vendorApiKey;

   public static void main(String[] args)
   {
      SpringApplication.run(IdeIntrudersBackendApplication.class, args);
   }

   @GetMapping("/queryLicense")
   @CrossOrigin(origins = "*")
   public boolean queryLicense(
         @RequestParam(value = "userId", required = false) String internalUserId,
         @RequestParam(value = "productId", required = true) String productId)
   {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication instanceof OAuth2AuthenticationToken token)
      {
         // In case OAuth is used, we can assume the linked account id
         // is the same as the internal user id.
         internalUserId = token.getName();
      }

      return checkLicenseServer(internalUserId, productId);
   }

   @PostMapping("/queryLicenseSession")
   @CrossOrigin(origins = "*")
   public boolean queryLicense(@RequestBody LicenseQuery query)
   {
      var jwt = getDecodedJwt(query.sessionToken());

      String accountId = jwt.getSubject();
      String productId = jwt.getClaimAsString("productId");

      return checkLicenseServer(accountId, productId);
   }

   private boolean checkLicenseServer(String accountId, String productId)
   {
      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBasicAuth(productId, vendorApiKey);

      JSONObject jsonObject = new JSONObject();
      jsonObject.put("environment", "preview");
      jsonObject.put("productId", productId);
      jsonObject.put("accountId", accountId);
      jsonObject.put("durationMinutes", 120);

      HttpEntity<String> licenseRequest = new HttpEntity<>(jsonObject.toString(), headers);

      String result;
      try
      {
         result = restTemplate.postForObject(licenseEndpoint, licenseRequest, String.class);
         ObjectMapper mapper = new ObjectMapper();
         JsonNode tree = mapper.readTree(result);
         System.out.println(tree.get("validity").asText());

         return tree.get("validity").asText().equals("LICENSED");
      }
      catch (RestClientException | JsonProcessingException e)
      {
         System.out.println("Not Found: " + e.getMessage());
      }

      return false;
   }

   private Jwt getDecodedJwt(String sessionToken)
   {
      var jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwksEndpoint).build();

      return jwtDecoder.decode(sessionToken);
   }
}
