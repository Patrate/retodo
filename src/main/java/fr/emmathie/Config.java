package fr.emmathie;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Config {
	@Id
	private String configKey;
	private String configValue;

	public Config() {
	}

	public Config(String key, String value) {
		this.configKey = key;
		this.configValue = value;
	}

	public String getValue() {
		return configValue;
	}

	public void setValue(String value) {
		this.configValue = value;
	}

	public String getKey() {
		return configKey;
	}

	public void setKey(String key) {
		this.configKey = key;
	}
}
