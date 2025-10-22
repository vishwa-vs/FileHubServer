package com.filehub.FileHubServer;

import com.filehub.FileHubServer.model.ApiResponse;
import com.filehub.FileHubServer.model.FileData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

import static com.filehub.FileHubServer.FMConstants.*;

@Service
public class FMUtil {

    @Autowired
    private Utils utils;
    public ResponseEntity<ApiResponse> getFilesList(String path)
    {
        try{
            if (path == null || path.isBlank() || path.equalsIgnoreCase("initial"))
                path = INITIAL_PATH;
            ArrayList<FileData> al = new ArrayList<>();
            File file = new File(path);
            if (!file.exists())
                return ResponseEntity.status(404).body(new ApiResponse(404,FILE_NOT_FOUND,al));
            al = getFileList(file);
            return ResponseEntity.ok().body(new ApiResponse<>(200,FILE_FOUND,al));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(500,INTERNAL_SERVER_ERROR,e.getMessage()));
        }
    }


    public ArrayList<FileData> getFileList(File file)
    {
        ArrayList<FileData> al = new ArrayList<>();
        try{
            int id = 1;
            for (File f: Objects.requireNonNull(file.listFiles()))
            {
                if (f == null)
                    continue;
                String fName = f.getName();
                String fPath = f.getPath();
                long fSize = f.length();
                long lastMod = f.lastModified();
                boolean isFile = f.isFile();
                String fileSize = fSize +" B";
                if (fSize > 1000)
                {
                    fSize /= 1000;
                    fileSize = fSize + " KB";
                    if (fSize > 1024)
                    {
                        fSize /= 1000;
                        fileSize = fSize + " MB";
                        if (fSize > 1024)
                        {
                            fSize /= 1000;
                            fileSize = fSize + " GB";
                        }
                    }
                }

                String lastModTime = utils.millisToTime(lastMod);
                String fileFormat = "file";
                int lastDot = fName.lastIndexOf('.');
                if (lastDot > 0 && lastDot < fName.length() - 1) {
                    fileFormat = fName.substring(lastDot+1);
                }

                if (!isFile)
                {
                    fileSize = "";
                    fileFormat = "";
                }
                FileData fileData = new FileData(id,fName,fPath,fileSize,lastModTime,fileFormat,isFile);
                al.add(fileData);
                id++;
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return al;
    }

    public ResponseEntity<?> getFile(String filePath)
    {
        System.out.println("sending file: "+filePath);
        try{
            File file = new File(filePath);
            if (!file.exists())
                return ResponseEntity.status(404).body(new ApiResponse<>(404,FILE_NOT_FOUND,null));
            if (file.isDirectory())
                return ResponseEntity.status(400).body(new ApiResponse<>(400,"Folder can't be downloaded",null));

            Resource resource = new FileSystemResource(file);

            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType == null) {
                mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // fallback
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .contentLength(file.length())
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(500,INTERNAL_SERVER_ERROR,null));
        }
    }

    public ResponseEntity<ApiResponse> putFile(String toPath, MultipartFile file)
    {
        try
        {
            Path uploadDir = Paths.get(toPath != null && !toPath.isBlank() ? toPath : "E:\\Uploads");
            Files.createDirectories(uploadDir);
            Path target = uploadDir.resolve(file.getOriginalFilename());
            Files.write(target, file.getBytes());
            return ResponseEntity.ok(new ApiResponse<>(200, "File uploaded successfully", file.getOriginalFilename()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Upload failed", e.getMessage()));
        }
    }


}
