package com.trippia.travel.file;

import com.trippia.travel.exception.file.FileException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?>uploadFile(@RequestParam("upload") MultipartFile file){
        try{
            return ResponseEntity.ok(fileService.uploadFile(file));
        } catch(FileException e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
