package com.trippia.travel.file;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FileResponse {
    private boolean isUploaded;
    private String url;
    private String message;
}
