//============================================================
//
//	Copyright (c) 1999-2015 . All Rights Reserved.
//
//------------------------------------------------------------
//
//	Fichero: ClusterNetInvalidParameterException.java  1.0 11/9/99
//
//
//	Descripci�n: Clase ClusterNetInvalidParameterException.
//
//
// 	Authors: 
//		 Alejandro Garc�a-Dom�nguez (alejandro.garcia.dominguez@gmail.com)
//		 Antonio Berrocal Piris (antonioberrocalpiris@gmail.com)
//
//  Historial: 
//  07.04.2015 Changed licence to Apache 2.0     
//
//  This file is part of ClusterNet 
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//------------------------------------------------------------


package cnet;

import java.io.*;

/**
 * Clase ClusterNetInvalidParameterException. Esta excepci�n es lanzada
 * cuando se pasa alg�n par�metro inv�lido a alg�n m�todo en las
 * clases del paquete ClusterNet
 * @version 1.0
 * @author M. Alejandro Garc�a Dom�nguez
 * <A HREF="mailto:alejandro.garcia.dominguez@gmail.com">(alejandro.garcia.dominguez@gmail.com)</A><p>
 *			   Antonio Berrocal Piris
 */

public class ClusterNetInvalidParameterException extends IOException {

  //==========================================================================
  /**
   * Constructor por defecto.
   */
  public ClusterNetInvalidParameterException() { super();
  // PRUEBAS
  Log.log ("EXCEPCION: PARAMETROINVALIDOEXCEPCION","");
//  System.exit (1);

   }

  //==========================================================================
  /**
   * Constructor con un mensaje informativo del error ocurrido.
   * @param msg La cadena informativa.
   */
  public ClusterNetInvalidParameterException(String msg) { super(msg);
  // PRUEBAS
  Log.log ("EXCEPCION: PARAMETROINVALIDOEXCEPCION. ",msg);

//  System.exit (1);

  }

  //==========================================================================
  /**
   * Este constructor crea un objeto excepci�n PTMFParametroInvalidoExcepcioncon un mensaje
   * informativo del error ocurrido, adem�s imprime el mensaje en stdout.
   * @param mn Nombre del m�todo que lanz� la excepci�n.
   * @param msg La cadena informativa.
   */
  public ClusterNetInvalidParameterException(String mn,String msg) {
      super("["+mn+"] "+msg);
      Log.log("ClusterNetExcepcion: "+mn,msg);
  // PRUEBAS
  Log.log ("EXCEPCION: PARAMETROINVALIDOEXCEPCION. ",mn+" "+msg);


  //  System.exit (1);
  }


}
