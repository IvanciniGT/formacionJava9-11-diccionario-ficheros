module diccionarioFicheros {
	
	requires diccionarioApi;
	
	provides com.curso.diccionario.SuministradorDeDiccionarios
	    with com.curso.diccionario.ficheros.SuministradorDeDiccionariosDesdeFicheros;
	
}