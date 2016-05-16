package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {

	private LinearLayout weatherLayout;

	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp1Text;
	private TextView temp2Text;
	private TextView currentDateText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather);
		this.weatherLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		this.cityNameText = (TextView) findViewById(R.id.city_name);
		this.publishText = (TextView) findViewById(R.id.publish_text);
		this.weatherDespText = (TextView) findViewById(R.id.weather_desp);
		this.temp1Text = (TextView) findViewById(R.id.temp1);
		this.temp2Text = (TextView) findViewById(R.id.temp2);
		this.currentDateText = (TextView) findViewById(R.id.current_date);
		String countryCode = getIntent().getStringExtra("country_code");
		if (!TextUtils.isEmpty(countryCode)) {
			this.publishText.setText("同步中...");
			this.weatherLayout.setVisibility(View.INVISIBLE);
			this.cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		} else {
			showWeather();
		}
	}

	private void queryWeatherCode(String countryCode) {
		String address = "http://www.weather.com.cn/data/list3/city" + countryCode + ".xml";
		queryFromServer(address, "countryCode");
	}

	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(final String response) {
				if ("countryCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						//
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					//
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						public void run() {
							showWeather();
						}
					});
				}
			}

			@Override
			public void onError(final Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						publishText.setText(e.getMessage());
					}
				});
			}
		});
	}

	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.cityNameText.setText(prefs.getString("city_name", ""));
		this.temp1Text.setText(prefs.getString("temp1", ""));
		this.temp2Text.setText(prefs.getString("temp2", ""));
		this.weatherDespText.setText(prefs.getString("weather_desp", ""));
		this.publishText.setText("今天"+prefs.getString("publish_time", "") + "发布");
		this.currentDateText.setText(prefs.getString("current_date", ""));
		this.weatherLayout.setVisibility(View.VISIBLE);
		this.cityNameText.setVisibility(View.VISIBLE);
	}
}
