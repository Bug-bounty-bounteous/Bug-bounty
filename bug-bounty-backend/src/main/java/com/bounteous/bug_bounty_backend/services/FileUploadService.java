package com.bounteous.bug_bounty_backend.services;

import com.bounteous.bug_bounty_backend.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {
    
    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir;
    
    @Value("${app.file.max-size:10485760}") // 10MB default
    private long maxFileSize;
    
    private static final String[] ALLOWED_EXTENSIONS = {
        ".pdf", ".doc", ".docx", ".txt", ".md", ".html", 
        ".mp4", ".avi", ".mov", ".zip", ".rar", ".tar.gz",
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".svg",
        ".mp3", ".wav", ".flac", ".ogg", ".aac", ".wma",
        ".ppt", ".pptx", ".xls", ".xlsx", ".csv", ".json",
    };
    
    /**
     * Upload a file and return the file path
     */
    public FileUploadResult uploadFile(MultipartFile file) throws IOException {
        // Validate file
        validateFile(file);
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return FileUploadResult.builder()
                .fileName(originalFilename)
                .filePath(filePath.toString())
                .fileSize(file.getSize())
                .build();
    }
    
    /**
     * Validate file before upload
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        
        if (file.getSize() > maxFileSize) {
            long maxSizeMB = maxFileSize / 1024 / 1024;
            throw new BadRequestException(
                "File size exceeds maximum limit of " + maxSizeMB + "MB. " +
                "Please provide a link to an external source instead."
            );
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new BadRequestException("Invalid filename");
        }
        
        String extension = getFileExtension(filename).toLowerCase();
        boolean isAllowed = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (extension.equals(allowedExt)) {
                isAllowed = true;
                break;
            }
        }
        
        if (!isAllowed) {
            throw new BadRequestException(
                "File type not allowed. Supported types: " + String.join(", ", ALLOWED_EXTENSIONS)
            );
        }
    }
    
    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
    
    /**
     * Delete a file
     */
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Log error but don't throw exception
            System.err.println("Error deleting file: " + filePath);
        }
    }
    
    /**
     * Get file content for download
     */
    public byte[] downloadFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new BadRequestException("File not found");
        }
        return Files.readAllBytes(path);
    }
    
    /**
     * Result of file upload operation
     */
    @lombok.Data
    @lombok.Builder
    public static class FileUploadResult {
        private String fileName;
        private String filePath;
        private Long fileSize;
    }
}