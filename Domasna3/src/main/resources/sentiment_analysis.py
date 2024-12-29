import asyncio
import aiohttp
import re
from datetime import datetime
from bs4 import BeautifulSoup
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import sys
import json

class NewsScraper:
    def __init__(self, session, max_concurrent_requests=10):
        self.base_url = "https://www.mse.mk/en/symbol/{issuer}"
        self.text_url = "https://api.seinet.com.mk/public/documents/single/{news_id}"
        self.session = session
        self.semaphore = asyncio.Semaphore(max_concurrent_requests)

    async def fetch_news_links(self, issuer):
        url = self.base_url.format(issuer=issuer)
        async with self.semaphore:
            try:
                async with self.session.get(url, timeout=15) as response:
                    response.raise_for_status()
                    page_content = await response.text()
                    soup = BeautifulSoup(page_content, "html.parser")
                    news_links = soup.select('#seiNetIssuerLatestNews a')
                    return [
                        {
                            "news_id": self.extract_news_id(link["href"]),
                            "date": self.extract_date(
                                link.select_one("ul li:nth-child(2) h4").text
                            ) if link.select_one("ul li:nth-child(2) h4") else None,
                        }
                        for link in news_links if "href" in link.attrs
                    ]
            except Exception as e:
                print(f"Error fetching news links for {issuer}: {e}")
                return []

    def extract_news_id(self, url):
        return url.split("/")[-1]

    def extract_date(self, date_str):
        try:
            match = re.search(r"\d{1,2}/\d{1,2}/\d{4}", date_str)
            if match:
                return datetime.strptime(match.group(), "%m/%d/%Y").date()
        except Exception as e:
            print(f"Error extracting date from {date_str}: {e}")
        return None

    async def fetch_news_content(self, news_id):
        url = self.text_url.format(news_id=news_id)
        async with self.semaphore:
            try:
                async with self.session.get(url, timeout=15) as response:
                    if response.status == 200:
                        data = await response.json()
                        content = data.get("data", {}).get("content")
                        return re.sub(r"<[^>]*>", "", content).strip() if content else None
            except Exception as e:
                print(f"Error fetching content for news ID {news_id}: {e}")
                return None

class SentimentAnalyzer:
    def __init__(self):
        self.tokenizer = AutoTokenizer.from_pretrained("cardiffnlp/twitter-roberta-base-sentiment")
        self.model = AutoModelForSequenceClassification.from_pretrained(
            "cardiffnlp/twitter-roberta-base-sentiment"
        )
        self.labels = ["negative", "neutral", "positive"]

    def analyze(self, text):
        # Define a maximum length for truncation
        max_length = 512  # Adjust this value as per model's max length
        inputs = self.tokenizer(text, return_tensors="pt", truncation=True, padding=True, max_length=max_length)
        outputs = self.model(**inputs)
        probabilities = outputs.logits.softmax(dim=-1).tolist()[0]
        label_index = probabilities.index(max(probabilities))
        return self.labels[label_index], probabilities


async def analyze_sentiment(issuer):
    async with aiohttp.ClientSession() as session:
        scraper = NewsScraper(session)
        analyzer = SentimentAnalyzer()

        news_links = await scraper.fetch_news_links(issuer)
        results = []

        for news in news_links:
            content = await scraper.fetch_news_content(news["news_id"])
            if content:
                sentiment_label, probabilities = analyzer.analyze(content)
                recommendation = "Buy" if sentiment_label == "positive" else "Sell" if sentiment_label == "negative" else "Hold"
                results.append({
                    "date": news["date"],
                    "sentiment": sentiment_label,
                    "recommendation": recommendation,
                    "positive_score": probabilities[2],
                    "negative_score": probabilities[0],
                    "neutral_score": probabilities[1],
                })

        return results

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python sentiment_analysis_pipeline.py <issuer>")
        sys.exit(1)

    issuer = sys.argv[1]
    result = asyncio.run(analyze_sentiment(issuer))
    print(json.dumps(result, indent=4, default=str))
