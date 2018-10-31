package com.oracle.pgbu.selenium.supporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.oracle.pgbu.selenium.io.WebEventListener;
import com.sun.glass.events.KeyEvent;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;
import static org.testng.Assert.assertTrue;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

/**************************************************************
 * Library - CommonTestBase Description- Includes all common handler
 * functions/methods. Date created - 5-Jan-2018 Developed by - Sushma Nandipati
 * Last Modified By - Sushma Nandipati Last Modified Date - 7-Feb-2018
 ***************************************************************/

public class CommonTestBase {

	public static final Logger log = Logger.getLogger(CommonTestBase.class.getName());
	public WebDriver driver;
	// public EventFiringWebDriver driver;
	public WebEventListener eventListener;
	public Properties prop = new Properties();
	public static ExtentReports extent;
	public static ExtentHtmlReporter htmlReporter;
	public static ExtentTest test;
	public ITestResult result;
	public Statement sqlStatement;
	public ResultSet rSet;
	public Connection con;
	public SoftAssert softAssertion = new SoftAssert();

	static {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
		htmlReporter = new ExtentHtmlReporter(
				System.getProperty("user.dir") + "/Reports/" + formatter.format(calendar.getTime()) + ".html");
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
	}

	/**************************************************************
	 * Method Name - afterMethod(ITestResult result) Description- calls methods that
	 * are defined after completing test case. Developed by - Sushma Nandipati Last
	 * Modified By - Last Modified Date -
	 ***************************************************************/
	@AfterMethod
	public void afterMethod(ITestResult result) {
		try {
			getResult(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**************************************************************
	 * Method Name -beforeMethod(Method result) Description- Calls reporting methods
	 * before starting of test case. Developed by - Sushma Nandipati Last Modified
	 * By - Last Modified Date -
	 ***************************************************************/
	@BeforeMethod()
	public void beforeMethod(Method method) {
		test = extent.createTest(method.getName());
		test.log(Status.INFO, method.getName() + " test Started");
		softAssertion = new SoftAssert();
	}

	/**************************************************************
	 * Method Name -endTest() Description-Closes browser after running test case.
	 * Terminate connection of extent reports as well. Developed by - Sushma
	 * Nandipati Last Modified By - Last Modified Date -
	 ***************************************************************/
	@AfterClass(alwaysRun = true)
	public void endTest() {
		closeBrowser();
	}

	public void closeBrowser() {
		driver.quit();
		log.info("browser closed");
		extent.flush();
	}

	/**************************************************************
	 * Method Name - loadData() Description- This method loads data from property
	 * file. Developed by - Sushma Nandipati Last Modified By - Last Modified Date -
	 ***************************************************************/

	public void loadData() throws IOException {
		File file = new File(
				System.getProperty("user.dir") + "/analytics/main/java/com/oracle/pgbu/selenium/io/Test.Properties");
		FileInputStream f = new FileInputStream(file);
		prop.load(f);
	}

	public String getProp(String propName) throws IOException {
		loadData();
		// log(prop.getProperty(propName));
		return prop.getProperty(propName);
	}

	/**************************************************************
	 * Method Name - openUrl(String url) Description- Opens url Developed by -
	 * Sushma Nandipati Last Modified By - Last Modified Date -
	 ***************************************************************/
	public void openUrl(String url) {
		log.info("navigating to :-" + url);
		try {
			driver.get(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**************************************************************
	 * Method Name - getResult(ITestResult result) Description- Gives result of
	 * current test case and executes the following lines depending on the result.
	 * Developed by - Sushma Nandipati Last Modified By - Last Modified Date -
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 ***************************************************************/
	public void getResult(ITestResult result) throws IOException, InterruptedException {
		if (result.getStatus() == ITestResult.SUCCESS) {
			test.log(Status.PASS, result.getName() + " test passed");
			String screen = captureScreen("");
			test.pass(" ", MediaEntityBuilder.createScreenCaptureFromPath(screen).build());
		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(Status.SKIP, result.getName() + " test skipped because:-" + result.getThrowable());
		} else if (result.getStatus() == ITestResult.FAILURE) {
			test.log(Status.ERROR, result.getName() + " test failed " + result.getThrowable());
			String screen = captureScreen("");
			test.fail("Screen shot of failed page is ", MediaEntityBuilder.createScreenCaptureFromPath(screen).build());
		} else if (result.getStatus() == ITestResult.STARTED) {
			test.log(Status.INFO, result.getName() + " test started");
		}
	}

	/**************************************************************
	 * Method Name - waitForElement(WebDriver driver, int timeOutInSeconds,
	 * WebElement element) Description- Wait for given time period untill the given
	 * conditions are met Developed by - Sushma Nandipati Last Modified By - Last
	 * Modified Date -
	 ***************************************************************/
	public void waitForElement(int timeOutInSeconds, WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	public void waitForAlert(int timeOutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.alertIsPresent());
	}

	/**************************************************************
	 * Method Name - highlightMe(WebDriver driver, WebElement element) Description-
	 * Highlight the element Developed by - Sushma Nandipati Last Modified By - Last
	 * Modified Date -
	 ***************************************************************/
	public void highlightElement(WebElement element) throws InterruptedException {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].style.border='4px solid yellow'", element);
	}

	/**************************************************************
	 * Method Name - bringToViewPort(WebDriver driver, WebElement element)
	 * Description- Bring the elements to the view port if it is not visible on
	 * current screen. Developed by - Sushma Nandipati Last Modified By - Last
	 * Modified Date -
	 ***************************************************************/
	public void bringToViewPort(WebElement element) throws InterruptedException {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView({behavior: \"instant\"})", element);
		log("Bringing " + element.getText() + " into viewport.");
	}

	public void scrollToPageTop() throws InterruptedException {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0,0)");
		log("Scrolling to top of page");
	}

	/**************************************************************
	 * Method Name - enterText(String texttoenter, WebElement ele) Description- To
	 * enter text in elements current screen. Developed by - Sushma Nandipati Last
	 * Modified By - Last Modified Date - March 22 , 2018
	 ***************************************************************/
	public void enterText(String texttoenter, WebElement ele) {
		try {
			ele.clear();
			ele.sendKeys(texttoenter);
			log("Entered " + texttoenter);
			// takeScreenshotinReport();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}
	}

	/**************************************************************
	 * Method Name -getAllWindows() Description- Returns the ids of all current
	 * opened windows of browser. Developed by - Sushma Nandipati Last Modified By -
	 * Last Modified Date -
	 ***************************************************************/
	public ArrayList<String> getAllWindows() {
		// String parentwindowHandle = driver.getWindowHandle();
		// Get the list of window handles
		ArrayList<String> windowIds = new ArrayList<String>(driver.getWindowHandles());
		return windowIds;
	}

	public void switchToChildWindow(int childwindownumber) {
		ArrayList<String> windowIds = getAllWindows();
		driver.switchTo().window(windowIds.get(childwindownumber));
	}

	public void switchToParentWindow() {
		ArrayList<String> windowIds = getAllWindows();
		driver.switchTo().window(windowIds.get(0));
	}
	
	public void closeChildWindows() {
		ArrayList<String> windowIds = getAllWindows();
		
		while(windowIds.size()>1) {
			driver.switchTo().window(windowIds.get(1));
			driver.close();	
			windowIds = getAllWindows();
		}
		driver.switchTo().window(windowIds.get(0));
	}

	/**************************************************************
	 * Method Name - captureScreen(String fileName) Description- Captures the screen
	 * and save it with current time stamp. Developed by - Sushma Nandipati Last
	 * Modified By - Last Modified Date -
	 ***************************************************************/
	public String captureScreen(String fileName) {
		if (fileName == "") {
			fileName = "img";
		}
		File destFile = null;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			String reportDirectory = new File(System.getProperty("user.dir")).getAbsolutePath() + "/Reports/";
			destFile = new File(
					(String) reportDirectory + fileName + "_" + formater.format(calendar.getTime()) + ".png");
			FileUtils.copyFile(scrFile, destFile);
			// // This will help us to link the screen shot in testNG report
			// Reporter.log("<a href='" + destFile.getAbsolutePath() + "'> <img src='" +
			// destFile.getAbsolutePath()
			// + "' height='100' width='100'/> </a>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destFile.toString();
	}

	public String captureFullScreen(String fileName) throws IOException {
		if (fileName == "") {
			fileName = "img";
		}
		// File destFile = null;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
		Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000))
				.takeScreenshot(driver);
		String reportDirectory = new File(System.getProperty("user.dir")).getAbsolutePath() + "/Reports/";
		String dest = reportDirectory + fileName + "_" + formater.format(calendar.getTime()) + ".png";
		// FileUtils.copyFile(scrFile, destFile);
		ImageIO.write(screenshot.getImage(), "PNG", new File(dest));
		return dest;
	}

	public void log(String data) {
		log.info(data);
		Reporter.log(data);
		test.log(Status.INFO, data);
	}

	public void takeScreenshotinReport() {
		System.out.println("Bypassing screenshots");
		/*
		String screen = captureScreen("");
		try {
			test.info(" ", MediaEntityBuilder.createScreenCaptureFromPath(screen).build());
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}

	public void takeFullPageScreenshotinReport() throws IOException {
		//String screen = captureFullScreen("");
		/*
		try {
			test.info(" ", MediaEntityBuilder.createScreenCaptureFromPath(screen).build());
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}

	/**************************************************************
	 * Method Name -hoverSelect(WebElement ele ) Description-Clicking on elements of
	 * hover menus. Developed by - Sushma Nandipati Last Modified By - Last Modified
	 * Date -
	 ***************************************************************/
	public void hoverToMenu(WebElement ele) throws InterruptedException {
		Actions action = new Actions(driver);
		// action.moveToElement(menu).click(subMenu).build().perform();
		action.moveToElement(ele)/* .click().build().perform() */;
		log("Clicked on " + ele.getText());
		// takeScreenshotinReport();
	}

	public void hoverToMenu(WebElement ele, String text) {
		Actions action = new Actions(driver);
		action.moveToElement(ele)/* .click().build().perform() */;
		log("Clicked on " + text);
		// takeScreenshotinReport();
	}

	/**************************************************************
	 * Method Name -dragAndDrop(WebElement source,WebElement dest) Description-
	 * Provides Drag and Drop operation. Developed by - Sushma Nandipati Last
	 * Modified By - Last Modified Date -
	 ***************************************************************/
	public void dragAndDrop(WebElement source, WebElement dest) {
		Actions action = new Actions(driver);
		action.dragAndDrop(source, dest).build().perform();
		// takeScreenshotinReport();
	}

	/**************************************************************
	 * Method Name -doubleClick(WebElement ele) Description- Provides double click
	 * operation. Developed by - Sushma Nandipati Last Modified By - Last Modified
	 * Date -
	 ***************************************************************/
	public void doubleClick(WebElement ele) {
		Actions action = new Actions(driver);
		waitForElement(6, ele);
		action.doubleClick(ele).build().perform();
		log("Double clicked on " + ele.getText());
		// takeScreenshotinReport();
	}

	/**
	 * Clicks the first element then shift clicks the last element from a given list
	 * to effectively select all elements in the list.
	 */
	public void shiftClickList(List<WebElement> list) {
		Actions click = new Actions(driver);
		log("Shift-clicking from \"" + list.get(0).getText() + "\" to \"" + list.get(list.size() - 1).getText() + "\"");
		list.get(0).click();
		click.keyDown(Keys.SHIFT).click(list.get(list.size() - 1)).keyUp(Keys.SHIFT).perform();
	}

	/**************************************************************
	 * Method Name -clickElement(WebElement ele) Description- Provides click
	 * operation. Developed by - Sushma Nandipati Last Modified By - Last Modified
	 * Date -
	 ***************************************************************/
	public void clickElement(WebElement ele) {
		waitForElement(6, ele);
		log("Clicking on \"" + ele.getText() + "\"");
//		if(prop.getProperty("BROWSER").equalsIgnoreCase("ie"))
//		{
//			ForceClick(ele);
//		}
//		else
		//log("Clicked on \"" + ele.getText() + "\"");
			ele.click();

	}

	public void clickElement(WebElement ele, String text) {
		//takeScreenshotinReport();
		waitForElement(6, ele);
		
		//log("value is "+getProp("BROWSER"));
//		if(prop.getProperty("BROWSER").equalsIgnoreCase("ie"))
//		{
//			ForceClick(ele);
//		}
//		else
	
			ele.click();
			log("Clicked on \"" + text + "\"");
		
	}

	public void clickElementSilent(WebElement ele) {
		waitForElement(6, ele);
		ele.click();
	}

	/**************************************************************
	 * Method Name -setImplicitWait(int timeUnitInSeconds) Description- Sets the
	 * timeout for acting on WebElements Developed by - Sushma Nandipati Last
	 * Modified By - Last Modified Date -
	 ***************************************************************/
	public void setImplicitWait(int timeUnitInSeconds) {
		driver.manage().timeouts().implicitlyWait(timeUnitInSeconds, TimeUnit.SECONDS);
	}

	/**************************************************************
	 * Method Name -ForceClick(WebElement ele) Description- Provides forceclick
	 * operation using js executor. Developed by - Sushma Nandipati Last Modified By
	 * - Last Modified Date -
	 ***************************************************************/
	@Deprecated
	public void ForceClick(WebElement ele) {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", ele);
	}

	  public void setAttribute(WebElement element, String attName, String attValue) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
	  
	        js.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", 
	                element, attName, attValue);
	  }
	/**************************************************************
	 * Method Name -acceptAlert() Description- Method is to accept alert Developed
	 * by - Sushma Nandipati Last Modified By - Last Modified Date -
	 ***************************************************************/
	public boolean acceptAlert() {
		try {
			driver.switchTo().alert().accept();
			log("Accepted alert");
			return true;
		} catch (NoAlertPresentException Ex) {
			return false;
		}
	}

	/**************************************************************
	 * Method Name - getAlertText() Description- Method is to get text of the alert
	 * Developed by - Sushma Nandipati Last Modified By - Last Modified Date -
	 ***************************************************************/
	public String getAlertText() {
		try {
			
			log("Alert content is : " + driver.switchTo().alert().getText());
		} catch (NoAlertPresentException Ex) {
		}
		return driver.switchTo().alert().getText();
	}

	/**************************************************************
	 * Method Name - getListText(int min, int max) Description- get an arraylist of
	 * the WebElements' text Developed by - Sushma Nandipati Last Modified By - Last
	 * Modified Date -
	 ***************************************************************/
	public ArrayList<String> getListText(List<WebElement> eleList) {
		// log("Children of the list available are");
		ArrayList<String> al = new ArrayList<String>();
		for (WebElement ele : eleList) {
			al.add(ele.getText());
		}
		return al;
	}

	/**************************************************************
	 * Method Name - randInt(int min, int max) Description- Method is to generate
	 * random integers in given range. Developed by - Sushma Nandipati Last Modified
	 * By - Last Modified Date -
	 ***************************************************************/
	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	/**************************************************************
	 * Method Name - launchApp(String browser , String url) Description- Method is
	 * to open browser and launch application. Developed by - Sushma Nandipati Last
	 * Modified By - Last Modified Date -
	 ***************************************************************/
	public void launchApp(String browser, String url) throws IOException {
		String currbrowser=browser;
		if (browser.equals("chrome")) {
			ChromeOptions options = new ChromeOptions();
			options.addArguments("disable-infobars");
			options.addArguments("--start-minimized");
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "/lib/Selenium3.9x/chromedriver.exe");
			DesiredCapabilities cap = DesiredCapabilities.chrome();
			cap.setBrowserName("chrome");
			driver = new ChromeDriver(options);
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			openUrl(url);
		} else if (browser.equalsIgnoreCase("firefox")) {
			System.setProperty("webdriver.gecko.driver",
					System.getProperty("user.dir") + "/lib/Selenium3.9x/geckodriver.exe");
			DesiredCapabilities cap = DesiredCapabilities.firefox();
			driver = new FirefoxDriver(cap);
			// driver= new EventFiringWebDriver(eDriver);
			// driver.register(eventListener);
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			openUrl(url);
			takeScreenshotinReport();
		} else if(browser.equals("ie")) {
//			ChromeOptions options = new ChromeOptions();
//			options.addArguments("disable-infobars");
//			options.addArguments("--start-minimized");
			System.setProperty("webdriver.ie.driver",
					System.getProperty("user.dir") + "/lib/Selenium3.9x/IEDriverServer3.9.exe");
			DesiredCapabilities cap = DesiredCapabilities.internetExplorer();
			cap.setCapability("nativeEvents", false);    
			cap.setCapability("unexpectedAlertBehaviour", "accept");
			cap.setCapability("ignoreProtectedModeSettings", true);
			cap.setCapability("disable-popup-blocking", true);
			cap.setCapability("enablePersistentHover", true);
			cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
			driver = new InternetExplorerDriver(cap);
			
			cap.setBrowserName("internetExplorer");
			//driver = new InternetExplorerDriver();
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			openUrl(url);
		}
		else {
			throw new IllegalArgumentException("The Browser Type is Undefined");
		}
	}

	public void checkNav(String expectedUrl) {
		log("Verifying that its navigated to correct url or not");
		String actUrl = driver.getCurrentUrl();
		// String extractUrl=actUrl.split("p6rdb/",1)[1];
		// log(""+extractUrl);
		assertTrue(actUrl.contains(expectedUrl));
		log("Navigated to correct url");
		takeScreenshotinReport();
	}

	public void hoverMenuAndSelect(WebElement menu, WebElement eletoselect) {
		try {
			log("Hovering to the menu");
			Actions action = new Actions(driver);
			action.moveToElement(menu).moveToElement(eletoselect).click().build().perform();
			//takeScreenshotinReport();
			log("Selected " + eletoselect.getText() + "from menu");
			//takeScreenshotinReport();
		} catch (NoSuchElementException e) {
		//	log("Element you are trying to access does not exist!!");
			e.printStackTrace();
		}
	}

	public ResultSet getQueryResult(String Query) throws SQLException, ClassNotFoundException, IOException {
		String driverName = "oracle.jdbc.driver.OracleDriver";
		Class.forName(driverName);
		// log();
		con = DriverManager.getConnection(getProp("JDBC_URL"), getProp("DB_USERNAME"), getProp("DB_PASSWORD"));
		sqlStatement = con.createStatement();
		rSet = sqlStatement.executeQuery(Query);
		return rSet;
	}

	public void closeConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			System.out.println("Failure while closing DB connection");
			e.printStackTrace();
		}
	}
   
