package edu.course.city.social.provider;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.exception.SocialAuthException;
import org.brickred.socialauth.provider.GenericOAuth2Provider;
import org.brickred.socialauth.util.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class VKontakteImpl extends GenericOAuth2Provider {

    private final static String API_VERSION = "5.29";

    private final static String PROFILE_URL = "https://api.vk.com/method/users.get";

    public VKontakteImpl(OAuthConfig providerConfig) throws Exception {
        super(providerConfig);
    }

    @Override
    public Profile getUserProfile() throws Exception {
        AccessGrant accessGrant = getAccessGrant();
        String accessToken = accessGrant.getKey();
        String userId = (String) accessGrant.getAttribute("user_id");

        StringBuilder profileRequestURL = new StringBuilder(PROFILE_URL);
        profileRequestURL.append("?").append("v=").append(API_VERSION);
        profileRequestURL.append("&").append("access_token=").append(accessToken);
        profileRequestURL.append("&").append("user_id=").append(userId);

        Response response = HttpUtil.doHttpRequest(profileRequestURL.toString(), MethodType.GET.toString(), null, null);
        String result;
        try {
            result = response.getResponseBodyAsString(Constants.ENCODING);
        } catch (IOException io) {
            throw new SocialAuthException(io);
        }

        Profile profile = new Profile();
        try {
            JSONObject user = new JSONObject(result).getJSONArray("response").getJSONObject(0);
            profile.setValidatedId(user.getString("id"));
            profile.setFirstName(user.getString("first_name"));
            profile.setLastName(user.getString("last_name"));
            profile.setEmail((String) accessGrant.getAttribute("email"));
        } catch (JSONException je) {
            throw new SocialAuthException("Unexpected auth response from " + profileRequestURL);
        }
        return profile;
    }
}
