package com.tiendavideojuegos.tienda.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String generateContent(String prompt) throws Exception {
        // 1. Crea cliente HTTP
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            // 2. Configura la solicitud POST
            HttpPost httpPost = new HttpPost(apiUrl + "?key=" + apiKey);

            // 3. Construye el cuerpo JSON para Gemini
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, String> content = new HashMap<>();
            content.put("text", prompt);
            requestBody.put("contents", new Map[]{Map.of("parts", new Map[]{content})});

            // 4. Convierte a JSON y configura encabezados
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(requestBody);
            httpPost.setEntity(new StringEntity(jsonBody, "UTF-8"));
            httpPost.setHeader("Content-Type", "application/json");

            // Ejecuta la solicitud
            CloseableHttpResponse response = client.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity());

            // Parsea la respuesta
            Map<String, Object> responseMap = mapper.readValue(result, Map.class);

            // Verifica si hay un error
            if (responseMap.containsKey("error")) {
                Map<String, Object> error = (Map<String, Object>) responseMap.get("error");
                String errorMessage = (String) error.get("message");
                Integer errorCode = (Integer) error.get("code");
                throw new Exception("Gemini API error [Code " + errorCode + "]: " + errorMessage);
            }

            // Valida y extrae el texto generado
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new Exception("No candidates found in Gemini response");
            }

            Map<String, Object> contentMap = (Map<String, Object>) candidates.get(0).get("content");
            if (contentMap == null) {
                throw new Exception("No content found in Gemini response");
            }

            List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
            if (parts == null || parts.isEmpty()) {
                throw new Exception("No parts found in Gemini response");
            }

            String text = (String) parts.get(0).get("text");
            if (text == null) {
                throw new Exception("No text found in Gemini response");
            }

            return text;
        } catch (Exception e) {
            System.err.println("Error processing Gemini response: " + e.getMessage());
            throw e; // Propaga la excepción para que el controlador la maneje
        } finally {
            client.close();
        }
    }
}