from flask import Flask, request
from flaskext.mysql import MySQL
from fun import *
from flask import Flask, jsonify, request



app = Flask(__name__)

@app.route('/')
async def Index():
    return ("Hola")

@app.route('/session', methods=['POST'])
async def Session():
    user = request.form['user']
    password = request.form['pass']
    res = session(user, password)
    print(res)
    return (res)

@app.route('/locks_user', methods=['POST'])
async def Locks_user():
    id = request.form['id']
    res = locks_user(id)
    print(res)
    return ((str)(res))

@app.route('/lock_on', methods=['POST'])
async def Lock_on():
    id_user = request.form['id_user']
    id_lock = request.form['id_lock']
    type_in = request.form['type']
    name_blue = request.form['name_bluet']
    name_in = request.form['name_user']
    dni_in = request.form['dni']

    res = update_statlock_open(id_user, id_lock, type_in, name_blue, name_in, dni_in)
    print(res)
    return ((str)(res))

@app.route('/lock_on_ble', methods=['POST'])
async def Lock_on_ble():
    id_lock = request.form['id_lock']
    mac_ble = request.form['mac_ble']
    mac_ble = mac_ble.upper()
    
    res = update_statlock_open_ble(id_lock, mac_ble)
    print(res)
    return ((str)(res))

@app.route('/lock_off', methods=['POST'])
async def Lock_off():
    id = request.form['id']
    res = update_statlock_close(id)
    print(res)
    return ((str)(res))

@app.route('/get_lock', methods=['POST'])
async def Get_lock():
    id = request.form['id']
    res = get_statlock(id)
    #print(res)
    return (res)

@app.route('/get_device_for_lock', methods=['POST'])
async def Get_device_for_lock():
    id_lock = request.form['id_lock']
    res = get_device_for_lock(id_lock)
    #print(res)
    return ((str)(res))

@app.route('/get_device_for_lock_json', methods=['POST'])
async def Get_device_for_lock_json():
    id_lock = request.form['id_lock']
    id_user = request.form['id_user']
    res = get_device_for_lock_json(id_lock, id_user)
    #print(res)
    return ((str)(res))

@app.route('/get_history_for_lock', methods=['POST'])
async def Get_history_for_lock():
    id_lock = request.form['id_lock']
    res = get_history_for_lock(id_lock)
    #print(res)
    return ((str)(res))


@app.route('/insert_device_ble', methods=['POST'])
async def Insert_device_ble():
    id_user = request.form['id_user']
    id_lock = request.form['id_lock']
    name_ble = request.form['name_ble']
    mac_ble = request.form['mac_ble']

    res = insert_device_ble(id_user, id_lock, name_ble, mac_ble)
    print(res)
    return (res)

if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0', port=7008, debug=True)

