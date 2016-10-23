
package application;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ParserController {
    private static final String DOWNLOAD = "/download-calorizator";
    private static final String BASE = "http://www.calorizator.ru";
    private static final String[] CATEGORIES = {
            "http://www.calorizator.ru/recipes/category/snacks",
            "http://www.calorizator.ru/recipes/category/salads",
            "http://www.calorizator.ru/recipes/category/sandwiches",
            "http://www.calorizator.ru/recipes/category/soups",
            "http://www.calorizator.ru/recipes/category/garnish",
            "http://www.calorizator.ru/recipes/category/sauces",
            "http://www.calorizator.ru/recipes/category/desserts",
            "http://www.calorizator.ru/recipes/category/cakes",
            "http://www.calorizator.ru/recipes/category/drinks"
    };

    private ReciepeBean reciepeBeanCurrent = new ReciepeBean();

    private NutrientsBean nutrientsBean = new NutrientsBean();

    @RequestMapping(value = DOWNLOAD)
    public List<ReciepeBean> parse(@RequestParam(value = "url", required = false, defaultValue = "http://www.calorizator.ru/recipes/all") String url, Model model) throws IOException {
        List<ReciepeBean> reciepes = new ArrayList<>();

        for (String category : CATEGORIES) {
            reciepes.addAll(parseCategory(category));
        }

        return reciepes;
    }

    private List<ReciepeBean> parseCategory(String url) throws IOException {
        List<ReciepeBean> res = new ArrayList<>();

        Document doc = Jsoup.connect(url).get();

        int lastPageNum = 0;
        String pageUrlBase = "";

        if (doc.getElementsByClass("pager-last").size() > 0) {
            lastPageNum = Integer.parseInt(doc
                    .getElementsByClass("pager-last")
                    .get(0)
                    .getElementsByAttributeValueContaining("href", "/recipes/category/")
                    .get(0)
                    .text()) - 1;
            pageUrlBase = doc
                    .getElementsByClass("pager-last")
                    .get(0)
                    .getElementsByAttributeValueContaining("href", "/recipes/category/")
                    .get(0)
                    .attr("href")
                    .replaceAll("[0-9]", "XXX");
        } else {
            lastPageNum = doc.getElementsByClass("pager-item").size();
            pageUrlBase = doc
                    .getElementsByClass("pager-item")
                    .get(0)
                    .getElementsByAttributeValueContaining("href", "/recipes/category/")
                    .get(0)
                    .attr("href")
                    .replaceAll("[0-9]", "XXX");
        }

        for (int i = 0; i <= lastPageNum; i++) {
            List<ReciepeBean> reciepeBean = parsePage(BASE + pageUrlBase.replace("XXX", Integer.toString(i)));
            res.addAll(reciepeBean);
        }

        return res;
    }

    private List<ReciepeBean> parsePage(String url) throws IOException {
        Document cat = Jsoup.connect(url).get();
        List<ReciepeBean> res = new ArrayList<>();

        List<Element> items = cat.getElementsByClass("odd");
        items.addAll(cat.getElementsByClass("even"));
        int i = 0;
        for (Element item : items) {
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


            nutrientsBean.setProteine(Float.parseFloat(proteine));
            nutrientsBean.setFats(Float.parseFloat(fats));
            nutrientsBean.setCarbonate(Float.parseFloat(ch));
            nutrientsBean.setKkal(Float.parseFloat(kcal));

            reciepeBeanCurrent = parseItem(BASE + href);
            reciepeBeanCurrent.setNutrientsBean(nutrientsBean);

            res.add(reciepeBeanCurrent);
            int id = ReciepeDao.saveReciepe(reciepeBeanCurrent);
            IngredientDao.saveIngredient(reciepeBeanCurrent.getIngredients(), id);
            NutrientsDao.saveNutrients(reciepeBeanCurrent.getNutrientsBean(), id);

            reciepeBeanCurrent = new ReciepeBean();
        }
        return res;
    }

    private ReciepeBean parseItem(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        ReciepeBean item = new ReciepeBean();
        item.setTitle(doc.getElementById("page-title").text());
        item.setRecipe(doc.getElementsByAttributeValue("itemprop", "recipeInstructions").get(0).text());

        List<Ingredient> ingr = new ArrayList<>();

        for (Element el : doc.getElementsByAttributeValue("itemprop", "ingredients")) {
            Ingredient tmpIng = new Ingredient();
            String[] ingredientText = el.text().split("-");
            tmpIng.setIngredient(ingredientText[0]);
            if (ingredientText.length > 1)
                tmpIng.setWeight(ingredientText[1]);
            ingr.add(tmpIng);
        }

        item.setIngredients(ingr);

        return item;
    }

    private ParserController() {

    }
}
