package com.filehub.FileHubServer.controller;

import com.filehub.FileHubServer.FMUtil;
import com.filehub.FileHubServer.model.ApiResponse;
import com.filehub.FileHubServer.model.ModifyFileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FMUtil FMUtil;

    public FileController(FMUtil FMUtil)
    {
        this.FMUtil = FMUtil;
    }


    @GetMapping("/filelist")
    public ResponseEntity<?> getFileList(@RequestParam String filePath)
    {
        return FMUtil.getFilesList(filePath);
    }


    @GetMapping("downloadfile")
    public ResponseEntity<?> sendFile(@RequestParam String filePath)
    {
        System.out.println("downloadfiles");

        return FMUtil.getFile(filePath);
    }

    @PostMapping(value = "/uploadfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam String toPath, @RequestParam("file") MultipartFile file) {
        return FMUtil.putFile(toPath,file);
    }

    @GetMapping("/viewfile")
    public ResponseEntity<Resource> viewFile(@RequestParam String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) return ResponseEntity.notFound().build();

        Resource resource = new FileSystemResource(file);
        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) mimeType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    @PostMapping("/movefile")
    public ResponseEntity<ApiResponse<String>> moveFile(@RequestBody ModifyFileRequest request)
    {
        String src = request.getSPath();
        String dest = request.getDPath();
        Path sourcePath = Paths.get(src); // file to move
        Path targetPath = Paths.get(dest); // destination path
        if (Files.isDirectory(targetPath)) {
            targetPath = targetPath.resolve(sourcePath.getFileName());
        }
        try {
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return ResponseEntity.ok(new ApiResponse<>(200,"File not moved",null));
        }
        return ResponseEntity.ok(new ApiResponse<>(200,"File moved",null));
    }

    @PostMapping("/copyfile")
    public ResponseEntity<ApiResponse<String>> copyFile(@RequestBody ModifyFileRequest request) {
        System.out.println("copyFile: " + request.toString());

        String src = request.getSPath();
        String dest = request.getDPath();

        Path sourcePath = Paths.get(src);
        Path targetPath = Paths.get(dest);
        if (Files.isDirectory(targetPath)) {
            targetPath = targetPath.resolve(sourcePath.getFileName());
        }
        System.out.println(targetPath);
        try {
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ApiResponse<>(500, "File not copied: " + e.getMessage(), null));
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "File copied successfully", null));
    }

    @PostMapping("/rename")
    public ResponseEntity<ApiResponse<String>> renameFileOrFolder(@RequestBody ModifyFileRequest request) {
        try {
            System.out.println("rename: "+request.toString());
            Path sourcePath = Paths.get(request.getSPath()); // current path
            Path targetPath;


            targetPath = sourcePath.resolveSibling(request.getDPath());


            // Check if source exists
            if (!Files.exists(sourcePath)) {
                return ResponseEntity.ok(new ApiResponse<>(404, "Source not found", null));
            }

            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok(new ApiResponse<>(200, "Renamed successfully", null));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ApiResponse<>(500, "Error renaming: " + e.getMessage(), null));
        }
    }


}
