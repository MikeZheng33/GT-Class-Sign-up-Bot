import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ClassSignUpInator {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner classesWanted = new Scanner(new File("classesWanted.txt"));
        Scanner classesToDrop = new Scanner(new File("classesToDrop.txt"));
        Scanner subjectToXpath = new Scanner(new File("subjectToXpath.txt"));

        String username = classesWanted.nextLine().trim();
        String password = classesWanted.nextLine().trim();

        //each element in classes is a list of classes in descending order of priority
        //only one element from each list in classes will be added to schedule
        //drops is in the format (if this class is added, drop this class)
        List<String> classes = new ArrayList<>();
        HashMap<String, List<String>> drops = new HashMap<>();
        List<String> subjectList = new ArrayList<>();

        //adds each line from classesWanted to classes as an ArrayList after trimming then splitting by ", "
        while (classesWanted.hasNext()) {
            classes.add(classesWanted.nextLine().trim());
        }

        //sets up the drops HashMap as (if this class is added, drop these classes)
        while (classesToDrop.hasNext()) {
            String[] temp = classesToDrop.nextLine().split(":");
            drops.put(temp[0].trim(), Arrays.asList(temp[1].trim().split(", ")));
        }

        //sets up the subjectList to convert user input to Xpath
        while (subjectToXpath.hasNext()) {
            subjectList.add(subjectToXpath.nextLine());
        }

        //sets up Chromedriver
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\chromedriver\\chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        WebDriverWait driverWait = new WebDriverWait(driver, 25);
        start(driver, username, password);

        while (classes.size() > 0) {
            try {
                for (int i = classes.size() - 1; i >= 0; i--) {
                        int id = checkClass(driver, subjectList, classes.get(i));
                        if (id > 0) {   //if there is an open class, id will be > 0
                            System.out.println("Class is open");
                            driver.findElementById("action_id" + id).click();

                            int index = 0;
                            while (true) {
                                index++;
                                try {
                                    driver.findElementByXPath("/html/body/div[3]/form/input[" + index + "]");
                                } catch (Exception e) {
                                    break;
                                }
                            }
                            System.out.println("test");
                            System.out.println(index);
                            driver.findElementByXPath("/html/body/div[3]/form/input[" + (index - 3) + "]").click();
                            System.out.println("classes.size() = " + classes.size());
                            String[] classInfo = classes.get(i).split(" ");
                            System.out.println("classInfo = " + classInfo.length);
                            System.out.println(Arrays.toString(classInfo));
                            String CRN = classes.get(i).split(" ")[2];

                            System.out.println("tester print");

                            List<WebElement> rows = driver.findElementsByCssSelector("body > div.pagebodydiv > form > table.datadisplaytable > tbody > tr");
                            System.out.println("rows.size() = " + rows.size());
                            if (drops.get(CRN) != null) {   //checks if there are any classes that need to be dropped
                                for (int k = 2; k <= rows.size(); k++) {    //goes through each row of registered classes
                                    if (drops.get(CRN).contains(driver.findElementByXPath("/html/body/div[3]/form/table[1]/tbody/tr[" + k + "]/td[3]").getText())) {
                                        Select dropper = new Select(driver.findElement(By.id("action_id" + (k - 1))));
                                        dropper.selectByIndex(1);   //selects the delete option
                                    }

                                }
                            }

                            System.out.println("CLASS REGISTEREDDDD" + classes.get(i));
                            driver.findElementByXPath("/html/body/div[3]/form/input[19]").click();
                            classes.remove(i);  //removes the class from the list of classes
                        } else {
                            System.out.println("TAKEN" + classes.get(i));
                        }

                        //navigates back to advanced search
                        driver.findElement(By.id("keyword_in_id")).sendKeys("Add/Drop");
                        driver.findElementByXPath("/html/body/div[1]/table/tbody/tr/td[1]/div/form/input[2]").click();
                        driverWait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Add/Drop"))).click();
                        driverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[3]/form/input[20]"))).click();
                        driverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[3]/form/input[83]"))).click();
                }
            } catch (Exception e) {  //if there are any errors, close all tabs and restart
                System.out.println(e.getMessage());
                System.out.println(e.toString());
                Set<String> handles = driver.getWindowHandles();
                String currentHandle = driver.getWindowHandle();
                driver.close();
                for (String handle : handles) {
                    if (!handle .equals(currentHandle)) {
                        driver.switchTo().window(handle);
                    }
                }

                driver.close();
                driver = new ChromeDriver();
                start(driver, username, password);
            }
        }
    }

    //opens up Chrome and navigates to advanced course search
    protected static void start(ChromeDriver driver, String username, String password)  {
        driver.get("https://buzzport.gatech.edu/my");

        //login
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.name("submit")).click();

        //go to registration
        WebDriverWait driverWait = new WebDriverWait(driver, 25);
        driverWait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#panel-registration-and-student-services > div > div > div:nth-child(1) > a"))).click();

        //switch focus to new tab
        Set<String> handles = driver.getWindowHandles();
        String currentHandle = driver.getWindowHandle();
        for (String handle : handles) {
            if (!handle .equals(currentHandle)) {
                driver.switchTo().window(handle);
            }
        }

        //goes to advanced search add/drop
        driverWait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Registration"))).click();
        driverWait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Add or Drop"))).click();
        driverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[3]/form/input"))).click();
        driverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[3]/form/input[20]"))).click();
        driverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[3]/form/input[83]"))).click();
    }

    //searches for and checks if the class if available
    protected static int checkClass(ChromeDriver driver, List<String> subjectList, String classInfo) {
        WebDriverWait driverWait = new WebDriverWait(driver, 25);

        //classInfoSplit will be in the format (subject, class number, CRN)
        String[] classInfoSplit = classInfo.split(" ");
        System.out.println("classInfoSplit = " + classInfoSplit[0]);
        System.out.println("classInfoSplit1 = " + subjectList.indexOf(classInfoSplit[0]));
        //selects the subject after converting to xpath
        driverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"subj_id\"]/option[" +
                (subjectList.indexOf(classInfoSplit[0]) + 1) + "]"))).click();

        //enters the course number
        driver.findElement(By.id("crse_id")).sendKeys(classInfoSplit[1]);
        driverWait.until(ExpectedConditions.elementToBeClickable(By.name("sub_btn"))).click();

        //finds the row where the requested CRN is and stores it in classRow
        int classRow = 0;
        try {
            for (int i = 3; classRow == 0; i++) {
                if (driver.findElementByXPath("/html/body/div[3]/form/table/tbody/tr[" + i + "]/td[2]/a").getText()
                        .equals(classInfoSplit[2])) {
                    classRow = i - 2;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }


        //finds all of the checkboxes
        List<Integer> action_ids = new ArrayList<>();
        int rowNum = 0;
        for (int i = 1; rowNum <= classRow + action_ids.size(); i++) {
            try {
                rowNum++;
                driver.findElementById("action_id" + i);
                action_ids.add(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //checks if there is a corresponding checkbox for the wanted CRN
        for (int i = 0; i < action_ids.size(); i++) {
            if (action_ids.get(i) - i == classRow) {
                return action_ids.get(i);
            }
        }
        return 0;
    }
}