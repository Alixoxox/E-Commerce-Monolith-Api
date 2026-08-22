package com.e_comerce.service;

import static com.e_comerce.config.RedisAndRabbitConfig.IMAGE_DEL_QUEUE;
import static com.e_comerce.config.RedisAndRabbitConfig.IMAGE_QUEUE;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.e_comerce.DTO.UserDto;
import com.e_comerce.repository.ProductRepo;
import com.e_comerce.repository.RatingRepo;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class S3Service {
    @Autowired
    private S3Client s3Client;
    @Autowired
    private S3Presigner s3Presigner;
    @Autowired
    private RatingRepo ratingRepo;
    @Autowired
    private ProductRepo productRepo;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    @Autowired
    private CacheManager cacheManager;

    @Transactional
    @RabbitListener(queues = IMAGE_QUEUE)
    public void uploadImage(UserDto.rateImg payload) throws IOException {
        System.out.println("commencing push");
//        type -> "products/" :"user-review/";
        String key = payload.getLocation() + UUID.randomUUID() + "-" + payload.getFile().getFilename();
        byte[] bytes = payload.getFile().getContent();
        String contentType = payload.getFile().getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            ResizedImage resized = resizeImage(bytes, contentType);
            if (resized != null) {
                bytes = resized.bytes;
                contentType = resized.contentType;
            }
        }

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(bytes));


        if (payload.getRatingId() == null) {
            // must be for product Img uploaded by admin they don't have rating but only productId
            productRepo.updateImage(payload.getProdId(), key);
            Long prodId = Long.valueOf(payload.getProdId().toString());
            cacheManager.getCache("products").evict(prodId);

        } else {
            // must be user adding review since only they have rating and productId
            ratingRepo.updateFeedbackImage(payload.getRatingId(), key);
                    cacheManager.getCache("prodFeedback").evict(payload.getProdId());
                    cacheManager.getCache("prodRating").evict(payload.getProdId());
                }

    }

    private record ResizedImage(byte[] bytes, String contentType) {}

    private ResizedImage resizeImage(byte[] original, String contentType) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(original));
        if (img == null) {
            return null;
        }
        int maxDim = 1000;
        int w = img.getWidth();
        int h = img.getHeight();
        if (w <= maxDim && h <= maxDim) {
            return null;
        }
        double scale = Math.min((double) maxDim / w, (double) maxDim / h);
        int newW = Math.max(1, (int) Math.round(w * scale));
        int newH = Math.max(1, (int) Math.round(h * scale));

        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(img, 0, 0, newW, newH, null);
        g.dispose();

        String format = contentType.contains("png") ? "png" : "jpg";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resized, format, baos);
        return new ResizedImage(baos.toByteArray(), "image/" + format);
    }
    @RabbitListener(queues = IMAGE_DEL_QUEUE)
    public void deleteImageByUrl(String key) {
        if (key == null || key.isBlank()) {
        return;
    }
         DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    public String getPresignedUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }
}