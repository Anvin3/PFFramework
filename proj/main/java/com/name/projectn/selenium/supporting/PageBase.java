package com.oracle.pgbu.selenium.supporting;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Reporter;

public class WebappPageBase {
	protected WebDriver driver;
	public static final Logger log = Logger.getLogger(WebappPageBase.class.getName());

	public void log(String data) {
			log.info(data);
			Reporter.log(data);
		}
	
	@FindBy(xpath = "//header[@id='pgbu-nav-header']/div/div[2]/span[2]/strong")
	public WebElement welcomeheader;
	
	@FindBy(linkText = "Log Out")
	public WebElement logout;
	
	@FindBy(xpath = "html/body/div[1]/ul/li[1]/a/i")
	public WebElement home;

	@FindBy(xpath = "/html/body/div[1]/ul/li[2]")
	public WebElement status;
	
	@FindBy(xpath = "html/body/div[1]/ul/li[3]/a/i")
	public WebElement codes;
	
	@FindBy(xpath = "html/body/div[1]/ul/li[4]/a/i")
	public WebElement udfs;
	
	@FindBy(xpath = "html/body/div[1]/ul/li[5]/a/i")
	public WebElement publication;
	
	@FindBy(xpath = "html/body/div[1]/ul/li[6]/a/i")
	public WebElement config;
	
	@FindBy(xpath = "html/body/div[1]/ul/li[3]/a/i")
	public WebElement configUnifier;
	
	@FindBy(id = "pageHeader")
	public WebElement allDropDown;

	@FindBy(xpath = "//div[@id='cssmenu']/ul/li[1]/ul/table[1]/tbody/tr/td/li/a")
	public WebElement allLink;
	//*[@id='cssmenu']/ul/li[1]/ul/table[2]/tbody/tr/td
	@FindBy(xpath = "//div[@id='cssmenu']/ul/li[1]/ul/table[2]/tbody/tr/td/li/a")
	public WebElement ds1Link;

	@FindBy(xpath = "//div[@id='cssmenu']/ul/li[1]/ul/table[3]/tbody/tr/td/li/a")
	public WebElement ds2Link;
	
}