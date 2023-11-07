import requests
import pandas as pd
import matplotlib.pyplot as plt

# Replace with the URL of your Java Spring Boot API endpoint
api_url = 'http://localhost:8080/NHAI/transactionList'

# Call the API to get the list of transactions
response = requests.get(api_url)
data = response.json()

# Create a DataFrame from the JSON data
df = pd.DataFrame(data)

# Group the transactions by state and calculate the sum of charges for each state
statewise_sum = df.groupby('state')['charges'].sum()

# Create a pie chart
plt.figure(figsize=(8, 8))
plt.pie(statewise_sum, labels=statewise_sum.index, autopct='%1.1f%%', startangle=140)
plt.title('State-wise Split of Charges')
plt.axis('equal')  # Equal aspect ratio ensures that pie is drawn as a circle.

# Display the pie chart
plt.show()
