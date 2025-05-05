package com.ecommerce.productservice.config;

import com.ecommerce.productservice.model.Category;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.model.ProductDetail;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.ecommerce.productservice.repository.ProductDetailRepository;
import com.ecommerce.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 資料庫初始化器
 * 
 * 在應用啟動時初始化一些基本的產品和類別數據
 * 僅在 "dev" 配置文件下啟用，避免在生產環境中初始化測試數據
 */
@Component
@Profile("dev")
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Override
    public void run(String... args) throws Exception {
        // 檢查資料庫是否已經初始化
        if (categoryRepository.count() > 0) {
            System.out.println("資料庫已經初始化，跳過初始化過程");
            return;
        }
        
        System.out.println("開始初始化產品資料庫...");
        
        // 初始化類別
        Map<String, Long> categoryIds = initCategories();
        
        // 初始化產品
        initProducts(categoryIds);
        
        System.out.println("產品資料庫初始化完成");
    }
    
    /**
     * 初始化產品類別
     * @return 類別名稱到ID的映射
     */
    private Map<String, Long> initCategories() {
        Map<String, Long> categoryIds = new HashMap<>();
        
        List<Category> categories = new ArrayList<>();
        
        // 創建頂級類別
        Category phones = new Category();
        phones.setName("手機");
        phones.setDescription("各種品牌的智能手機");
        phones.setParentId(null);
        phones.setIsActive(true);
        
        Category laptops = new Category();
        laptops.setName("筆記型電腦");
        laptops.setDescription("各種品牌的筆記型電腦");
        laptops.setParentId(null);
        laptops.setIsActive(true);
        
        Category tablets = new Category();
        tablets.setName("平板電腦");
        tablets.setDescription("各種品牌的平板電腦");
        tablets.setParentId(null);
        tablets.setIsActive(true);
        
        Category headphones = new Category();
        headphones.setName("耳機");
        headphones.setDescription("各種品牌的耳機");
        headphones.setParentId(null);
        headphones.setIsActive(true);
        
        Category smartwatches = new Category();
        smartwatches.setName("智能手錶");
        smartwatches.setDescription("各種品牌的智能手錶");
        smartwatches.setParentId(null);
        smartwatches.setIsActive(true);
        
        categories.add(phones);
        categories.add(laptops);
        categories.add(tablets);
        categories.add(headphones);
        categories.add(smartwatches);
        
        List<Category> savedCategories = categoryRepository.saveAll(categories);
        
        // 將類別名稱和ID存入映射
        for (Category category : savedCategories) {
            categoryIds.put(category.getName(), category.getId());
        }
        
        System.out.println("類別初始化完成");
        return categoryIds;
    }
    
    /**
     * 初始化產品
     * @param categoryIds 類別名稱到ID的映射
     */
    private void initProducts(Map<String, Long> categoryIds) {
        // 獲取已保存的類別ID
        Long phoneId = categoryIds.get("手機");
        Long laptopId = categoryIds.get("筆記型電腦");
        Long tabletId = categoryIds.get("平板電腦");
        Long headphoneId = categoryIds.get("耳機");
        Long watchId = categoryIds.get("智能手錶");
        
        // 初始化手機產品
        Product phone1 = new Product();
        phone1.setName("iPhone 13");
        phone1.setShortDescription("Apple最新的智能手機");
        phone1.setPrice(new BigDecimal("799.99"));
        phone1.setStock(100);
        phone1.setCategoryId(phoneId);
        phone1.setSku("IP13-64GB");
        phone1.setBrand("Apple");
        phone1.setWeight(new BigDecimal("0.174")); // 174g = 0.174kg
        phone1.setDimensions("146.7 x 71.5 x 7.65 mm");
        phone1.setIsActive(true);
        
        Product phone2 = new Product();
        phone2.setName("Samsung Galaxy S21");
        phone2.setShortDescription("Samsung的旗艦智能手機");
        phone2.setPrice(new BigDecimal("699.99"));
        phone2.setStock(80);
        phone2.setCategoryId(phoneId);
        phone2.setSku("SG21-128GB");
        phone2.setBrand("Samsung");
        phone2.setWeight(new BigDecimal("0.169")); // 169g = 0.169kg
        phone2.setDimensions("151.7 x 71.2 x 7.9 mm");
        phone2.setIsActive(true);
        
        // 保存手機產品
        phone1 = productRepository.save(phone1);
        phone2 = productRepository.save(phone2);
        
        // 初始化手機產品詳情
        ProductDetail phone1Detail = new ProductDetail();
        phone1Detail.setProductId(phone1.getId());
        phone1Detail.setFullDescription("iPhone 13 具有超級視網膜 XDR 顯示屏，A15 仿生芯片，先進的雙攝像頭系統，以及更長的電池壽命。");
        
        List<String> phone1Images = new ArrayList<>();
        phone1Images.add("iphone13_1.jpg");
        phone1Images.add("iphone13_2.jpg");
        phone1Images.add("iphone13_3.jpg");
        phone1Detail.setImageUrls(phone1Images);
        
        Map<String, String> phone1Specs = new HashMap<>();
        phone1Specs.put("螢幕尺寸", "6.1英寸");
        phone1Specs.put("處理器", "A15仿生芯片");
        phone1Specs.put("相機", "雙攝像頭系統");
        phone1Specs.put("解鎖方式", "Face ID");
        phone1Specs.put("網路", "5G連接");
        phone1Detail.setSpecifications(phone1Specs);
        
        Map<String, String> phone1FeaturesMap = new HashMap<>();
        phone1FeaturesMap.put("feature1", "防水防塵");
        phone1FeaturesMap.put("feature2", "無線充電");
        phone1FeaturesMap.put("feature3", "MagSafe");
        phone1Detail.setFeatures(phone1FeaturesMap);
        
        Map<String, String> phone1TagsMap = new HashMap<>();
        phone1TagsMap.put("tag1", "智能手機");
        phone1TagsMap.put("tag2", "Apple");
        phone1TagsMap.put("tag3", "iPhone");
        phone1Detail.setTags(phone1TagsMap);
        
        ProductDetail phone2Detail = new ProductDetail();
        phone2Detail.setProductId(phone2.getId());
        phone2Detail.setFullDescription("Galaxy S21 配備動態 AMOLED 2X 顯示屏，Exynos 2100 處理器，專業級三攝像頭系統，以及全天候電池。");
        
        List<String> phone2Images = new ArrayList<>();
        phone2Images.add("galaxys21_1.jpg");
        phone2Images.add("galaxys21_2.jpg");
        phone2Images.add("galaxys21_3.jpg");
        phone2Detail.setImageUrls(phone2Images);
        
        Map<String, String> phone2Specs = new HashMap<>();
        phone2Specs.put("螢幕尺寸", "6.2英寸");
        phone2Specs.put("處理器", "Exynos 2100");
        phone2Specs.put("相機", "三攝像頭系統");
        phone2Specs.put("解鎖方式", "指紋識別");
        phone2Specs.put("網路", "5G連接");
        phone2Detail.setSpecifications(phone2Specs);
        
        Map<String, String> phone2FeaturesMap = new HashMap<>();
        phone2FeaturesMap.put("feature1", "防水防塵");
        phone2FeaturesMap.put("feature2", "無線充電");
        phone2FeaturesMap.put("feature3", "反向無線充電");
        phone2Detail.setFeatures(phone2FeaturesMap);
        
        Map<String, String> phone2TagsMap = new HashMap<>();
        phone2TagsMap.put("tag1", "智能手機");
        phone2TagsMap.put("tag2", "Samsung");
        phone2TagsMap.put("tag3", "Galaxy");
        phone2Detail.setTags(phone2TagsMap);
        
        // 保存手機產品詳情
        productDetailRepository.save(phone1Detail);
        productDetailRepository.save(phone2Detail);
        
        // 初始化筆記型電腦產品
        Product laptop1 = new Product();
        laptop1.setName("MacBook Pro");
        laptop1.setShortDescription("Apple的專業筆記型電腦");
        laptop1.setPrice(new BigDecimal("1299.99"));
        laptop1.setStock(50);
        laptop1.setCategoryId(laptopId);
        laptop1.setSku("MBP-13-M1");
        laptop1.setBrand("Apple");
        laptop1.setWeight(new BigDecimal("1.4")); // 1.4kg
        laptop1.setDimensions("304.1 x 212.4 x 15.6 mm");
        laptop1.setIsActive(true);
        
        Product laptop2 = new Product();
        laptop2.setName("Dell XPS 13");
        laptop2.setShortDescription("Dell的輕薄筆記型電腦");
        laptop2.setPrice(new BigDecimal("999.99"));
        laptop2.setStock(40);
        laptop2.setCategoryId(laptopId);
        laptop2.setSku("XPS13-11");
        laptop2.setBrand("Dell");
        laptop2.setWeight(new BigDecimal("1.2")); // 1.2kg
        laptop2.setDimensions("295.7 x 198.7 x 14.8 mm");
        laptop2.setIsActive(true);
        
        // 保存筆記型電腦產品
        laptop1 = productRepository.save(laptop1);
        laptop2 = productRepository.save(laptop2);
        
        // 初始化筆記型電腦產品詳情
        ProductDetail laptop1Detail = new ProductDetail();
        laptop1Detail.setProductId(laptop1.getId());
        laptop1Detail.setFullDescription("MacBook Pro 配備 M1 芯片，提供突破性的性能和電池壽命，以及驚艷的視網膜顯示屏。");
        
        List<String> laptop1Images = new ArrayList<>();
        laptop1Images.add("macbookpro_1.jpg");
        laptop1Images.add("macbookpro_2.jpg");
        laptop1Detail.setImageUrls(laptop1Images);
        
        Map<String, String> laptop1Specs = new HashMap<>();
        laptop1Specs.put("螢幕尺寸", "13.3英寸視網膜顯示屏");
        laptop1Specs.put("處理器", "Apple M1芯片");
        laptop1Specs.put("記憶體", "8GB統一內存");
        laptop1Specs.put("儲存", "256GB SSD");
        laptop1Specs.put("電池壽命", "最長20小時");
        laptop1Detail.setSpecifications(laptop1Specs);
        
        Map<String, String> laptop1FeaturesMap = new HashMap<>();
        laptop1FeaturesMap.put("feature1", "Touch Bar");
        laptop1FeaturesMap.put("feature2", "Touch ID");
        laptop1FeaturesMap.put("feature3", "背光鍵盤");
        laptop1Detail.setFeatures(laptop1FeaturesMap);
        
        Map<String, String> laptop1TagsMap = new HashMap<>();
        laptop1TagsMap.put("tag1", "筆記型電腦");
        laptop1TagsMap.put("tag2", "Apple");
        laptop1TagsMap.put("tag3", "MacBook");
        laptop1Detail.setTags(laptop1TagsMap);
        
        ProductDetail laptop2Detail = new ProductDetail();
        laptop2Detail.setProductId(laptop2.getId());
        laptop2Detail.setFullDescription("Dell XPS 13 配備 InfinityEdge 顯示屏，Intel 第11代處理器，以及出色的構建質量。");
        
        List<String> laptop2Images = new ArrayList<>();
        laptop2Images.add("xps13_1.jpg");
        laptop2Images.add("xps13_2.jpg");
        laptop2Detail.setImageUrls(laptop2Images);
        
        Map<String, String> laptop2Specs = new HashMap<>();
        laptop2Specs.put("螢幕尺寸", "13.4英寸");
        laptop2Specs.put("處理器", "Intel Core i7-1165G7");
        laptop2Specs.put("記憶體", "16GB RAM");
        laptop2Specs.put("儲存", "512GB SSD");
        laptop2Specs.put("電池壽命", "最長12小時");
        laptop2Detail.setSpecifications(laptop2Specs);
        
        Map<String, String> laptop2FeaturesMap = new HashMap<>();
        laptop2FeaturesMap.put("feature1", "指紋識別");
        laptop2FeaturesMap.put("feature2", "背光鍵盤");
        laptop2FeaturesMap.put("feature3", "Thunderbolt 4");
        laptop2Detail.setFeatures(laptop2FeaturesMap);
        
        Map<String, String> laptop2TagsMap = new HashMap<>();
        laptop2TagsMap.put("tag1", "筆記型電腦");
        laptop2TagsMap.put("tag2", "Dell");
        laptop2TagsMap.put("tag3", "XPS");
        laptop2Detail.setTags(laptop2TagsMap);
        
        // 保存筆記型電腦產品詳情
        productDetailRepository.save(laptop1Detail);
        productDetailRepository.save(laptop2Detail);
        
        System.out.println("產品初始化完成");
    }
}