package duplicatedCode.ant.isRebuildRequired;


import org.apache.tools.ant.util.FileUtils;
import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.types.Path;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.AntClassLoader;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.io.FileOutputStream;
import java.io.InputStream;

public abstract class IntermediateGenericDeploymentTool extends GenericDeploymentTool {
	/**
	* File utilities instance for copying jars 
	*/
	protected static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

	/**
	* Helper method invoked by isRebuildRequired to get a ClassLoader for a Jar File passed to it.
	* @param classjar  java.io.File representing jar file to get classes from.
	* @return  the classloader for the jarfile.
	* @throws IOException  if there is a problem.
	*/
	protected ClassLoader getClassLoaderFromJar(File classjar) throws IOException {
		Path lookupPath = new Path(getTask().getProject());
		lookupPath.setLocation(classjar);
		Path classpath = getCombinedClasspath();
		if (classpath != null) {
			lookupPath.append(classpath);
		}
		return getTask().getProject().createClassLoader(lookupPath);
	}

	protected boolean isRebuildRequiredExtracted(File weblogicJarFile, File genericJarFile, String arg0, String arg1,
			String arg2) {
		boolean rebuild = false;
		JarFile genericJar = null;
		JarFile wlJar = null;
		File newWLJarFile = null;
		JarOutputStream newJarStream = null;
		ClassLoader genericLoader = null;
		try {
			log(arg0 + weblogicJarFile.getName(), Project.MSG_VERBOSE);
			if (genericJarFile.exists() && genericJarFile.isFile() && weblogicJarFile.exists()
					&& weblogicJarFile.isFile()) {
				genericJar = new JarFile(genericJarFile);
				wlJar = new JarFile(weblogicJarFile);
				Hashtable genericEntries = new Hashtable();
				Hashtable wlEntries = new Hashtable();
				Hashtable replaceEntries = new Hashtable();
				for (Enumeration e = genericJar.entries(); e.hasMoreElements();) {
					JarEntry je = (JarEntry) e.nextElement();
					genericEntries.put(je.getName().replace('\\', '/'), je);
				}
				for (Enumeration e = wlJar.entries(); e.hasMoreElements();) {
					JarEntry je = (JarEntry) e.nextElement();
					wlEntries.put(je.getName(), je);
				}
				genericLoader = getClassLoaderFromJar(genericJarFile);
				for (Enumeration e = genericEntries.keys(); e.hasMoreElements();) {
					String filepath = (String) e.nextElement();
					if (wlEntries.containsKey(filepath)) {
						JarEntry genericEntry = (JarEntry) genericEntries.get(filepath);
						JarEntry wlEntry = (JarEntry) wlEntries.get(filepath);
						if ((genericEntry.getCrc() != wlEntry.getCrc())
								|| (genericEntry.getSize() != wlEntry.getSize())) {
							if (genericEntry.getName().endsWith(".class")) {
								String classname = genericEntry.getName().replace(File.separatorChar, '.').replace('/',
										'.');
								classname = classname.substring(0, classname.lastIndexOf(".class"));
								Class genclass = genericLoader.loadClass(classname);
								if (genclass.isInterface()) {
									log("Interface " + genclass.getName() + " has changed", Project.MSG_VERBOSE);
									rebuild = true;
									break;
								} else {
									replaceEntries.put(filepath, genericEntry);
								}
							} else {
								if (!genericEntry.getName().equals("META-INF/MANIFEST.MF")) {
									log("Non class file " + genericEntry.getName() + " has changed",
											Project.MSG_VERBOSE);
									rebuild = true;
									break;
								}
							}
						}
					} else {
						log("File " + filepath + arg1, Project.MSG_VERBOSE);
						rebuild = true;
						break;
					}
				}
				if (!rebuild) {
					log("No rebuild needed - updating jar", Project.MSG_VERBOSE);
					newWLJarFile = new File(weblogicJarFile.getAbsolutePath() + ".temp");
					if (newWLJarFile.exists()) {
						newWLJarFile.delete();
					}
					newJarStream = new JarOutputStream(new FileOutputStream(newWLJarFile));
					newJarStream.setLevel(0);
					for (Enumeration e = wlEntries.elements(); e.hasMoreElements();) {
						byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
						int bytesRead;
						InputStream is;
						JarEntry je = (JarEntry) e.nextElement();
						if (je.getCompressedSize() == -1 || je.getCompressedSize() == je.getSize()) {
							newJarStream.setLevel(0);
						} else {
							newJarStream.setLevel(JAR_COMPRESS_LEVEL);
						}
						if (replaceEntries.containsKey(je.getName())) {
							log("Updating Bean class from generic Jar " + je.getName(), Project.MSG_VERBOSE);
							je = (JarEntry) replaceEntries.get(je.getName());
							is = genericJar.getInputStream(je);
						} else {
							is = wlJar.getInputStream(je);
						}
						newJarStream.putNextEntry(new JarEntry(je.getName()));
						while ((bytesRead = is.read(buffer)) != -1) {
							newJarStream.write(buffer, 0, bytesRead);
						}
						is.close();
					}
				} else {
					log(arg2 + "interface or XML", Project.MSG_VERBOSE);
				}
			} else {
				rebuild = true;
			}
		} catch (ClassNotFoundException cnfe) {
			String cnfmsg = "ClassNotFoundException while processing ejb-jar file" + ". Details: " + cnfe.getMessage();
			throw new BuildException(cnfmsg, cnfe);
		} catch (IOException ioe) {
			String msg = "IOException while processing ejb-jar file " + ". Details: " + ioe.getMessage();
			throw new BuildException(msg, ioe);
		} finally {
			if (genericJar != null) {
				try {
					genericJar.close();
				} catch (IOException closeException) {
				}
			}
			if (wlJar != null) {
				try {
					wlJar.close();
				} catch (IOException closeException) {
				}
			}
			if (newJarStream != null) {
				try {
					newJarStream.close();
				} catch (IOException closeException) {
				}
				try {
					FILE_UTILS.rename(newWLJarFile, weblogicJarFile);
				} catch (IOException renameException) {
					log(renameException.getMessage(), Project.MSG_WARN);
					rebuild = true;
				}
			}
			if (genericLoader != null && genericLoader instanceof AntClassLoader) {
				AntClassLoader loader = (AntClassLoader) genericLoader;
				loader.cleanup();
			}
		}
		return rebuild;
	}
}