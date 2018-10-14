package by.kominch.firstwebsocketapp.Commands;

/**
 * @author Вадим
 *	Enum for supported commands
 *	Contains three commands(REGISTER("/register"),LEAVE("/leave"),EXIT("/exit"))
 *	@param 		command text of the command
 */
public enum Commands {
		REGISTER("/register"),
		LEAVE("/leave"),
		EXIT("/exit");
		
		private String command;
		
		/**
		 * Commands enum constructor. Get one parameter command
		 * @param command
		 */
		Commands(String command) {
			this.command=command;
		}
		
		/**
		 * Method which get text of the command and return it.
		 * @return command    
		 */
		public String getCommand() {
			return command;
		}
}
