import pymysql
try:
    conn = pymysql.connect(host='localhost', port=3308, user='root', password='123456', database='crm_backend')
    cursor = conn.cursor()
    cursor.execute("SHOW CREATE PROCEDURE import_customersV2")
    res = cursor.fetchone()
    print("=== DUMP STORED PROCEDURE ===")
    print(res[2])
except Exception as e:
    print(e)
