package com.oracle.pgbu.selenium.pageobjects.obi;

import com.oracle.pgbu.selenium.supporting.CommonTestBase;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Reporter;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

/**
 * @author Sushma N
 */
public class LoginPage extends CommonTestBase {

	public static final Logger log = Logger.getLogger(LoginPage.class.getName());
	HomePage homePage;
	@FindBy(id = "sawlogonuser")
	public WebElement username;

	@FindBy(id = "sawlogonpwd")
	public WebElement password;

	@FindBy(xpath = "//input[@id='idlogon']")
	public WebElement submitButton;

	@FindBy(xpath = "//form[@id='logonForm']/table/tbody/tr/td/table/tbody/tr[2]/td[2]/div/div")
	public WebElement loginFail;

	public LoginPage(WebDriver driver) {
		this.driver = driver;
		// CommonTestBase = new CommonTestBase();
		PageFactory.initElements(driver, this);
	}

	public void Login(String user, String pass) {
		homePage = new HomePage(driver);
		try {
			log("-----------------Login Started------------------");
			enterText(user, username);
			enterText(pass, password);
			clickElement(submitButton);
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			try {
				if (loginFail.isDisplayed()) {
					log(loginFail.getText());
					assertFalse(loginFail.isDisplayed());
				}
			} catch (NoSuchElementException e) {
				waitForElement(1, homePage.signOut);
				assertTrue(homePage.signOut.isDisplayed());
				log("Successfully Logged in");
			}
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}
	}

	public boolean verifySignoutDisplay() {
		try {
			homePage.signOut.isDisplayed();
			log("logout is displayed and object is:-" + homePage.signOut.toString());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void clickOnLogout() {
		clickElement(homePage.signOut);
	}

	public void log(String data) {
		log.info(data);
		Reporter.log(data);
	}

}
