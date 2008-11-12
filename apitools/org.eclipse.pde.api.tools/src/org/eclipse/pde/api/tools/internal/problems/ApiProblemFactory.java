/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.internal.problems;

import java.text.ChoiceFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.pde.api.tools.internal.builder.BuilderMessages;
import org.eclipse.pde.api.tools.internal.provisional.comparator.IDelta;
import org.eclipse.pde.api.tools.internal.provisional.problems.IApiProblem;
import org.eclipse.pde.api.tools.internal.provisional.problems.IApiProblemTypes;
import org.eclipse.pde.api.tools.internal.util.Util;

import com.ibm.icu.text.MessageFormat;

/**
 * Factory for creating {@link IApiProblem}s
 * 
 * @since 1.0.0
 */
public class ApiProblemFactory {
	
	public static final int TYPE_CONVERSION_ID = 76;

	/**
	 * The current mapping of problem id to message
	 */
	private static Hashtable fMessages = null;
	
	/**
	 * Creates a new {@link IApiProblem}
	 * @param resourcepath the path to the resource this problem was found in
	 * @param typeName the type name this problem was found in
	 * @param messageargs listing of arguments to pass in to the localized message.
	 * The arguments are passed into the string in the order they appear in the array.
	 * @param argumentids the ids of arguments passed into the problem
	 * @param arguments the arguments that correspond to the listing of ids
	 * @param linenumber the number of the line the problem occurred on
	 * @param charstart the start of a char selection range
	 * @param charend the end of a char selection range
	 * @param category the category of the problem. See {@link IApiProblem} for categories
	 * @param element the id of the backing element for this problem See {@link IElementDescriptor}, {@link IDelta} and {@link IJavaElement} for kinds
	 * @param kind the kind of the problem
	 * @param flags any additional flags for the kind
	 * @return a new {@link IApiProblem}
	 */
	public static IApiProblem newApiProblem(String resourcepath, String typeName, String[] messageargs, String[] argumentids, Object[] arguments, int linenumber, int charstart, int charend, int category, int element, int kind, int flags) {
		return newApiProblem(resourcepath, typeName, messageargs, argumentids, arguments, linenumber, charstart, charend, createProblemId(category, element, kind, flags));
	}
	
	/**
	 * Creates a new {@link IApiProblem}
	 * @param resourcepath the path to the resource this problem was found in
	 * @param typeName the type name this problem was found in
	 * @param messageargs listing of arguments to pass in to the localized message.
	 * The arguments are passed into the string in the order they appear in the array.
	 * @param argumentids the ids of arguments passed into the problem
	 * @param arguments the arguments that correspond to the listing of ids
	 * @param linenumber the number of the line the problem occurred on
	 * @param charstart the start of a char selection range
	 * @param charend the end of a char selection range
	 * @param id the composite id of the problem
	 * @return a new {@link IApiProblem}
	 */
	public static IApiProblem newApiProblem(String resourcepath, String typeName, String[] messageargs, String[] argumentids, Object[] arguments, int linenumber, int charstart, int charend, int id) {
		return new ApiProblem(resourcepath, typeName, messageargs, argumentids, arguments, linenumber, charstart, charend, id);
	}
	
	/**
	 * Creates a new API usage {@link IApiProblem}
	 * @param resourcepath the path to the resource this problem was found in
	 * @param typeName the type name this problem was found in
	 * @param messageargs listing of arguments to pass in to the localized message.
	 * The arguments are passed into the string in the order they appear in the array.
	 * @param argumentids the ids of arguments passed into the problem
	 * @param arguments the arguments that correspond to the listing of ids
	 * @param linenumber the number of the line the problem occurred on
	 * @param charstart the start of a char selection range
	 * @param charend the end of a char selection range
	 * @param element the element kind
	 * @param kind the kind
	 * @return a new {@link IApiProblem} for API usage
	 */
	public static IApiProblem newApiUsageProblem(String resourcepath, String typeName, String[] messageargs, String[] argumentids, Object[] arguments, int linenumber, int charstart, int charend, int element, int kind) {
		int id = createProblemId(IApiProblem.CATEGORY_USAGE, element, kind, IApiProblem.NO_FLAGS);
		return newApiProblem(resourcepath, typeName, messageargs, argumentids, arguments, linenumber, charstart, charend, id);
	}

