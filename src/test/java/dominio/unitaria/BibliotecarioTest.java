package dominio.unitaria;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import org.junit.Test;

import dominio.Bibliotecario;
import dominio.Libro;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;
import testdatabuilder.LibroTestDataBuilder;

public class BibliotecarioTest {

	@Test
	public void esPrestadoTest() {

		// arrange
		LibroTestDataBuilder libroTestDataBuilder = new LibroTestDataBuilder();

		Libro libro = libroTestDataBuilder.build();

		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);

		when(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn())).thenReturn(libro);

		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);

		// act
		boolean esPrestado = bibliotecario.esPrestado(libro.getIsbn());

		// assert
		assertTrue(esPrestado);
	}

	@Test
	public void libroNoPrestadoTest() {

		// arrange
		LibroTestDataBuilder libroTestDataBuilder = new LibroTestDataBuilder();

		Libro libro = libroTestDataBuilder.build();

		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);

		when(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn())).thenReturn(null);

		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);

		// act
		boolean esPrestado = bibliotecario.esPrestado(libro.getIsbn());

		// assert
		assertFalse(esPrestado);
	}

	/**
	 * Prueba para verificar si es ISBN de un libro es palindromo o capicua La
	 * prueba se ejecuta correctamente si el ISBN es palindromo.
	 */
	@Test
	public void esIsbnPalindromo() {

		// arrange
		LibroTestDataBuilder libroTestDataBuilder = new LibroTestDataBuilder();

		Libro libro = libroTestDataBuilder.build();

		// act
		StringBuilder isbnInvertido = new StringBuilder(libro.getIsbn());
		isbnInvertido = isbnInvertido.reverse();

		// assert para el ISBN de prueba por defecto ---> ISBN=1234
		// Arroja como resultado un false
		assertFalse(libro.getIsbn().equalsIgnoreCase(isbnInvertido.toString()));
	}

	/**
	 * Metodo para encontrar el resultado equivalente a la suma de todos los digitos
	 * numericos que posee el ISBN del libro a prestar.
	 */

	@Test
	public void valorSumatoriaDigitos() {

		// arrange
		LibroTestDataBuilder libroTestDataBuilder = new LibroTestDataBuilder();

		Libro libro = libroTestDataBuilder.build();

		// act
		int resultadoSuma = 0;
		char[] arregloDeDigitos = libro.getIsbn().toCharArray();

		for (char digitoNumerico : arregloDeDigitos) {
			if (Character.isDigit(digitoNumerico)) {
				resultadoSuma += Integer.parseInt(Character.toString(digitoNumerico));
			}
		}

		// Assert
		assertNotEquals(0, resultadoSuma);
	}

	/**
	 * Metodo para calcular la fecha en la que debe ser devuelto un libro prestado
	 * Precondicion: Que la suma de los digitos del ISBN sea mayor a 30
	 * Postcondicion: Si la fecha de devolucion resulta ser un domingo se le suma un
	 * 1 dia
	 * 
	 * @see Este metodo no se implemento en el paquete de pruebas de integracion
	 *      porque no implica la ejecucion de una funcion basica de base de datos,
	 *      esta en medio.
	 * @since El primer dia de la semana es el domingo
	 */

	@Test
	public void calcularFechaEntregaLibro() {

		// arrange
		Calendar fechaDevolucionDelPrestamo = Calendar.getInstance();

		// act
		// ciclo para ir añadiendo iterativamente los dias a la fecha de solicitud del
		// prestamo
		for (int i = 1; i < 15; i++) {

			fechaDevolucionDelPrestamo.add(Calendar.DATE, 1);

			// Si el dia es domingo se le suma un dia de mas
			if (fechaDevolucionDelPrestamo.get(Calendar.DAY_OF_WEEK) == 1)
				fechaDevolucionDelPrestamo.add(Calendar.DATE, 1);
		}

		// assert
		// Es error si la fecha resultante es un domingo
		assertNotEquals(1, fechaDevolucionDelPrestamo.get(Calendar.DAY_OF_WEEK));
	}
}
