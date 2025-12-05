package com.onboard.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileUploadService {

    private final SupabaseStorageService supabaseStorageService;

    public FileUploadService(SupabaseStorageService supabaseStorageService) {
        this.supabaseStorageService = supabaseStorageService;
    }

    public String uploadProfilePhoto(MultipartFile file, String userId) throws IOException {
        String fileName = userId + "/" + file.getOriginalFilename();
        return supabaseStorageService.uploadFile(file, fileName, "perfil");
    }

    public List<String> uploadVehiclePhotos(MultipartFile[] files, String vehicleId) throws IOException {
        List<String> photoUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = "vehicle/" + vehicleId + "/" + file.getOriginalFilename();
            String fileUrl = supabaseStorageService.uploadFile(file, fileName, "vehiculos");
            photoUrls.add(fileUrl);
        }
        return photoUrls;
    }

    public void deletePhotoByUrl(String fileUrl) {
        supabaseStorageService.deleteFileByUrl(fileUrl);
    }

    public String uploadDocumentAsPdf(MultipartFile file, String userId, String carId, String documentName)
            throws IOException {
        String fileName = "documents/" + userId + "/" + carId + "/" + documentName;
        return supabaseStorageService.uploadDocumentAsPdf(file, fileName, "documentos");
    }

}