package ru.netology;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static final String URL = "https://api.nasa.gov/planetary/apod?api_key=XXXXXXXXXXXXXX";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();
        HttpGet request = new HttpGet(URL);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            //записываем ответ в массив байт
            byte[] responseBytes = response.getEntity().getContent().readAllBytes();
            //парсим json в объект NasaObject
            NasaObject answer = mapper.readValue(responseBytes, new TypeReference<>() {});

            //получаем url картинки из поля url
            String imageUrl = answer.getUrl();
            //разбиваем url по знаку /
            String[] byteUrl = imageUrl.split("/");
            //имя файла будет последним элементом массива байт
            String imageName  = byteUrl[byteUrl.length - 1];
            //создаем новый запрос к адресу картинки
            HttpGet requestImage = new HttpGet(imageUrl);
            CloseableHttpResponse responseImage = httpClient.execute(requestImage);
            byte[] responceBytesImage = responseImage.getEntity().getContent().readAllBytes();

            FileOutputStream fos = new FileOutputStream( "Image/" + imageName + ".jpg");
            fos.write(responceBytesImage);

        } catch (IOException e) {
            System.out.println("Error decline server NASA");
        }


    }
}
