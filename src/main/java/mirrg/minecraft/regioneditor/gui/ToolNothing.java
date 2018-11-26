package mirrg.minecraft.regioneditor.gui;

public class ToolNothing implements ITool
{

	protected final IToolContext toolContext;

	public ToolNothing(IToolContext toolContext)
	{
		this.toolContext = toolContext;
	}

	@Override
	public void on()
	{

	}

	@Override
	public void off()
	{

	}

}
