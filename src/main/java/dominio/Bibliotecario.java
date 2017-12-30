package dominio;

import java.util.Calendar;
import java.util.Date;

import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;

public class Bibliotecario {

	public static final String EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE = "El libro no se encuentra disponible. ";
	// Mensaje para avisar que los libros palindromos no se pueden prestar
	public static final String EL_LIBRO_ES_CAPICUA = "los libros palíndromos solo se pueden utilizar en la biblioteca. ";

	private RepositorioLibro repositorioLibro;
	private RepositorioPrestamo repositorioPrestamo;

	public Bibliotecario(RepositorioLibro repositorioLibro, RepositorioPrestamo repositorioPrestamo) {
		this.repositorioLibro = repositorioLibro;
		this.repositorioPrestamo = repositorioPrestamo;

	}

	/**
	 * Metodo para prestar un libro
	 * 
	 * @param isbn:
	 *            identificador unico del libro
	 * @param nombreUsuario:
	 *            nombre del usuario que lo desea prestar (Este usuario no esta
	 *            registrado en la base de datos)
	 */
	public void prestar(String isbn, String nombreUsuario) {

		// Verifica que el libro si este en la bilbioteca
		Libro libroSolicitado = repositorioLibro.obtenerPorIsbn(isbn);
		Prestamo prestamoSolicitado = null;

		// Verifica que el libro no este prestado ya
		if (repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libroSolicitado.getIsbn()) == null) {

			// Si no es palindromo
			if (esIsbnPalindromo(libroSolicitado.getIsbn()) == false) {

				// Si la suma de los digitos del ISBN es >30
				if (valorSumatoriaDigitos(libroSolicitado.getIsbn()) > 30) {

					Date fechaEntregaMaxima = calcularFechaEntregaLibro();

					// Se crea el prestamo con todos los datos
					prestamoSolicitado = new Prestamo(new Date(), libroSolicitado, fechaEntregaMaxima, nombreUsuario);
					repositorioPrestamo.agregar(prestamoSolicitado);

				} else {
					// Se crea el prestamo pero sin la fecha de devolucion=null
					prestamoSolicitado = new Prestamo(new Date(), libroSolicitado, null, nombreUsuario);
					repositorioPrestamo.agregar(prestamoSolicitado);
				}

			} else {

				// Se retorna una excepcion porque el libro no se puede prestar porque es
				// PALINDROMO
				throw new PrestamoException(EL_LIBRO_ES_CAPICUA);
			}
		} else {
			// Se retorna una excepcion porque el libro ya ha sido prestado
			throw new PrestamoException(EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE);
		}

	}

	/**
	 * Metodo para verificar si un libro ya esta prestado y denegar un posible
	 * prestamo nuevamente
	 * 
	 * @param isbn:
	 *            correspondiente al identificador unico del libro
	 * @return true: si el libro ya ha sido prestado y la consulta en la base de
	 *         datos de prestamos devolvio un libro
	 * @return false: si no se encontro el libro en la BD de prestamos
	 */
	public boolean esPrestado(String isbn) {

		Libro libroYaPrestado = repositorioPrestamo.obtenerLibroPrestadoPorIsbn(isbn);

		if (libroYaPrestado != null)
			return true;
		else
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

		for (int i = 1; i < 15; i++) {

			fechaDevolucionDelPrestamo.add(Calendar.DATE, 1);

			// Si el dia es domingo se le suma un dia de mas
			if (fechaDevolucionDelPrestamo.get(Calendar.DAY_OF_WEEK) == 1)
				fechaDevolucionDelPrestamo.add(Calendar.DATE, 1);
		}

		return fechaDevolucionDelPrestamo.getTime();
	}
}
