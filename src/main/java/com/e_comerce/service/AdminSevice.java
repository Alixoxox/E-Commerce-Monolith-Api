package com.e_comerce.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.e_comerce.model.Product;
import com.e_comerce.repository.PastOrderRepo;
import com.e_comerce.repository.ProductRepo;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminSevice {

    @Autowired
    private ProductRepo productRepository;
    @Autowired
    private PastOrderRepo pastOrderRepo;
    @PersistenceContext
    private EntityManager entityManager;

    @SneakyThrows
    @Transactional
    public String bulkInsertFromCsv(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        List<Product> products = new ArrayList<>();

        try (CSVReader csvReader = new CSVReaderBuilder(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                .withSkipLines(1) // skip header row
                .build()) {

            List<String[]> rows = csvReader.readAll();

            if (rows.isEmpty()) {
                throw new IllegalArgumentException("CSV has no data rows");
            }

            int lineNumber = 2; // header was line 1
            for (String[] row : rows) {
                if (row.length < 6) {
                    throw new IllegalArgumentException("Row " + lineNumber + " has missing columns");
                }

                String title = row[0].trim();
                String description = row[1].trim();
                String image = row[2].trim();
                BigDecimal price;
                Integer stock;
                String category = row[5].trim();

                if (title.isBlank()) {
                    throw new IllegalArgumentException("Row " + lineNumber + ": title is required");
                }

                try {
                    price = new BigDecimal(row[3].trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Row " + lineNumber + ": invalid price '" + row[3] + "'");
                }
                if (price.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Row " + lineNumber + ": price cannot be negative");
                }

                try {
                    stock = Integer.parseInt(row[4].trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Row " + lineNumber + ": invalid stock '" + row[4] + "'");
                }
                if (stock < 0) {
                    throw new IllegalArgumentException("Row " + lineNumber + ": stock cannot be negative");
                }

                if (category.isBlank()) {
                    throw new IllegalArgumentException("Row " + lineNumber + ": category is required");
                }

                Product product = new Product();
                product.setTitle(title);
                product.setDescription(description);
                product.setImage(image);
                product.setPrice(price);
                product.setStock(stock);
                product.setCategory(category);

                products.add(product);
                lineNumber++;
            }

        } catch (IOException | CsvException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }

        productRepository.saveAll(products);
        return "Products Successfully uploaded to the Database";
    }
}