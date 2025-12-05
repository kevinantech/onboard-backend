package com.onboard.backend.service;

import okhttp3.*;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import com.onboard.backend.exception.InvalidInputException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;

@Service
public class SupabaseStorageService {

    private static final String SUPABASE_URL = "https://vbtvwszlngotrdoqcanb.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZidHZ3c3psbmdvdHJkb3FjYW5iIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEyNjQ3OTAsImV4cCI6MjA3Njg0MDc5MH0.-UzbPEwPj46xyUEWnRn1lefTy4kv1MDQESOPZqf1HwI";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Logger logger = LoggerFactory.getLogger(SupabaseStorageService.class);

    public String uploadFile(MultipartFile file, String filePath, String bucketName) throws IOException {

        byte[] fileBytes = convertToJpegAndCompress(file, 0.5f);

        if (!filePath.toLowerCase().endsWith(".jpg")) {
            filePath += ".jpg";
        }

        String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + bucketName + "/" + filePath;

        RequestBody requestBody = RequestBody.create(fileBytes, MediaType.parse("image/jpeg"));
        Request request = new Request.Builder()
                .url(uploadUrl)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .addHeader("x-upsert", "true")
                .put(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("Failed to upload image. Status: {}, Body: {}",
                        response.code(),
                        response.body() != null ? response.body().string() : "null");
                throw new InvalidInputException(
                        "Image upload failed",
                        "UPLOAD_FAILED",
                        "Image upload failed due to a service response error. Please contact the administrator.");
            }
            return SUPABASE_URL + "/storage/v1/object/public/" + bucketName + "/" + filePath;

        } catch (IOException e) {
            logger.error("Unexpected error while uploading image", e);
            throw new InvalidInputException(
                    "Unexpected error",
                    "UPLOAD_IO_EXCEPTION",
                    "An unexpected I/O error occurred while uploading the image. Please contact the administrator.");
        }

    }

    private byte[] convertToJpegAndCompress(MultipartFile file, float quality) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());

        if (image == null) {
            throw new InvalidInputException(
                    "Invalid image file",
                    "INVALID_IMAGE_FILE",
                    "The uploaded file could not be read as a valid image.");
        }

        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) {
            if (!writers.hasNext()) {
                throw new InvalidInputException(
                        "Image compression error",
                        "JPEG_WRITER_NOT_FOUND",
                        "No JPEG writer available in the current environment to compress image.");
            }

        }

        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        writer.setOutput(new MemoryCacheImageOutputStream(compressed));
        writer.write(null, new IIOImage(image, null, null), param);
        writer.dispose();

        return compressed.toByteArray();
    }

    public void deleteFileByUrl(String fileUrl) {
        try {

            String prefix = SUPABASE_URL + "/storage/v1/object/public/";
            if (!fileUrl.startsWith(prefix)) {
                throw new InvalidInputException(
                        "Invalid image URL",
                        "INVALID_IMAGE_URL",
                        "The provided image URL is not valid or doesn't belong to Supabase public storage.");
            }

            String path = fileUrl.substring(prefix.length());
            int slashIndex = path.indexOf("/");
            if (slashIndex == -1) {
                throw new InvalidInputException(
                        "Malformed image URL",
                        "MALFORMED_IMAGE_URL",
                        "The provided image URL is malformed or missing the file path.");
            }

            String bucketName = path.substring(0, slashIndex);
            String filePath = path.substring(slashIndex + 1);

            String deleteUrl = SUPABASE_URL + "/storage/v1/object/" + bucketName + "/" + filePath;

            Request request = new Request.Builder()
                    .url(deleteUrl)
                    .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                    .delete()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("Failed to delete image. Status: {}, Body: {}",
                            response.code(),
                            response.body() != null ? response.body().string() : "null");

                    throw new InvalidInputException(
                            "Image deletion failed",
                            "DELETE_FAILED",
                            "Image deletion failed due to a service response error. Please contact the administrator.");
                }
                logger.info("Image deleted successfully: {}", fileUrl);
            }

        } catch (IOException e) {
            logger.error("Unexpected error while deleting image", e);
            throw new InvalidInputException(
                    "Unexpected error",
                    "DELETE_IO_EXCEPTION",
                    "An unexpected I/O error occurred while deleting the image. Please contact the administrator.");
        }
    }

    public String uploadDocumentAsPdf(MultipartFile file, String filePath, String bucketName) throws IOException {
        byte[] pdfBytes;

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new InvalidInputException(
                        "Invalid file format",
                        "INVALID_FILE_FORMAT",
                        "The uploaded file is not a valid image or PDF.");
            }

            try (PDDocument doc = new PDDocument();
                    ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
                doc.addPage(page);

                PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, convertToJpegAndCompress(file, 0.8f),
                        null);
                try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                    contents.drawImage(pdImage, 0, 0, image.getWidth(), image.getHeight());
                }

                doc.save(out);
                pdfBytes = out.toByteArray();
            }
        } else {
            pdfBytes = file.getBytes();
        }

        pdfBytes = compressPdf(pdfBytes);

        if (!filePath.toLowerCase().endsWith(".pdf")) {
            filePath += ".pdf";
        }

        String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + bucketName + "/" + filePath;

        RequestBody requestBody = RequestBody.create(pdfBytes, MediaType.parse("application/pdf"));
        Request request = new Request.Builder()
                .url(uploadUrl)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .addHeader("x-upsert", "true")
                .put(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("Failed to upload PDF. Status: {}, Body: {}",
                        response.code(),
                        response.body() != null ? response.body().string() : "null");
                throw new InvalidInputException(
                        "PDF upload failed",
                        "UPLOAD_FAILED",
                        "The document could not be uploaded due to a service error.");
            }
            return SUPABASE_URL + "/storage/v1/object/public/" + bucketName + "/" + filePath;
        }
    }

    private byte[] compressPdf(byte[] inputPdfBytes) throws IOException {
        try (PDDocument document = PDDocument.load(inputPdfBytes);
                ByteArrayOutputStream compressedOut = new ByteArrayOutputStream()) {

            for (PDPage page : document.getPages()) {
                page.setResources(new PDResources());
            }

            document.setAllSecurityToBeRemoved(true); 
            document.save(compressedOut);
            return compressedOut.toByteArray();
        }
    }

}