	/**
	 * Creates a new API usage {@link IApiProblem}
	 * @param resourcepath the path to the resource this problem was found in
	 * @param typeName the type name this problem was found in
	 * @param messageargs listing of arguments to pass in to the localized message.
	 * The arguments are passed into the string in the order they appear in the array.
	 * @param argumentids the ids of arguments passed into the problem
	 * @param arguments the arguments that correspond to the listing of ids
	 * @param linenumber the number of the line the problem occurred on
	 * @param charstart the start of a char selection range
	 * @param charend the end of a char selection range
	 * @param element the element kind
	 * @param kind the kind
	 * @param flags the flags
	 * @return a new {@link IApiProblem} for API usage
	 */
	public static IApiProblem newApiUsageProblem(String resourcepath, String typeName, String[] messageargs, String[] argumentids, Object[] arguments, int linenumber, int charstart, int charend, int element, int kind, int flags) {
		int id = createProblemId(IApiProblem.CATEGORY_USAGE, element, kind, flags);
		return newApiProblem(resourcepath, typeName, messageargs, argumentids, arguments, linenumber, charstart, charend, id);
	}
	
	/**
	 * Creates a new API usage {@link IApiProblem}
	 * @param resourcepath the path to the resource this problem was found in
	 * @param typeName the type name this problem was found in
	 * @param messageargs listing of arguments to pass in to the localized message.
	 * The arguments are passed into the string in the order they appear in the array.
	 * @param argumentids the ids of arguments passed into the problem
	 * @param arguments the arguments that correspond to the listing of ids
	 * @param linenumber the number of the line the problem occurred on
	 * @param charstart the start of a char selection range
	 * @param charend the end of a char selection range
	 * @param element the element kind
	 * @param kind the kind
	 * @return a new {@link IApiProblem} for API usage
	 */
	public static IApiProblem newApiProfileProblem(String resourcepath, String typeName, String[] messageargs, String[] argumentids, Object[] arguments, int linenumber, int charstart, int charend, int element, int kind) {
		int id = createProblemId(IApiProblem.CATEGORY_API_PROFILE, element, kind, IApiProblem.NO_FLAGS);
		return newApiProblem(resourcepath, typeName, messageargs, argumentids, arguments, linenumber, charstart, charend, id);
	}
	
	/**
	 * Creates a new since tag {@link IApiProblem}
	 * @param resourcepath the path to the resource this problem was found in
	 * @param typeName the type name this problem was found in
	 * @param messageargs listing of arguments to pass in to the localized message.
	 * The arguments are passed into the string in the order they appear in the array.
	 * @param argumentids the ids of arguments passed into the problem
	 * @param arguments the arguments that correspond to the listing of ids
	 * @param linenumber the number of the line the problem occurred on
	 * @param charstart the start of a char selection range
	 * @param charend the end of a char selection range
	 * @param element the element kind
	 * @param kind the kind
	 * @return a new {@link IApiProblem} for since tags
	 */
	public static IApiProblem newApiSinceTagProblem(String resourcepath, String typeName, String[] messageargs, String[] argumentids, Object[] arguments, int linenumber, int charstart, int charend, int element, int kind) {
		int id = createProblemId(IApiProblem.CATEGORY_SINCETAGS, element, kind, IApiProblem.NO_FLAGS);
		return newApiProblem(resourcepath, typeName, messageargs, argumentids, arguments, linenumber, charstart, charend, id);
	}
	
	/**
	 * Creates a new version number {@link IApiProblem}
	 * @param resourcepath the path to the resource this problem was found in
	 * @param typeName the type name this problem was found in
	 * @param messageargs listing of arguments to pass in to the localized message.
	 * The arguments are passed into the string in the order they appear in the array.
	 * @param argumentids the ids of arguments passed into the problem
	 * @param arguments the arguments that correspond to the listing of ids
	 * @param linenumber the number of the line the problem occurred on
	 * @param charstart the start of a char selection range
	 * @param charend the end of a char selection range
	 * @param element the element kind
	 * @param kind the kind
	 * @return a new {@link IApiProblem} for version numbers
	 */
	public static IApiProblem newApiVersionNumberProblem(String resourcepath, String typeName, String[] messageargs, String[] argumentids, Object[] arguments, int linenumber, int charstart, int charend, int element, int kind) {
		int id = createProblemId(IApiProblem.CATEGORY_VERSION, element, kind, IApiProblem.NO_FLAGS);
		return newApiProblem(resourcepath, typeName, messageargs, argumentids, arguments, linenumber, charstart, charend, id);
	}
	
