package com.oracle.pgbu.selenium.pageobjects.obi;

import com.oracle.pgbu.selenium.supporting.CommonTestBase;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Reporter;

import java.util.ArrayList;
//import java.util.Iterator;
import java.util.List;

/**
 * @author Sushma N
 */

public class HomePage extends CommonTestBase {
	public static final Logger log = Logger.getLogger(HomePage.class.getName());

	@FindBy(xpath = "//span[@id='logout']/span/span")
	public WebElement signOut;

	@FindBy(xpath = "//span[@id='new']/span[1]/span")
	public WebElement newMenu;

	@FindBy(linkText = "Analysis")
	@CacheLookup
	public WebElement analysisLink;

	// @FindBy(xpath ="//tr[@id='\"Primavera-BurnDown\"_r1']/td/a")
	// public WebElement primaveraSubjectArea;

	@FindBy(xpath = "//img[@id='SAPaneHeaderToolbar_searchSA_image']")
	public WebElement searchIcon;

	@FindBy(id = "criteriaDataBrowserSASearchBoxInput")
	public WebElement searchInputBox;

	@FindBy(xpath = "//img[@id='criteriaDataBrowser$Project_disclosure']")
	public WebElement project;

	// @FindBy(xpath ="//span[text()='Project']")
	// public WebElement project;
	@FindBy(xpath = "//img[@id='criteriaDataBrowser$saTreeNode_129_disclosure']")
	public WebElement projectCodes;

	@FindBy(xpath = "//table[@id='criteriaTab_tab']/tbody/tr/td/div")
	public WebElement criteriaLink;

	@FindBy(xpath = "//table[@id='resultsTab_tab']/tbody/tr/td/div")
	public WebElement resultLink;

	@FindBy(xpath = "//*[@id='criteriaDataBrowser$saTreeNode_144_disclosure']")
	public WebElement generalProj;

	@FindBy(xpath = "//span[text()='Text UDFs - (Project)']")
	public WebElement textUdfsProject;

	@FindBy(xpath = "//span[text()='Codes - (Project)']")
	public WebElement codesProject;

	@FindBy(xpath = "//span[text()='Date UDFs - (Project)']")
	public WebElement dateUdfs;

	@FindBy(css = ".ErrorInfo")
	public WebElement noResultMsg;

	@FindBy(xpath = "//div[text()='Compound Layout']")
	public WebElement compoundLayout;

	public HomePage(WebDriver driver) {
		this.driver = driver;
		// CommonTestBase = new CommonTestBase();
		PageFactory.initElements(driver, this);
	}

	public void log(String data) {
		log.info(data);
		Reporter.log(data);
	}
}
