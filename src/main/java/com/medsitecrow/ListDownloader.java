package com.medsitecrow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class ListDownloader implements Runnable {

    private final Elements list;
    private final int i;

    public ListDownloader(Elements list, int i) {
        this.list = list;
        this.i = i;
    }

    @Override
    public void run() {


        JSONArray ja = new JSONArray();

        list.forEach(element -> {
            String attr = element.attr("href");
            String itemUrl = "https://www.medica-tradefair.com" + attr;

            Document document = null;
            try {
                document = Jsoup.connect(itemUrl).get();

                Elements name = document.select("[itemprop='name']");
                Elements addressCountry = document.select("[itemprop='addressCountry']");
                Elements telephone = document.select("[itemprop='telephone']");
                Elements email = document.select("[itemprop='email']");
                Elements url = document.select("[itemprop='url']");

                JSONObject jo = new JSONObject();
                jo.put("name", name.text());
                jo.put("addressCountry", addressCountry.text());
                jo.put("telephone", telephone.text());
                jo.put("email", email.text());
                jo.put("url", url.text());

                ja.put(jo);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }


        });

        if (ja.length() > 0) {
            JsonNode jsonTree = null;
            try {
                jsonTree = new ObjectMapper().readTree(String.valueOf(ja));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            CsvSchema.Builder csvSchemaBuilder = (CsvSchema.Builder) CsvSchema.builder();
            assert jsonTree != null;
            JsonNode firstObject = jsonTree.elements().next();
            firstObject.fieldNames().forEachRemaining(csvSchemaBuilder::addColumn);
            CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();

            CsvMapper csvMapper = new CsvMapper();
            try {
                csvMapper.writerFor(JsonNode.class)
                        .with(csvSchema)
                        .writeValue(new File("C:\\Users\\Hristo\\Desktop\\medica\\medica-list-" + i + ".csv"), jsonTree);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
