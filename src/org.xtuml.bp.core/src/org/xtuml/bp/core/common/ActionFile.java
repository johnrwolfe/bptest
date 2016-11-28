// ========================================================================
//
//File: ActionFile.java
//
//========================================================================
//
package org.xtuml.bp.core.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.xtuml.bp.core.Actiondialect_c;
import org.xtuml.bp.core.CorePlugin;
import org.xtuml.bp.core.Ooaofooa;

public class ActionFile {
	
	private HashMap<String,IFile> fileMap;
	
	public ActionFile( String componentFileName ) {
                String[] dialects = getAvailableDialects();
		fileMap = new HashMap<String,IFile>();
		for ( int i = 0; i < dialects.length; i++ ) {
			IPath actionFilePath = getPathFromComponent(componentFileName, dialects[i]);
			fileMap.put(dialects[i], ResourcesPlugin.getWorkspace().getRoot().getFile(actionFilePath));
		}
	}
	
	public ActionFile( IPath componentPath ) {
                String[] dialects = getAvailableDialects();
		fileMap = new HashMap<String,IFile>();
		for ( int i = 0; i < dialects.length; i++ ) {
			IPath actionFilePath = getPathFromComponent(componentPath, dialects[i]);
			fileMap.put(dialects[i], ResourcesPlugin.getWorkspace().getRoot().getFile(actionFilePath));
		}
	}
	
	public void updateFiles( IPath newPath ) {
                String[] dialects = getAvailableDialects();
		fileMap = new HashMap<String,IFile>();
		for ( int i = 0; i < dialects.length; i++ ) {
			IPath actionFilePath = getPathFromComponent(newPath, dialects[i]);
			fileMap.put(dialects[i], ResourcesPlugin.getWorkspace().getRoot().getFile(actionFilePath));
		}
	}
	
	// get the dialect for this action file manager. This will check to see if actions exist
	// for one dialect, if not it will go to the default dialect
	public String getDialect() {
            String dialect = "";
            Iterator<Map.Entry<String,IFile>> it = fileMap.entrySet().iterator();
            while ( it.hasNext() ) {
                Map.Entry<String,IFile> entry = (Map.Entry<String,IFile>)it.next();
                IFile file = entry.getValue();
                if ( file != null && file.exists() ) {
                    if ( "" == dialect ) {
                        dialect = entry.getKey();
                    }
                    else {
	                return getDefaultDialect(); // more than one action file exists
                    }
                }
            }
            if ( dialect == "" ) {
	        return getDefaultDialect(); // no action file exists
            }
            else {
                return dialect.toLowerCase();
            }
	}
	
	// get the file for this action file manager. This will check to see if actions exist
	// for one dialect, if not it will go to the default dialect
	public IFile getFile() {
		return fileMap.get(getDialect());
	}

        // get all the files for this action file manager
        public IFile[] getAllFiles() {
            return fileMap.values().toArray(new IFile[0]);
        }

        // get all available dialects 
        public static String[] getAvailableDialects() {
            List<String> dialects = new ArrayList<String>();
            Class actionDialects = Actiondialect_c.class;
            try {
                Field[] dialectFields = actionDialects.getFields();
                for ( int i = 0; i < dialectFields.length; i++ ) {
	            if ( !dialectFields[i].getName().equals("OOA_UNINITIALIZED_ENUM") ) {
                        dialects.add( dialectFields[i].getName().toLowerCase() );
                    }
                }
            } catch ( SecurityException e ) {
                System.out.println( e );
            }

            return dialects.toArray(new String[0]);
        }

        // get enumerator codes of all available dialects 
        public static int[] getAvailableDialectCodes() {
            List<Integer> dialects = new ArrayList<Integer>();
            Class actionDialects = Actiondialect_c.class;
            try {
                Field[] dialectFields = actionDialects.getFields();
                for ( int i = 0; i < dialectFields.length; i++ ) {
	            if ( !dialectFields[i].getName().equals("OOA_UNINITIALIZED_ENUM") ) {
                        dialects.add( dialectFields[i].getInt(null) );
                    }
                }
            } catch ( SecurityException e ) {
                System.out.println( e );
            } catch ( IllegalAccessException e ) {
                System.out.println( e );
            } catch ( IllegalArgumentException e ) {
                System.out.println( e );
            } catch ( NullPointerException e ) {
                System.out.println( e );
            } catch ( ExceptionInInitializerError e ) {
                System.out.println( e );
            }

            return dialects.stream().mapToInt(i->i).toArray();
        }

        // get the file for this action file manager with a specific dialect
	public IFile getFile( String dialect ) {
		return fileMap.get(dialect);
	}

        // get the file for this action file manager with a specific dialect code
	public IFile getFile( int dialect ) {
                int[] dialectCodes = getAvailableDialectCodes();
                String[] dialects = getAvailableDialects();
                for ( int i = 0; i < dialectCodes.length; i++ ) {
                    if ( dialect == dialectCodes[i] ) {
                        return getFile( dialects[i] );
                    }
                }
		return null;
	}
	
	public static String getDefaultDialect() {
                String[] dialects = getAvailableDialects();
                IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
                String prefDialect = store.getString(BridgePointPreferencesStore.DEFAULT_ACTION_LANGUAGE_DIALECT).toLowerCase();
                if ( Arrays.asList( dialects ).contains( prefDialect ) ) {
                    return prefDialect;
                }
                else if ( dialects.length > 0 ) {
                    return dialects[0];     // if the preference does not match a dialect, use the first dialect in the list
                }
                else {
                    return "";
                }
	}
	
	// get the action file path from the component file path
	public IPath getPathFromComponent( IPath path ) {
		return getPathFromComponent( path, getDialect() );
	}

	public static IPath getPathFromComponent( IPath path, String tag ) {
		if ( null != path ) {
			return path.removeFileExtension().addFileExtension(tag);
		}
		else {
			return null;
		}
	}

	public static IPath getPathFromComponent( IFile file, String tag ) {
		if ( null != file ) {
			return getPathFromComponent(file.getFullPath(), tag);
		}
		else {
			return null;
		}
	}

	public static IPath getPathFromComponent( String fileName, String tag ) {
		return getPathFromComponent(new Path(fileName), tag);
	}
	
	// get the component file path from the action file path
	public static IPath getComponentPath( IPath path ) {
		if ( null != path ) {
			return path.removeFileExtension().addFileExtension(Ooaofooa.MODELS_EXT);
		}
		else {
			return null;
		}
	}

	public static IPath getComponentPath( IFile file ) {
		if ( null != file ) {
			return getComponentPath(file.getFullPath());
		}
		else {
			return null;
		}
	}

	public static IPath getComponentPath( String fileName ) {
		return getComponentPath(new Path(fileName));
	}

}
