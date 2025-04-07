package com.trippia.travel.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class FileResponse {
    private boolean isUploaded;
    private String url;
    private String message;
}
