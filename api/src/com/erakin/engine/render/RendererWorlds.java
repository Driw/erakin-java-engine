package com.erakin.engine.render;

import com.erakin.api.lwjgl.math.Vector3i;
import com.erakin.api.render.WorldRender;

/**
 * <h1>Renderizador para Mundos</h1>
 *
 * <p>Essa interface � usada no gerenciador de renderiza��o com as funcionalidades padr�es que deve possuir.
 * Assim � poss�vel fazer com que as renderiza��es possam atuar de formas diferentes para cada aplica��o.
 * Podendo ainda melhorar a performance conforme o desenvolvedor deseja ou conforme a aplica��o funcione.</p>
 *
 * <p>Possui funcionalidades bem b�sicas relacionadas sempre a mundos, somente mundos ser�o trabalhados.
 * Permite definir um �nico mundo por vez para que possa ser renderizado na tela como tamb�m a posi��o central
 * em rela��o ao espa�o do mundo e a dist�ncia de vis�o que vai determinar quais terrenos poder�o ser vistos.</p>
 *
 * @see RendererManager
 * @see WorldRender
 *
 * @author Andrew Mello
 */

public interface RendererWorlds extends RendererManager
{
	/**
	 * Ir� indicar qual o mundo que ser� usado pelo renderizador para ser renderizado na tela.
	 * Para que o renderizador funcione, � necess�rio definir um novo mundo para se renderizar.
	 * @param world refer�ncia do mundo renderiz�vel do qual ser� renderizado pelo renderizador de mundos.
	 */

	void setWorld(WorldRender world);

	/**
	 * Ao definir a posi��o de renderiza��o ser� indicado a partir de que ponto ser� feito a renderiza��o.
	 * A nova posi��o ser� o centro da renderiza��o, tenho <b>n</b> de dist�ncia do seu ponto renderizado.
	 * @param position vetor contendo as coordenadas para renderiza��o do novo mundo nos eixos X, Y e Z.
	 */

	void setRenderPosition(Vector3i position);

	/**
	 * Permite definir qual ser� o alcance de vis�o durante a renderiza��o do mundo.
	 * Isso ir� implicar no qual distante o jogador poder� ver ao seu redor.
	 * @param distance dist�ncia do qual poder� ser visualizada na tela.
	 */

	void setRenderRange(int distance);
}
