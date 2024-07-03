import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Klishyna ALina
 */
public class Parser {

    private static String dateToday = "";

    // Регулярные выражения. Задача достать "2/7"(дату) из текста -> oggi 2/7 domani 3/7
    // \\d{1,2}/\\d{1,2}    \\d - цифровой символ  \\d{1,2} - цифровой символ от 1 до 2
    private static Pattern PATTERN_DATE = Pattern.compile("\\d{1,2}/\\d{1,2}");

    private static List<String> groupDatesWeek = new ArrayList<>();

    private static List<List<String>> daysInfoMore = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Document page = getPage();
        getDatesWeek(page);
        getListInfoMore(page);
        getWeatherInfo();
    }

    /**
     * Скачивание/получение страницы сайта
     */
    private static Document getPage() throws IOException {
        final String url = "https://www.meteolive.it/previsione-meteo/italia/Lombardia/Milano/settimana/";
        //3000-время обработки в миллисекундах, сайт же не будет грузиться вечно
        return Jsoup.parse(new URL(url), 3500);
    }

    /**
     * Получение 1-ой(текущей) даты в списке
     * @param page
     * @return date
     */
    private static String getDateToday(Document page) throws Exception {
        Elements currentDate = page.select("div[class=forecast-container]").first().
                select("li[class=\"\"]").first().select("span");
        Matcher matcher = PATTERN_DATE.matcher(currentDate.text());
        if (matcher.find()) {
            return formatDate(matcher.group());
        }
        throw new Exception("Can't extract date from string!");
    }

    private static List<String> getDatesWeek(Document page) throws Exception {
        Element containerDates = page.select("div[class=forecast-container]").first();
        Elements currentDate = containerDates.select("li[class=\"\"]").select("span");
        Matcher matcher = PATTERN_DATE.matcher(currentDate.text());
        int week = 1;
        while (matcher.find()) {
            if (week <= 7) {
                groupDatesWeek.add(formatDate(matcher.group()));
            }
            week++;
        }
        if (!groupDatesWeek.isEmpty()) {
            return groupDatesWeek;
        }
        throw new Exception("Can't extract date from string!");
    }

    private static String formatDate(int day, int month, int year) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy");
        Calendar calendar = new GregorianCalendar(day, month, year);
        return simpleDateFormat.format(calendar.getTime());
    }

    private static String formatDate(String textSite) {
        String[] textDate = (textSite.replaceAll("/", " ") + " " + Year.now()).split(" ");
        return formatDate(Integer.parseInt(textDate[2]), Integer.parseInt((textDate[1])) - 1,
                Integer.parseInt(textDate[0]));
    }

    private static void getWeatherInfo() {
        System.out.printf("%-30S| %-20S| %-20S| %-20S| %-20S| %-20S%n", "giorno", "tempo previsto",
                "min °C", "max °C", "precipitazioni", "vento km/h");
        for (int i = 0; i < groupDatesWeek.size(); i++) {
            for (List<String> infoMore : daysInfoMore) {
                System.out.printf("%-30S| ", groupDatesWeek.get(i++));
                toString(infoMore);
                System.out.println();
            }
        }
    }

    private static List<List<String>> getListInfoMore(Document page) {
        Elements container = page.select("div[class=forecast-content]").first().select("tr").
                next().select("tr").select("td").not("td[class=\"giorno\"]");
        List<String> day = new ArrayList<>();
        for (Element item : container) {
            day.add(item.text());
            if (day.size() == 5) {
                daysInfoMore.add(day);
                day = new ArrayList<>();
            }
        }
        return daysInfoMore;
    }

    public static void toString(List<String> list) {
        for (String line : list) {
            System.out.printf("%-20s| ", line);
        }
    }
}
