package fr.umlv.thaw.data.chatter;

public interface Chatter {

	/**
	 * Get the chatting name of the chatter
	 * 
	 * @return the chatting name of the chatter
	 */
	public String getChattingName();
	
	/**
	 * Get the strict implementation (without other attributes) of the specified chatter.
	 * 
	 * @param 	chatter the specified chatter.
	 * @return	The chatter.
	 */
	public static Chatter asSimpleChatter(Chatter chatter) {
		return new Chatter() {

			@Override
			public String getChattingName() {
				return chatter.getChattingName();
			}
			
		};
	}

}
