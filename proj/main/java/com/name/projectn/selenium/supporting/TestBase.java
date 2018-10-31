package com.oracle.pgbu.selenium.supporting;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

import com.oracle.pgbu.selenium.pageobjects.webapp.ConfigPage;
import com.oracle.pgbu.selenium.pageobjects.webapp.GlobalStatusPage;
import com.oracle.pgbu.selenium.pageobjects.webapp.HomePage;
import com.oracle.pgbu.selenium.pageobjects.webapp.LoginPage;
import com.oracle.pgbu.selenium.pageobjects.webapp.StarStatusPage;

public class WebappTestBase extends CommonTestBase {

	public void init() throws IOException, NoSuchMethodException {
		loadData();
		String log4jConfPath = "log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		log.info(" Executing in " + prop.getProperty("BROWSER"));
		launchApp(prop.getProperty("BROWSER"), prop.getProperty("BASE_URL"));
	}
	public static final Logger log = Logger.getLogger(WebappTestBase.class.getName());

	public void log(String data) {
			log.info(data);
			Reporter.log(data);
		}
	/**
	 * Checks if the list of elements from one of the tables on the UDFs page is
	 * empty. An empty list will contain one element which has the text: No data
	 * available in table.
	 */
	public boolean checkIfEmptyTable(List<WebElement> list) {
		return list.get(0).getText().contains("No data available in table");
	}
	
	/**
	 * Similar to the checkIfEmptyTable method above except this method checks
	 * if the list contains the different string that appears when a search
	 * returns no results.
	 */
	public boolean checkIfEmptySearchTable(List<WebElement> list) {
		return list.get(0).getText().contains("No matching records found");
	}

	public void staretlCheck(String staretlid, GlobalStatusPage gspage) {
		int noofentries = gspage.statusTablRows.size();
		int rownum;
		log("Status table has " + noofentries + " entries");
		if (noofentries > 0) {
			for (rownum = 1; rownum <= noofentries; rownum++) {
				WebElement starname = driver
						.findElement(By.xpath("//table[@id='statusTable']/tbody/tr[" + rownum + "]/td[3]"));
				if (starname.getText().contentEquals(staretlid)) {
					log(staretlid + " is at row " + rownum);
					log("Custom name is "
							+ driver.findElement(By.xpath("//table[@id='statusTable']/tbody/tr[" + rownum + "]/td[2]"))
									.getText());
					break;
				} else
					log("Given data source has no etl runs yet");
			}
		} else {
			log("No etl records are present at this instance");
		}
	}

	public int checkNumberofDatasources(GlobalStatusPage gspage) {
		hoverToMenu(gspage.allDropDown, " datasources menu");
		List<WebElement> list = driver.findElements(By.xpath("//div[@id='cssmenu']/ul/li[1]/ul/table"));
		int NoOfMenuItems = list.size();
		// We are removing all from the list and considering only data sources, so
		// total-1.
		log("Total number of data sources are " + (NoOfMenuItems - 1));
		return NoOfMenuItems;
	}
//	
//	public void columnValuesOfStatusTable(GlobalStatusPage gspage,int columnNumber) {
//	    List<String> arrayList;
//		for(int i = 1;i<=gspage.statusTablRows.size();i++)
//		{
//		List<WebElement> eleList = driver.findElement(By.xpath("//table[@id='statusTable']/tbody/tr["+i+"]/td[4]"));
//		arrayList.add(eleList(i).getText());
//		}
//		
//	}

	public ArrayList<String> columnValuesOfStatusTable(int colnum, GlobalStatusPage gspage) {
		int noofentries = gspage.statusTablRows.size();
		int rownum;
		ArrayList<String> aList=null;
		log("Status table has " + noofentries + " entries");
		if (noofentries > 0) 
		{   aList=new ArrayList<String>();
			for (rownum = 1; rownum <= noofentries; rownum++)
			{
				WebElement starname = driver
						.findElement(By.xpath("//table[@id='statusTable']/tbody/tr[" + rownum + "]/td["+colnum+"]"));
				
				aList.add(starname.getText());
				
		     } 
		}else {
			log("No etl records are present at this instance");
		}
		return aList;
	}
	public List<WebElement> getDatasourceList() {
		return driver.findElements(By.xpath("//div[@id='cssmenu']/ul/li[1]/ul/table"));
	}

