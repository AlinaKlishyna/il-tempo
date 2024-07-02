import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Parser {

    public static void main(String[] args) throws IOException {
        Document page = getPage();

        //css query language
        Element containerDates = page.select("div[class=forecast-container]").first();
        System.out.println(containerDates);

        System.out.println(getDate(containerDates));
        ;
    }

    private static Document getPage() throws IOException {
        String url = "https://www.meteolive.it/previsione-meteo/italia/Lombardia/Milano/settimana/";
        //3000-время обработки в миллисекундах, сайт же не будет грузиться вечно
        return Jsoup.parse(new URL(url), 4000);
    }

    private static String getDate(Element containerDates) {
        Element currentDate = containerDates.select("li[class=\"\"]").first();
        String dateDM = currentDate.select("span").last().text();
        String[] date = (dateDM.replaceAll("/", " ") + " " + Year.now()).split(" ");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy");
        Calendar calendar = new GregorianCalendar(Integer.parseInt(date[2]), (Integer.parseInt(date[1]) - 1), Integer.parseInt(date[0]));
        return simpleDateFormat.format(calendar.getTime());
    }
}
