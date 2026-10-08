import pandas as pd
import random
from datetime import datetime, timedelta

def generate_excel():
    print("Generating 5000 rows of test data...")
    data = []
    start_date = datetime(1980, 1, 1)
    
    for i in range(1, 5001):
        name = f"Khách Hàng Test {i}"
        phone = f"09{str(i).zfill(8)}"
        email = f"test{i}@abc.com"
        if random.random() > 0.5:
            email += f"; phụ{i}@abc.com"
        
        dob = (start_date + timedelta(days=random.randint(0, 15000))).strftime("%Y-%m-%d")
        location = random.choice(["Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Hải Phòng", "Cần Thơ"])
        gender = random.choice(["true", "false"])
        
        data.append({
            "name": name,
            "phoneNumber": phone,
            "email": email,
            "dateOfBirth": dob,
            "location": location,
            "gender": gender
        })
    
    df = pd.DataFrame(data)
    now = datetime.now()
    file_name = now.strftime("%H%M_%d%m%Y.xlsx")
    
    df.to_excel(file_name, index=False)
    print(f"Successfully created file: {file_name}")

if __name__ == "__main__":
    generate_excel()
