public class Pair{
		private  String key;
		private  Integer value;
		public Pair(String k, Integer v) {
			key = k;
			value = v;
		}
		public  String getKey() {
			return key;
		}
		public  Integer getValue() {
			return value;
		}
		public String toString() {
			return ("[" + key + ", " + value + "]");
		}
	}