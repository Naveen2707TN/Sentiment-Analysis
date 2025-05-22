from flask import Flask, request, jsonify
from transformers import pipeline, AutoTokenizer, AutoModelForSequenceClassification
from sklearn.cluster import KMeans
import numpy as np

app = Flask(__name__)

# Load tokenizer and model for 3-class sentiment
MODEL_NAME = "cardiffnlp/twitter-roberta-base-sentiment"
tokenizer = AutoTokenizer.from_pretrained(MODEL_NAME)
model = AutoModelForSequenceClassification.from_pretrained(MODEL_NAME)
sentiment_pipeline = pipeline("sentiment-analysis", model=model, tokenizer=tokenizer)

all_data = []

# Convert label to sentiment string
def convert_label(label):
    if label == "LABEL_0":
        return "Negative"
    elif label == "LABEL_1":
        return "Neutral"
    elif label == "LABEL_2":
        return "Positive"
    else:
        return "Unknown"

# Convert sentiment to numeric for clustering
def sentiment_to_num(label):
    if label == "LABEL_0":
        return 0
    elif label == "LABEL_1":
        return 1
    elif label == "LABEL_2":
        return 2
    return -1

@app.route('/receive-data', methods=['POST'])
def receive_data():
    if not request.is_json:
        return jsonify({"message": "Invalid JSON format"}), 400

    data = request.get_json()

    if isinstance(data, list):
        messages = [item.get("review", "") for item in data]
        results = sentiment_pipeline(messages)
        print(messages)
        response_data = []
        feature_vectors = []

        for msg, result in zip(messages, results):
            sentiment_label = result['label']
            sentiment_name = convert_label(sentiment_label)
            confidence = round(result['score'], 2)
            sentiment_num = sentiment_to_num(sentiment_label)

            feature_vectors.append([sentiment_num, confidence])
            response_data.append({
                "review": msg,
                "sentiment": sentiment_name,
                "confidence": confidence
            })

        # Apply KMeans clustering
        kmeans = KMeans(n_clusters=3, random_state=0)
        clusters = kmeans.fit_predict(feature_vectors)

        for i, cluster_id in enumerate(clusters):
            response_data[i]["cluster"] = int(cluster_id)

        all_data.extend(response_data)
        return jsonify(response_data), 200

    elif isinstance(data, dict):
        message = data.get("review", "")
        result = sentiment_pipeline(message)[0]
        sentiment_name = convert_label(result['label'])
        confidence = round(result['score'], 2)

        return jsonify({
            "review": message,
            "sentiment": sentiment_name,
            "confidence": confidence
        }), 200

    return jsonify({"message": "Unsupported JSON format"}), 400

@app.route('/receive-datas', methods=['GET'])
def get_All_data():
    data_to_return = all_data.copy()  # Create a copy of the data
    all_data.clear()  # Clear the stored data to avoid storing previous results
    return jsonify(data_to_return), 200

if __name__ == '__main__':
    app.run(debug=True)
