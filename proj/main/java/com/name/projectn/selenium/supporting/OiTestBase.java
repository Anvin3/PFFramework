package com.oracle.pgbu.selenium.supporting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ObiTestBase extends CommonTestBase {

	public void init() throws IOException, NoSuchMethodException {
		loadData();
		String log4jConfPath = "log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		log.info(" Executing in " + prop.getProperty("BROWSER"));
		launchApp(prop.getProperty("BROWSER"), prop.getProperty("OBI_URL"));
	}
	public static final Logger log = Logger.getLogger(ObiTestBase.class.getName());

	public void log(String data) {
			log.info(data);
			Reporter.log(data);
		}
	public boolean selectAreaUnderSubjectArea(String subjectareaname, WebElement newMenu, WebElement analysisLink) {
		boolean clicked = false;
		setImplicitWait(6);
		try {
			// clickElement(newMenu);
			clickElement(analysisLink);
			log("Clicked on " + subjectareaname + " Subject area");
			driver.findElement(By.linkText(subjectareaname)).click();
			clicked = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		setImplicitWait(1);
		return clicked;
	}

	public boolean checkForData(WebElement reportResult) {
		try {
			waitForElement(10, reportResult);
			String result = reportResult.getAttribute("result");
			if (result.equals("finished")) {
				log("data found");
				return true;
			} else if (result.equals("noData")) {
				log("data missing");
				return false;
			} else if (result.equals("searching")) {
				// If we're still searching,
				// log("searching for data");
				Thread.sleep(250);
				return (checkForData(driver.findElement(By.xpath("//*[@id=\"o:viewpreview~r:reportResult\"]"))));
			}
		} catch (StaleElementReferenceException e) {
			return (checkForData(driver.findElement(By.xpath("//*[@id=\"o:viewpreview~r:reportResult\"]"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**************************************************************
	 * Method Name - childElementsAndSelectOne(String subArea) Description- Select a
	 * single column from each folder/subfolder of the specified area Developed by -
	 * Jonathan Bees Last Modified By - Last Modified Date -
	 * 
	 * @throws IOException
	 ***************************************************************/
	public void childElementsAndSelectOne(String subArea) throws InterruptedException, IOException {
		setImplicitWait(3);
		WebElement subAreaExpander = driver
				.findElement(By.xpath("//img[@id='criteriaDataBrowser$" + subArea + "_disclosure']"));
		clickElement(subAreaExpander, subArea);

		// create a list of all of the subArea's children
		String childFields = "//div[@id='criteriaDataBrowser$" + subArea + "_children']/div";
		List<WebElement> childList = driver.findElements(By.xpath(childFields));

		// for all elements, check if they're folders. If they are, iterate through
		// them. If they're not,
		// just double-click the first one and move on.
		boolean selectedChild = false;
		boolean selectedGrandChild = false;

		// iterate through all of the children until we've selected one
		for (int i = 1; i <= childList.size() && !selectedChild; i++) {

			// Figure out the type of the child based on its image, then either open the
			// folder or select the column
			String childImageXPath = "//*[@id='criteriaDataBrowser$" + subArea + "_children']/div[" + i
					+ "]/span/span/img";
			WebElement childImage = driver.findElement(By.xpath(childImageXPath));

			// if our child is a folder, open it and then select the first column inside
			if (childImage.getAttribute("src").contains("folder.gif")) {
				String childXPath = "//div[@id='criteriaDataBrowser$" + subArea + "_children']/div[" + i
						+ "]/span/span";
				String grndChildXPath = "//div[@id='criteriaDataBrowser$" + subArea + "_children']/div[" + i
						+ "]/div/div";

				// find the xpath of the folder, then open it.
				WebElement child = driver.findElement(By.xpath(childXPath));
				if (!child.isDisplayed()) {
					bringToViewPort(child);
				}
				doubleClick(child);

				// create a list of grandchildren so we can select the first one
				List<WebElement> grandChildList = driver.findElements(By.xpath(grndChildXPath));
				// log(grandChildList.toString());
				// check if we actually got any grandchildren or if the folder's empty
				if (grandChildList.size() > 0) {
					// log(subArea + " has grandchildren under " + childList.get(i - 1).getText());

					WebElement grandChild = driver.findElement(By.xpath(grndChildXPath + "[1]/span/span/span"));
					// log(grandChild.toString());
					if (!grandChild.isDisplayed()) {
						bringToViewPort(grandChild);
					}
					doubleClick(grandChild);
					selectedChild = true;
					selectedGrandChild = true;

					doubleClick(child);

				} else {
					log("The child " + childList.get(i - 1).getText() + " has no children.");
				}
				softAssertion.assertEquals(selectedGrandChild, true,
						"Could not find grandchild of " + childList.get(i - 1).getText());
			}

			// if our child is a column, select it directly
			else if (childImage.getAttribute("src").contains("column.png")) {
				String childXPath = "//div[@id='criteriaDataBrowser$" + subArea + "_children']/div[1]/span/span";
				try {
					WebElement child = driver.findElement(By.xpath(childXPath));
					if (!child.isDisplayed()) {
						bringToViewPort(child);
					}
					doubleClick(child);
					selectedChild = true;
				} catch (NoSuchElementException e) {
				}
			}
			// make sure we've actually gotten through and selected a child. Fail the test
			// if something went wrong.
			softAssertion.assertEquals(selectedChild, true,
					"could not find child of " + childList.get(i - 1).getText());
		}

		subAreaExpander = driver.findElement(By.xpath("//img[@id='criteriaDataBrowser$" + subArea + "_disclosure']"));
		clickElement(subAreaExpander, subArea);

		setImplicitWait(1);
	}

	/**************************************************************
	 * Method Name - childElementsAndSelectOne(String subArea) Description- Select a
	 * single column from each folder/subfolder of the specified area Developed by -
	 * Jonathan Bees Last Modified By - Last Modified Date -
	 * 
	 * @throws IOException
	 ***************************************************************/
	public void childElementsAndSelectAll(String subArea) throws InterruptedException, IOException {
		setImplicitWait(3);
		WebElement subAreaExpander = driver
				.findElement(By.xpath("//img[@id='criteriaDataBrowser$" + subArea + "_disclosure']"));
		clickElement(subAreaExpander, subArea);

		// create a list of all of the subArea's children
		String childFields = "//div[@id='criteriaDataBrowser$" + subArea + "_children']/div";
		List<WebElement> childList = driver.findElements(By.xpath(childFields));

		// for all elements, check if they're folders. If they are, iterate through
		// them. If they're not,
		// just double-click the first one and move on.
		boolean selectedChild = false;
		boolean selectedGrandChild = false;

		// iterate through all of the children until we've selected one
		for (int i = 1; i <= childList.size() /* && !selectedChild */; i++) {

			// Figure out the type of the child based on its image, then either open the
			// folder or select the column
			String childImageXPath = "//*[@id='criteriaDataBrowser$" + subArea + "_children']/div[" + i
					+ "]/span/span/img";
			WebElement childImage = driver.findElement(By.xpath(childImageXPath));

			// if our child is a folder, open it and then select the first column inside
			if (childImage.getAttribute("src").contains("folder.gif")) {
				String childXPath = "//div[@id='criteriaDataBrowser$" + subArea + "_children']/div[" + i
						+ "]/span/span";
				String grndChildXPath = "//div[@id='criteriaDataBrowser$" + subArea + "_children']/div[" + i
						+ "]/div/div";

				// find the xpath of the folder, then open it.
				WebElement child = driver.findElement(By.xpath(childXPath));
				if (!child.isDisplayed()) {
					bringToViewPort(child);
				}
				doubleClick(child);

				// create a list of grandchildren so we can select all of them
				List<WebElement> grandChildList = driver.findElements(By.xpath(grndChildXPath));
				// log(grandChildList.toString());
				// check if we actually got any grandchildren or if the folder's empty
				if (grandChildList.size() > 0) {
					// log(subArea + " has grandchildren under " + childList.get(i - 1).getText());
					for (int j = 1; j <= grandChildList.size(); j++) {
						WebElement grandChild = driver
								.findElement(By.xpath(grndChildXPath + "[" + j + "]/span/span/span"));
						WebElement grandChildImage = driver
								.findElement(By.xpath(grndChildXPath + "[" + j + "]/span/span/img"));
						if (grandChildImage.getAttribute("src").contains("column.png")) {
							if (!grandChild.isDisplayed()) {
								bringToViewPort(grandChild);
							}
							doubleClick(grandChild);
							selectedChild = true;
							selectedGrandChild = true;
						}

					}

					doubleClick(child);

				} else {
					log("The child " + childList.get(i - 1).getText() + " has no children.");
				}
				softAssertion.assertEquals(selectedGrandChild, true,
						"Could not find grandchild of " + childList.get(i - 1).getText());
			}

			// if our child is a column, select it directly
			else if (childImage.getAttribute("src").contains("column.png")) {
				String childXPath = "//div[@id='criteriaDataBrowser$" + subArea + "_children']/div[1]/span/span";
				try {
					WebElement child = driver.findElement(By.xpath(childXPath));
					if (!child.isDisplayed()) {
						bringToViewPort(child);
					}
					doubleClick(child);
					selectedChild = true;
				} catch (NoSuchElementException e) {
				}
			}
			// make sure we've actually gotten through and selected a child. Fail the test
			// if something went wrong.
			softAssertion.assertEquals(selectedChild, true,
					"could not find child of " + childList.get(i - 1).getText());
		}

		subAreaExpander = driver.findElement(By.xpath("//img[@id='criteriaDataBrowser$" + subArea + "_disclosure']"));
		clickElement(subAreaExpander, subArea);

		setImplicitWait(1);
	}

	public void selectElements(String subArea) {
		try {
			if (prop.getProperty("EXTENDED_TEST").equalsIgnoreCase("TRUE")) {
				childElementsAndSelectAll(subArea);
			} else {
				childElementsAndSelectOne(subArea);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// grab an updated
	// pgbu_analytics/projects/db/analytics/repository/PresentationLayerDef.xml from
	// the main analytics git repo

	public static ArrayList<String> getCatalogFolders(String catalogName) {
		ArrayList<String> folderList = new ArrayList<String>();

		// open the xml export of the RPD file
		File rpdCatalogXml = new File(System.getProperty("user.dir")
				+ "/analytics/main/java/com/oracle/pgbu/selenium/io/PresentationLayerDef.xml");

		try {
			// Create a DOM representation of the XML file
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document catalog = builder.parse(rpdCatalogXml);
			catalog.getDocumentElement().normalize();
			// Get all of the PresentationCatalog elements
			NodeList presentationCatalogs = catalog.getElementsByTagName("PresentationCatalog");
			// System.out.println("number of catalogs: " +
			// presentationCatalogs.getLength());
			// Find the PresentationCatalog with the name we're currently looking for
			int listPos = 0;
			while ((listPos < presentationCatalogs.getLength())) {
				String curCatalogName = ((Element) presentationCatalogs.item(listPos)).getElementsByTagName("name")
						.item(0).getTextContent();
				// System.out.println("Found catalog name: " + curCatalogName);
				if (curCatalogName.equals(catalogName)) {
					break;
				}
				listPos++;
			}
			// Get the list of tables in the presentation catalog
			NodeList tableNodes = ((Element) (presentationCatalogs.item(listPos)))
					.getElementsByTagName("PresentationTable");
			// System.out.println("Number of tableNodes: " + tableNodes.getLength());
			for (int i = 0; i < tableNodes.getLength(); i++) {
				String folderName = ((Element) tableNodes.item(i)).getElementsByTagName("name").item(0)
						.getTextContent();

				// System.out.println("Adding folder " + folderName);
				folderList.add(folderName);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return folderList;
	}

	public ArrayList<String> getDisplayedFolders(String catalogName) {
		setImplicitWait(3);

		ArrayList<String> folderList = new ArrayList<String>();

		ArrayList<String> dimensions = new ArrayList<String>();
		String dimensionList = "//div[@id='criteriaDataBrowser$\"" + catalogName + "\"']/div/div";
		List<WebElement> colList = driver.findElements(By.xpath(dimensionList));
		for (WebElement element : colList) {
			dimensions.add(element.getAttribute("id").substring(20, element.getAttribute("id").length()));
		}

		for (String dim : dimensions) {
			folderList.add(dim);

			WebElement subAreaExpander = driver
					.findElement(By.xpath("//img[@id='criteriaDataBrowser$" + dim + "_disclosure']"));
			clickElementSilent(subAreaExpander);

			String childFields = "//div[@id='criteriaDataBrowser$" + dim + "_children']/div";
			List<WebElement> childList = driver.findElements(By.xpath(childFields));
			for (int i = 0; i < childList.size(); i++) {
				String childImageXPath = "//*[@id='criteriaDataBrowser$" + dim + "_children']/div[" + (i + 1)
						+ "]/span/span/img";
				WebElement childImage = driver.findElement(By.xpath(childImageXPath));
				if (childImage.getAttribute("src").contains("folder.gif")) {
					folderList.add(childList.get(i).getText());
				}
			}

			subAreaExpander = driver.findElement(By.xpath("//img[@id='criteriaDataBrowser$" + dim + "_disclosure']"));
			clickElementSilent(subAreaExpander);

		}
		
		setImplicitWait(1);

		return folderList;

	}

}