	public void checkTypeofDs(int datasourcenumber, StarStatusPage sspage) {
		if (datasourcenumber == 1) {
			try {
				hoverMenuAndSelect(sspage.allDropDown, sspage.ds1Link);
				if (sspage.addButton.isDisplayed()) {
					log("First source is P6");
				}
			} catch (NoSuchElementException e) {
				log("First source is Unifier");
			}
		} else if (datasourcenumber == 2) {
			try {
				hoverMenuAndSelect(sspage.allDropDown, sspage.ds2Link);
				if (sspage.addButton.isDisplayed()) {
					log("Second source is P6");
				}
			} catch (NoSuchElementException e) {
				log("Second source is Unifier");
			}
		}
	}

	public void checkTypeofDsAndSelect(String DatasourceType, StarStatusPage sspage) {
		List<WebElement> ListOfDS = getDatasourceList();
		for (WebElement ele : ListOfDS.subList(1, ListOfDS.size())) {
			int i = 1;
			hoverMenuAndSelect(sspage.allDropDown, ele);
			if (sspage.addButton.isDisplayed() && DatasourceType.equalsIgnoreCase("p6")) {
				log("source" + i + " is P6");
				break;
			} else {
				log("source" + i + " is Unifier");
				break;
			}
		}
	}

	public boolean checkReport(GlobalStatusPage gspage) throws IOException {
	//public boolean srt(GlobalStatusPage gspage) {
		boolean exist = true;
		if (gspage.statusCol.getText().contentEquals("Running")) {
			log("Etl is still running , Report is not available.");
			exist = false;
		} else {
			clickElement(gspage.reportCol, "Report");
		}
		return exist;
	}

