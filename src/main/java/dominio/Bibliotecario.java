package dominio;

import java.util.Calendar;
import java.util.Date;

import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;

public class Bibliotecario {

	public static final String EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE = "El libro no se encuentra disponible";
	public static final String EL_LIBRO_ES_CAPICUA = "los libros palíndromos solo se pueden utilizar en la biblioteca";

	private RepositorioLibro repositorioLibro;
	private RepositorioPrestamo repositorioPrestamo;

	public Bibliotecario(RepositorioLibro repositorioLibro, RepositorioPrestamo repositorioPrestamo) {
		this.repositorioLibro = repositorioLibro;
		this.repositorioPrestamo = repositorioPrestamo;

	}

	public void prestar(String isbn, String nombreUsuario) {

		Libro libroSolicitado = repositorioLibro.obtenerPorIsbn(isbn);
		Prestamo prestamoSolicitado = null;

		// Si no es palindromo
		if (esIsbnPalindromo(libroSolicitado.getIsbn()) == false) {

			// Si la suma de los digitos del ISBN es >30
			if (valorSumatoriaDigitos(libroSolicitado.getIsbn()) > 30) {

				Date fechaEntregaMaxima = calcularFechaEntregaLibro();

				prestamoSolicitado = new Prestamo(new Date(), libroSolicitado, fechaEntregaMaxima, nombreUsuario);
				repositorioPrestamo.agregar(prestamoSolicitado);

			} else {
				prestamoSolicitado = new Prestamo(new Date(), libroSolicitado, null, nombreUsuario);
				repositorioPrestamo.agregar(prestamoSolicitado);
			}

		} else {

			throw new PrestamoException(EL_LIBRO_ES_CAPICUA);
		}

	}

	public boolean esPrestado(String isbn) {

		// Método pendiente por implementar
		return false;
	}

	/**
	 * Metodo para verificar si el ISBN de un libro es palindromo
	 * 
	 * @param ISBN:
	 *            codigo del libro que se debe verificar
	 * @return true si es palindromo y false en caso contrario
	 */
	public boolean esIsbnPalindromo(String isbn) {

		StringBuilder isbnInvertido = new StringBuilder(isbn);
		isbnInvertido = isbnInvertido.reverse();

		return isbn.equalsIgnoreCase(isbnInvertido.toString());
	}

	/**
	 * Metodo para encontrar el resultado equivalente a la suma de todos los digitos
	 * numericos que posee el ISBN del libro a prestar
	 * 
	 * @param isbn
	 * @return resultadoSuma: corresponde a la sumatoria de todos los digitos
	 *         numericos del ISBN
	 */
	public static int valorSumatoriaDigitos(String isbn) {

		int resultadoSuma = 0;
		char[] arregloDeDigitos = isbn.toCharArray();

		for (char digitoNumerico : arregloDeDigitos) {
			if (Character.isDigit(digitoNumerico)) {
				resultadoSuma += Integer.parseInt(Character.toString(digitoNumerico));
			}
		}

		return resultadoSuma;
	}

	/**
	 * Metodo para calcular la fecha en la que debe ser devuelto un libro prestado
	 * Precondicion: Que la suma de los digitos del ISBN sea mayor a 30
	 * Postcondicion: Si la fecha de devolucion resulta ser un domingo se le suma un
	 * 1 dia
	 * 
	 * @category Prestamo
	 * @return fechaDevolucionDelPrestamo: Fecha en la que debe ser devuelto el
	 *         libro
	 */
	public Date calcularFechaEntregaLibro() {

		Calendar fechaDevolucionDelPrestamo = Calendar.getInstance();
		fechaDevolucionDelPrestamo.add(Calendar.DATE, 15);

		// Si el dia es domingo se le suma un dia mas
		if (fechaDevolucionDelPrestamo.DAY_OF_WEEK == 7) {
			fechaDevolucionDelPrestamo.add(Calendar.DATE, 1);
			return fechaDevolucionDelPrestamo.getTime();
		} else {
			// No es domingo, entonces retornar la fecha de entrega calculada inicialmente
			return fechaDevolucionDelPrestamo.getTime();
		}
	}
}
