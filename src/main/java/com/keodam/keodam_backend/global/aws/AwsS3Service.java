//package com.keodam.keodam_backend.global.aws;
//
//import com.keodam.keodam_backend.exception.handler.S3Handler;
//import com.keodam.keodam_backend.global.code.status.ErrorStatus;
//import lombok.RequiredArgsConstructor;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.DeleteObjectRequest;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.UUID;
//
//
//@Service
//@RequiredArgsConstructor
//public class AwsS3Service {
//
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//
//    private final AmazonS3 amazonS3;
//
//    public String uploadFile(MultipartFile multipartFile){
//
//        if (multipartFile == null || multipartFile.isEmpty()) {
//            return null;
//        }
//
//        String fileName = createFileName(multipartFile.getOriginalFilename());
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setContentLength(multipartFile.getSize());
//        objectMetadata.setContentType(multipartFile.getContentType());
//
//        System.out.println(multipartFile.getContentType());
//
//        try(InputStream inputStream = multipartFile.getInputStream()){
//            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
//        } catch (IOException e){
//            throw new S3Handler(ErrorStatus.S3_FAILED);
//        }
//
//        return "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;
//    }
//
//    // 파일 업로드시, 파일명을 난수화하기 위해 UUID를 활용
//    public String createFileName(String fileName){
//        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
//    }
//
//    private String getFileExtension(String fileName){
//        try{
//            return fileName.substring(fileName.lastIndexOf("."));
//        } catch (StringIndexOutOfBoundsException e){
//            throw new S3Handler(ErrorStatus.S3_FORMAT);
//        }
//    }
//
//
//    public void deleteFile(String fileName){
//        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
//    }
//}
