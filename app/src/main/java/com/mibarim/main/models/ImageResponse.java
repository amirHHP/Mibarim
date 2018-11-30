package com.mibarim.main.models;

import com.mibarim.main.models.enums.ImageTypes;

import java.io.Serializable;

/**
 * Created by Hamed on 4/12/2016.
 */
public class ImageResponse implements Serializable {
    public String ImageId;
    public ImageTypes ImageType;
    public String ImageFilePath;
    public String Base64ImageFile;
}
