package com.curso.diccionario.ficheros;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Optional;

import com.curso.diccionario.Diccionario;
import com.curso.diccionario.SuministradorDeDiccionarios;

public class SuministradorDeDiccionariosDesdeFicheros implements SuministradorDeDiccionarios{

	private final Map<String, Diccionario> cacheDeDiccionarios = new WeakHashMap<>(); // Java 1.2
	
	@Override
	public boolean tienesDiccionario(String idioma) {
		return cacheDeDiccionarios.containsKey(idioma) || Utilidades.existeElFicheroDelDiccionario(idioma);
	}

	@Override
	public Optional<Diccionario> getDiccionario(String idioma) {
		if(idioma == null ) 
			return Optional.empty();
		
		if(!cacheDeDiccionarios.containsKey(idioma)) { // Intento cargar el diccionario
			Optional<Map<String, List<String>>> palabrasConSignificados = Utilidades.leerFicheroDiccionario(idioma);
			if(palabrasConSignificados.isPresent()) {
				cacheDeDiccionarios.put( idioma, new DiccionarioDesdeFichero(idioma, palabrasConSignificados.get()));
			}
		}
		return Optional.ofNullable(cacheDeDiccionarios.get(idioma));
	}

}
