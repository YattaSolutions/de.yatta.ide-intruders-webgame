package de.yatta.ideintruders.backend;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record LicenseQuery(String sessionToken, String productId)
{

}
