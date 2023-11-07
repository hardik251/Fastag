import pandas as pd
import matplotlib.pyplot as plt
import mysql.connector
from fastapi import FastAPI
from fastapi.responses import HTMLResponse
import io
import base64
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()

# Configure CORS settings
origins = [ 
    "http://127.0.0.1:5500", 
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],  # You can restrict to specific HTTP methods
    allow_headers=["*"],  # You can restrict to specific headers
)


    # sql_query = """
    # SELECT
    #     T.Transaction_Id,
    #     T.date_time,
    #     T.vehicle_number,
    #     T.toll_plaza_id,
    #     T.Closing_Account_balance,
    #     T.Transaction_amount,
    #     T.Journey_Type,
    #     VD.Category_id,
    #     C.CategoryName,
    #     TL.toll_plaza_name,
    #     TL.state
    # FROM
    #     transactions AS T
    # JOIN
    #     vehicle_details AS VD ON T.vehicle_number = VD.Vehicle_Number
    # JOIN
    #     category AS C ON VD.Category_id = C.CategoryID
    # JOIN
    #     toll_list AS TL ON T.toll_plaza_id = TL.id
    # """
    
# custom_colors = ['#E63946', '#F1FAEE', '#A8DADC', '#457B9D', '#1D3557']
# custom_colors = ['#FF5733', '#FFBD33', '#FF33A8', '#33FF57', '#33B5FF', '#3333FF', '#FF33F3', '#33FFBD', '#FF5733', '#A833FF']


@app.get("/byState", response_class=HTMLResponse)
async def get_pie_chart():
    connection = mysql.connector.connect(
    host="localhost",
    user="root",
    password="root",
    database="fastag"
    )

    # SQL query to fetch data with joins and grouping by state
    sql_query = """
    SELECT
        TL.state,
        SUM(T.Transaction_amount) AS total_charges
    FROM
        transactions AS T
    JOIN
        toll_list AS TL ON T.toll_plaza_id = TL.id
    GROUP BY
        TL.state
    """
    
    # Fetch data into a Pandas DataFrame
    df = pd.read_sql_query(sql_query, connection)

    plt.figure(figsize=(6.2, 6.2))
    # colors = plt.cm.Paired(range(len(df['state'])))  # Generate unique colors for each state
    custom_colors = [
    '#FF33A8', '#33FF57', '#33B5FF', '#3333FF', '#FF33F3',
    '#F4A261', '#2A9D8F', '#E9C46A', '#264653', '#F4A261',
    '#E76F51', '#06D6A0', '#F4A261', '#B83B5E', '#247BA0',
    '#F4A261', '#F4A261', '#F4A261', '#F4A261', '#F4A261',
    '#F4A261', '#F4A261', '#F4A261', '#F4A261', '#F4A261',
    '#F4A261', '#F4A261', '#F4A261', '#F4A261'
    ]
    plt.pie(df['total_charges'], labels=None, autopct='%1.1f%%', startangle=140, colors=custom_colors)

    # Create a legend with states and colors
    plt.legend(df['state'], title="State", loc="upper right", bbox_to_anchor=(1.05, 1))

    plt.axis('equal')
    plt.title("Toll collection split by State")

    # Save the pie chart to a BytesIO object
    chart_buffer = io.BytesIO()
    plt.savefig(chart_buffer, format='png')
    chart_buffer.seek(0)
    plt.close()

    # Encode the chart as base64 for embedding in HTML
    chart_base64 = base64.b64encode(chart_buffer.read()).decode('utf-8')

    # HTML response with the embedded chart
    html_content = f"<img src='data:image/png;base64,{chart_base64}'/>"
    return HTMLResponse(content=html_content)

@app.get("/byVehicle", response_class=HTMLResponse)
async def get_pie_chart():
    connection = mysql.connector.connect(
    host="localhost",
    user="root",
    password="root",
    database="fastag"
    )

    # SQL query to fetch data with joins and grouping by state
    sql_query = """
    SELECT
        C.CategoryName AS vehicle_type,
        SUM(T.Transaction_amount) AS total_charges
    FROM
        transactions AS T
    JOIN
        vehicle_details AS VD ON T.vehicle_number = VD.Vehicle_Number
    JOIN
        category AS C ON VD.Category_id = C.CategoryID
    GROUP BY
        vehicle_type
    """
    
    # Fetch data into a Pandas DataFrame
    df = pd.read_sql_query(sql_query, connection)

    plt.figure(figsize=(6.2, 6.2))
    # colors = plt.cm.Paired(range(len(df['vehicle_type'])))  # Generate unique colors for each category
    custom_colors = ['#FF5733', '#FFBD33', '#FF33A8', '#33FF57', '#33B5FF', '#3333FF', '#FF33F3']
    plt.pie(df['total_charges'], labels=None, autopct='%1.1f%%', startangle=140, colors=custom_colors)

    # Create a legend with vehicle types and colors
    plt.legend(df['vehicle_type'], title="Vehicle Type", loc="upper right", bbox_to_anchor=(1.05, 1))

    plt.axis('equal')
    plt.title("Toll collection split by Vehicle Type")

    # Save the pie chart to a BytesIO object
    chart_buffer = io.BytesIO()
    plt.savefig(chart_buffer, format='png')
    chart_buffer.seek(0)
    plt.close()

    # Encode the chart as base64 for embedding in HTML
    chart_base64 = base64.b64encode(chart_buffer.read()).decode('utf-8')

    # HTML response with the embedded chart
    html_content = f"<img src='data:image/png;base64,{chart_base64}'/>"
    return HTMLResponse(content=html_content)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="localhost", port=8000)
