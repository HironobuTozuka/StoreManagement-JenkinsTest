package inc.roms.rcs.service.omnichannel.v1.config;

import inc.roms.rcs.service.omnichannel.config.OmniChannelProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AuthRequest3EFactory {

    private String password;
    private String username;
    private String grantType;
    private String scope;
    private String clientId;
    private String clientPassword;

    public AuthRequest3EFactory(OmniChannelProperties omniChannelProperties) {
        this.password = omniChannelProperties.getPassword();
        this.username = omniChannelProperties.getUsername();
        this.grantType = "password";
        this.scope = "write";
        this.clientId = omniChannelProperties.getClientId();
        this.clientPassword = omniChannelProperties.getClientPassword();
    }

    public Object getRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth("cm9tczpzZWNyZXQ=");

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("password", this.password);
        map.add("username", this.username);
        map.add("grant_type", this.grantType);
        map.add("scope", this.scope);
        map.add("client_id", this.clientId);
        map.add("client_password", this.clientPassword);

        return new HttpEntity<>(map, headers);
    }
}
