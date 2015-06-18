
CREATE TABLE [rooms] (
    [_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [icon_res] INTEGER,
    [name] [VARCHAR(255)],
    [icon] [VARCHAR(255)]);

CREATE TABLE [controls] (
    [_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [config_file] [VARCHAR(255)],
    [custom_view] INTEGER, --пользователь менял разметку, или чтобы вернуть к исходному - флаг убрать
    [name] [VARCHAR(255)],
    [room_id] INTEGER,
    [company] [VARCHAR(255)],
    [type] INTEGER, -- тип устройства, управляемый данным пультом
    FOREIGN KEY (room_id) REFERENCES rooms(id));

-- проблема будет с соединенными кнопками
-- они несколько записей занимают
CREATE TABLE [buttons] (
    [_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [name] [VARCHAR(255)], --имя в файле конфигурций, по нему узнаем иконку
    [type] INTEGER, -- счетверенная, обычная, двойная
    [icon] INTEGER, -- иконка кнопки
    [code] INTEGER, -- код сигнала
    [x] INTEGER,
    [y] INTEGER,
    [visible] INTEGER, -- в дополнительном меню или на экране
    [control_id] INTEGER, -- ид пульта хозяина
    FOREIGN KEY (control_id) REFERENCES controls(id));