	public String getFontColor(WebElement elmEle) {
		String sColor = elmEle.getCssValue("color");
		String[] numbers = sColor.replace("rgba(", "").replace(")", "").split(",");
		int iR = Integer.parseInt(numbers[0].trim());
		int iG = Integer.parseInt(numbers[1].trim());
		int iB = Integer.parseInt(numbers[2].trim());
		String hex = String.format("#%02x%02x%02x", iR, iG, iB).toUpperCase();
		return hex;
	}

	/**************************************************************
	 * Function Name - SelectOptionFromDropDown(WebElement WebList, String Option)
	 * Description- This function selects an option from the Dropdown Parameters -
	 * WebElement WebList, String Option Date created - March 6, 2018 Developed by -
	 * Sushma Nandipati Last Modified By - Last Modified Date -
	 ***************************************************************/
	public boolean SelectOptionFromDropDown(WebElement lstWebList, String sOption)
			throws HeadlessException, IOException, AWTException {
		try {
			if (lstWebList.isEnabled() || lstWebList.isDisplayed() || lstWebList.isSelected()) {
				Select OptionSelect = new Select(lstWebList);
				OptionSelect.selectByVisibleText(sOption);
				log("Selected " + sOption);
				
				return true;
			} else {
				log("Given option " + sOption + " is not available in list");
				return false;
			}
		} catch (Exception e) {
			log(lstWebList + "WebList is NOT present");
			return false;
		}
	}
	
	public boolean IsElementPresent(WebElement ele)
	{
	    try
	    {
	        ele.isDisplayed();
	        return true;
	    }
	    catch (NoSuchElementException e)
	    {
	        return false;
	    }
	}
	
	public boolean RobotKeyPress(String keytoPress) throws AWTException
	{
	    try
	    {	
	    	Robot r=new Robot();
	    	
	       if(keytoPress.contains("right"))
	       {
	    	   r.keyPress(KeyEvent.VK_RIGHT);
	    	   log("Pressed right arrow");
	       }
	        return true;
	    }
	    catch (NoSuchElementException e)
	    {
	        return false;
	    }
	}
	public String extractString(String value, String a, String b) {
        // Return a substring between the two strings.
        int posA = value.indexOf(a);
        if (posA == -1) {
            return "";
        }
        int posB = value.lastIndexOf(b);
        if (posB == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= posB) {
            return "";
        }
        return value.substring(adjustedPosA, posB);
    }
	

}
