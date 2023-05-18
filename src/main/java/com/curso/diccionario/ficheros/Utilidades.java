package com.curso.diccionario.ficheros;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

public interface Utilidades {

	static String normalizar(String palabra) { // J1.8
		return palabra.toLowerCase();
	}
	
   static int puntuacionLevenshtein(String str1, String str2) {
       return computeLevenshteinDistance(str1.toCharArray(),
                                         str2.toCharArray());
   }

   private static int minimum(int a, int b, int c) { // J1.9
        return Math.min(a, Math.min(b, c));
   }

   private static int computeLevenshteinDistance(char [] str1, char [] str2) {
       int [][]distance = new int[str1.length+1][str2.length+1];

       for(int i=0;i<=str1.length;i++){
               distance[i][0]=i;
       }
       for(int j=0;j<=str2.length;j++){
               distance[0][j]=j;
       }
       for(int i=1;i<=str1.length;i++){
           for(int j=1;j<=str2.length;j++){ 
                 distance[i][j]= minimum(distance[i-1][j]+1,
                                       distance[i][j-1]+1,
                                       distance[i-1][j-1]+
                                       ((str1[i-1]==str2[j-1])?0:1));
           }
       }
       return distance[str1.length][str2.length];
       
   }

	static boolean existeElFicheroDelDiccionario(String idioma) {
		return rutaFicheroDelDiccionario(idioma).isPresent();
	}

	private static Optional<URL> rutaFicheroDelDiccionario(String idioma) {
		return Optional.ofNullable( Utilidades.class.getClassLoader().getResource("diccionario."+idioma+".txt")); // Busca en el classpath
	}

	static Optional<Map<String, List<String>>> leerFicheroDiccionario(String idioma) {
		Optional<URL> urlDelFichero = rutaFicheroDelDiccionario( idioma ) ;
		if(urlDelFichero.isPresent()) {
			try {
				String contenido = Files.readString(Path.of(urlDelFichero.get().toURI())); // Java 11 Files.readString y Files.writeString
				var palabras=contenido.lines()	// Java 11
						 .filter(    linea -> !linea.isBlank()       ) // Java 11
						 .map(	     linea -> linea.split("=")       )
						 .collect(   Collectors.toMap(
								 	// Funcion que transforma el dato que recibo a la clave						> PALABRA
								 	array -> normalizar(array[0])
								 ,
								 	// Función que transforma el dato que recibo a el value asociado a la clave > SIGNIFICADOS
								 	array -> Arrays.asList(array[1].split("\\|") )
								 , 	// Función para fusionar las definiciones de palabras que aparezcan repetidas
								 	(significados1, significados2) -> {
								 		ArrayList<String> significadosFusionados = new ArrayList<>();
								 		significadosFusionados.addAll(significados1);
								 		significadosFusionados.addAll(significados2);
								 		return significadosFusionados;
								 	} 
								 )
						);
				return Optional.of( palabras );
			} catch (Exception e) {
				System.err.println("Error al leer el fichero del diccionario"); // No se puede quedar así... Hay que sacarlo a un Logger
				e.printStackTrace();
			}
		}
		
		return Optional.empty();
	}
}





