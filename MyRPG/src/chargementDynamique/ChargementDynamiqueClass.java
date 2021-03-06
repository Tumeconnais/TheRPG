package chargementDynamique;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

// TODO: Auto-generated Javadoc
/**
 * The Class ChargementDynamiqueClass.
 */
public class ChargementDynamiqueClass extends ChargementDynamique {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new chargement dynamique for class File.
	 *
	 * @param fileAccess the file access
	 * @throws MalformedURLException the malformed url exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public ChargementDynamiqueClass(String fileAccess)
			throws MalformedURLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		this.fichier = new File(fileAccess);

	}

	

	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					fichier));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n;
			while ((n = in.read(buf, 0, buf.length)) > 0) {
				out.write(buf, 0, n);
			}
			in.close();
			byte[] data = out.toByteArray();
			Class<?> c = defineClass(null, data, 0, data.length);
			resolveClass(c);
			return c;
		} catch (ClassFormatError | IOException t) {
			throw new ClassNotFoundException("nop");
		}
	}
	
	/**
	 * Init du chargement de classe, a faire a chaque nouvel objet cr�er
	 *
	 * @return true, if successful
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public boolean ChargementClass() throws InstantiationException, IllegalAccessException {
		try {
			this.classCharged = this.loadClass("");
			this.classInstancie = classCharged.newInstance();
			this.listAllMethod();
		} catch (ClassNotFoundException e) {
			return false;
		}

		return true;
	}
	
}
