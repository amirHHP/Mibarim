package com.mibarim.main.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Hamed on 2/26/2016.
 */
public class ApiResponse  implements Serializable {
    protected int StatusCode;
    public String Status;
    public int Count;
    public String Type;
    public List<MessageResponse> Errors;
    public List<MessageResponse> Warnings;
    public List<MessageResponse> Infos;
    public List<String> Messages;

}
