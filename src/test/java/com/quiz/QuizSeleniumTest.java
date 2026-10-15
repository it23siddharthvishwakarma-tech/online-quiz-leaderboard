package com.quiz;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuizSeleniumTest {

    @LocalServerPort
    private int port;

    @Test
    void quizCanBeSubmittedAndReviewed() {
        WebDriver driver = new ChromeDriver();

        try {
            WebDriverWait wait = new WebDriverWait(
                    driver, Duration.ofSeconds(15));

            driver.get("http://localhost:" + port);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("studentName"))).sendKeys("Test Student");

           wait.until(d -> d.findElements(
        By.cssSelector("#quizSelect option")).size() > 1);

WebElement quizSelect = driver.findElement(By.id("quizSelect"));
quizSelect.findElements(By.tagName("option")).get(0).click();
            driver.findElement(By.id("startBtn")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("questions")));

            wait.until(d -> !d.findElements(
                    By.cssSelector("#questions input[type='radio']"))
                    .isEmpty());

            var answers = driver.findElements(
                    By.cssSelector("#questions input[type='radio']"));

            for (WebElement answer : answers) {
                if (answer.isDisplayed() && answer.isEnabled()) {
                    answer.click();
                }
            }

            driver.findElement(By.id("submitBtn")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("resultSummary")));

            assertTrue(driver.findElement(By.id("resultSummary"))
                    .getText().contains("Test Student"));

            assertTrue(driver.findElement(By.id("review"))
                    .isDisplayed());

        } finally {
            driver.quit();
        }
    }
}