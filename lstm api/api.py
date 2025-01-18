from flask import Flask, jsonify
import csv
import re

app = Flask(__name__)

@app.route('/predicted-prices', methods=['GET'])
def predicted_prices():
    data = []
    with open('predicted_average_prices.csv', 'r') as file:
        reader = csv.reader(file)
        next(reader)
        for row in reader:
            issuer_code = row[0].strip()
            predicted_price_str = re.sub(r'[^0-9.]', '', row[1].split(" ")[0]).strip()
            if predicted_price_str:
                data.append({"issuer": issuer_code, "predictedPrice": predicted_price_str})
    return jsonify(data)

if __name__ == '__main__':
    app.run(debug=True, host="127.0.0.1", port=5000)
