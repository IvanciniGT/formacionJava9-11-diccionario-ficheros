package com.curso.diccionario.ficheros;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.curso.diccionario.Diccionario;

public class DiccionarioDesdeFichero implements Diccionario{
	
	private String idioma;
	private Map<String, List<String>> palabrasConSignificados;

	public DiccionarioDesdeFichero(String idioma, Map<String, List<String>> palabrasConSignificados) {
		this.idioma = idioma;
		this.palabrasConSignificados = palabrasConSignificados;
	}
	
	@Override
	public String getIdioma() {
		return idioma;
	}

	@Override
	public boolean existe(String palabra) {
		return palabrasConSignificados.containsKey(Utilidades.normalizar(palabra));
	}

	@Override
	public Optional<List<String>> getSignificados(String palabra) {
		return (palabra == null)?																			// Si me pasan como palara null
			 Optional.empty():																	// Devuelvo un Optional vacio
		     Optional.ofNullable(palabrasConSignificados.get(Utilidades.normalizar(palabra)));	// Si no... devolvere un Optional con contenido si se encuentar en mi Mapa de palabras
	}

	@Override
	public List<String> getAlternativas(String palabraSuministrada) {
		final String palabraObjetivo = Utilidades.normalizar(palabraSuministrada);
		return palabrasConSignificados.keySet()																		// Sacar las palabras que existen en el diccionario
					   .parallelStream()																			// Para cada palabra (usando CPUs por un tubo)
					   .filter( palabra     -> Math.abs( palabra.length() - palabraObjetivo.length() ) <= Configuracion.DISTANCIA_MAXIMA_ADMISIBLE ) // Paso de palabras muchos más largas o cortas que la objetivo
					   .map(    palabra     -> new Sugerencia(palabra, Utilidades.puntuacionLevenshtein(palabra, palabraObjetivo))) // Añadir la distancia de Levenshtein
					   .filter( sugerencia  -> (sugerencia.puntuacion <= Configuracion.DISTANCIA_MAXIMA_ADMISIBLE)) // Me quedo solo con palabras que se parezcan 
					   .sorted( Comparator.comparing( sugerencia -> sugerencia.puntuacion) )						// Ordeno por puntuaciones
					   .limit(  Configuracion.NUMERO_MAXIMO_SUGERENCIAS )											// Me quedo solo con las mejores
					   .map(    sugerencia  -> sugerencia.palabra )													// Me quedo solo con las palabras
					   .collect( Collectors.toList() );																// Lo convierto a una lista de palabras (Strings)
						// Aqui es donde se ejecuta todo lo anterior.
	}
	

	private static class Sugerencia {
	    String palabra;  
	    int puntuacion;	 
	    
	    public Sugerencia(String palabra,int puntuacion){
	        this.palabra=palabra;
	        this.puntuacion=puntuacion;
	    }
	}

}
