package com.erakin.engine.input;

import org.diverproject.jni.input.KeyEvent;
import org.diverproject.jni.input.KeyboardDispatcher;
import org.diverproject.jni.input.enums.EnumKEY;
import org.diverproject.util.collection.List;
import org.diverproject.util.collection.abstraction.DynamicList;
import org.diverproject.util.lang.IntUtil;
import org.diverproject.util.service.ServiceException;

import com.erakin.engine.Input;
import com.erakin.engine.Keyboard;

/**
 * <h1>Teclado Virtual</h1>
 *
 * <p>JNI Input � uma das bibliotecas utilizadas pelo engine, permite detectar a��es para mouse e teclado.
 * Nesse caso ser� considerado apenas as funcionalidades de teclado na classe, que funciona como despachante.</p>
 * 
 * <p>A biblioteca JNI dever� detectar as a��es do teclado e repassar para o sistema de entradas e repassar.
 * Quando repassado, o sistema ir� verificar qual o despachante de teclado usado e repassar para este,
 * determinar o que ser� feito com o evento que foi constru�do a partir da detec��o da a��o acionada.</p>
 *
 * <p>A finalidade do despachante de teclado � receber os eventos detectados e determinar o que fazer.
 * Para cada evento de tecla detectado h� um tipo de a��o no mesmo e alguns atributos informativos sobre.</p>
 *
 * <p>Essa classe teclado ir� despachar adequadamente cada evento para suas a��es respectivas.
 * As a��es que poder�o ser encontradas pelos eventos de teclado s�o de tecla pressionada, clicada ou solta.
 * Todos os eventos ir�o possuir informa��es sobre qual a tecla que foi usada na a��o e alguns outros afins.</p>
 *
 * <p>Tem como alta prioridade no sistema do engine, pois garante metade da intera��o do jogador com o jogo.
 * Pode ser usado para identificar qual a��o usar ou funcionalidade que deve ser ativada de acordo com o que
 * foi programado para tal, como por exemplo quando a tecla Escape for pressionada exibir o menu principal.</p>
 *
 * @see Keyboard
 * @see KeyboardDispatcher
 * @see KeyEvent
 * @see KeyTypedListener
 * @see KeyPressedListener
 * @see KeyReleasedListener
 * @see KeyListener
 * @see KeyAction
 * @see EnumKEY
 *
 * @author Andrew Mello
 */

public class VirtualKeyboard extends Keyboard implements Input, KeyboardDispatcher
{
	/**
	 * C�digo do estado de servi�o que o teclado se encontrada.
	 */
	private int state;

	/**
	 * Lista contendo as escutas quando teclas detectarem a��es ou caracteres.
	 */
	private List<KeyTypedListener> typedListeners;

	/**
	 * Lista contendo as escutas em quanto teclas estiverem pressionadas.
	 */
	private List<KeyPressedListener> pressedListeners;

	/**
	 * Lista contendo as escutas quando teclas forem liberadas.
	 */
	private List<KeyReleasedListener> releasedListeners;

	/**
	 * Vetor que ir� identificar as teclas que est�o pressionadas.
	 */
	private boolean keyPresseds[];

	/**
	 * Fila com as teclas que foram recentemente clicadas.
	 */
	private List<KeyAction> keyClickeds;

	@Override
	public void update(long delay)
	{
		while (keyClickeds.size() > 0)
			while (keyClickeds.get(0).isOver())
				keyClickeds.remove(0);
	}

	@Override
	public void start() throws ServiceException
	{
		state = SERVICE_STARTED;

		if (typedListeners == null)		typedListeners = new DynamicList<>();
		if (pressedListeners == null)	pressedListeners = new DynamicList<>();
		if (releasedListeners == null)	releasedListeners = new DynamicList<>();
		if (keyPresseds == null)		keyPresseds = new boolean[EnumKEY.KEY_MAX];
		if (keyClickeds == null)		keyClickeds = new DynamicList<>();

		state = SERVICE_RUNNING;
	}

	@Override
	public void terminate() throws ServiceException
	{
		typedListeners.clear();
		pressedListeners.clear();
		releasedListeners.clear();

		state = SERVICE_TERMINATED;
	}

	@Override
	public void interrupted() throws ServiceException
	{
		state = SERVICE_STOPED;
	}

