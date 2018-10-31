package com.oracle.pgbu.selenium.webapp;

import com.oracle.pgbu.selenium.supporting.WebappTestBase;
import com.oracle.pgbu.selenium.pageobjects.webapp.HomePage;
import com.oracle.pgbu.selenium.pageobjects.webapp.LoginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class TC001_LoginValidationCorrectCredentials extends WebappTestBase {
	LoginPage loginpage;
	HomePage homepage;

	@BeforeClass
	public void setUp() throws IOException, NoSuchMethodException {
		System.out.println("-----------------TC001 started------------------");
		init();
	}

	@Test
	public void LoginValidationCorrectCredential() {
		try {
			loginpage = new LoginPage(driver);
			homepage = new HomePage(driver);
			login(getProp("WEBAPP_USERNAME"), getProp("WEBAPP_PASSWORD"), loginpage, homepage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
