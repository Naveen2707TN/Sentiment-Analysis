package com.sentiment.anlaysis.demo.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sentiment.anlaysis.demo.Module.ReviewReceiver;

@Service
public class CSVFileService {

    public List<ReviewReceiver> getAlldata(){
        RestTemplate restTemplate = new RestTemplate();
        String pythonUrl = "http://127.0.0.1:5000/receive-datas";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<ReviewReceiver[]> rEntity = restTemplate.exchange(pythonUrl, HttpMethod.GET, httpEntity, ReviewReceiver[].class);
        List<ReviewReceiver> list = new ArrayList<>(Arrays.asList(rEntity.getBody()));
        return list;
    }
    
}