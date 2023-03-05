import os
import re
import pymysql
from conection_mysql import *
import threading
import time



def session(user,password):
    res = Database().get_user(user,password)
    if res is not None:
        return res
    else: 
        return("{}")
def locks_user(id):
    res = Database().get_locks_user(id)
    if res is not None:
        return res
    else: 
        return("[]")

def update_statlock_open(id_user, id_lock, type_in, name_blue, name_in, dni_in):
    res = Database().update_lock_open(id_lock)
    threading_close = threading.Thread(target=timer5_statlock_close, args=(id_lock,))
    threading_history = threading.Thread(target=into_history_lock, args=(id_user, id_lock, type_in, name_blue, name_in, dni_in,))
    threading_history.start()
    threading_close.start()

    return res

def update_statlock_open_ble(id_lock, mac_ble):
    res = Database().update_lock_open(id_lock)
    threading_close = threading.Thread(target=timer5_statlock_close, args=(id_lock,))
    threading_history = threading.Thread(target=into_history_ble_lock, args=(id_lock, mac_ble,))
    threading_history.start()
    threading_close.start()

    return res

def update_statlock_close(id):
    res = Database().update_lock_close(id)
    return res

def get_statlock(id):
    res = Database().get_lock_stat(id)
    if res is not None:
        return res
    else: 
        return("{}")

def get_device_for_lock(id_lock):
    res = Database().get_deviceble_for_lock(id_lock)
    if res is not None:
        return res
    else: 
        return("[]")

def get_device_for_lock_json(id_lock, id_user):
    res = Database().get_deviceble_for_lock_json(id_lock, id_user)
    if res is not None:
        return res
    else: 
        return("[]")

def get_history_for_lock(id_lock):
    res = Database().get_history_for_lock(id_lock)
    if res is not None:
        return res
    else: 
        return("[]")

def insert_val(id, number, value):
    return Database().insert_valx(id, number, value)

def insert_device_ble(id_user, id_lock, name_ble, mac_ble):
    return Database().insert_device_ble(id_user, id_lock, name_ble, mac_ble)

def timer5_statlock_close(id):
    time.sleep(5)
    res = update_statlock_close(id)

def into_history_lock(id_user, id_lock, type_in, name_blue, name_in, dni_in):
    res = Database().insert_data_history(id_user, id_lock, type_in, name_blue, name_in, dni_in)
    return res

def into_history_ble_lock(id_lock, mac_ble):
    res = Database().insert_data_history_ble_lock(id_lock, mac_ble)
    return res