	public void runETL(StarStatusPage sspage) {
		try {
			clickElement(sspage.actionButton);
			clickElement(sspage.runEtl);
			//log("Clicked on run etl");
			//log("Content on modal is "+sspage.runModal);
			clickElement(sspage.cancelBtn, "Cancel Button");
			log("Again clicking on Run from actions");
			clickElement(sspage.actionButton);
			clickElement(sspage.runEtl);
			clickElement(sspage.runBtn, "Run Button");
			//waitForAlert(2);
			waitForAlert(2);
			String successAlertText = getAlertText();
			//log("Content on alert is "+successAlertText);
			acceptAlert();
			if(successAlertText.contains("already in Progress"))
			{
				log("ETL is already running");
			}
			else
			   {
				assertEquals("ETL started Successfully", successAlertText);
			   }

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isDaySelected(String day) {
		// The checkbox ids are the same as the days of the week
		WebElement dayCheckbox = driver.findElement(By.id(day.toLowerCase().trim()));
		return dayCheckbox.isSelected();
	}

	public void addRunDay(String day) {
		// The checkbox ids are the same as the days of the week
		String fday=day+"1";
		WebElement dayCheckbox = driver.findElement(By.id(fday.toLowerCase().trim()));
		if (!dayCheckbox.isSelected()) {
			clickElement(dayCheckbox,"Day checkbox");
			Assert.assertTrue(day + " should be checked.", dayCheckbox.isSelected());
		}
		else
			log(""+day+"is already selected");
	}

	public void setRunTime(Integer hr, Integer min, String pmAm, StarStatusPage sspage) {
		String time = formatTimeString(hr, min, pmAm);
		enterText(time, sspage.timePicker);
	}

	public String formatTimeString(Integer hr, Integer min, String pmAm) {
		String time = "";
		// Add Hour
		if (hr < 10) {
			time += "0" + hr.toString();
		} else {
			time += hr.toString();
		}
		// hh:mm
		time += ":";
		// Add Min
		if (min < 10) {
			time += "0" + min.toString();
		} else {
			time += min.toString();
		}
		// Add pm/am
		time += " " + pmAm;
		return time;
	}

	public String getPropvalueFromUi(String globalorconfig, String proptobechecked, ConfigPage cpage) {
		String propvalue = null;
		int i;
		if (globalorconfig.equalsIgnoreCase("global")) {
			for (i = 0; i < cpage.globalallprop.size(); i++) { // log(globalallprop.get(i).getText());
				if (cpage.globalallprop.get(i).getText().contentEquals(proptobechecked)) {
					log("Property value is " + cpage.globalpropvalues.get(i).getText());
					propvalue = cpage.globalpropvalues.get(i).getText();
					break;
				}
			}
		} else if (globalorconfig.equalsIgnoreCase("config")) {
			for (i = 0; i < cpage.configallprop.size(); i++) {
				if (cpage.configallprop.get(i).getText().contentEquals(proptobechecked)) {
					log("Property value is " + cpage.configpropvalues.get(i).getText());
					propvalue = cpage.configpropvalues.get(i).getText();
					break;
				}
			}
		}
		return propvalue;
	}

	public String getPropValueFromDb(String propname)
			throws SQLException, IOException, InterruptedException, ClassNotFoundException {
		String propvalue = "none";
		ResultSet rSet = getQueryResult("select * from etl_global_parameter where P_FEATURE like '" + propname + "'");
		log("Checking table as " + getProp("DB_USERNAME"));
		while (rSet.next()) {
			propvalue = rSet.getString("P_1");
			log(propname + " value in db is: " + propvalue);
		}
		closeConnection();
		return propvalue;
	}
	public boolean checkPropInDb(String propname)
			throws SQLException, IOException, InterruptedException, ClassNotFoundException {
		boolean exists = true;
		String propvalue="none";
		ResultSet rSet = getQueryResult("select * from etl_global_parameter where P_FEATURE like '" + propname + "'");
		log("Checking table as " + getProp("DB_USERNAME"));
		try {
		if(rSet.wasNull())
		{
			log(" "+propname+" does not exist in DB");
			exists=false;
		}
		else {
		while (rSet.next()) {
			propvalue = rSet.getString("P_1");
		}
		}
		}
		catch(SQLException e)
		{
			log(" "+propname+" does not exist in DB");
			exists=false;
		}
		
		closeConnection();
		return exists;
	}

	public void rpdLinkUpdateDB(String newUnifierUrl, String newP6Url, ConfigPage cpage)
			throws SQLException, IOException, InterruptedException, ClassNotFoundException {
		enterText(newUnifierUrl, cpage.unifierBaseurl);
		enterText(newP6Url, cpage.p6Baseurl);
		clickElement(cpage.saveBtn, "Save");
		try {
			Thread.sleep(100);

		} catch (InterruptedException e1) {
		}
		assertEquals(cpage.successMsg.isDisplayed(), true, "Failed because success message is not getting displayed");

		ResultSet rSet = getQueryResult("select * from etl_global_parameter where P_FEATURE like '%db.star%base%'");
		log("Checking etl parameter table as " + getProp("DB_USERNAME"));
		while (rSet.next()) {
			if (rSet.getString("P_FEATURE").equalsIgnoreCase("db.star.unifier.base.url")) {
				log("Unifier Url in DB is " + rSet.getString("P_1"));
				assertEquals(newUnifierUrl, rSet.getString("P_1"));
			}
			if (rSet.getString("P_FEATURE").equalsIgnoreCase("db.star.p6.base.url")) {
				log("P6 Url in DB is " + rSet.getString("P_1"));
				assertEquals(newP6Url, rSet.getString("P_1"));
			}
		}
		closeConnection();
	}

	public void getRadioButtonvalueFromUi(ConfigPage cpage) {
		int i, j = 0;
		for (i = 1, j = 2; i < cpage.configallprop.size(); i++) {
			log("Property value is: " + cpage.configallprop.get(i).getText());
			if (cpage.configallprop.get(i).getText().contains("Level")) {
				log("Radio Button values are " + cpage.configpropradiovalues.get(j).getText());
				assertEquals(cpage.configpropradiovalues.get(j).getText().contains("Month"), true);
				j += 2;
			} else {
				j = j + 2;
				log("This is not history level property.");
			}
		}
	}

	public void login(String uname, String pword, LoginPage lpage, HomePage hpage) {
		try {
			log("-----------------Login Started------------------");
			enterText(uname, lpage.username);
			enterText(pword, lpage.password);
			clickElement(lpage.login);
			
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			//Thread.sleep(900000);
			waitForElement(30, hpage.logout);
			
			assertTrue(hpage.logout.isDisplayed());
			log("Successfully Logged in");
			takeScreenshotinReport();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			log("Login page is not loaded properly");
			//assertEquals(false, true, "Login failed");
		}
		catch (Exception e) {
			e.printStackTrace();
			//assertEquals(false, true, "Login failed");
		}

	}

}
