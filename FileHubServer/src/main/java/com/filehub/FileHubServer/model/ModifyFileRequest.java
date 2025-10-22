package com.filehub.FileHubServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModifyFileRequest {
    private String sPath;
    private String dPath;
}

