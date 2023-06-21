# How to use Yatta OAuth

## Basics

1. **Download the Spring Boot OAuth2 Client sample:** Visit the following link to download the sample code: [GitHub Repository](https://github.com/YattaSolutions/de.yatta.ide-intruders-webgame/tree/main/springboot-oauth2-client-sample).

2. **Register with Yatta OAuth:** Send an email to [checkout@yatta.de](mailto:checkout@yatta.de) and provide the following information:
    - Client name
    - Client logo (PNG or SVG, at least 128 x 128)
    - Client description
    - Client main URL & list of redirect URLs
    - Choose between public client or confidential client (For more information, click [here](https://condatis.com/news/blog/oauth-confidential-clients/))

   Once registered, you will receive:
    - Client ID
    - Client Secret

3. **Update the application.properties file:** Open the file `./src/main/resources/application.properties` and replace the following values with your Client ID and Client Secret:
```properties
   # Replace with your client id and client secret.
   spring.security.oauth2.client.registration.yatta.client-id=<clientID>
   spring.security.oauth2.client.registration.yatta.client-secret=<clientSecret>
```

4. **Start the Spring Boot Application:** Run the Spring Boot Application. Once it's running, open your browser and visit the following URL: [http://localhost:8080/api/v1/sample](http://localhost:8080/api/v1/sample). This will provide you with the user ID, user email address, and user access token.
