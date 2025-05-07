import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import static org.junit.jupiter.api.Assertions.*;

public class CardOrderTest {
    private WebDriver driver;
    
    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }
    
    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.get("http://localhost:9999");
    }
    
    @AfterEach
    void teardown() {
        driver.quit();
    }

    // 1. Happy Path
    @Test
    void shouldSubmitSuccessfullyWithValidData() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иван Петров");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79211234567");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button")).click();
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", 
            driver.findElement(By.cssSelector("[data-test-id=order-success]")).getText().trim());
    }

    // 2. Все поля пустые
    @Test
    void shouldShowErrorWhenAllFieldsAreEmpty() {
        driver.findElement(By.cssSelector("button")).click();
        assertEquals("Поле обязательно для заполнения", 
            driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText());
    }

    // 3. Поле имени пустое
    @Test
    void shouldShowErrorWhenNameIsEmpty() {
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79211234567");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button")).click();
        assertEquals("Поле обязательно для заполнения", 
            driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText());
    }

    // 4. Поле телефона пустое
    @Test
    void shouldShowErrorWhenPhoneIsEmpty() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иван Петров");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button")).click();
        assertEquals("Поле обязательно для заполнения", 
            driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText());
    }

    // 5. Чекбокс не отмечен
    @Test
    void shouldShowErrorWhenAgreementNotChecked() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иван Петров");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79211234567");
        driver.findElement(By.cssSelector("button")).click();
        assertTrue(driver.findElement(By.cssSelector("[data-test-id=agreement].input_invalid")).isDisplayed());
    }

    // 6. Латиница в ФИО
    @Test
    void shouldShowErrorWhenNameContainsLatinLetters() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Ivan Petrov");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79211234567");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button")).click();
        assertEquals("Имя и Фамилия указанные неверно. Допустимы только русские буквы, пробелы и дефисы.", 
            driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText());
    }
}