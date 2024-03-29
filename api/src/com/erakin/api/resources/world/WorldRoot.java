package com.erakin.api.resources.world;

import java.io.File;

import org.diverproject.util.ObjectDescription;

import com.erakin.api.resources.ResourceRoot;

/**
 * <h1>Mundo Ra�z</h1>
 *
 * <p>Essa ra�z � permite a constru��o de novos recursos do tipo mundos (terrenos).
 * Como outra ra�z qualquer � necess�rio o conhecimento de algumas informa��es b�sicas.
 * Para tal uma identifica��o, tamanho de acordo com a unidade de medida usada.
 * E o mais importante de tudo a refer�ncia em File da pasta contendo seus arquivos.</p>
 *
 * @see ResourceRoot
 *
 * @author Andrew
 */

public class WorldRoot extends ResourceRoot<World>
{
	/**
	 * Pr�-fixo para facilitar a identifica��o do mundo.
	 */
	String prefix;

	/**
	 * C�digo de identifica��o do mundo no sistema da engine.
	 */
	int id;

	/**
	 * Quantidade de terrenos que podem ser alocados no eixo da longitude.
	 */
	int width;

	/**
	 * Quantidade de terrenos que podem ser alocados no eixo da latitude.
	 */
	int length;

	/**
	 * Tamanho de cada unidade (c�lula) do terreno.
	 */
	float unit;

	/**
	 * Pasta da onde se encontra os arquivos dos terrenos.
	 */
	File folder;

	/**
	 * Carregador de terrenos a ser usado quando necess�rio.
	 */
	TerrainLoader terrainLoader;

	/**
	 * Dimens�o em que todos os terrenos devem seguir para estar nesse mundo.
	 */
	TerrainDimension terrainDimension;

	/**
	 * Construtor em package para permitir apenas que WorldLoader construa um.
	 * Isso ir� garantir que um Mundo Ra�z inv�lido possa ser criado na engine.
	 * @param filepath caminho do arquivo em disco com os dados do modelo carregado.
	 */

	WorldRoot(String filepath)
	{
		super(filepath);
	}

	@Override
	public World genResource()
	{
		World map = new World(this);
		map.setWorldTerrainLoader(terrainLoader);
		addReference(map);

		return map;
	}

	@Override
	public void toString(ObjectDescription description)
	{
		description.append("id", id);
		description.append("width", width);
		description.append("height", length);
		description.append("folder", folder == null ? null : folder.getPath());
	}
}
