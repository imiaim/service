package application;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ParserController {
    private static final String DOWNLOAD = "/download";
    private static final String BASE = "http://www.calorizator.ru";

    @RequestMapping(value = DOWNLOAD)
    public List<List<Object>> parse(@RequestParam(value = "url", required = false, defaultValue = "http://www.calorizator.ru/recipes/category/garnish") String url, Model model) throws IOException {
        final String destinationUrl = url.isEmpty() ? "http://www.calorizator.ru/recipes/16383" : url;
        List<List<Object>> res = new ArrayList<>();

        Document doc = Jsoup.connect(url).get();

        int lastPageNum = Integer.parseInt(doc
                .getElementsByClass("pager-last")
                .get(0)
                .getElementsByAttributeValueContaining("href", "/recipes/category/")
                .get(0)
                .text()) - 1;

        String pageUrlBase = doc
                .getElementsByClass("pager-last")
                .get(0)
                .getElementsByAttributeValueContaining("href", "/recipes/category/")
                .get(0)
                .attr("href")
                .replaceAll("[0-9]", "XXX");

        for (int i = 0; i <= lastPageNum; i++) {
            List<List<Object>> reciepe = parsePage(BASE + pageUrlBase.replace("XXX", Integer.toString(i)));
            res.addAll(reciepe);
        }

        return res;
    }

    private List<List<Object>> parsePage(String url) throws IOException {
        Document cat = Jsoup.connect(url).get();
        List<List<Object>> res = new ArrayList<>();

        List<Element> items = cat.getElementsByClass("odd");
        items.addAll(cat.getElementsByClass("even"));
        int i = 0;
        for (Element item : items) {
            List<String> nutrients = new ArrayList<>();
            String href = item
                    .getElementsByClass("views-field-title")
                    .get(0)
                    .getElementsByAttributeValueContaining("href", "/recipes/")
                    .get(0)
                    .attr("href")
                    .replace(".jpg", "");

            String proteine = item
                    .getElementsByClass("views-field-field-protein-value")
                    .get(0)
                    .text();

            String fats = item
                    .getElementsByClass("views-field-field-fat-value")
                    .get(0)
                    .text();

            String ch = item
                    .getElementsByClass("views-field-field-carbohydrate-value")
                    .get(0)
                    .text();

            String kcal = item
                    .getElementsByClass("views-field-field-kcal-value")
                    .get(0)
                    .text();

            nutrients.add(proteine);
            nutrients.add(fats);
            nutrients.add(ch);
            nutrients.add(kcal);

            List<Object> itemDetails = parseItem(BASE + href);
            itemDetails.addAll(nutrients);

            res.add(itemDetails);
        }
        return res;
    }

    private List<Object> parseItem(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        List<Object> item = new ArrayList<>();
        Element title = doc.getElementById("page-title");
        Element reciepe = doc.getElementsByAttributeValue("itemprop", "recipeInstructions").get(0);
        List<Element> ingredients = doc.getElementsByAttributeValue("itemprop", "ingredients");

        item.add(title);
        item.add(reciepe);
        item.add(ingredients);

        return item;
    }

    private ParserController() {

    }
}
