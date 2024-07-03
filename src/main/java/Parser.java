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

    public static void main(String[] args) throws Exception {
        Document page = getPage();

        System.out.println(getDateToday(page));
        System.out.println(getTwoWeeks(page));
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

    private static List<String> getTwoWeeks(Document page) throws Exception {
        Element containerDates = page.select("div[class=forecast-container]").first();
        Elements currentDate = containerDates.select("li[class=\"\"]").select("span");
        Matcher matcher = PATTERN_DATE.matcher(currentDate.text());
        List<String> group = new ArrayList<>();
        while (matcher.find()) {
            group.add(formatDate(matcher.group()));
        }
        if (!group.isEmpty()) {
            return group;
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

}
