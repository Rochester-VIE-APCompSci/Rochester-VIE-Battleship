/*
 * Copyright (c) 2014,2017 IBM Corporation
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package my.battleship;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is an annotation to hold the copyright string for Java source files.
 * The advantage of using this annotation is that is does not require a method
 * or constant in the Java file so the class members can stick to the API at hand.
 * <p>
 * The copyright constant used here should match what is in the header for
 * the source code.
 * <p>
 * This copyright annotation does not consume any resources at runtime, unlike
 * a static variable.
 * <p>
 * Usage in source code:
 * <pre>
 * import com.ibm.zoszmf.pdw.Copyright;
 * // Note for the javadoc source I had to replace the 'at' sign with the wierd entity reference
 * &#064;Copyright("(C) some copyright string")
 * public MyJavaClass
 * {
 *    ...
 * </pre>
 * Normally, you would use this, even though the redundancy makes it look funny.
 * <pre>
 * import com.ibm.zoszmf.pdw.Copyright;
 * // Note for the javadoc source I had to replace the 'at' sign with the wierd entity reference 
 * &#064;Copyright(Copyright.COPYRIGHT)
 * public MyJavaClass
 * {
 *    ...
 * </pre>
 * 
 *
 *
 */
@Retention(RetentionPolicy.CLASS) // Retains the values set on the annotation in the compiled .class file of the referring class (e.g. MyJavaClass.class), but so it is not visible or consume resources at runtime
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})  // This annotation only applies to classes and annotations, not methods, variables, etc.
public @interface Copyright
{
    // Note this annotation class itself doesn't have a copyright string, but since this is not something original
    // or took significant design effort it does not need it.

    String c2014 = "Licensed Materials - Property of IBM\n"
            + "com.ibm.mn.rochester.vie.battleship\n"
            +"(C) Copyright IBM Corp. 2014,2017. All Rights Reserved.\n"
            +"US Government Users Restricted Rights - Use, duplication or\n"
            +"disclosure restricted by GSA ADP Schedule Contract with IBM Corp.\n"; 
    
    /**
     * <code>value</code> is a special name for annotation data and for our purposes
     * holds the copyright string that applies to the source files for some Java class.
     * Should use one of the constants above.
     * <p>
     * Source code like MyJavaClass.java must explicitly refer to a constant or string value in the
     * annotation.  Annotation's can have default values, but the default
     * does not get compiled into the MyJavaClass.class file, which defeats the purpose.
     * 
     * @return Copyright string for the source file.
     */    
    String value();

}

