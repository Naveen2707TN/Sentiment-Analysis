package com.sentiment.anlaysis.demo.Controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.sentiment.anlaysis.demo.Module.ReviewReceiver;
import com.sentiment.anlaysis.demo.Module.ReviewSender;
import com.sentiment.anlaysis.demo.Service.CSVFileService;
import com.sentiment.anlaysis.demo.Service.FileService;

@Controller
public class CSVFileHandlerControl {
    @Autowired
	FileService fileService;
	
	public String jsonString = "";
	
    @Autowired
    CSVFileService csvFileService;

	public String Dir = "D://Upload//UserFiles//"; 
	public String path = Dir + "Report_" + System.currentTimeMillis() + ".csv";
	
	@GetMapping("/read")
	public String UploadFileReader() throws CsvValidationException, IOException {
		ArrayList<ReviewSender> review = new ArrayList<ReviewSender>();
		String path = fileService.filePath();
		FileReader fileReader = new FileReader(path);
		CSVReader reader = new CSVReader(fileReader);
		String[] lineStrings;
		while((lineStrings = reader.readNext()) != null) {
			String valString = lineStrings[0];
			ReviewSender reviewSender = new ReviewSender(valString);
			review.add(reviewSender);
		}
		reader.close();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			jsonString = objectMapper.writeValueAsString(review);
		}catch (Exception e) {
			e.printStackTrace();
		}
		review.clear();
		RestTemplate restTemplate = new RestTemplate();
		String pythonUrlString = "http://127.0.0.1:5000/receive-data";
		HttpHeaders headers = new org.springframework.http.HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(pythonUrlString, HttpMethod.POST, entity, String.class);
		HttpStatusCode code = responseEntity.getStatusCode();
		if(code.is2xxSuccessful()) {
			return "redirect:/result";
		}
		return "redirect:/dashboard";
	}

	public void ReadFile(){
		List<ReviewReceiver> list = csvFileService.getAlldata();
		
    	File file = new File(path);

		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
			String[] headers = {"Review", "Sentiment", "Confidence", "Cluster"};
			writer.writeNext(headers); 

			for (ReviewReceiver rc : list) {
				String[] rows = {
					rc.getReview(),
					rc.getSentiment(),
					String.valueOf(rc.getConfidence()),
					String.valueOf(rc.getCluster())
				};
				writer.writeNext(rows); 
			}
		} catch (Exception e) {
			System.out.println("Error writing CSV: " + e.getMessage());
		}
	}

	public List<ReviewReceiver> receivers(){
		List<ReviewReceiver> list = new ArrayList<>();

		try (CSVReader reader = new CSVReader(new FileReader(path))){
			String[] data;
			reader.readNext();
			while ((data = reader.readNext()) != null) {
				ReviewReceiver receiver = new ReviewReceiver();
				receiver.setReview(data[0]);
				receiver.setSentiment(data[1]);
				receiver.setConfidence(Double.parseDouble(data[2]));
				receiver.setCluster(Integer.parseInt(data[3]));
				list.add(receiver);
			}
			reader.close();
		} catch (Exception e) {
		}
		return list;
	}

	@GetMapping("/download")
	public ResponseEntity<InputStreamResource> fileDownload() {
		List<ReviewReceiver> list = receivers();
		String path = fileService.filePath();
		File file = new File(path);

		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
			String[] headers = {"Review", "Sentiment", "Confidence", "Cluster"};
			writer.writeNext(headers); 

			for (ReviewReceiver rc : list) {
				String[] rows = {
					rc.getReview(),
					rc.getSentiment(),
					String.valueOf(rc.getConfidence()),
					String.valueOf(rc.getCluster())
				};
				writer.writeNext(rows); 
			}
			double count = 0;
			for(ReviewReceiver rr : list){
				count += rr.getConfidence();
			}
			double result = count / list.size();
			String[] lower = {"Accuracy : ","",String.valueOf(result),""};
			writer.writeNext(lower);
			list.clear();

		} catch (Exception e) {
			System.out.println("Error writing CSV: " + e.getMessage());
		}

		try {
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
			String fileName = "Final_Report_Sentiment_Analysis_" + System.currentTimeMillis() + ".csv";

			return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
				.contentType(MediaType.parseMediaType("text/csv"))
				.body(resource);

		} catch (FileNotFoundException e) {
			return ResponseEntity.internalServerError().build();
		}
	}
	@GetMapping("/positive")
	public ResponseEntity<InputStreamResource> positiveFile() {
		List<ReviewReceiver> list = receivers();

		String filename = "Positive_Review_Report_" + System.currentTimeMillis() + ".csv";
		String path = System.getProperty("java.io.tmpdir") + File.separator + filename;
		File file = new File(path);

		try (CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
			String[] header = {"Review", "Sentiment", "Confidence", "Cluster"};
			csvWriter.writeNext(header);

			for (ReviewReceiver receiver : list) {
				if ("Positive".equalsIgnoreCase(receiver.getSentiment())) {
					String[] row = {
						receiver.getReview(),
						receiver.getSentiment(),
						String.valueOf(receiver.getConfidence()),
						String.valueOf(receiver.getCluster())
					};
					csvWriter.writeNext(row);
				}
			}
			list.clear();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new InputStreamResource(new ByteArrayInputStream("Error writing to CSV".getBytes())));
		}

		try {
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
					.contentType(MediaType.parseMediaType("text/csv"))
					.body(resource);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/negative")
	public ResponseEntity<InputStreamResource> negativeFile() {
		List<ReviewReceiver> list = receivers();

		String filename = "Positive_Review_Report_" + System.currentTimeMillis() + ".csv";
		String path = System.getProperty("java.io.tmpdir") + File.separator + filename;
		File file = new File(path);

		try (CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
			String[] header = {"Review", "Sentiment", "Confidence", "Cluster"};
			csvWriter.writeNext(header);

			for (ReviewReceiver receiver : list) {
				if ("Negative".equalsIgnoreCase(receiver.getSentiment())) {
					String[] row = {
						receiver.getReview(),
						receiver.getSentiment(),
						String.valueOf(receiver.getConfidence()),
						String.valueOf(receiver.getCluster())
					};
					csvWriter.writeNext(row);
				}
			}
			list.clear();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new InputStreamResource(new ByteArrayInputStream("Error writing to CSV".getBytes())));
		}

		try {
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
					.contentType(MediaType.parseMediaType("text/csv"))
					.body(resource);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}
	@GetMapping("/neutral")
	public ResponseEntity<InputStreamResource> neutralFile() {
		List<ReviewReceiver> list = receivers();

		String filename = "Positive_Review_Report_" + System.currentTimeMillis() + ".csv";
		String path = System.getProperty("java.io.tmpdir") + File.separator + filename;
		File file = new File(path);

		try (CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
			String[] header = {"Review", "Sentiment", "Confidence", "Cluster"};
			csvWriter.writeNext(header);

			for (ReviewReceiver receiver : list) {
				if ("Neutral".equalsIgnoreCase(receiver.getSentiment())) {
					String[] row = {
						receiver.getReview(),
						receiver.getSentiment(),
						String.valueOf(receiver.getConfidence()),
						String.valueOf(receiver.getCluster())
					};
					csvWriter.writeNext(row);
				}
			}
			list.clear();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new InputStreamResource(new ByteArrayInputStream("Error writing to CSV".getBytes())));
		}

		try {
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
					.contentType(MediaType.parseMediaType("text/csv"))
					.body(resource);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/pie-chart")
	public ResponseEntity<Map<String, Integer>> mapp(){
		List<ReviewReceiver> list = receivers();
		HashMap<String, Integer> map = new HashMap<>();
		for(ReviewReceiver rr : list){
			map.put(rr.getSentiment(), map.getOrDefault(rr.getSentiment(), 0)+1);
		}
		return ResponseEntity.ok(map);
	}
}
