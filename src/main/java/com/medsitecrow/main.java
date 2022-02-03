package com.medsitecrow;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 *@author Hristo Nikolov
 */
public class main {

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        int t = 1;
        for (int i = 0; i <= 3200; i += 100) {

                int s = (i == 0 ) ? 0 : i - 100;
                Document page = Jsoup.connect("https://www.medica-tradefair.com/vis-api/vis/v1/en/search/slice?oid=80396&lang=2&_query=&f_type=profile&f_event=medcom2021.MEDICA&_compact=true&_start=" + s + "&_rows=" + i)
                        .header("Sec-Fetch-Dest", "empty")
                        .header("Sec-Fetch-Mode", "cors")
                        .header("Sec-Fetch-Site", "same-origin")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .header("X-Vis-Domain", "https://www.medica-tradefair.com")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:96.0) Gecko/20100101 Firefox/96.0")
                        .get();
                Elements list = page.select("a[href*='/vis/v1/en/exhibitors/medcom']");

                ListDownloader listDownloader = new ListDownloader(list, i);

                Thread thread = new Thread(listDownloader);
                thread.start();

            System.out.print("Treat started: " + t);
            t++;

        }

    }

}
