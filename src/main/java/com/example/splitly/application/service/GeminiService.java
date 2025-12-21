package com.example.splitly.application.service;

import com.example.splitly.presentation.dto.response.BillOcrResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Extract bill information from single image
     */
    public BillOcrResponse extractBillInformation(MultipartFile image, String additionalContext) {
        return extractBillInformationFromMultipleImages(Collections.singletonList(image), additionalContext);
    }

    /**
     * Extract bill information from multiple images of the SAME bill
     * All images are analyzed together to produce ONE complete bill
     */
    public BillOcrResponse extractBillInformationFromMultipleImages(
            List<MultipartFile> images,
            String additionalContext) {
        try {
            // Prepare request with multiple images
            String prompt = buildMultiImagePrompt(additionalContext, images.size());
            Map<String, Object> requestBody = buildGeminiRequestWithMultipleImages(prompt, images);

            // Call Gemini API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = geminiApiUrl + "?key=" + geminiApiKey;
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Sending {} bill images to Gemini API for combined OCR processing", images.size());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Parse response
            return parseGeminiResponse(response.getBody());

        } catch (Exception e) {
            log.error("Error processing bill images with Gemini: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process bill images: " + e.getMessage());
        }
    }

    private String buildMultiImagePrompt(String additionalContext, int imageCount) {
        StringBuilder prompt = new StringBuilder();

        if (imageCount > 1) {
            prompt.append("You are analyzing ").append(imageCount)
                    .append(" images of the SAME bill/receipt. ");
            prompt.append("These images may show different parts of one long bill, or multiple angles of the same bill. ");
            prompt.append("Combine ALL information from ALL images into ONE complete bill. ");
            prompt.append("Do not duplicate items - if you see the same item in multiple images, include it only once.\n\n");
        }

        prompt.append("Extract all information from this bill/receipt and return ONLY a valid JSON object with the following structure:\n\n");
        prompt.append("{\n");
        prompt.append("  \"merchantName\": \"string\",\n");
        prompt.append("  \"merchantAddress\": \"string\",\n");
        prompt.append("  \"billDate\": \"YYYY-MM-DD\",\n");
        prompt.append("  \"billDateTime\": \"YYYY-MM-DD HH:mm:ss\",\n");
        prompt.append("  \"billNumber\": \"string\",\n");
        prompt.append("  \"taxId\": \"string\",\n");
        prompt.append("  \"items\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"name\": \"string\",\n");
        prompt.append("      \"quantity\": number,\n");
        prompt.append("      \"unitPrice\": number,\n");
        prompt.append("      \"totalPrice\": number,\n");
        prompt.append("      \"notes\": \"string\"\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"subtotal\": number,\n");
        prompt.append("  \"tax\": number,\n");
        prompt.append("  \"discount\": number,\n");
        prompt.append("  \"serviceCharge\": number,\n");
        prompt.append("  \"total\": number,\n");
        prompt.append("  \"currency\": \"string\",\n");
        prompt.append("  \"paymentMethod\": \"string\",\n");
        prompt.append("  \"rawText\": \"string\",\n");
        prompt.append("  \"confidence\": number (0-100)\n");
        prompt.append("}\n\n");
        prompt.append("Important rules:\n");
        prompt.append("- Return ONLY the JSON object, no markdown formatting, no explanations\n");
        prompt.append("- Use null for missing fields\n");
        prompt.append("- Parse dates carefully (common formats: DD/MM/YYYY, MM/DD/YYYY, YYYY-MM-DD)\n");
        prompt.append("- Extract ALL items from ALL images without duplication\n");
        prompt.append("- Combine partial information from multiple images intelligently\n");
        prompt.append("- If multiple images show different sections, merge them into one complete list\n");
        prompt.append("- Calculate totals based on all items found\n");
        prompt.append("- Include tax, service charges, discounts if present\n");
        prompt.append("- Set confidence based on overall image quality and consistency across images\n");

        if (additionalContext != null && !additionalContext.trim().isEmpty()) {
            prompt.append("\nAdditional context: ").append(additionalContext);
        }

        return prompt.toString();
    }

    private Map<String, Object> buildGeminiRequestWithMultipleImages(
            String prompt,
            List<MultipartFile> images) throws Exception {

        Map<String, Object> request = new HashMap<>();

        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();

        List<Map<String, Object>> parts = new ArrayList<>();

        // Add text part first
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);
        parts.add(textPart);

        // Add all image parts
        for (MultipartFile image : images) {
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            String mimeType = image.getContentType();

            Map<String, Object> imagePart = new HashMap<>();
            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mimeType", mimeType);
            inlineData.put("data", base64Image);
            imagePart.put("inline_data", inlineData);
            parts.add(imagePart);
        }

        content.put("parts", parts);
        contents.add(content);

        request.put("contents", contents);

        // Add generation config
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.1);
        generationConfig.put("maxOutputTokens", 8192); // Increased for multiple images
        generationConfig.put("topP", 0.95);
        generationConfig.put("topK", 40);
        request.put("generationConfig", generationConfig);

        return request;
    }

    private BillOcrResponse parseGeminiResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode candidatesNode = rootNode.path("candidates");

            if (candidatesNode.isEmpty()) {
                throw new RuntimeException("No response from Gemini API");
            }

            String textContent = candidatesNode.get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            log.info("Raw Gemini response: {}", textContent);

            // Clean the response (remove markdown formatting if present)
            textContent = textContent.trim();
            if (textContent.startsWith("```json")) {
                textContent = textContent.substring(7);
            }
            if (textContent.startsWith("```")) {
                textContent = textContent.substring(3);
            }
            if (textContent.endsWith("```")) {
                textContent = textContent.substring(0, textContent.length() - 3);
            }
            textContent = textContent.trim();

            // Fix incomplete JSON by ensuring it closes properly
            textContent = fixIncompleteJson(textContent);

            log.info("Cleaned JSON: {}", textContent);

            // Parse JSON response
            JsonNode billData = objectMapper.readTree(textContent);

            return BillOcrResponse.builder()
                    .merchantName(getStringValue(billData, "merchantName"))
                    .merchantAddress(getStringValue(billData, "merchantAddress"))
                    .billDate(getDateValue(billData, "billDate"))
                    .billDateTime(getDateTimeValue(billData, "billDateTime"))
                    .billNumber(getStringValue(billData, "billNumber"))
                    .taxId(getStringValue(billData, "taxId"))
                    .items(parseItems(billData.path("items")))
                    .subtotal(getBigDecimalValue(billData, "subtotal"))
                    .tax(getBigDecimalValue(billData, "tax"))
                    .discount(getBigDecimalValue(billData, "discount"))
                    .serviceCharge(getBigDecimalValue(billData, "serviceCharge"))
                    .total(getBigDecimalValue(billData, "total"))
                    .currency(getStringValue(billData, "currency"))
                    .paymentMethod(getStringValue(billData, "paymentMethod"))
                    .rawText(getStringValue(billData, "rawText"))
                    .confidence(getIntegerValue(billData, "confidence"))
                    .build();

        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse bill information: " + e.getMessage());
        }
    }

    private List<BillOcrResponse.BillItem> parseItems(JsonNode itemsNode) {
        List<BillOcrResponse.BillItem> items = new ArrayList<>();

        if (itemsNode.isArray()) {
            for (JsonNode itemNode : itemsNode) {
                items.add(BillOcrResponse.BillItem.builder()
                        .name(getStringValue(itemNode, "name"))
                        .quantity(getIntegerValue(itemNode, "quantity"))
                        .unitPrice(getBigDecimalValue(itemNode, "unitPrice"))
                        .totalPrice(getBigDecimalValue(itemNode, "totalPrice"))
                        .notes(getStringValue(itemNode, "notes"))
                        .build());
            }
        }

        return items;
    }

    private String getStringValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);
        return fieldNode.isNull() ? null : fieldNode.asText();
    }

    private Integer getIntegerValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);
        return fieldNode.isNull() ? null : fieldNode.asInt();
    }

    private BigDecimal getBigDecimalValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);
        if (fieldNode.isNull() || fieldNode.isMissingNode()) {
            return null;
        }
        try {
            return new BigDecimal(fieldNode.asText());
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate getDateValue(JsonNode node, String fieldName) {
        String dateStr = getStringValue(node, fieldName);
        if (dateStr == null) return null;

        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse date: {}", dateStr);
            return null;
        }
    }

    private LocalDateTime getDateTimeValue(JsonNode node, String fieldName) {
        String dateTimeStr = getStringValue(node, fieldName);
        if (dateTimeStr == null) return null;

        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse datetime: {}", dateTimeStr);
            return null;
        }
    }

    /**
     * Fix incomplete JSON by closing unclosed braces/brackets
     */
    private String fixIncompleteJson(String json) {
        int openBraces = 0;
        int openBrackets = 0;
        boolean inString = false;
        boolean escaped = false;

        for (char c : json.toCharArray()) {
            if (escaped) {
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '"' && !escaped) {
                inString = !inString;
                continue;
            }

            if (inString) continue;

            if (c == '{') openBraces++;
            else if (c == '}') openBraces--;
            else if (c == '[') openBrackets++;
            else if (c == ']') openBrackets--;
        }

        // Close unclosed structures
        StringBuilder fixed = new StringBuilder(json);

        // Remove trailing comma if present
        String trimmed = fixed.toString().trim();
        if (trimmed.endsWith(",")) {
            fixed = new StringBuilder(trimmed.substring(0, trimmed.length() - 1));
        }

        // Close arrays and objects
        while (openBrackets > 0) {
            fixed.append("]");
            openBrackets--;
        }

        while (openBraces > 0) {
            fixed.append("}");
            openBraces--;
        }

        return fixed.toString();
    }
}