	@Override
	public int getState()
	{
		return state;
	}

	@Override
	public String getIdentificator()
	{
		return "Erakin.Keyboard";
	}

	@Override
	public void dispatch(KeyEvent event)
	{
		if (getState() != SERVICE_RUNNING)
			return;

		switch (event.getType())
		{
			case KeyEvent.KT_TYPED: dispatchTypedKey(event); break;
			case KeyEvent.KT_PRESSED: dispatchPressedKey(event); break;
			case KeyEvent.KT_RELEASED: dispatchRelasedKey(event); break;
		}
	}

	/**
	 * Procedimento chamado assim que um evento de teclado do tipo typed � detectado.
	 * Eventos typed s�o desencadeados sempre que um caracter ou a��o de tecla ocorrer.
	 * @param event refer�ncia do evento contendo as informa��es da tecla digitada.
	 */

	private void dispatchTypedKey(KeyEvent event)
	{
		for (KeyTypedListener listener : typedListeners)
			listener.keyTyped(event);
	}

	/**
	 * Procedimento chamado assim que um evento de teclado do tipo pressed � detectado.
	 * Eventos typed s�o desencadeados somente em quanto uma tecla estiver pressionada.
	 * @param event refer�ncia do evento contendo as informa��es da tecla pressionada.
	 */

	private void dispatchPressedKey(KeyEvent event)
	{
		keyPresseds[event.getKey()] = true;

		for (KeyPressedListener listener : pressedListeners)
			listener.keyPressed(event);
	}

	/**
	 * Procedimento chamado assim que um evento de teclado do tipo released � detectado.
	 * Eventos typed s�o desencadeados somente uma vez ap�s a tecla para de ser pressionada.
	 * @param event refer�ncia do evento contendo as informa��es da tecla liberada.
	 */

	private void dispatchRelasedKey(KeyEvent event)
	{
		keyPresseds[event.getKey()] = false;

		for (KeyReleasedListener listener : releasedListeners)
			listener.keyReleased(event);
	}

	@Override
	public boolean wasClicked(int... key)
	{
		if (getState() != SERVICE_RUNNING)
			return false;

		for (int i = 0; i < key.length; i++)
			for (KeyAction action : keyClickeds)
				if (!action.isKey(key[i]))
					return false;

		return true;
	}

	@Override
	public boolean isPressed(int... key)
	{
		if (getState() != SERVICE_RUNNING)
			return false;

		for (int i = 0; i < key.length; i++)
			if (IntUtil.interval(key[i], 0, keyPresseds.length - 1))
				if (!keyPresseds[key[i]])
					return false;

		return true;
	}

	@Override
	public void addListener(KeyListener listener)
	{
		if (getState() != SERVICE_RUNNING)
			return;

		addTypedListener(listener);
		addPressedListener(listener);
		addReleasedListener(listener);
	}

	@Override
	public void removeListener(KeyListener listener)
	{
		if (getState() != SERVICE_RUNNING)
			return;

		removeTypedListener(listener);
		removePressedListener(listener);
		removeReleasedListener(listener);
	}

	@Override
	public void addTypedListener(KeyTypedListener listener)
	{
		if (getState() != SERVICE_RUNNING)
			return;

		if (!typedListeners.contains(listener))
			typedListeners.add(listener);
	}

	@Override
	public void removeTypedListener(KeyTypedListener listener)
	{
		if (getState() != SERVICE_RUNNING)
			return;

		typedListeners.remove(listener);
	}

	@Override
	public void addPressedListener(KeyPressedListener listener)
	{
		if (getState() != SERVICE_RUNNING)
			return;

		if (!pressedListeners.contains(listener))
			pressedListeners.add(listener);
	}

	@Override
	public void removePressedListener(KeyPressedListener listener)
	{
		if (getState() != SERVICE_RUNNING)
			return;

		pressedListeners.remove(listener);
	}

	@Override
	public void addReleasedListener(KeyReleasedListener listener)
	{
		if (getState() != SERVICE_RUNNING)
			return;

		if (!releasedListeners.contains(listener))
			releasedListeners.add(listener);
	}

	@Override
	public void removeReleasedListener(KeyReleasedListener listener)
	{
		if (getState() != SERVICE_RUNNING)
			return;

		releasedListeners.remove(listener);
	}
}