	/**
	 * Returns the localized message for the given {@link IApiProblem}. Returns
	 * <code>null</code> if no localized message cannot be created.
	 * @param problemid the id of the problem to create a message for
	 * @param arguments the arguments to pass into the localized string. The arguments are passed in to the string
	 * in the order they appear in the array.
	 * 
	 * @return a localized message for the given {@link IApiProblem} or <code>null</code>
	 */
	public static String getLocalizedMessage(IApiProblem problem) {
		return getLocalizedMessage(problem.getMessageid(), problem.getMessageArguments());
	}
	
	/**
	 * Returns the localized message for the given problem id and message arguments. Returns
	 * a not found message if no localized message cannot be created.
	 * @param messageid
	 * @param messageargs
	 * @return a localized message for the given arguments or a 'not found' message
	 */
	public static String getLocalizedMessage(int messageid, String[] messageargs){
		if(fMessages == null) {
			fMessages = loadMessageTemplates(Locale.getDefault());
		}
		String pattern = (String) fMessages.get(new Integer(messageid));
		if(pattern == null) {
			return MessageFormat.format(BuilderMessages.ApiProblemFactory_problem_message_not_found, new String[] {Integer.toString(messageid)});
		}
		if (messageid == TYPE_CONVERSION_ID) {
			MessageFormat messageFormat = new MessageFormat(pattern);
			double[] typeElementTypes = {
				IDelta.ANNOTATION_ELEMENT_TYPE,
				IDelta.CLASS_ELEMENT_TYPE,
				IDelta.ENUM_ELEMENT_TYPE,
				IDelta.INTERFACE_ELEMENT_TYPE,
			};
			String [] typeElementTypesStrings = {
					(String) fMessages.get(Util.getDeltaElementType(IDelta.ANNOTATION_ELEMENT_TYPE)),
					(String) fMessages.get(Util.getDeltaElementType(IDelta.CLASS_ELEMENT_TYPE)),
					(String) fMessages.get(Util.getDeltaElementType(IDelta.ENUM_ELEMENT_TYPE)),
					(String) fMessages.get(Util.getDeltaElementType(IDelta.INTERFACE_ELEMENT_TYPE)),
			};
			ChoiceFormat choiceFormat = new ChoiceFormat(typeElementTypes, typeElementTypesStrings);
			messageFormat.setFormatByArgumentIndex(1, choiceFormat);
			messageFormat.setFormatByArgumentIndex(2, choiceFormat);
			Object[] args = new Object[messageargs.length];
			args[0] = messageargs[0];
			args[1] = Integer.decode(messageargs[1]);
			args[2] = Integer.decode(messageargs[2]);
			return messageFormat.format(args);
		}
		return MessageFormat.format(pattern, messageargs);
	}
	
