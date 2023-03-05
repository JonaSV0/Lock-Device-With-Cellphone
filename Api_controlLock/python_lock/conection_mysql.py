import pymysql
from pymysql.cursors import DictCursor

class Database:

    def __init__(self):
        self.connection = pymysql.connect(
            host = 'localhost',
            user = 'root',
            password = '',
            db = 'lock_generical1',
            cursorclass = DictCursor
        )

        self.cursor = self.connection.cursor()
    

    def insert_datax(self, name):
        #sql = """INSERT INTO datax (id, datex, namex) VALUES (NULL, NOW(), %s)"""
        #args = (name)
        #print(sql)
        
        try:
            self.cursor.execute("INSERT INTO datax (id, datex, namex) VALUES (NULL, NOW(), %s)", name)
            self.connection.commit()
            return True

        except Exception as e:
            return False
    
    def get_user(self, name, password):

        try:
            self.cursor.execute("SELECT id, name, surname, dni, http FROM user_l WHERE mail=%s AND pass=%s", (name, password))
            emp = self.cursor.fetchone()
            return emp

        except Exception as e:
            return False
    def get_locks_user(self, id):
        try:
            self.cursor.execute("SELECT l.id, l.nickname, l.unicode, l.stat, l.stat_lock FROM lock_l as l JOIN user_lock as ul ON l.id=ul.id_lock WHERE ul.id_user = %s", id)
            emp = self.cursor.fetchall()
            return emp

        except Exception as e:
            return False
    def get_lock_stat(self, id):

        try:
            self.cursor.execute("SELECT nickname, stat, stat_lock, pin FROM lock_l WHERE id = %s", id)
            emp = self.cursor.fetchone()
            return emp
        except Exception as e:
            return False
    
    def get_deviceble_for_lock(self, id_lock):
        try:
            self.cursor.execute("SELECT db.mac_b FROM user_lock_ble as ulb JOIN device_ble as db ON ulb.id_ble=db.id WHERE ulb.id_lock = %s", id_lock)
            emp = self.cursor.fetchall()
            array = []
            for e in emp:
                array.append(e["mac_b"])
            
            str_array = (str)(array)
            str_array = str_array.replace("'",'')
            str_array = str_array.replace("[",'')
            str_array = str_array.replace("]",'')
            str_array = str_array.replace(" ",'')
            str_array = str_array.lower() + ','
            print(str_array)
            return str_array

        except Exception as e:
            print(e)
            return "False"

    def get_deviceble_for_lock_json(self, id_lock, id_user):
        try:
            self.cursor.execute("SELECT db.id, db.mac_b, db.name_b FROM user_lock_ble as ulb JOIN device_ble as db ON ulb.id_ble=db.id WHERE ulb.id_lock = %s AND ulb.id_user = %s", (id_lock, id_user))
            emp = self.cursor.fetchall()
            return emp

        except Exception as e:
            print(e)
            return "False"

    def get_history_for_lock(self, id_lock):

        try:
            self.cursor.execute("SELECT id_user, id_lock, type_in, name_bluet, name_in, dni_in, DATE_FORMAT(date_in, GET_FORMAT(DATE,'ISO')) AS 'date_in', DATE_FORMAT(time_in, GET_FORMAT(TIME,'ISO')) AS 'time_in' FROM history WHERE id_lock = %s", id_lock)
            emp = self.cursor.fetchall()
            return emp
        except Exception as e:
            return False

    def update_lock_open(self, id):
        try:
            self.cursor.execute("UPDATE lock_l SET stat_lock = 1 WHERE id = %s", id)
            self.connection.commit()
            return True
            
        except Exception as e:
            return False

    def update_lock_close(self, id):
        try:
            self.cursor.execute("UPDATE lock_l SET stat_lock = 0 WHERE id = %s", id)
            self.connection.commit()
            return True
            
        except Exception as e:
            return False

    def get_users(self):
        try:
            self.cursor.execute("SELECT * FROM user_l")
            emp = self.cursor.fetchall()
            return emp

        except Exception as e:
            return False
    
    def insert_valx(self, id, number, value):
        try:
            self.cursor.execute("INSERT INTO valx (id, id_data, numero, val) VALUES (NULL, %s, %s, %s)", (id, number, value))
            self.connection.commit()
            return True
            
        except Exception as e:
            return False

    def insert_data_history(self, id_user, id_lock, type_in, name_blue, name_in, dni_in):
        #sql = """INSERT INTO datax (id, datex, namex) VALUES (NULL, NOW(), %s)"""
        #args = (name)
        #print(sql)
        
        try:
            self.cursor.execute("INSERT INTO history (id, id_user, id_lock, type_in, name_bluet, name_in, dni_in, date_in, time_in) VALUES (NULL, %s, %s, %s, %s, %s, %s, NOW(), NOW())", (id_user, id_lock, type_in, name_blue, name_in, dni_in))
            self.connection.commit()
            return True

        except Exception as e:
            return False
    
    def insert_data_history_ble_lock(self, id_lock, mac_ble):
        try:
            self.cursor.execute(" SELECT ulb.id_user, db.name_b, al.name, al.surname, al.dni  FROM user_l AS al JOIN user_lock_ble AS ulb ON ulb.id_user=al.id JOIN device_ble AS db ON db.id=ulb.id_ble WHERE  db.mac_b = %s AND ulb.id_lock = %s", (mac_ble, id_lock))
            emp = self.cursor.fetchone()
            name_in = emp["name"] + " " + emp["surname"]

            self.cursor.execute("INSERT INTO history (id, id_user, id_lock, type_in, name_bluet, name_in, dni_in, date_in, time_in) VALUES (NULL, %s, %s, %s, %s, %s, %s, NOW(), NOW())", (emp["id_user"], id_lock, 'BleDevice', emp["name_b"], name_in, emp["dni"]))
            self.connection.commit()
            return True

        except Exception as e:
            return False

    def insert_device_ble(self, id_user, id_lock, name_ble, mac_ble):
        
        try:
            self.cursor.execute("INSERT INTO device_ble (id, name_b, mac_b) VALUES (NULL, %s, %s)", (name_ble, mac_ble))
            self.connection.commit()

            self.cursor.execute("SELECT * FROM device_ble WHERE mac_b= %s", mac_ble)
            emp = self.cursor.fetchone()

            self.cursor.execute("INSERT INTO user_lock_ble (id, id_user, id_lock, id_ble, type_rel) VALUES (NULL, %s, %s, %s, NULL)", (id_user, id_lock, emp["id"]))
            self.connection.commit()

            return "True"

        except Exception as e:
            return "False"
    