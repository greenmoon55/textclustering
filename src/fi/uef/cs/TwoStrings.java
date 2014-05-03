package fi.uef.cs;

public class TwoStrings {
	String first, second;

	public TwoStrings(String a, String b) {
		if (a.hashCode() < b.hashCode()) {
			this.first = a;
			this.second = b;
		} else {
			this.first = b;
			this.second = a;
		}
	}

	public int hashCode() {
		int hashFirst = first != null ? first.hashCode() : 0;
		int hashSecond = second != null ? second.hashCode() : 0;
		return (hashFirst + hashSecond) * hashSecond + hashFirst;
	}

	public boolean equals(Object other) {
		if (other instanceof TwoStrings) {
			TwoStrings otherPair = (TwoStrings) other;
			return ((this.first == otherPair.first || (this.first != null
					&& otherPair.first != null && this.first
						.equals(otherPair.first))) && (this.second == otherPair.second || (this.second != null
					&& otherPair.second != null && this.second
						.equals(otherPair.second))));
		}
		return false;
	}

}
