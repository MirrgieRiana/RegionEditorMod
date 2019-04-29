package mirrg.minecraft.regioneditor.data;

public class ModelException extends Exception
{

	public ModelException()
	{
		super();
	}

	protected ModelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ModelException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ModelException(String message)
	{
		super(message);
	}

	public ModelException(Throwable cause)
	{
		super(cause);
	}

}
