package testiranje;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Raiffeisen {

    private WebDriver driver;
    private WebDriverWait wait;
    private String regex = "^\\d+(\\.\\d{1,2})? [A-Z]{3} = \\d+(\\.\\d{1,2})? [A-Z]{3}$";

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofMillis(10000));
    }

    @Test
    public void kalkulator() {
        driver.get("https://www.rba.hr");

        try {
            WebElement cookies = wait.until(ExpectedConditions.elementToBeClickable(By.id("onetrust-reject-all-handler")));
            cookies.click();
        } catch (TimeoutException e) {}

        driver.findElement(By.id("button-3faa825664")).click();

        Set<String> windowHandles = driver.getWindowHandles();
        List<String> handles = new ArrayList<>(windowHandles);
        driver.switchTo().window(handles.get(handles.size() - 1));
        Assert.assertTrue(driver.getCurrentUrl().contains("/alati/tecajni-kalkulator"));

        WebElement tecaj = driver.findElement(By.id("rateExch"));
        String tecaj_o = tecaj.getText();
        new Select(driver.findElement(By.id("val1"))).selectByVisibleText("EUR");
        new Select(driver.findElement(By.id("val2"))).selectByVisibleText("GBP");

        WebElement input = driver.findElement(By.id("suma1"));
        input.sendKeys(Keys.chord(Keys.LEFT_CONTROL, "a"));
        input.sendKeys(Keys.BACK_SPACE);
        WebElement iznos = driver.findElement(By.id("toHouseExch"));
        String iznos_o = iznos.getText();
        input.sendKeys("15.75");

        wait.until((ExpectedCondition<Boolean>) driver -> !tecaj.getText().equals(tecaj_o));
        wait.until((ExpectedCondition<Boolean>) driver -> !iznos.getText().equals(iznos_o));

        Reporter.log("Kupnja funti");
        Reporter.log("Tečaj: " + tecaj.getText(), true);
        Reporter.log("Iznos: " + iznos.getText(), true);

        Assert.assertTrue(tecaj.getText().matches(regex));
        Assert.assertTrue(iznos.getText().matches(regex));

        new Select(driver.findElement(By.id("val1"))).selectByVisibleText("USD");
        String tecaj2_o = tecaj.getText();
        new Select(driver.findElement(By.id("val2"))).selectByVisibleText("EUR");

        input.sendKeys(Keys.chord(Keys.LEFT_CONTROL, "a"));
        input.sendKeys(Keys.BACK_SPACE);
        String iznos2_o = iznos.getText();
        input.sendKeys("24.25");

        wait.until((ExpectedCondition<Boolean>) driver -> !tecaj.getText().equals(tecaj2_o));
        wait.until((ExpectedCondition<Boolean>) driver -> !iznos.getText().equals(iznos2_o));

        Reporter.log("Prodaja dolara");
        Reporter.log("Tečaj: " + tecaj.getText(), true);
        Reporter.log("Iznos: " + iznos.getText(), true);

        Assert.assertTrue(tecaj.getText().matches(regex));
        Assert.assertTrue(iznos.getText().matches(regex));
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
