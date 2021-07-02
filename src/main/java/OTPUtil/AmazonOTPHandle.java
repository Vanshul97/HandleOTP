package OTPUtil;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;

import io.github.bonigarcia.wdm.WebDriverManager;

public class AmazonOTPHandle {
	public static final String Account_SID = "AC2f6098173b2831571f1e96e60f6afd42";
	public static final String Autho_Token = "ec3ead1e6a4b9f545263bf3c93dbe2de";

	private static Stream<Message> getMessages() {
		ResourceSet<Message> messages = Message.reader(Account_SID).read();
		return StreamSupport.stream(messages.spliterator(), false);
	}

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();
		driver.get("https://www.amazon.in/");
		driver.manage().window().maximize();
		driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
		Actions action = new Actions(driver);
		action.moveToElement(driver.findElement(By.xpath("//span[contains(text(),'Hello, Sign in')]"))).build()
				.perform();
		driver.findElement(By.linkText("Start here.")).click();
		driver.findElement(By.id("ap_customer_name")).sendKeys("VanshulTest");
		driver.findElement(By.id("auth-country-picker-container")).click();
		driver.findElement(By.xpath("//ul[@role=\"application\"]//li//a[contains(text(),'United States +1')]")).click();
		driver.findElement(By.id("ap_phone_number")).sendKeys("9087683833");
		driver.findElement((By.id("ap_password"))).sendKeys("Password@12345");
		driver.findElement(By.id("continue")).click();
		Thread.sleep(40000);
		// get the OTP using Twilio API
		Twilio.init(Account_SID, Autho_Token);
		String smsbody = getMessage();
		System.out.println(smsbody);
		String OTPNumber = smsbody.replaceAll("[^0-9]", "");
		System.out.println(OTPNumber);

	}

	public static String getMessage() {
		return getMessages().filter(m -> m.getDirection().compareTo(Message.Direction.INBOUND) == 0)
				.filter(m -> m.getTo().equals("+19087683833")).map(Message::getBody).findFirst()
				.orElseThrow(IllegalStateException::new);
	}
}
