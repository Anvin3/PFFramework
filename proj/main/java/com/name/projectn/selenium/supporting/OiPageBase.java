package com.oracle.pgbu.selenium.supporting;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Reporter;

public class ObiPageBase {
	protected WebDriver driver;

	@FindBy(xpath = "//span[@id='logout']/span/span")
	public WebElement signOut;

	@FindBy(xpath = "//span[@id='home']/span/span")
	public WebElement home;

	@FindBy(xpath = "//span[@id='new']/span[1]/span")
	public WebElement newMenu;

	@FindBy(linkText = "Analysis")
	public WebElement analysisLink;

	@FindBy(xpath = "//table[@id='criteriaTab_tab']/tbody/tr/td/div")
	public WebElement criteriaLink;

	@FindBy(xpath = "//table[@id='resultsTab_tab']/tbody/tr/td/div")
	public WebElement resultLink;

	@FindBy(xpath = "//img[@id='idAnswersColumnsToolbar_removeAll_image']")
	public WebElement clearButton;

	@FindBy(xpath = "//img[@id='SAPaneHeaderToolbar_searchSA_image']")
	public WebElement searchIcon;

	@FindBy(id = "criteriaDataBrowserSASearchBoxInput")
	public WebElement searchInputBox;

	@FindBy(xpath = "//div[text()='Compound Layout']")
	public WebElement compoundLayout;

	@FindBy(xpath = "//*[@id=\"o:viewpreview~r:reportResult\"]")
	public WebElement reportResult;

	/*
	 * @FindBy(css = ".ErrorInfo") public WebElement noResultMsg;
	 */

	public ObiPageBase(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	public static final Logger log = Logger.getLogger(ObiPageBase.class.getName());

	public void log(String data) {
			log.info(data);
			Reporter.log(data);
		}
}
