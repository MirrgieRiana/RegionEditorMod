package mirrg.minecraft.regioneditor.data;

public class ParseException extends Exception
{

	public ParseException()
	{
		super();
	}

	protected ParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ParseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ParseException(String message)
	{
		super(message);
	}

	public ParseException(Throwable cause)
	{
		super(cause);
	}

}