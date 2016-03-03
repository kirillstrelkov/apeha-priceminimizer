package apeha.priceminimizer.webdriver;

import com.google.common.base.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CommonUtils {
    private WebDriver driver = null;
    private long timeout = 3;

    public void goTo(String url) {
        setDriver();
        if (!driver.getCurrentUrl().equals(url))
            driver.get(url);
    }

    private void setDriver() {
        if (driver == null) {
            FirefoxProfile firefoxProfile = new FirefoxProfile();
            firefoxProfile.setPreference("dom.max_script_run_time", 200);
            driver = new FirefoxDriver(firefoxProfile);
            driver.manage().window().maximize();
        }
    }

    public WebDriver getDriver() {
        return this.driver;
    }

    public WebElement findElement(By by) {
        waitForPresent(by);
        return driver.findElement(by);
    }

    public List<WebElement> findElements(By by) {
        waitForPresent(by);
        return driver.findElements(by);
    }

    public void click(By by) {
        click(findElement(by));
    }

    public void click(WebElement element) {
        waitForVisible(element);
        element.click();
    }

    public void type(By by, String value) {
        type(findElement(by), value);
    }

    public void type(WebElement element, String value) {
        waitForVisible(element);
        element.clear();
        element.sendKeys(value);
    }

    public String getText(By by) {
        return getText(findElement(by));
    }

    public String getText(WebElement element) {
        waitForVisible(element);
        return element.getText();
    }

    public String getAttribute(By by, String attr) {
        return getAttribute(findElement(by), attr);
    }

    public String getAttribute(WebElement element, String attr) {
        waitForVisible(element);
        return element.getAttribute(attr);
    }

    public List<String> getValuesFromDropDown(WebElement element) {
        Select select = new Select(element);
        List<WebElement> options = select.getOptions();
        List<String> values = new LinkedList<String>();
        Iterator<WebElement> iterator = options.iterator();
        while (iterator.hasNext()) {
            WebElement next = iterator.next();
            values.add(getText(next));
        }
        return values;
    }

    public void selectByVisibleTextFromDropDown(WebElement element, String text) {
        Select select = new Select(element);
        select.selectByVisibleText(text);
    }

    public void selectByValueFromDropDown(WebElement element, String value) {
        Select select = new Select(element);
        select.selectByValue(value);
    }

    public void waitForVisible(final By by) {
        waitForVisible(driver.findElement(by));
    }

    public void waitForVisible(final WebElement element) {
        new WebDriverWait(driver, timeout).until(new Predicate<WebDriver>() {

            @Override
            public boolean apply(WebDriver arg0) {
                try {
                    return element.isDisplayed();
                } catch (WebDriverException e) {
                    return false;
                }
            }

        });
    }

    public void waitForNotVisible(final By by) {
        waitForNotVisible(driver.findElement(by));
    }

    public void waitForNotVisible(final WebElement element) {
        new WebDriverWait(driver, timeout).until(new Predicate<WebDriver>() {

            @Override
            public boolean apply(WebDriver arg0) {
                try {
                    return !element.isDisplayed();
                } catch (WebDriverException e) {
                    return true;
                }

            }

        });
    }

    public void waitForPresent(final By by) {
        new WebDriverWait(driver, timeout).until(new Predicate<WebDriver>() {

            @Override
            public boolean apply(WebDriver arg0) {
                return driver.findElements(by).size() > 0;
            }

        });
    }

    public void waitForNotPresent(final By by) {
        new WebDriverWait(driver, timeout).until(new Predicate<WebDriver>() {

            @Override
            public boolean apply(WebDriver arg0) {
                return driver.findElements(by).size() == 0;
            }

        });
    }

    public void closeDriver() {
        if (driver != null)
            driver.quit();
    }

    public boolean waitForIsVisible(By by) {
        try {
            waitForVisible(by);
            return true;
        } catch (WebDriverException e) {
            return false;
        }
    }

    public String getUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isVisible(By by) {
        try {
            WebElement element = driver.findElement(by);
            return element.isDisplayed();
        } catch (WebDriverException e) {
            return false;
        }
    }

    public WebElement findElement(final WebElement webItem, final By by) {
        return findElement(webItem, by, 0);
    }

    public WebElement findElement(final WebElement webItem, final By by, int i) {
        new WebDriverWait(driver, timeout).until(new Predicate<WebDriver>() {

            @Override
            public boolean apply(WebDriver arg0) {
                try {
                    webItem.findElements(by);
                    return true;
                } catch (WebDriverException e) {
                    return false;
                }
            }

        });
        return webItem.findElements(by).get(i);
    }

}
