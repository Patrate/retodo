package fr.emmathie;

public enum DConfig {
	COLOR("color","#303952");
	
	public final String key, value;

	private DConfig(String key, String value) {
		this.key = key;
		this.value = value;
	}
}
