package com.infinity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Pdf;
import org.openqa.selenium.PrintsPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class AppTest {
    WebDriver driver;
    Actions actions;
    WebDriverWait wait;
    ChromeOptions options;

    XSSFWorkbook workbook;
    XSSFSheet sheet1;
    XSSFSheet sheet2;

    JavascriptExecutor executor;

    List<ExcellData> excellDatasList = new ArrayList<>();

    private static final String CHAT_GPT_URL = "https://chat.openai.com/";
    private static final String PROMPT_BOX_XPATH = "/html/body/div[1]/div[1]/div[2]/main/div[2]/div[2]/form/div/div[2]/div/textarea";
    private static final String SUBMIT_BUTTON_XPATH = "//*[@id=\"__next\"]/div[1]/div[2]/main/div[2]/div[2]/form/div/div[2]/div/button";

    private static final String EXECUTABLE_PATH = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
    private static final String PROFILE_PATH = "C:\\Users\\<username>\\AppData\\Local\\Google\\Chrome\\User Data";
    private static final String PROFILE_NAME = "Profile 2";

    private static final String EXCEL_PATH = "D:\\projects\\question2answer\\assets\\excelFiles\\Question.xlsx";
    private static final String PDF_PATH = "D:\\projects\\question2answer\\assets\\PDFs\\";

    @BeforeTest
    public void driverSetup() {

        this.options = new ChromeOptions();
        options.setBinary(EXECUTABLE_PATH);
        options.addArguments("user-data-dir=" + PROFILE_PATH);
        options.addArguments("profile-directory=" + PROFILE_NAME);

        this.driver = new ChromeDriver(options);
    }

    @BeforeTest
    public void otherPreRequisites() {
        this.executor = (JavascriptExecutor) driver;
        this.actions = new Actions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @BeforeTest
    public void XssfClassesDefinition() throws Exception {
        this.workbook = new XSSFWorkbook(EXCEL_PATH);
        this.sheet1 = workbook.getSheetAt(0);
        this.sheet2 = workbook.getSheetAt(1);
    }

    @BeforeMethod
    public void ExcellDataRetrieval() throws Exception {
        int rowCount = sheet1.getLastRowNum();
        int colCount = sheet1.getRow(1).getLastCellNum();

        for (int i = 1; i <= rowCount; i++) {
            XSSFRow currentrow = sheet1.getRow(i);

            int curSerialNumber = (int) currentrow.getCell(0).getNumericCellValue();
            String curQuestion = currentrow.getCell(1).toString();
            int curMarks = (int) currentrow.getCell(2).getNumericCellValue();
            String curAdditionalInfo = currentrow.getCell(3) == null ? "" : currentrow.getCell(3).toString();

            ExcellData excellDataobject = new ExcellData(curSerialNumber, curQuestion, curMarks, curAdditionalInfo);
            excellDatasList.add(excellDataobject);
        }
    }

    @Test
    public void testQuestion2Answer() throws InterruptedException {
        this.openChatGPT();
        this.automateQuestion2Answer();
        try {
            this.chatToPDF();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void openChatGPT() {
        driver.get(CHAT_GPT_URL);
    }

    private void automateQuestion2Answer() {

        for (ExcellData excellData : excellDatasList) {
            String question = excellData.getQuestion();
            String marks = " Answer the question as " + excellData.getMarks() + " marks.";
            String additionalInfo = "";
            if( !excellData.getAdditionalInfo().isEmpty() && excellData.getAdditionalInfo() != null)
                additionalInfo = " Answer the question with the following: " + excellData.getAdditionalInfo();
            String prompt = question + marks + additionalInfo;

            this.sendQuestionAndAwaitResponse(prompt);
        }
    }

    private void sendQuestionAndAwaitResponse(String prompt) {
        enterQuestion(prompt);
        submitQuestion();
        try {
            waitUntilResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enterQuestion(String prompt) {
        By promptBoxBy = By.xpath(PROMPT_BOX_XPATH);
        wait.until(ExpectedConditions.presenceOfElementLocated(promptBoxBy));

        WebElement promptBox = driver.findElement(promptBoxBy);
        promptBox.sendKeys(prompt);
    }

    private void submitQuestion() {
        By submitButtonBy = By.xpath(SUBMIT_BUTTON_XPATH);
        
        WebElement submitButton = driver.findElement(submitButtonBy);
        submitButton.click();
    }

    private void waitUntilResponse() throws InterruptedException {
        By submitButtonBy = By.xpath(SUBMIT_BUTTON_XPATH);

        wait = new WebDriverWait(driver, Duration.ofMinutes(3));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(submitButtonBy));
        wait.until(ExpectedConditions.presenceOfElementLocated(submitButtonBy));
        Thread.sleep(5000); // to reduce stress on the server
    }

    private void chatToPDF() throws InterruptedException {
        
        executor.executeScript(
                "document.querySelector('#__next>div').classList.remove('h-full', 'overflow-hidden');" +
                "document.querySelector('#__next>div>div').classList.remove('overflow-hidden');" +
                "document.querySelector('#__next main').classList.remove('overflow-auto');" +
                "document.querySelector('#__next main')?.parentElement.classList.remove('overflow-hidden');" +
                "document.querySelector('#__next main>div>div').classList.remove('overflow-hidden');" +
                "document.querySelector('#__next main>div>div.w-full').classList.add('hidden');" +
                "document.querySelector('#__next header')?.classList.add('hidden');" +
                "document.body.classList.remove('dark');");
        

        PrintsPage printer = (PrintsPage) driver;
        Pdf pdf = printer.print(new PrintOptions());
        Path src = Paths.get(PDF_PATH+"answer.pdf");
        try {
            Files.write(src, OutputType.BYTES.convertFromBase64Png(pdf.getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread.sleep(10000);
    }

    @AfterTest
    public void closeDriver() {
        driver.quit();
    }
}
