//package com.example.full.auth.and.jwt.controller;
//
//
//import com.example.full.auth.and.jwt.dto.ImageResponse;
//import com.example.full.auth.and.jwt.service.CloudinaryService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/images")
//@RequiredArgsConstructor
//public class ImageController {
//
//    private final CloudinaryService cloudinaryService;
//
//    @PostMapping("/upload")
//    public ResponseEntity<ImageResponse> uploadImage(
//            @RequestParam("image") MultipartFile file
//    ) throws IOException {
//
//        Map result = cloudinaryService.uploadImage(file);
//
//        ImageResponse response = ImageResponse.builder()
//                .url(result.get("secure_url").toString())
//                .publicId(result.get("public_id").toString())
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/{publicId}")
//    public ResponseEntity<String> deleteImage(
//            @PathVariable String publicId
//    ) throws IOException {
//
//        cloudinaryService.deleteImage(publicId);
//
//        return ResponseEntity.ok("Image deleted");
//    }
//}