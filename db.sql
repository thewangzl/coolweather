create table Province (
	id integer primary key autoincrement,
	province_name text,
	province_code text
);

create table City(
	id integer primary key autoincrement,
	city_name text,
	city_code text,
	province_id integer
);

create table Country(
	id integer primary key autoincrement,
	country_name text,
	country_code text,
	city_id integer
);