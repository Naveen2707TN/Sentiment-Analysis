package com.sentiment.anlaysis.demo.Module;

public class ReviewReceiver {
    private int cluster;
	private double confidence;
	private String review;
	private String sentiment;
		
	public ReviewReceiver() {
	
	}
	
	public ReviewReceiver(int cluster, double confidence, String review, String sentiment) {
		this.cluster = cluster;
		this.confidence = confidence;
		this.review = review;
		this.sentiment = sentiment;
	}
	
	public int getCluster() {
		return cluster;
	}
	
	public void setCluster(int cluster) {
		this.cluster = cluster;
	}
	
	public double getConfidence() {
		return confidence;
	}
	
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
	public String getReview() {
		return review;
	}
	
	public void setReview(String review) {
		this.review = review;
	}
	
	public String getSentiment() {
		return sentiment;
	}
	
	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}
}
