package com.mibarim.main.models;

import java.io.Serializable;

/**
 * Created by Hamed on 3/1/2016.
 */
public class TokenResponse implements Serializable {
    public String access_token;
    public String token_type;
    public String userName;
    public String name;
    public String family;
    public String error;
    public boolean isMobileConfirmed;
}
