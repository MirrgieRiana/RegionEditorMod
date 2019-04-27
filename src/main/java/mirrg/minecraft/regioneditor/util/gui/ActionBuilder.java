package mirrg.minecraft.regioneditor.util.gui;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

public class ActionBuilder<A extends ActionBase>
{

	private final A action;

	public ActionBuilder(A action)
	{
		this.action = action;
	}

	public ActionBuilder<A> value(String key, Object value)
	{
		action.putValue(key, value);
		return this;
	}

	private boolean keyStroke = false;
	private KeyStroke keyStrokeMain;
	private KeyStroke[] keyStrokes;

	public ActionBuilder<A> keyStroke(KeyStroke keyStrokeMain, KeyStroke... keyStrokes)
	{
		keyStroke = true;
		this.keyStrokeMain = keyStrokeMain;
		this.keyStrokes = keyStrokes;
		return this;
	}

	public A register(InputMap inputMap, ActionMap actionMap)
	{
		if (keyStroke) {
			action.putValue(Action.ACCELERATOR_KEY, keyStrokeMain);
			inputMap.put(keyStrokeMain, action);
			for (KeyStroke keyStroke : keyStrokes) {
				inputMap.put(keyStroke, action);
			}
		}

		actionMap.put(action, action);
		action.register();
		return action;
	}

}