	/**
	 * This method initializes the MessageTemplates class variable according
	 * to the current Locale.
	 * @param loc Locale
	 * @return HashtableOfInt
	 */
	public static Hashtable loadMessageTemplates(Locale loc) {
		ResourceBundle bundle = null;
		String bundleName = "org.eclipse.pde.api.tools.internal.problems.problemmessages"; //$NON-NLS-1$
		try {
			bundle = ResourceBundle.getBundle(bundleName, loc); 
		} catch(MissingResourceException e) {
			System.out.println("Missing resource : " + bundleName.replace('.', '/') + ".properties for locale " + loc); //$NON-NLS-1$//$NON-NLS-2$
			throw e;
		}
		Hashtable templates = new Hashtable(700);
		Enumeration keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
		    String key = (String)keys.nextElement();
		    try {
		        int messageID = Integer.parseInt(key);
				templates.put(new Integer(messageID), bundle.getString(key));
		    } catch(NumberFormatException e) {
		        // key is not a number
		    	templates.put(key, bundle.getString(key));
			} catch (MissingResourceException e) {
				// available ID
		    }
		}
		return templates;
	}
	
	/**
	 * Creates a problem id from the composite members of a problem id.
	 * @param category
	 * @param element
	 * @param kind
	 * @param flags
	 * @return a new problem id
	 */
	public static int createProblemId(int category, int element, int kind, int flags) {
		return category | element << IApiProblem.OFFSET_ELEMENT | 
						  kind << IApiProblem.OFFSET_KINDS | 
						  flags << IApiProblem.OFFSET_FLAGS |
						  getProblemMessageId(category, element, kind, flags);
	}
	
	/**
	 * Returns the kind of the problem from the given problem id. The returned kind is not checked to see if it
	 * is correct or existing.
	 * 
	 * @see IApiProblem#getKind()
	 * @see IDelta#getKind()
	 * 
	 * @param problemid
	 * @return the kind from the given problem id
	 */
	public static int getProblemKind(int problemid) {
		return (problemid & ApiProblem.KIND_MASK) >> IApiProblem.OFFSET_KINDS;
	}
	
	/**
	 * Returns the kind of element from the given problem id. The returned element kind is not checked to see if it
	 * is correct or existing.
	 * 
	 * @see IElementDescriptor#getElementType()
	 * @see IDelta#getElementType()
	 * 
	 * @param problemid
	 * @return the element kind from the given problem id
	 */
	public static int getProblemElementKind(int problemid) {
		return (problemid & ApiProblem.ELEMENT_KIND_MASK) >> IApiProblem.OFFSET_ELEMENT;
	}
	
	/**
	 * Returns the flags from the given problem id. The returned flags are not checked to see if they
	 * are correct or existing.
	 * 
	 * @see IDelta#getFlags()
	 * 
	 * @param problemid
	 * @return the flags from the given problem id
	 */
	public static int getProblemFlags(int problemid) {
		return (problemid & ApiProblem.FLAGS_MASK) >> IApiProblem.OFFSET_FLAGS;
	}
	
	/**
	 * Returns the category of the given problem id. The returned category is not checked to see if it
	 * is correct or existing.
	 * 
	 * @see IApiProblem#getCategory()
	 * 
	 * @param problemid
	 * @return the category of this problem id
	 */
	public static int getProblemCategory(int problemid) {
		return (problemid & ApiProblem.CATEGORY_MASK);
	}
	
	/**
	 * Convenience method to get the message id from a problem id
	 * @param problemid
	 * @return the message id to use for the given problem id
	 */
	public static int getProblemMessageId(int problemid) {
		return getProblemMessageId(getProblemCategory(problemid), getProblemElementKind(problemid), getProblemKind(problemid), getProblemFlags(problemid));
	}
	
	/**
	 * Returns the problem message id for the given problem parameters.
	 * @param category
	 * @param element
	 * @param kind
	 * @param flags
	 * @return the id of the message to use for the given problem parameters or <code>0</code>
	 */
	public static int getProblemMessageId(int category, int element, int kind, int flags) {
		switch(category) {
			case IApiProblem.CATEGORY_API_PROFILE: {
				switch(kind) {
					case IApiProblem.API_PROFILE_MISSING: return 1;
				}
				break;
			}
			case IApiProblem.CATEGORY_SINCETAGS: {
				switch(kind) {
					case IApiProblem.SINCE_TAG_INVALID: return 2;
					case IApiProblem.SINCE_TAG_MALFORMED: return 3;
					case IApiProblem.SINCE_TAG_MISSING: return 4;
				}
				break;
			}
			case IApiProblem.CATEGORY_VERSION: {
				switch(kind) {
					case IApiProblem.MAJOR_VERSION_CHANGE: return 5;
					case IApiProblem.MAJOR_VERSION_CHANGE_NO_BREAKAGE: return 6;
					case IApiProblem.MINOR_VERSION_CHANGE: return 7;
					case IApiProblem.MINOR_VERSION_CHANGE_NO_NEW_API: return 56;
					case IApiProblem.REEXPORTED_MAJOR_VERSION_CHANGE : return 19;
					case IApiProblem.REEXPORTED_MINOR_VERSION_CHANGE : return 20;
				}
				break;
			}
			case IApiProblem.CATEGORY_USAGE: {
				switch(kind) {
					case IApiProblem.ILLEGAL_IMPLEMENT: return 8;
					case IApiProblem.ILLEGAL_EXTEND: return 9;
					case IApiProblem.ILLEGAL_INSTANTIATE: return 10;
					case IApiProblem.ILLEGAL_OVERRIDE: return 11;
					case IApiProblem.ILLEGAL_REFERENCE: {
						switch(flags) {
							case IApiProblem.FIELD: return 12;
							case IApiProblem.CONSTRUCTOR_METHOD: return 110;
							case IApiProblem.METHOD: return 111;
						}
						break;
					}
					case IApiProblem.API_LEAK: {
						switch(flags) {
							case IApiProblem.LEAK_EXTENDS: return 13;
							case IApiProblem.LEAK_IMPLEMENTS: return 14;
							case IApiProblem.LEAK_FIELD: return 15;
							case IApiProblem.LEAK_RETURN_TYPE: return 16;
							case IApiProblem.LEAK_METHOD_PARAMETER: return 17;
							case IApiProblem.LEAK_CONSTRUCTOR_PARAMETER: return 109;
						}
						break;
					}
					case IApiProblem.UNSUPPORTED_TAG_USE: return 112;
					case IApiProblem.DUPLICATE_TAG_USE: return 22;
					case IApiProblem.INVALID_REFERENCE_IN_SYSTEM_LIBRARIES :
						switch(flags) {
							case IApiProblem.METHOD :
								// problem message for wrong reference to method
								return 33;
							case IApiProblem.CONSTRUCTOR_METHOD :
								// problem message for wrong reference to constructor
								return 34;
							case IApiProblem.FIELD :
								// problem message for wrong reference to field
								return 35;
							default:
								 // problem message for wrong reference to type
								return 36;
						}
				}
				break;
			}
			case IApiProblem.CATEGORY_COMPATIBILITY: {
				switch(kind) {
					case IDelta.ADDED: {
						switch(element) {
							case IDelta.CLASS_ELEMENT_TYPE: {
								switch(flags) {
									case IDelta.METHOD: return 41;
									case IDelta.RESTRICTIONS: return 72;
								}
								break;
							}
							case IDelta.ANNOTATION_ELEMENT_TYPE: {
								switch(flags) {
									case IDelta.FIELD: return 39;
									case IDelta.METHOD: return 43;
								}
								break;
							}
							case IDelta.INTERFACE_ELEMENT_TYPE: {
								switch(flags) {
									case IDelta.FIELD: return 40;
									case IDelta.METHOD: return 44;
									case IDelta.RESTRICTIONS: return 72;
									case IDelta.SUPER_INTERFACE_WITH_METHODS : return 133;
								}
								break;
							}
							case IDelta.METHOD_ELEMENT_TYPE : {
								switch(flags) {
									case IDelta.RESTRICTIONS: return 132;
								}
							}
						}
						switch(flags) {
							case IDelta.ANNOTATION_DEFAULT_VALUE: return 18;
							case IDelta.CLASS_BOUND: return 21;
							case IDelta.CONSTRUCTOR: return 23;
							case IDelta.INTERFACE_BOUND: return 26;
							case IDelta.METHOD_WITHOUT_DEFAULT_VALUE: return 29;
							case IDelta.TYPE_PARAMETER: return 32;
							case IDelta.TYPE_ARGUMENT: return 106;
						}
						break;
					}
					case IDelta.CHANGED: {
						switch(element) {
							case IDelta.FIELD_ELEMENT_TYPE: {
								switch(flags) {
									case IDelta.TYPE: return 81;
									case IDelta.VALUE: return 84;
									case IDelta.DECREASE_ACCESS: return 114;
									case IDelta.NON_FINAL_TO_FINAL: return 118;
									case IDelta.STATIC_TO_NON_STATIC: return 121;
									case IDelta.NON_STATIC_TO_STATIC: return 69;
								}
								break;
							}
							case IDelta.METHOD_ELEMENT_TYPE : {
								switch(flags) {
									case IDelta.DECREASE_ACCESS : return 115;
									case IDelta.NON_ABSTRACT_TO_ABSTRACT : return 117;
									case IDelta.NON_FINAL_TO_FINAL: return 119;
									case IDelta.NON_STATIC_TO_STATIC: return 120;
									case IDelta.STATIC_TO_NON_STATIC: return 122;
								}
								break;
							}
							case IDelta.CONSTRUCTOR_ELEMENT_TYPE : {
								switch(flags) {
									case IDelta.DECREASE_ACCESS : return 116;
								}
							}
						}
						switch(flags) {
							case IDelta.CLASS_BOUND: return 52;
							case IDelta.CONTRACTED_SUPERINTERFACES_SET: return 54;
							case IDelta.DECREASE_ACCESS: return 55;
							case IDelta.FINAL_TO_NON_FINAL_STATIC_CONSTANT: return 61;
							case IDelta.INTERFACE_BOUND: return 64;
							case IDelta.NON_ABSTRACT_TO_ABSTRACT: return 66;
							case IDelta.NON_FINAL_TO_FINAL: return 67;
							case IDelta.NON_STATIC_TO_STATIC: return 123;
							case IDelta.STATIC_TO_NON_STATIC: return 73;
							case IDelta.TYPE_CONVERSION: return TYPE_CONVERSION_ID;
							case IDelta.VARARGS_TO_ARRAY: return 85;
							case IDelta.TYPE_ARGUMENT: return 124;
						}
						break;
					}
					case IDelta.REMOVED: {
						switch(flags) {
							case IDelta.ANNOTATION_DEFAULT_VALUE: return 86;
							case IDelta.API_COMPONENT: return 87;
							case IDelta.CLASS_BOUND: return 89;
							case IDelta.CONSTRUCTOR: return 91;
							case IDelta.ENUM_CONSTANT: return 92;
							case IDelta.FIELD: return 94;
							case IDelta.INTERFACE_BOUND: return 96;
							case IDelta.METHOD: return 98;
							case IDelta.METHOD_WITH_DEFAULT_VALUE: return 100;
							case IDelta.METHOD_WITHOUT_DEFAULT_VALUE: return 101;
							case IDelta.TYPE: return 102;
							case IDelta.TYPE_ARGUMENTS: return 103;
							case IDelta.TYPE_MEMBER: return 104;
							case IDelta.TYPE_PARAMETER: return 105;
							case IDelta.VALUE : return 108;
							case IDelta.API_TYPE: return 113;
							case IDelta.API_FIELD: return 125;
							case IDelta.API_METHOD: return 126;
							case IDelta.API_CONSTRUCTOR: return 127;
							case IDelta.API_ENUM_CONSTANT: return 128;
							case IDelta.API_METHOD_WITH_DEFAULT_VALUE : return 129;
							case IDelta.API_METHOD_WITHOUT_DEFAULT_VALUE: return 130;
							case IDelta.TYPE_ARGUMENT: return 107;
							case IDelta.SUPERCLASS: return 131;
						}
					}
				}
			}
		}
		return 0;
	}
	
	/**
	 * Returns the problem severity id for the given problem parameters.
	 * @param category
	 * @param element
	 * @param kind
	 * @param flags
	 * @return the id of the preference to use to lookup the user specified severity level for the given {@link IApiProblem}
	 */
	public static String getProblemSeverityId(IApiProblem problem) {
		switch(problem.getCategory()) {
			case IApiProblem.CATEGORY_API_PROFILE: {
				switch(problem.getKind()) {
					case IApiProblem.API_PROFILE_MISSING: return IApiProblemTypes.MISSING_DEFAULT_API_BASELINE;
				}
				break;
			}
			case IApiProblem.CATEGORY_SINCETAGS: {
				switch(problem.getKind()) {
					case IApiProblem.SINCE_TAG_INVALID: return IApiProblemTypes.INVALID_SINCE_TAG_VERSION;
					case IApiProblem.SINCE_TAG_MALFORMED: return IApiProblemTypes.MALFORMED_SINCE_TAG;
					case IApiProblem.SINCE_TAG_MISSING: return IApiProblemTypes.MISSING_SINCE_TAG;
				}
				break;
			}
			case IApiProblem.CATEGORY_VERSION: {
				return IApiProblemTypes.INCOMPATIBLE_API_COMPONENT_VERSION;
			}
			case IApiProblem.CATEGORY_USAGE: {
				switch(problem.getKind()) {
					case IApiProblem.ILLEGAL_IMPLEMENT: return IApiProblemTypes.ILLEGAL_IMPLEMENT;
					case IApiProblem.ILLEGAL_EXTEND: return IApiProblemTypes.ILLEGAL_EXTEND;
					case IApiProblem.ILLEGAL_INSTANTIATE: return IApiProblemTypes.ILLEGAL_INSTANTIATE;
					case IApiProblem.ILLEGAL_OVERRIDE: return IApiProblemTypes.ILLEGAL_OVERRIDE;
					case IApiProblem.ILLEGAL_REFERENCE: return IApiProblemTypes.ILLEGAL_REFERENCE;
					case IApiProblem.API_LEAK: {
						switch(problem.getFlags()) {
							case IApiProblem.LEAK_EXTENDS : return IApiProblemTypes.LEAK_EXTEND;
							case IApiProblem.LEAK_FIELD : return IApiProblemTypes.LEAK_FIELD_DECL;
							case IApiProblem.LEAK_IMPLEMENTS : return IApiProblemTypes.LEAK_IMPLEMENT;
							case IApiProblem.LEAK_CONSTRUCTOR_PARAMETER: 
							case IApiProblem.LEAK_METHOD_PARAMETER : return IApiProblemTypes.LEAK_METHOD_PARAM;
							case IApiProblem.LEAK_RETURN_TYPE : return IApiProblemTypes.LEAK_METHOD_RETURN_TYPE;
						}
						break;
					}
					case IApiProblem.UNSUPPORTED_TAG_USE: return IApiProblemTypes.INVALID_JAVADOC_TAG;
					case IApiProblem.DUPLICATE_TAG_USE: return IApiProblemTypes.INVALID_JAVADOC_TAG;
					case IApiProblem.INVALID_REFERENCE_IN_SYSTEM_LIBRARIES: return IApiProblemTypes.INVALID_REFERENCE_IN_SYSTEM_LIBRARIES;
				}
				break;
			}
			case IApiProblem.CATEGORY_COMPATIBILITY: {
				return Util.getDeltaPrefererenceKey(problem.getElementKind(), problem.getKind(), problem.getFlags());
			}
		}
		return null;
	}
	
	/**
	 * Returns the problem kind from the given preference key.
	 * 
	 * @see IApiProblemTypes for a listing of all preference keys
	 * @param prefkey
	 * @return the corresponding kind for the given preference key, or 0 if the pref key is unknown
	 */
	public static int getProblemKindFromPref(String prefkey) {
		if(IApiProblemTypes.ILLEGAL_EXTEND.equals(prefkey)) {
			return IApiProblem.ILLEGAL_EXTEND;
		}
		if(IApiProblemTypes.ILLEGAL_IMPLEMENT.equals(prefkey)) {
			return IApiProblem.ILLEGAL_IMPLEMENT;
		}
		if(IApiProblemTypes.ILLEGAL_INSTANTIATE.equals(prefkey)) {
			return IApiProblem.ILLEGAL_INSTANTIATE;
		}
		if(IApiProblemTypes.ILLEGAL_REFERENCE.equals(prefkey)) {
			return IApiProblem.ILLEGAL_REFERENCE;
		}
		if(IApiProblemTypes.ILLEGAL_OVERRIDE.equals(prefkey)) {
			return IApiProblem.ILLEGAL_OVERRIDE;
		}
		if(IApiProblemTypes.INVALID_REFERENCE_IN_SYSTEM_LIBRARIES.equals(prefkey)) {
			return IApiProblem.INVALID_REFERENCE_IN_SYSTEM_LIBRARIES;
		}
		if(IApiProblemTypes.MISSING_SINCE_TAG.equals(prefkey)) {
			return IApiProblem.SINCE_TAG_MISSING;
		}
		if(IApiProblemTypes.MALFORMED_SINCE_TAG.equals(prefkey)) {
			return IApiProblem.SINCE_TAG_MALFORMED;
		}
		if(IApiProblemTypes.INVALID_SINCE_TAG_VERSION.equals(prefkey)) {
			return IApiProblem.SINCE_TAG_INVALID;
		}
		if(prefkey != null) {
			if(prefkey.indexOf("ADDED") > -1) { //$NON-NLS-1$
				return IDelta.ADDED;
			}
			if(prefkey.indexOf("CHANGED") > -1) { //$NON-NLS-1$
				return IDelta.CHANGED;
			}
			if(prefkey.indexOf("REMOVED") > -1) { //$NON-NLS-1$
				return IDelta.REMOVED;
			}
		}
		return 0;
	}
}
