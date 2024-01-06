package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;

import util.shortcut;
import util.ContactAPIValidator;
import util.ExcelData;
import util.SelectorRepository;


public class P02_SupportPage extends shortcut{
	private static final int DEFAULT_WAIT_TIMEOUT = 5;
	private static final String EXCEL_RELATIVE_PATH = "/src/main/java/selenium/resources/FormSubmissions.xlsx";
	private static final String EXCEL_SHEET_NAME = "Submissions";
	
	WebDriver driver;
	WebDriverWait wait;
	private ContactAPIValidator apiValidator;
	private Map<String, String> formData;
	private ExcelData excelRecorder;
	private boolean verboseLogging = false;

    public P02_SupportPage(WebDriver driver) {
    	this.driver = driver;
    	this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT));
    	this.apiValidator = new ContactAPIValidator();
    	this.formData = new HashMap<>();
    	
    	try {
    		String projectDir = System.getProperty("user.dir");
    		String excelPath = projectDir + EXCEL_RELATIVE_PATH;
    		this.excelRecorder = new ExcelData(excelPath, EXCEL_SHEET_NAME);
    	} catch (Exception e) {
    		System.err.println("Warning: Excel recorder initialization failed - " + e.getMessage());
    	}
    	
    	try {
    		wait.until(ExpectedConditions.urlContains(SelectorRepository.get("urls.contactUsUrlPart")));
    		wait.until(ExpectedConditions.presenceOfElementLocated(SelectorRepository.getByTagName("contactForm.form.tagName")));
    		wait.until(ExpectedConditions.elementToBeClickable(SelectorRepository.getByTagName("contactForm.form.tagName")));
    	} catch (Exception e) {
    		System.err.println("Contact form failed to load");
    		Assert.fail("Contact form not loaded");
    	}
    }
    
    public P02_SupportPage fillName(String name) {
    	try {
    		WebElement nameField = wait.until(ExpectedConditions.elementToBeClickable(
    				SelectorRepository.getByXpath("contactForm.fullName.xpath")));
    		nameField.clear();
    		nameField.sendKeys(name);
    		formData.put("fullName", name);
    	} catch (Exception e) {
    		System.err.println("Failed to fill name field");
    		Assert.fail("Failed to fill name field");
    	}
    	return this;
    }
    
    public P02_SupportPage fillEmail(String email) {
    	try {
    		WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(
    				SelectorRepository.getByXpath("contactForm.email.xpath")));
    		emailField.clear();
    		emailField.sendKeys(email);
    		formData.put("email", email);
    	} catch (Exception e) {
    		System.err.println("Failed to fill email field");
    		Assert.fail("Failed to fill email field");
    	}
    	return this;
    }
    
    public P02_SupportPage fillPhone(String phone) {
    	try {
    		WebElement phoneField = wait.until(ExpectedConditions.elementToBeClickable(
    				SelectorRepository.getByXpath("contactForm.phoneNumber.xpath")));
    		phoneField.clear();
    		phoneField.sendKeys(phone);
    		formData.put("phoneNumber", phone);
    	} catch (Exception e) {
    		System.err.println("Failed to fill phone field");
    		Assert.fail("Failed to fill phone field");
    	}
    	return this;
    }
    
    public P02_SupportPage fillNationalId(String nationalId) {
    	try {
    		WebElement nationalIdField = wait.until(ExpectedConditions.elementToBeClickable(
    				SelectorRepository.getByXpath("contactForm.nationalId.xpath")));
    		nationalIdField.clear();
    		nationalIdField.sendKeys(nationalId);
    		formData.put("nationalId", nationalId);
    	} catch (Exception e) {
    		System.err.println("Failed to fill National ID field");
    		Assert.fail("Failed to fill National ID field");
    	}
    	return this;
    }
    
    public P02_SupportPage fillIBAN(String iban) {
    	try {
    		WebElement ibanField = wait.until(ExpectedConditions.elementToBeClickable(
    				SelectorRepository.getByXpath("contactForm.iban.xpath")));
    		ibanField.clear();
    		ibanField.sendKeys(iban);
    		formData.put("iban", iban);
    	} catch (Exception e) {
    		System.err.println("Failed to fill IBAN field");
    		Assert.fail("Failed to fill IBAN field");
    	}
    	return this;
    }
    
    public P02_SupportPage fillMessage(String message) {
    	try {
    		WebElement messageField = wait.until(ExpectedConditions.elementToBeClickable(
    				SelectorRepository.getByXpath("contactForm.message.xpath")));
    		messageField.clear();
    		messageField.sendKeys(message);
    		formData.put("message", message);
    	} catch (Exception e) {
    		System.err.println("Failed to fill message field");
    		Assert.fail("Failed to fill message field");
    	}
    	return this;
    }
    
    public P02_SupportPage selectIssueType(String issueType) {
    	try {
    		WebElement issueTypeInput = wait.until(ExpectedConditions.elementToBeClickable(
    				SelectorRepository.getByXpath("contactForm.issueType.input.xpath")));
    		
    		((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
    				"arguments[0].scrollIntoView({block: 'center', behavior: 'smooth'});", issueTypeInput);
    		wait.until(driver -> {
    			return (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver)
    				.executeScript("return arguments[0].getBoundingClientRect().top >= 0 && arguments[0].getBoundingClientRect().bottom <= window.innerHeight;", issueTypeInput);
    		});
    		
    		issueTypeInput.click();
    		
    		try {
    			WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
    			shortWait.until(ExpectedConditions.or(
    					ExpectedConditions.presenceOfElementLocated(SelectorRepository.getByXpath("contactForm.dropdownMenu.menuClass.xpath")),
    					ExpectedConditions.presenceOfElementLocated(SelectorRepository.getByXpath("contactForm.dropdownMenu.menuListClass.xpath"))
    			));
    		} catch (Exception e) {
    			System.err.println("Dropdown menu did not appear, continuing anyway...");
    		}
    		
	issueTypeInput.clear();
	issueTypeInput.sendKeys(issueType);
	
	WebDriverWait filterWait = new WebDriverWait(driver, Duration.ofSeconds(3));
	filterWait.until(ExpectedConditions.visibilityOfElementLocated(
		SelectorRepository.getByCss("contactForm.dropdownMenu.menuClass.css")));
	
	issueTypeInput.sendKeys(org.openqa.selenium.Keys.ENTER);    	// Wait for dropdown menu to disappear (confirms selection was registered)
    	WebDriverWait closeWait = new WebDriverWait(driver, Duration.ofSeconds(5));
    	try {
    		closeWait.until(ExpectedConditions.invisibilityOfElementLocated(
    			SelectorRepository.getByXpath("contactForm.dropdownMenu.menuClass.xpath")));
    	} catch (Exception e) {
    	}
    	
    	closeWait.until(driver -> {
    		String ariaExpanded = issueTypeInput.getAttribute("aria-expanded");
    		return ariaExpanded == null || "false".equals(ariaExpanded);
    	});
    	
    	formData.put("subject", issueType);    	} catch (Exception e) {
    		System.err.println("Failed to select issue type: " + issueType);
    		e.printStackTrace();
    		Assert.fail("Failed to select issue type: " + issueType + " - " + e.getMessage());
    	}
    	return this;
    }
    
    public P02_SupportPage selectCategory(String category) {
    	try {
    		WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(15));
    		
    		longWait.until(ExpectedConditions.presenceOfElementLocated(
    				SelectorRepository.getByXpath("contactForm.category.input.xpath")));
    		
    		WebElement categoryInput = longWait.until(ExpectedConditions.elementToBeClickable(
    				SelectorRepository.getByXpath("contactForm.category.input.xpath")));
    		
    		((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
    				"arguments[0].scrollIntoView({block: 'center'});", categoryInput);
    		longWait.until(driver -> {
    			return (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver)
    				.executeScript("return arguments[0].getBoundingClientRect().top >= 0;", categoryInput);
    		});
    		
    		categoryInput.click();
    		
    		try {
    			WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
    			shortWait.until(ExpectedConditions.or(
    					ExpectedConditions.presenceOfElementLocated(SelectorRepository.getByXpath("contactForm.dropdownMenu.menuClass.xpath")),
    					ExpectedConditions.presenceOfElementLocated(SelectorRepository.getByXpath("contactForm.dropdownMenu.menuListClass.xpath"))
    			));
    		} catch (Exception e) {
    			System.err.println("Category dropdown menu did not appear, continuing anyway...");
    		}
    		
	categoryInput.clear();
	categoryInput.sendKeys(category);
	
	WebDriverWait filterWait = new WebDriverWait(driver, Duration.ofSeconds(3));
	filterWait.until(ExpectedConditions.visibilityOfElementLocated(
		SelectorRepository.getByCss("contactForm.dropdownMenu.menuClass.css")));
	
	categoryInput.sendKeys(org.openqa.selenium.Keys.ENTER);    	// Wait for dropdown to close (confirms selection was registered)
    	WebDriverWait closeWait = new WebDriverWait(driver, Duration.ofSeconds(5));
    	closeWait.until(driver -> {
    		String ariaExpanded = categoryInput.getAttribute("aria-expanded");
    		return ariaExpanded == null || "false".equals(ariaExpanded);
    	});
    	
    	formData.put("category", category);    	} catch (Exception e) {
    		System.err.println("Failed to select category: " + category);
    		e.printStackTrace();
    		Assert.fail("Failed to select category: " + category + " - " + e.getMessage());
    	}
    	return this;
    }
    
    public P02_SupportPage selectSubCategory(String subCategory) {
    	try {
    		if (subCategory == null || subCategory.isEmpty()) {
    			return this;
    		}
    		
    		final WebElement subCategoryInput;
    		try {
    			subCategoryInput = wait.until(ExpectedConditions.presenceOfElementLocated(
    					SelectorRepository.getByXpath("contactForm.subCategory.input.xpath")));
    		} catch (Exception e) {
    			return this;
    		}
    		
    	if (subCategoryInput.isDisplayed()) {
    		((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
    				"arguments[0].scrollIntoView({block: 'center'});", subCategoryInput);
    		wait.until(driver -> {
    			return (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver)
    				.executeScript("return arguments[0].getBoundingClientRect().top >= 0;", subCategoryInput);
    		});
    		
    		subCategoryInput.click();
    		
    		try {
    			WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
    			shortWait.until(ExpectedConditions.or(
    					ExpectedConditions.presenceOfElementLocated(SelectorRepository.getByXpath("contactForm.dropdownMenu.menuClass.xpath")),
    					ExpectedConditions.presenceOfElementLocated(SelectorRepository.getByXpath("contactForm.dropdownMenu.menuListClass.xpath"))
    			));
    		} catch (Exception e) {
    			System.err.println("SubCategory dropdown menu did not appear, continuing anyway...");
    		}
    		
    	subCategoryInput.clear();
    	subCategoryInput.sendKeys(subCategory);
    	
    	WebDriverWait filterWait = new WebDriverWait(driver, Duration.ofSeconds(3));
    	filterWait.until(ExpectedConditions.visibilityOfElementLocated(
    		SelectorRepository.getByCss("contactForm.dropdownMenu.menuClass.css")));
    	
    	subCategoryInput.sendKeys(org.openqa.selenium.Keys.ENTER);
    	WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
    	shortWait.until(driver -> {
    			String ariaExpanded = subCategoryInput.getAttribute("aria-expanded");
    			return ariaExpanded == null || "false".equals(ariaExpanded);
    		});    			formData.put("subCategory", subCategory);
    		}
    	} catch (Exception e) {
    		System.err.println("Could not select sub-category (may not be required): " + e.getMessage());
    	}
    	return this;
    }
    
    public P02_SupportPage acceptPrivacyNotice() {
    	try {
    		WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(
    				SelectorRepository.getByXpath("contactForm.privacyConsent.xpath")));
    		if (!checkbox.isSelected()) {
    			checkbox.click();
    		}
    		formData.put("privacyPolicyConsent", "true");
    	} catch (Exception e) {
    		System.err.println("Failed to accept privacy policy");
    		Assert.fail("Failed to accept privacy policy");
    	}
    	return this;
    }
    
	public P02_SupportPage clickSend() {
		try {
			final WebElement sendButton = wait.until(ExpectedConditions.elementToBeClickable(
					SelectorRepository.getByXpath("contactForm.submitButton.xpath")));
			
			((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
			"arguments[0].scrollIntoView({block: 'center'});", sendButton);
		wait.until(driver -> {
			return (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver)
				.executeScript("return arguments[0].getBoundingClientRect().top >= 0;", sendButton);
		});
		
	sendButton.click();
	} catch (Exception e) {
			System.err.println("Failed to locate or click send button: " + e.getMessage());
			Assert.fail("Failed to locate or click send button: " + e.getMessage());
		}
		
		try {
			WebDriverWait submitWait = new WebDriverWait(driver, Duration.ofSeconds(15));
			submitWait.until(ExpectedConditions.or(
				ExpectedConditions.visibilityOfElementLocated(SelectorRepository.getByXpath("messages.successMessage.xpath")),
				ExpectedConditions.presenceOfElementLocated(SelectorRepository.getByCss("messages.errorMessage.css"))
			));
		} catch (Exception e) {
			System.err.println("Timeout waiting for success/error message after 15 seconds: " + e.getMessage());
			Assert.fail("Form submission response not received after 15 seconds: " + e.getMessage());
		}
		return this;
	}
	
	public String captureRecaptchaToken() {
		try {
			WebDriverWait recaptchaWait = new WebDriverWait(driver, Duration.ofSeconds(3));
			recaptchaWait.until(driver -> {
				return (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver)
					.executeScript("return typeof grecaptcha !== 'undefined' && grecaptcha.ready !== undefined;");
			});
			
			try {
				List<WebElement> recaptchaTextareas = driver.findElements(SelectorRepository.getByName("recaptcha.responseTextarea.name"));
				for (WebElement textarea : recaptchaTextareas) {
					String token = textarea.getAttribute("value");
					if (token != null && !token.isEmpty() && token.length() > 20) {
						return token;
					}
				}
			} catch (Exception e) {
			}
			
			try {
				String token = (String) ((org.openqa.selenium.JavascriptExecutor) driver)
						.executeScript(
							"if (typeof grecaptcha !== 'undefined' && grecaptcha.getResponse) {" +
							"  var response = grecaptcha.getResponse();" +
							"  console.log('grecaptcha response:', response);" +
							"  return response || '';" +
							"}" +
							"return '';"
						);
				if (token != null && !token.isEmpty() && token.length() > 20) {
					return token;
				}
			} catch (Exception e) {
			}
			
			try {
				String token = (String) ((org.openqa.selenium.JavascriptExecutor) driver)
						.executeScript(
							"return window.recaptchaToken || " +
							"window.captchaToken || " +
							"window.grecaptchaToken || " +
							"window.__recaptchaToken || " +
							"'';"
						);
				if (token != null && !token.isEmpty() && token.length() > 20) {
					return token;
				}
			} catch (Exception e) {
			}
			
			try {
				String token = (String) ((org.openqa.selenium.JavascriptExecutor) driver)
						.executeScript(
							"if (typeof grecaptcha !== 'undefined' && typeof grecaptcha.execute === 'function') {" +
							"  try {" +
							"    return grecaptcha.execute();" +
							"  } catch(e) {" +
							"    console.log('grecaptcha.execute failed:', e);" +
							"  }" +
							"}" +
							"return '';"
						);
				if (token != null && !token.isEmpty() && token.length() > 20) {
					return token;
				}
			} catch (Exception e) {
			}
			
			try {
				List<WebElement> hiddenInputs = driver.findElements(SelectorRepository.getByXpath("recaptcha.hiddenInputs.xpath"));
				for (WebElement input : hiddenInputs) {
					String token = input.getAttribute("value");
					if (token != null && !token.isEmpty() && token.length() > 20) {
						return token;
					}
				}
			} catch (Exception e) {
			}
			
			return null;
			
		} catch (Exception e) {
			System.err.println("! Failed to capture reCAPTCHA token: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
    public List<String> getValidationErrors() {
    	return getRedErrorMessages();
    }
    
	public String getSuccessMessage() {
		try {
		WebElement successMsg = null;
		try {
			WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(4));
			successMsg = shortWait.until(ExpectedConditions.visibilityOfElementLocated(
					SelectorRepository.getByXpath("messages.successMessage.xpath")));				String messageText = successMsg.getText();
				
				recordSubmissionToExcel("SUCCESS", 200, 0);
				
				return messageText;
				
			} catch (Exception successEx) {
			}
			
			List<String> errorMessages = getRedErrorMessages();
			if (!errorMessages.isEmpty()) {
				recordSubmissionToExcel("VALIDATION_FAILED", 0, 0);
				return "VALIDATION_FAILED: " + errorMessages;
			}
			
			recordSubmissionToExcel("ERROR_NO_CONFIRMATION", 0, 0);
			return "NO_CONFIRMATION";
			
		} catch (Exception e) {
			System.err.println("Unexpected error in getSuccessMessage: " + e.getMessage());
			recordSubmissionToExcel("ERROR_EXCEPTION", 0, 0);
			return "ERROR: " + e.getMessage();
		}
	}
    private List<String> getRedErrorMessages() {
    	List<String> errorMessages = new ArrayList<>();
    	try {
    		List<WebElement> errorElements = driver.findElements(SelectorRepository.getByCss("messages.errorMessage.css"));
    		if (verboseLogging) {
    		}
    		
    		for (WebElement errorElement : errorElements) {
    			
    			if (errorElement.isDisplayed()) {
    				String elementText = errorElement.getText().trim();
    				
    				if (verboseLogging) {
    				}
    				
    				try {
    					List<WebElement> spans = errorElement.findElements(SelectorRepository.getByTagName("messages.errorSpans.tagName"));
    					
    					for (WebElement span : spans) {
    						String text = span.getText().trim();
    						if (!text.isEmpty()) {
    							errorMessages.add(text);
    							if (verboseLogging) {
    							}
    						}
    					}
    					if (spans.isEmpty() && !elementText.isEmpty()) {
    						errorMessages.add(elementText);
    						if (verboseLogging) {
    						}
    					}
    				} catch (Exception e) {
    					if (!elementText.isEmpty()) {
    						errorMessages.add(elementText);
    					}
    				}
    			}
    		}
    		
    		if (errorMessages.isEmpty()) {
    		} else {
    		}
    	} catch (Exception e) {
    	}
    	return errorMessages;
    }
    
    public ContactAPIValidator getApiValidator() {
    	return apiValidator;
    }
    
    public String getLastApiResponse() {
    	return apiValidator != null ? apiValidator.getResponseBody() : "";
    }
    
    public int getLastApiStatusCode() {
    	return apiValidator != null ? apiValidator.getStatusCode() : 0;
    }
    
    public String getLastApiUrl() {
    	return SelectorRepository.get("urls.portalBaseUrl") + SelectorRepository.get("urls.contactApiEndpoint");
    }
    
    public Map<String, String> getFormData() {
    	return new HashMap<>(formData);
    }
    
    private void recordSubmissionToExcel(String status, int statusCode, long responseTime) {
    	if (excelRecorder == null) {
    		System.err.println("Excel recorder not initialized - skipping recording");
    		return;
    	}
    	
    	try {
    		Map<String, String> recordData = new HashMap<>(formData);
    		recordData.put("status", status);
    		recordData.put("apiStatusCode", String.valueOf(statusCode));
    		recordData.put("responseTime", String.valueOf(responseTime));
    		
    		String notes = "";
    		if (statusCode >= 200 && statusCode < 300) {
    			notes = "Successful submission";
    		} else if (statusCode >= 400 && statusCode < 500) {
    			notes = "Client error - validation failed";
    		} else if (statusCode >= 500) {
    			notes = "Server error";
    		}
    		recordData.put("notes", notes);
    		
    		excelRecorder.writeFormSubmission(recordData);
    		
    	} catch (Exception e) {
    		System.err.println("Failed to record submission to Excel: " + e.getMessage());
    		e.printStackTrace();
    	}
    }
    
    public boolean validateExcelRecording() {
    	if (excelRecorder == null) {
    		System.err.println("Excel recorder not initialized");
    		return false;
    	}
    	
    	try {
    		Map<String, String> recordedData = excelRecorder.readLastSubmission();
    		
    		boolean matches = true;
    		String[] fieldsToValidate = {"fullName", "email", "phoneNumber", "message"};
    		
    		for (String field : fieldsToValidate) {
    			String submitted = formData.get(field);
    			String recorded = recordedData.get("Name");
    			
    			if (field.equals("fullName")) recorded = recordedData.get("Name");
    			else if (field.equals("email")) recorded = recordedData.get("Email");
    			else if (field.equals("phoneNumber")) recorded = recordedData.get("Phone");
    			else if (field.equals("message")) recorded = recordedData.get("Message");
    			
    			if (submitted != null && !submitted.equals(recorded)) {
    				System.err.println("Mismatch in " + field + ": submitted='" + submitted + "', recorded='" + recorded + "'");
    				matches = false;
    			}
    		}
    		
    		if (matches) {
    		}
    		
    		return matches;
    		
    	} catch (Exception e) {
    		System.err.println("Failed to validate Excel recording: " + e.getMessage());
    		return false;
    	}
    }
    
    public int getExcelSubmissionCount() {
    	if (excelRecorder == null) return 0;
    	return excelRecorder.getSubmissionCount();
    }
}
