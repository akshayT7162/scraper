package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import demo.wrappers.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestCases {
    ChromeDriver driver;
    Wrappers wrappers;

    @BeforeTest(alwaysRun = true)    
    public void startBrowser() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wrappers = new Wrappers(driver);
        System.out.println("Browser started and maximized.");
    }

    @AfterTest
    public void endTest() {
        driver.close();
        driver.quit();
    } 

    @Test
    public void testCase01() throws IOException {
        //Navigate to the Hockey Teams page
        driver.get("https://www.scrapethissite.com/pages/");
        System.out.println("Navigating to the Hockey Teams page.");
        wrappers.click(By.linkText("Hockey Teams: Forms, Searching and Pagination"));

        List<HashMap<String, Object>> hockeyTeamsData = new ArrayList<>();
        long currentEpoch = Instant.now().getEpochSecond();

        // Loop to handle pagination and scrape data from each page
        for (int i = 1; i <= 4; i++) {
            wrappers.waitForPageToLoad();
            System.out.println("Scraping page " + i);
           
            List<WebElement> rows = wrappers.getElements(By.xpath("//table[@class=' hockey-teams']//tbody//tr"));

            for (WebElement row : rows) {
                String teamName = row.findElement(By.xpath("//td[1]")).getText();// relative XPath
                String year = row.findElement(By.xpath("//td[2]")).getText();// relative XPath
                String winPercentage = row.findElement(By.xpath("//td[3]")).getText();// relative XPath

                if (winPercentage.equals("-") || Double.parseDouble(winPercentage.replace("%", "")) < 40) {
                    HashMap<String, Object> teamData = new HashMap<>();
                    teamData.put("Epoch Time of Scrape", currentEpoch);
                    teamData.put("Team Name", teamName);
                    teamData.put("Year", year);
                    teamData.put("Win %", winPercentage);

                    hockeyTeamsData.add(teamData);
                }
            }

            if (i < 4) {   
                wrappers.click(By.xpath("//a[@aria-label='Next']"));
                System.out.println("Navigating to the next page.");
            }
        }

        //Convert to the Json and save to the file 
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File jsonFile = new File("C:/Users/Akshay Tiwari/OneDrive/Desktop/Crio/akshay-tiwari0215-ME_QA_XSCRAPE_DATA/JSON files/hockey-team-data.json");
            objectMapper.writeValue(jsonFile, hockeyTeamsData);
            System.out.println("JSON data written to: " + jsonFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCase02() throws IOException {
        //Navigate to the Oscar Winning Films page 
        driver.get("https://www.scrapethissite.com/pages/");
        System.out.println("Navigating to the Oscar Winning Films page.");
        wrappers.click(By.xpath("//a[contains(text(),'Oscar Winning Films')]"));

        List<HashMap<String, Object>> oscarFilmsData = new ArrayList<>();
        long currentEpoch = Instant.now().getEpochSecond();

        List<WebElement> years = wrappers.getElements(By.xpath("//div[@class='year']"));
        for (WebElement yearElement : years) {
            String year = yearElement.getText();
            wrappers.click(By.xpath("//div[@class='year' and text()='" + year + "']"));

            //Collect top 5 movies
            List<WebElement> movies = wrappers.getElements(By.xpath("//table[@class='oscar-winners']//tr"));
            for (int i = 1; i <= Math.min(5, movies.size()); i++) {
                WebElement movieRow = movies.get(i);
                String title = movieRow.findElement(By.xpath("//td[1]")).getText();
                String nomination = movieRow.findElement(By.xpath("//td[2]")).getText();
                String awards = movieRow.findElement(By.xpath("//td[3]")).getText();
                boolean isWinner = movieRow.findElement(By.xpath("//td[4]")).getText().contains("Winner");

                HashMap<String, Object> movieData = new HashMap<>();
                movieData.put("Epoch Time of Scrape", currentEpoch);
                movieData.put("Year", year);
                movieData.put("Title", title);
                movieData.put("Nomination", nomination);
                movieData.put("Awards", awards);
                movieData.put("isWinner", isWinner);

                oscarFilmsData.add(movieData);
            }
        }

        //Convert to the Json and save to the file 
        ObjectMapper objectMapper2015 = new ObjectMapper();
        try {
            File jsonFile = new File("C:/Users/Akshay Tiwari/OneDrive/Desktop/Crio/akshay-tiwari0215-ME_QA_XSCRAPE_DATA/JSON files/movieList"+2014+"-data.json");
            objectMapper2015.writeValue(jsonFile, oscarFilmsData);
            System.out.println("JSON data written to: " + jsonFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Oscar film data saved to 'output/oscar-winner-data.json'.");
    }
}   
