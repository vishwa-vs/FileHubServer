package com.filehub.FileHubServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileData {
    private int id;
    private String fileName;
    private String filePath;
    private String fileSize;
    private String lastModified;
    private String fileFormat;
    private boolean isFile;
}
