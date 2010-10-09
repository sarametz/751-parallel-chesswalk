package cz.hejl.chesswalk;

@SuppressWarnings("serial")
public class LoginException extends Exception {
	public LoginException(){}
	public LoginException(String message){
		super(message);
	}
}
